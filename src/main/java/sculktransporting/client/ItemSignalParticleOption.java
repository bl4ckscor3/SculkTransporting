package sculktransporting.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.PositionSource;
import sculktransporting.registration.STParticleTypes;

public record ItemSignalParticleOption(PositionSource destination, int arrivalInTicks, ItemStack stack) implements ParticleOptions {

	//@formatter:off
	public static final MapCodec<ItemSignalParticleOption> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					VibrationParticleOption.SAFE_POSITION_SOURCE_CODEC.fieldOf("destination").forGetter(p -> p.destination),
					Codec.INT.fieldOf("arrival_in_ticks").forGetter(p -> p.arrivalInTicks),
					ItemStack.CODEC.fieldOf("stack").forGetter(p -> p.stack))
			.apply(instance, ItemSignalParticleOption::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemSignalParticleOption> STREAM_CODEC = StreamCodec.composite(
            PositionSource.STREAM_CODEC, ItemSignalParticleOption::destination,
            ByteBufCodecs.VAR_INT, ItemSignalParticleOption::arrivalInTicks,
            ItemStack.STREAM_CODEC, ItemSignalParticleOption::stack,
            ItemSignalParticleOption::new);
	//@formatter:on
	@Override
	public ParticleType<ItemSignalParticleOption> getType() {
		return STParticleTypes.ITEM_SIGNAL.get();
	}
}
