package xyz.coolsa.ondeck;

import java.util.concurrent.CompletableFuture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.Source;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import xyz.coolsa.ondeck.mixin.SoundManagerAccessor;
import xyz.coolsa.ondeck.mixin.SoundSystemAccessor;

public class ItemTest extends Item {

//	Source source = SoundEngine.createSource(SoundEngine.RunMode.STREAMING);
	public ItemTest(Settings settings) {
		super(settings);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		AudioStream test = new AudioStreamTest();
		float vol = MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.BLOCKS);
		SoundSystemAccessor soundSystem = (SoundSystemAccessor) ((SoundManagerAccessor) (MinecraftClient.getInstance()
				.getSoundManager())).getSoundSystem();
		Channel.SourceManager cf = soundSystem.getChannel().createSource(SoundEngine.RunMode.STREAMING).join();
		if (cf != null) {
			cf.run(source -> {
				source.setPitch(1.0F);
				source.setVolume(vol);
//				source.setAttenuation(0.1F);
				source.disableAttenuation();
				source.setLooping(false);
				source.setPosition(playerEntity.getPos());
				source.setRelative(false);
				source.setStream(test);
			});
			cf.run(source -> {
				source.tick();
				source.play();
			});
			
		}
		System.out.println("RIGHT CLICKED YO");
//		playerEntity.playSound(SoundEvents.MUSIC_DISC_CHIRP, 1.0F, 1.0F);
		return TypedActionResult.success(playerEntity.getStackInHand(hand));
	}

}
