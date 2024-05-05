package sculktransporting.registration;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sculktransporting.SculkTransporting;
import sculktransporting.client.ItemSignalParticleOption;

public class STParticleTypes {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, SculkTransporting.MODID);
	public static final DeferredHolder<ParticleType<?>, ParticleType<ItemSignalParticleOption>> ITEM_SIGNAL = PARTICLE_TYPES.register("item_signal", () -> new ParticleType<ItemSignalParticleOption>(false) {
		@Override
		public MapCodec<ItemSignalParticleOption> codec() {
			return ItemSignalParticleOption.CODEC;
		}

		@Override
		public StreamCodec<? super RegistryFriendlyByteBuf, ItemSignalParticleOption> streamCodec() {
			return ItemSignalParticleOption.STREAM_CODEC;
		}
	});

	private STParticleTypes() {}
}
