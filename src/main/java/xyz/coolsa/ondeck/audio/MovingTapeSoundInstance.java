package xyz.coolsa.ondeck.audio;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class MovingTapeSoundInstance extends EntityTrackingSoundInstance {

	public MovingTapeSoundInstance(SoundEvent sound, SoundCategory soundCategory, ClientWorld world, int entityID) {
		super(sound, soundCategory, 1.0F, 1.0F, world.getEntityById(entityID));
	}

	/**
	 * This creates an EntityTrackingSoundInstance via a packet. Yup. Thats it.
	 * 
	 * @param sound         A sound event
	 * @param soundCategory The category that this sound event is in
	 * @param world         The ClientWorld the entity is in.
	 * @param volume        The volume, a float between 0 and 1.
	 * @param pitch         The pitch, a float normally between 0 and 2.
	 * @param entityID      The integer number of an entity, used to identify it.
	 */
	public MovingTapeSoundInstance(SoundEvent sound, SoundCategory soundCategory, ClientWorld world, float volume,
			float pitch, int entityID, boolean isClient) {
		super(sound, soundCategory, volume, pitch, world.getEntityById(entityID));
		if (isClient) {
			this.looping = true;
			this.x=0;
			this.y=0;
			this.z=0;
		}
	}

	@Override
	public void tick() {
		if (!this.looping)
			super.tick();
	}

}
