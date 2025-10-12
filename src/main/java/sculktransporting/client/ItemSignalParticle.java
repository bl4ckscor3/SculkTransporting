package sculktransporting.client;

import java.util.Optional;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public class ItemSignalParticle extends BreakingItemParticle {
	private final PositionSource target;

	public ItemSignalParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, PositionSource target, int lifetime, TextureAtlasSprite sprite) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite);
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

				setPos(x, y, z); //this also sets the bounding box of the particle, so there are no conflicts with Forge's particle culling. for more info see https://github.com/MinecraftForge/MinecraftForge/pull/8925
			}
		}
	}

	public static class Provider extends ItemParticleProvider<ItemSignalParticleOption> {
		@Override
		public Particle createParticle(ItemSignalParticleOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
			return new ItemSignalParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type.destination(), type.arrivalInTicks(), getSprite(type.stack(), level, random));
		}
	}
}
