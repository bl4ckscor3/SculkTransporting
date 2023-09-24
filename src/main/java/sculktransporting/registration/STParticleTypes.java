package sculktransporting.registration;

import com.mojang.serialization.Codec;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sculktransporting.SculkTransporting;
import sculktransporting.client.ItemSignalParticleOption;

public class STParticleTypes {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SculkTransporting.MODID);
	public static final RegistryObject<ParticleType<VibrationParticleOption>> ITEM_SIGNAL = PARTICLE_TYPES.register("item_signal", () -> new ParticleType<>(false, ItemSignalParticleOption.DESERIALIZER) {
		@Override
		public Codec<VibrationParticleOption> codec() {
			return ItemSignalParticleOption.CODEC;
		}
	});

	private STParticleTypes() {}
}
