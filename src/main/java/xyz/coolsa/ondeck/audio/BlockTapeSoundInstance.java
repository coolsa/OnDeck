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
	Vec3d center;
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
		double x = 0;
		double y = 0;
		double z = 0;
		for (BlockPos pos : this.blockPos) {
			x += (pos.getX() + 0.5);
			y += (pos.getY() + 0.5);
			z += (pos.getZ() + 0.5);
		}
		this.center = new Vec3d(x / this.blockPos.size(), y / this.blockPos.size(), z / this.blockPos.size());
	}

	/**
	 * Remove or add a block, and update the center.
	 * 
	 * @param pos The position to add/remove.
	 */
	public void toggleBlock(BlockPos pos) {
		double x = this.center.getX() * this.blockPos.size();
		double y = this.center.getY() * this.blockPos.size();
		double z = this.center.getZ() * this.blockPos.size();
		if (!blockPos.remove(pos)) {
			blockPos.add(pos);
			x += (pos.getX() + 0.5);
			y += (pos.getY() + 0.5);
			z += (pos.getZ() + 0.5);
		} else {
			x -= (pos.getX() + 0.5);
			y -= (pos.getY() + 0.5);
			z -= (pos.getZ() + 0.5);
		}
		this.center = new Vec3d(x / this.blockPos.size(), y / this.blockPos.size(), z / this.blockPos.size());
	}

	private Vec3d test1() {
		Vec3d player = new Vec3d(this.player.getPos().getX(), this.player.getPos().getY(), this.player.getPos().getZ());
		double px = 1;
		double py = 1;
		double pz = 1;
		double sx = 1;
		double sy = 1;
		double sz = 1;
		double dx = 1;
		double dy = 1;
		double dz = 1;
		double wx = 0;
		double wy = 0;
		double wz = 0;
		double dwx = 0;
		double dwy = 0;
		double dwz = 0;
		double weight = 1;
		double dirX = 0;
		double dirY = 0;
		double dirZ = 0;
		for (BlockPos pos : blockPos) {
			double x = (player.x - (pos.getX() + 0.5));
			double y = (player.y - (pos.getY()));
			double z = (player.z - (pos.getZ() + 0.5));
//			if(x==0) {x = Double.MIN_NORMAL;}
//			if(y==0) {y = Double.MIN_NORMAL;}
//			if(z==0) {z = Double.MIN_NORMAL;}
			dx += 2.0 * (y * y + z * z) / (x * (x * x + y * y + z * z));
			dy += 2.0 * (x * x + z * z) / (y * (x * x + y * y + z * z));
			dz += 2.0 * (y * y + x * x) / (z * (x * x + y * y + z * z));
			px *= x * x / (x * x + y * y + z * z);
			py *= y * y / (x * x + y * y + z * z);
			pz *= z * z / (x * x + y * y + z * z);
			sx += x * x / (x * x + y * y + z * z);
			sy += y * y / (x * x + y * y + z * z);
			sz += z * z / (x * x + y * y + z * z);
			wx += 1.0 / (x);
			wy += 1.0 / (y);
			wz += 1.0 / (z);
			dwx += -1.0 / (x * x);
			dwy += -1.0 / (y * y);
			dwz += -1.0 / (z * z);
			weight *= 1.0 / (x + y + z);
		}
		dx *= px;
		dy *= py;
		dz *= pz;
		sx = (sx - 1) / blockPos.size();
		sy = (sy - 1) / blockPos.size();
		sz = (sz - 1) / blockPos.size();
		wx *= 1.0 / blockPos.size();
		wy *= 1.0 / blockPos.size();
		wz *= 1.0 / blockPos.size();
		dwx *= 1.0 / blockPos.size();
		dwy *= 1.0 / blockPos.size();
		dwz *= 1.0 / blockPos.size();
//		px = Math.pow(Math.abs(px), 1.0/blockPos.size());
//		py = Math.pow(Math.abs(py), 1.0/blockPos.size());
//		pz = Math.pow(Math.abs(pz), 1.0/blockPos.size());
//		sx *= wx;
//		sy *= wy;
//		sz *= wz;
//		weight /= blockPos.size();
		for (BlockPos pos : blockPos) {
			double x = (player.x - (pos.getX() + 0.5));
			double y = (player.y - (pos.getY()));
			double z = (player.z - (pos.getZ() + 0.5));
//			if(x==0) {x = Double.MIN_NORMAL;}
//			if(y==0) {y = Double.MIN_NORMAL;}
//			if(z==0) {z = Double.MIN_NORMAL;}
			dirX += Math.abs(x) * wx / (dwx) / blockPos.size();
			dirY += Math.abs(y) * wy / (dwy) / blockPos.size();
			dirZ += Math.abs(z) * wz / (dwz) / blockPos.size();
		}
		if (dirX == Double.NaN || wx == Double.POSITIVE_INFINITY)
			dirX = 0;
		if (dirY == Double.NaN || wy == Double.POSITIVE_INFINITY)
			dirY = 0;
		if (dirZ == Double.NaN || wz == Double.POSITIVE_INFINITY)
			dirZ = 0;
		double signX = dirX > 0 ? 1 : -1;
		double signY = dirY > 0 ? 1 : -1;
		double signZ = dirZ > 0 ? 1 : -1;
		dirX = signX * Math.pow(dirX * dirX, 1.0 / (4));
		dirY = signY * Math.pow(dirY * dirY, 1.0 / (4));
		dirZ = signZ * Math.pow(dirZ * dirZ, 1.0 / (4));
		this.volume = (float) (this.trueVol
				* (Math.min(Math.abs(wx), 1.0) + Math.min(Math.abs(wy), 1.0) + Math.min(Math.abs(wz), 1.0))/3);
		System.out.println("\n" + this.volume);
//		System.out.println("\n" + dx + "\tdx\n" + dy + "\n" + dz);
//		System.out.println("\n" + px + "\tpx\n" + py + "\n" + pz);
		System.out.println("\n" + wx + "\twx\n" + wy + "\n" + wz);
//		System.out.println("\n" + sx + "\tsx\n" + sy + "\n" + sz);
		// "\n" + weight);
//		System.out.println("\n" + dirX + "\tdirX\n" + dirY + "\n" + dirZ);
		Vec3d relative = new Vec3d(player.x + dirX, player.y + dirY, player.z + dirZ);
		return relative;
	}

	/**
	 * Get the percieved position of the block. limit is the center of everything.
	 * 
	 * @return A Vec3d position of the ideal sound center.
	 */
	private Vec3d relativePos() {
		Vec3d player = new Vec3d(this.player.getPos().getX(), this.player.getPos().getY(), this.player.getPos().getZ());
		// products of the positions.
		double px = 1;
		double py = 1;
		double pz = 1;
		// we recognize that the sum is pretty useless overall...
		// lets keep it, but create a derivative of the x product!
		// Derivative of product of positions.
		double dx = 0;
		double dy = 0;
		double dz = 0;
		// original curve product
		// sum of the positions.
		double sx = 0;
		double sy = 0;
		double sz = 0;
		double distProp = player.squaredDistanceTo(center);
		for (BlockPos pos : blockPos) {
			double dist = pos.getSquaredDistance(player.x - 0.5, this.player.getY() - 0.5, player.z - 0.5, false);
			dist = Math.pow(dist, 1.0 / 2.0);
//			dist += 1;
//			dist = (dist+1)/(avgDist+1);
			double x = (player.x - (pos.getX() + 0.5));
			double y = (player.y - (pos.getY() + 0.5));
			double z = (player.z - (pos.getZ() + 0.5));
//			dx += (2*(x*x)-dist)/dist;
//			dy += (2*(y*y)-dist)/dist;
//			dz += (2*(z*z)-dist)/dist;
			// derivative of product = product * the sum of all of these things.
			// sum of dist - x / dist^2
			dx += x * (dist * dist - x * x) / (dist * dist);
			dy += y * (dist * dist - y * y) / (dist * dist);
			dz += z * (dist * dist - z * z) / (dist * dist);
			x = x / dist;
			y = y / dist;
			z = z / dist;
			// now scale x by distance.
//			x = Math.pow(x, 5);
//			y = Math.pow(y, 5);
//			z = Math.pow(z, 5);
//			x = 1 - x * x;
//			y = 1 - y * y;
//			z = 1 - z * z;
//			double exp = 1.5;
//			x = Math.pow(x, exp);
//			y = Math.pow(x, exp);
//			z = Math.pow(z, exp);
			px *= x;
			py *= y;
			pz *= z;
			sx += x;
			sy += y;
			sz += z;
			// relative coordinates to the player.
//			System.out.println("\n" + pos + "\n" + dist + "\t" + (x) + "\t" + (y) + "\t" + (z));
//			relative = relative.add(x, y, z);
		}
		// now calculate final derivatives
		dx *= px;
		dy *= py;
		dz *= pz;
		sx /= blockPos.size();
		sy /= blockPos.size();
		sz /= blockPos.size();
		double ux = (1 + px) / 2;
		double uy = (1 + py) / 2;
		double uz = (1 + pz) / 2;
		double weight = Math.pow(1 - Math.abs(ux * uy * uz), 1);
//		weight = Math.pow(weight, 1/blockPos.size());
		System.out.println(
				"\n" + (px) + "\n" + (py) + "\n" + (pz) + "\n" + (dx) + "\n" + (dy) + "\n" + (dz) + "\n" + (weight));
		double multi = blockPos.size() * weight;
		double multi2 = 1 / 2;// Math.sqrt(weight)/(weight);
		Vec3d relative = new Vec3d(weight * (player.x - player.x * dx * multi2) + (1 - weight) * center.x,
				weight * (player.y - dy * multi2) + (1 - weight) * center.y,
				weight * (player.z - dz * multi2) + (1 - weight) * center.z);// (this.player.getZ()+(sz*pz)));
//		System.out.println(relative);
		System.out.println(relative);
		return relative;
	}

	@Override
	public void tick() {
		if (this.player.removed) {
			this.setDone();
			return;
		}
		Vec3d pos = this.test1();
		System.out.println(pos);
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
	}

}
