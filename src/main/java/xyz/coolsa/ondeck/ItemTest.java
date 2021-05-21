package xyz.coolsa.ondeck;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
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
		if (world.isClient == false)
			return TypedActionResult.success(playerEntity.getStackInHand(hand));
		runItemTest(playerEntity);
		return TypedActionResult.success(playerEntity.getStackInHand(hand));
	}
	
	@Environment(EnvType.CLIENT)
	private void runItemTest(PlayerEntity playerEntity) {
		Identifier soundId = new Identifier("ondeck:tape_play");
		SoundInstance soundInst = new PositionedSoundInstance(soundId, SoundCategory.RECORDS, 1.0F, 1.0F, false, 0,
				SoundInstance.AttenuationType.LINEAR, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
				false);
		WeightedSoundSet soundSet = new WeightedSoundSet(soundId, "This is a very very long subtitle which I need to keep around for longer, or keep updating.");
		soundInst.getSoundSet(MinecraftClient.getInstance().getSoundManager());
		AudioStream test = new AudioStreamTest();
		float vol = MinecraftClient.getInstance().options.getSoundVolume(soundInst.getCategory());
		SoundSystemAccessor soundSystem = (SoundSystemAccessor) ((SoundManagerAccessor) (MinecraftClient.getInstance()
				.getSoundManager())).getSoundSystem();
		for(final SoundInstanceListener l : soundSystem.getListeners()) {
			l.onSoundPlayed(soundInst, soundSet);
		}
		soundSystem.getSoundEndTicks().put(soundInst, soundSystem.getTicks()+100);
		Channel.SourceManager cf = soundSystem.getChannel().createSource(SoundEngine.RunMode.STREAMING).join();
		soundSystem.getSounds().put(soundInst.getCategory(), soundInst);
		if (cf != null) {
			soundSystem.getSources().put(soundInst, cf);
			cf.run(source -> {
				source.setPitch(1F);//1.46484275F*4);
				source.setVolume(vol);
				if (soundInst.getAttenuationType().equals(SoundInstance.AttenuationType.NONE))
					source.disableAttenuation();
				else
					source.setAttenuation(soundInst.getSound().getAttenuation()*4);
//				source.disableAttenuation();
				source.setLooping(false);
				source.setPosition(playerEntity.getPos());
				source.setRelative(false);
			});
			cf.run(source -> {
				source.setStream(test);
//				source.tick();
				source.play();
			});
		}
//		System.out.println("RIGHT CLICKED YO");
//		playerEntity.playSound(SoundEvents.MUSIC_DISC_CHIRP, 1.0F, 1.0F);
	}

}
