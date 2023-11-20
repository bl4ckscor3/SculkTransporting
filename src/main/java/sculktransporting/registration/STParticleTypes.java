package sculktransporting.registration;

import com.mojang.serialization.Codec;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sculktransporting.SculkTransporting;
import sculktransporting.client.ItemSignalParticleOption;

public class STParticleTypes {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, SculkTransporting.MODID);
	public static final DeferredHolder<ParticleType<?>, ParticleType<VibrationParticleOption>> ITEM_SIGNAL = PARTICLE_TYPES.register("item_signal", () -> new ParticleType<>(false, ItemSignalParticleOption.DESERIALIZER) {
		@Override
		public Codec<VibrationParticleOption> codec() {
			return ItemSignalParticleOption.CODEC;
		}
	});

	private STParticleTypes() {}
}
