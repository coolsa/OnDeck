package xyz.coolsa.ondeck.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import xyz.coolsa.ondeck.OnDeckConstants;
import xyz.coolsa.ondeck.mixin.SoundManagerAccessor;
import xyz.coolsa.ondeck.mixin.SoundSystemAccessor;

/**
 * This function implements the client recieving part of packets.
 * 
 * @author cloud
 *
 */
@Environment(EnvType.CLIENT)
public class ClientAudioPacketHandler {
	private Map<Integer, Pair<AudioStream, SoundInstance>> playingStreams;

	public ClientAudioPacketHandler() {
		playingStreams = new HashMap<Integer, Pair<AudioStream, SoundInstance>>();
		// TODO Auto-generated method stub
//		playingStreams = new HashMap<Integer, AudioStream>();
		ClientPlayNetworking.registerGlobalReceiver(OnDeckConstants.DFPWM_ENTITY_PLAY,
				(client, handler, buf, responseSender) -> {
					this.receiveDfpwmEntity(client, handler, buf, responseSender);
				});
		// This will begin a positioned sound instance, stationary, non moving!
		ClientPlayNetworking.registerGlobalReceiver(OnDeckConstants.DFPWM_BLOCK_PLAY,
				(client, handler, buf, responseSender) -> {
					this.receiveDfpwmBlock(client, handler, buf, responseSender);
				});
		// This will update the data from a particular sound when receieved.
		ClientPlayNetworking.registerGlobalReceiver(OnDeckConstants.DFPWM_DATA_PACKET,
				(client, handler, buf, responseSender) -> {
					this.receiveDfpwmData(client, handler, buf, responseSender);
				});
		ClientPlayNetworking.registerGlobalReceiver(OnDeckConstants.DFPWM_BLOCK_UPDATE,
				(client, handler, buf, responseSender) -> {
					this.receiveDfpwmUpdate(client, handler, buf, responseSender);
				});
		ClientPlayNetworking.registerGlobalReceiver(OnDeckConstants.DFPWM_STOP_PLAY,
				(client, handler, buf, responseSender) -> {
					this.receiveDfpwmStop(client, handler, buf, responseSender);
				});
	}

	/**
	 * Decode and begin playing a new DFPWM entity player.
	 * 
	 * @param client         The client that will play the audio.
	 * @param handler
	 * @param buf
	 * @param responseSender
	 */
	private void receiveDfpwmEntity(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
			PacketSender responseSender) {
		int soundId = buf.readInt();
		float volume = buf.readFloat();
		float pitch = buf.readFloat();
		int entityId = buf.readInt();
		client.execute(() -> {
			if (playingStreams.get(soundId) == null || playingStreams.get(soundId).getRight() == null) {
//				float volume = vol;
				DfpwmAudioStream stream;
				if (playingStreams.get(soundId) == null || playingStreams.get(soundId).getLeft() == null
						|| playingStreams.get(soundId).getLeft() == null
						|| !((DfpwmAudioStream) playingStreams.get(soundId).getLeft()).isOpen()) {
					// if the stream does not exist yet (or ended), lets go ahead and add in a bit
					// of silence (1024*4/256th of a second)
					stream = new DfpwmAudioStream(null);
					byte[] blank = new byte[1024 * 4];
					for (int i = 0; i < blank.length; i++) {
						blank[i] = (byte) 0xAA; // yes. silence in DFPWM is 0xAAAAAAAAAAAAAAAA
					}
					stream.addPacket(blank, blank.length);
				} else {
					// otherwise we go ahead and load the stream we have.
					stream = (DfpwmAudioStream) playingStreams.get(soundId).getLeft();
				}
				SoundInstance soundInst = new MovingTapeSoundInstance(
						new SoundEvent(new Identifier("ondeck:tape_play")), SoundCategory.RECORDS, client.world, volume,
						pitch, entityId, client.player.getEntityId() == entityId);
				Channel.SourceManager cf = AudioHandler.playSound(soundInst, "Subtitles! Wowzers!", stream);

				if (client.player.getEntityId() == entityId) {
					cf.run(source -> {
						source.setRelative(true);
					});
				} else {
					cf.run(source -> {
						source.setRelative(false);
					});
				}
				Pair<AudioStream, SoundInstance> pair = new Pair<AudioStream, SoundInstance>(stream, soundInst);
				playingStreams.put(soundId, pair);
			}
		});
	}

	private void receiveDfpwmBlock(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
			PacketSender responseSender) {
		int soundId = buf.readInt();
		float vol = buf.readFloat();
		float pitch = buf.readFloat();
		long[] blockPosLong = buf.readLongArray(null);
		ArrayList<BlockPos> blockPos = new ArrayList<BlockPos>();
		for (long l : blockPosLong) {
			blockPos.add(BlockPos.fromLong(l));
		}
		client.execute(() -> {
			if (playingStreams.get(soundId) == null || playingStreams.get(soundId).getRight() == null) {
//				float volume = vol;
				DfpwmAudioStream stream;
				if (playingStreams.get(soundId) == null || playingStreams.get(soundId).getLeft() == null
						|| playingStreams.get(soundId).getLeft() == null
						|| !((DfpwmAudioStream) playingStreams.get(soundId).getLeft()).isOpen()) {
					// if the stream does not exist yet (or ended), lets go ahead and add in a bit
					// of silence (1024*4/256th of a second)
					stream = new DfpwmAudioStream(null);
					byte[] blank = new byte[1024 * 4];
					for (int i = 0; i < blank.length; i++) {
						blank[i] = (byte) 0xAA; // yes. silence in DFPWM is 0xAAAAAAAAAAAAAAAA
					}
					stream.addPacket(blank, blank.length);
				} else {
					// otherwise we go ahead and load the stream we have.
					stream = (DfpwmAudioStream) playingStreams.get(soundId).getLeft();
				}
				SoundInstance soundInst = new BlockTapeSoundInstance(new SoundEvent(new Identifier("ondeck:tape_play")),
						SoundCategory.RECORDS, vol, pitch, blockPos);
				Channel.SourceManager cf = AudioHandler.playSound(soundInst, "Subtitles! Wowzers!", stream);
				cf.run(source -> {
//					source.setAttenuation(Float.MAX_VALUE);
//					source.disableAttenuation();
					source.setRelative(false);
				});
//				System.out.println(soundInst.getAttenuationType()); //so attenuation doesnt change! NICE
				Pair<AudioStream, SoundInstance> pair = new Pair<AudioStream, SoundInstance>(stream, soundInst);
				playingStreams.put(soundId, pair);
			}
		});
	}

	private void receiveDfpwmData(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
			PacketSender responseSender) {
		int soundId = buf.readInt();
		byte[] data = buf.readByteArray();
//		System.out.println("got data");
		client.execute(() -> {
			//lets see if we are too far away to hear.
			SoundInstance instance = this.playingStreams.get(soundId).getRight();
			if (Math.sqrt(client.player.squaredDistanceTo(instance.getX(), instance.getY(), instance.getZ())) >= 100) {
				((SoundManagerAccessor) (MinecraftClient.getInstance().getSoundManager())).getSoundSystem().stop(instance);
				return;
			}
			if (playingStreams.get(soundId) == null || playingStreams.get(soundId).getLeft() == null
					|| !((DfpwmAudioStream) playingStreams.get(soundId).getLeft()).isOpen()) {
				// this would be cause for us to send a response via handler to get them to send
				// us info on the DFPWM player.
				DfpwmAudioStream stream = new DfpwmAudioStream(null);
				byte[] blank = new byte[1024 * 4];
				for (int i = 0; i < blank.length; i++) {
					blank[i] = (byte) 0xAA; // yes. silence in DFPWM is 0xAAAAAAAAAAAAAAAA
				}
				stream.addPacket(blank, blank.length);
				stream.addPacket(data, data.length);
				this.playingStreams.put(soundId, new Pair<AudioStream, SoundInstance>(stream, null));
				PacketByteBuf request = PacketByteBufs.create();
				request.writeInt(soundId);
				responseSender.sendPacket(OnDeckConstants.DFPWM_STREAM_CONT, request);
			} else {
				((DfpwmAudioStream) playingStreams.get(soundId).getLeft()).addPacket(data, data.length);
			}
		});
	}

	private void receiveDfpwmStop(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
			PacketSender responseSender) {
		int soundId = buf.readInt();
	}

	private void receiveDfpwmUpdate(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
			PacketSender responseSender) {
		int soundId = buf.readInt();
		long[] blockPosLong = buf.readLongArray(null);
		if (playingStreams.get(soundId) == null || playingStreams.get(soundId).getRight() == null) {
			return;
		}
		for (long l : blockPosLong) {
			BlockPos pos = BlockPos.fromLong(l);
			
		}
	}
}
