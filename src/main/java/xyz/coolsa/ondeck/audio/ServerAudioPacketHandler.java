package xyz.coolsa.ondeck.audio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.coolsa.ondeck.OnDeckConstants;

/**
 * This class (ideally) will handle all of the sending and requests for dfpwms
 * 
 * @author cloud
 *
 */
public class ServerAudioPacketHandler {
	private int streamNo;
	private Map<Integer, Pair<Integer, PacketByteBuf>> currentPackets;

	public ServerAudioPacketHandler() {
		streamNo = 0;
		this.currentPackets = new HashMap<Integer, Pair<Integer, PacketByteBuf>>();
		ServerPlayNetworking.registerGlobalReceiver(OnDeckConstants.DFPWM_STREAM_CONT,
				(server, player, handler, buf, responseSender) -> {
					sendRequestedStream(server, player, handler, buf, responseSender);
				});
		ServerPlayNetworking.registerGlobalReceiver(OnDeckConstants.DFPWM_STREAM_START,
				(server, player, handler, buf, responseSender) -> {
					startDfpwmStream(server, player, handler, buf, responseSender);
				});
		ServerPlayNetworking.registerGlobalReceiver(OnDeckConstants.DFPWM_STREAM_STOP,
				(server, player, handler, buf, responseSender) -> {
					stopDfpwmStream(server, player, handler, buf, responseSender);
				});
	}

	private void sendRequestedStream(MinecraftServer server, ServerPlayerEntity player,
			ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		int stream = buf.readInt();
		Pair<Integer, PacketByteBuf> packet = currentPackets.get(stream);
		if (packet != null) {
			if (packet.getLeft() == 0) {
				responseSender.sendPacket(OnDeckConstants.DFPWM_ENTITY_PLAY, packet.getRight());
			} else if (packet.getLeft() == 1) {
				responseSender.sendPacket(OnDeckConstants.DFPWM_BLOCK_PLAY, packet.getRight());
			}
		}
	}

	private void startDfpwmStream(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
			PacketByteBuf buf, PacketSender responseSender) {
		int stream = buf.readInt();
		Pair<Integer, PacketByteBuf> packet = currentPackets.get(stream);
		if (packet != null) {
			if (packet.getLeft() == 0) {
				responseSender.sendPacket(OnDeckConstants.DFPWM_ENTITY_PLAY, packet.getRight());
			} else if (packet.getLeft() == 1) {
				responseSender.sendPacket(OnDeckConstants.DFPWM_BLOCK_PLAY, packet.getRight());
			}
		}
	}

	private void stopDfpwmStream(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
			PacketByteBuf buf, PacketSender responseSender) {
		int stream = buf.readInt();
		Pair<Integer, PacketByteBuf> packet = currentPackets.get(stream);
		if (packet != null) {
			if (packet.getLeft() == 0) {
				responseSender.sendPacket(OnDeckConstants.DFPWM_ENTITY_PLAY, packet.getRight());
			} else if (packet.getLeft() == 1) {
				responseSender.sendPacket(OnDeckConstants.DFPWM_BLOCK_PLAY, packet.getRight());
			}
		}
	}

	public void newDfpwmStream(World world, PlayerEntity playerEntity) throws FileNotFoundException {
		streamNo += 1;
//		InputStream input = null;
		PacketByteBuf buf = PacketByteBufs.create();
//		if (input == null) {
		InputStream input = new FileInputStream("/home/cloud/Code/Java/OnDeck/src/main/resources/assets/NY_Bacon_Pancakes.dfpwm");
//		}
		int soundId = streamNo;
		float vol = 1.0F;
		float pitch = 1.0F;
		int entityId = playerEntity.getId();
		long[] test = new long[5];
		buf.writeInt(soundId);
		buf.writeFloat(vol);
		buf.writeFloat(pitch);
		test[0] = new BlockPos(0, 56, 0).asLong();
		test[1] = new BlockPos(20, 56, 0).asLong();
		test[2] = new BlockPos(-10, 56, 0).asLong();
		test[3] = new BlockPos(0, 56, 10).asLong();
		test[4] = new BlockPos(0, 56, -10).asLong();
		buf.writeLongArray(test);
		currentPackets.put(soundId, new Pair<Integer, PacketByteBuf>(1, buf));
		for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world, playerEntity.getBlockPos(), 500))
			ServerPlayNetworking.send(player, OnDeckConstants.DFPWM_BLOCK_PLAY, buf);

		// now lets send the data to the clients on a thread, so we can do it
		// arbitrarily.
		new Thread(() -> {
			try {
				int size = 1024;
				byte[] reading = new byte[size];
				int read = input.read(reading);
				while (read != -1) {
					if (reading.length != size) {
						byte[] dest = new byte[read];
						System.arraycopy(reading, 0, dest, 0, read);
						reading = dest;
					}
					PacketByteBuf data = PacketByteBufs.create();
					data.writeInt(soundId);
					data.writeByteArray(reading);
					for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world,
							playerEntity.getBlockPos(), 500))
						ServerPlayNetworking.send(player, OnDeckConstants.DFPWM_DATA_PACKET, data);
					Thread.sleep((long) (250 / pitch));
					reading = new byte[size];
					read = input.read(reading);
				}
				System.out.println("FINISHED FILE");
				input.close();
				currentPackets.remove(soundId);
			} catch (IOException e) {
			} catch (InterruptedException e) {
				// we are exiting since the thread is intended to be exited now.
			}
		}).start();
	}
}
