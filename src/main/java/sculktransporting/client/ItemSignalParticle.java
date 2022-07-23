package sculktransporting.client;

import java.util.Optional;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public class ItemSignalParticle extends BreakingItemParticle {
	private final PositionSource target;

	public ItemSignalParticle(ClientLevel level, double x, double y, double z, PositionSource target, int lifetime, ItemStack item) {
		super(level, x, y, z, item);
		this.x += xd;
		this.y += yd;
		this.z += zd;
		this.target = target;
		this.lifetime = lifetime;
	}

	@Override
	public void tick() { //mostly copied from VibrationSignalParticle#tick
		this.xo = x;
		this.yo = y;
		this.zo = z;

		if (this.age++ >= lifetime)
			remove();
		else {
			Optional<Vec3> optional = target.getPosition(level);

			if (optional.isEmpty())
				remove();
			else {
				int aliveTime = lifetime - age;
				double delta = 1.0D / (double)aliveTime;
				Vec3 targetPos = optional.get().add(xd, yd, zd);
				double x = Mth.lerp(delta, this.x, targetPos.x());
				double y = Mth.lerp(delta, this.y, targetPos.y());
				double z = Mth.lerp(delta, this.z, targetPos.z());

				setPos(x, y, z); //this also sets the BoundingBox of the particle, so it gets rendered properly (this is a bug with the Vibration particle that is fixed in 1.19.1)
			}
		}
	}

	public static class Provider implements ParticleProvider<ItemSignalParticleOption> {
		@Override
		public Particle createParticle(ItemSignalParticleOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ItemSignalParticle(level, x, y, z, type.getDestination(), type.getArrivalInTicks(), type.getItem());
		}
	}
}
