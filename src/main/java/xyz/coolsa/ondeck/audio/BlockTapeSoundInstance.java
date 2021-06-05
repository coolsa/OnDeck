package xyz.coolsa.ondeck.audio;

import java.util.List;

import ca.weblite.objc.Client;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockTapeSoundInstance extends EntityTrackingSoundInstance {
	List<BlockPos> blockPos;
	Entity player;
	double trueVol;
	Channel.SourceManager source;

	public BlockTapeSoundInstance(SoundEvent sound, SoundCategory soundCategory, float volume, float pitch,
			List<BlockPos> blockPos) {
		super(sound, soundCategory, volume, pitch, MinecraftClient.getInstance().player);
//		this.volume = 2;
		this.trueVol = volume;
		this.blockPos = blockPos;
		this.player = MinecraftClient.getInstance().player;
		// init center.
	}

	/**
	 * Remove or add a block, and update the center.
	 * 
	 * @param pos The position to add/remove.
	 */
	public void toggleBlock(BlockPos pos) {
		if (!blockPos.remove(pos)) { // if we fail to remove,
			blockPos.add(pos); // then we know we can add it!
		}
	}

	/**
	 * Update the player position and volume.
	 */
	private void updatePlayerPosition() {
		// begin by getting the players position
		Vec3d player = new Vec3d(this.player.getX(), this.player.getEyeY(), this.player.getZ());
		// start keeping track of average, weighted, positional vectors.
		double dirX = 0;
		double dirY = 0;
		double dirZ = 0;
		double weightTotal = 0;
		// loop through all the block positions
		for (BlockPos pos : blockPos) {
			// start by getting the squared distance between the player and the block.
			double squareDistance = pos.getSquaredDistance(player.x, player.y, player.z, false);
			double weight = 1.0 / (1.0 + squareDistance);
			dirX = dirX * weightTotal + pos.getX() * weight;
			dirY = dirY * weightTotal + pos.getY() * weight;
			dirZ = dirZ * weightTotal + pos.getZ() * weight;
			weightTotal += weight;
			dirX /= weightTotal;
			dirY /= weightTotal;
			dirZ /= weightTotal;
		}
		weightTotal = Math.min(Math.pow(weightTotal, 1.0 / 2) * 4, 1.0);
		this.volume = (float) (this.trueVol * weightTotal);
//		System.out.println(weightTotal + "\tweight");
//		System.out.println("\n" + dirX + "\tdirX\n" + dirY + "\n" + dirZ);
		this.x = dirX;
		this.y = dirY;
		this.z = dirZ;
	}

	@Override
	public void tick() {
//		System.out.println("ticming");
		if (this.player.removed) {
			this.setDone();
			return;
		}
		this.updatePlayerPosition();
	}

}
