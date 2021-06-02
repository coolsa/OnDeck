package xyz.coolsa.ondeck;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.ArrayUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.Source;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.coolsa.ondeck.mixin.SoundManagerAccessor;
import xyz.coolsa.ondeck.mixin.SoundSystemAccessor;

/**
 * Huge ugly debug class is ugly and huge.
 * @author cloud
 *
 */
public class ItemTest extends Item {
	InputStream input;
	boolean playing = false;
	int streamNo;

//	Source source = SoundEngine.createSource(SoundEngine.RunMode.STREAMING);
	public ItemTest(Settings settings) {
		super(settings);
		streamNo = 0;
		// TODO Auto-generated constructor stub
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		if (world.isClient == true)
			return TypedActionResult.success(playerEntity.getStackInHand(hand));
		try {
			OnDeck.serverAudioPacketHandler.newDfpwmStream(world, playerEntity);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return TypedActionResult.success(playerEntity.getStackInHand(hand));
	}

}
