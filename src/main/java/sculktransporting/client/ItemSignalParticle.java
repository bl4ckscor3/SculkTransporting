package sculktransporting.client;

import java.util.Optional;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.VibrationParticleOption;
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
		xo = x;
		yo = y;
		zo = z;

		if (age++ >= lifetime)
			remove();
		else {
			Optional<Vec3> optional = target.getPosition(level);

			if (optional.isEmpty())
				remove();
			else {
				int aliveTime = lifetime - age;
				double delta = 1.0D / aliveTime;
				Vec3 targetPos = optional.get().add(xd, yd, zd);
				double x = Mth.lerp(delta, this.x, targetPos.x());
				double y = Mth.lerp(delta, this.y, targetPos.y());
				double z = Mth.lerp(delta, this.z, targetPos.z());

				setPos(x, y, z); //this also sets the bounding box of the particle, so it gets rendered properly (this is a bug with the vibration particle that is introduced by Forge's particle culling)
			}
		}
	}

	public static class Provider implements ParticleProvider<VibrationParticleOption> {
		@Override
		public Particle createParticle(VibrationParticleOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ItemSignalParticle(level, x, y, z, type.getDestination(), type.getArrivalInTicks(), ((ItemSignalParticleOption) type).getItem());
		}
	}
}
