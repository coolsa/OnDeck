package xyz.coolsa.ondeck.audio;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import xyz.coolsa.ondeck.mixin.SoundManagerAccessor;
import xyz.coolsa.ondeck.mixin.SoundSystemAccessor;

public class AudioHandler {

	//Play sound, but don't clamp the pitch. Just the volume.
	@Environment(EnvType.CLIENT)
	public static Channel.SourceManager playSound(SoundInstance soundInst, @Nullable String subtitle,
			AudioStream audioStream) {
		int pointer = 0;
		Identifier soundId = soundInst.getId();
		WeightedSoundSet soundSet = new WeightedSoundSet(soundId, subtitle);
		soundInst.getSoundSet(MinecraftClient.getInstance().getSoundManager());
		SoundSystemAccessor soundSystem = (SoundSystemAccessor) ((SoundManagerAccessor) (MinecraftClient.getInstance()
				.getSoundManager())).getSoundSystem();
		for (final SoundInstanceListener l : soundSystem.getListeners()) {
			l.onSoundPlayed(soundInst, soundSet);
		}
		Channel.SourceManager cf = soundSystem.getChannel().createSource(SoundEngine.RunMode.STREAMING).join();
		if (cf != null) {
			soundSystem.getSources().put(soundInst, cf);
			soundSystem.getSoundEndTicks().put(soundInst, soundSystem.getTicks() + 20);
			soundSystem.getSounds().put(soundInst.getCategory(), soundInst);
			cf.run(source -> {
				source.setPitch(soundInst.getPitch());
				source.setVolume(soundSystem.invokeGetAdjustedVolume(soundInst));
				if (soundInst.getAttenuationType().equals(SoundInstance.AttenuationType.NONE))
					source.disableAttenuation();
				else
					source.setAttenuation(soundInst.getSound().getAttenuation() * 4);
				source.setLooping(false);
				source.setRelative(true);
			});
			cf.run(source -> {
				source.setStream(audioStream);
				source.play();
			});
		}
		if(soundInst instanceof TickableSoundInstance) {
			soundSystem.getTickingSounds().add((TickableSoundInstance) soundInst);
		}
		return cf;
	}
	
//	@Environment(EnvType.CLIENT)
//	private void runItemTest(PlayerEntity playerEntity) {
//		Identifier soundId = new Identifier("ondeck:tape_play");
//		SoundInstance soundInst = new PositionedSoundInstance(soundId, SoundCategory.RECORDS, 1.0F, 1.0F, false, 0,
//				SoundInstance.AttenuationType.LINEAR, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
//				false);
//		WeightedSoundSet soundSet = new WeightedSoundSet(soundId, "This is a very very long subtitle which I need to keep around for longer, or keep updating.");
//		soundInst.getSoundSet(MinecraftClient.getInstance().getSoundManager());
//		AudioStream test = new AudioStreamTest();
//		float vol = MinecraftClient.getInstance().options.getSoundVolume(soundInst.getCategory());
//		SoundSystemAccessor soundSystem = (SoundSystemAccessor) ((SoundManagerAccessor) (MinecraftClient.getInstance()
//				.getSoundManager())).getSoundSystem();
//		for(final SoundInstanceListener l : soundSystem.getListeners()) {
//			l.onSoundPlayed(soundInst, soundSet);
//		}
//		soundSystem.getSoundEndTicks().put(soundInst, soundSystem.getTicks()+100);
//		Channel.SourceManager cf = soundSystem.getChannel().createSource(SoundEngine.RunMode.STREAMING).join();
//		soundSystem.getSounds().put(soundInst.getCategory(), soundInst);
//		if (cf != null) {
//			soundSystem.getSources().put(soundInst, cf);
//			cf.run(source -> {
//				source.setPitch(1F);//1.46484275F*4);
//				source.setVolume(vol);
//				if (soundInst.getAttenuationType().equals(SoundInstance.AttenuationType.NONE))
//					source.disableAttenuation();
//				else
//					source.setAttenuation(soundInst.getSound().getAttenuation()*4);
//				source.setLooping(false);
//				source.setPosition(playerEntity.getPos());
//				source.setRelative(false);
//			});
//			cf.run(source -> {
//				source.setStream(test);
//				source.play();
//			});
//		}
//	}
}
