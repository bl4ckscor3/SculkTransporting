package sculktransporting.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import sculktransporting.registration.STParticleTypes;

public class ItemSignalParticleOption extends VibrationParticleOption {
	//@formatter:off
	public static final Codec<VibrationParticleOption> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(PositionSource.CODEC.fieldOf("destination")
				.forGetter(p -> p.destination), Codec.INT.fieldOf("arrival_in_ticks")
				.forGetter(p -> p.arrivalInTicks), ItemStack.CODEC.fieldOf("stack")
				.forGetter(p -> ((ItemSignalParticleOption) p).stack))
			.apply(instance, ItemSignalParticleOption::new));
	//@formatter:on
	public static final ParticleOptions.Deserializer<VibrationParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
		@Override
		public VibrationParticleOption fromCommand(ParticleType<VibrationParticleOption> particleType, StringReader reader) throws CommandSyntaxException {
			float x, y, z;
			int arrivalInTicks;
			ItemParser.ItemResult itemResult;

			reader.expect(' ');
			x = (float) reader.readDouble();
			reader.expect(' ');
			y = (float) reader.readDouble();
			reader.expect(' ');
			z = (float) reader.readDouble();
			reader.expect(' ');
			arrivalInTicks = reader.readInt();
			reader.expect(' ');
			itemResult = ItemParser.parseForItem(BuiltInRegistries.ITEM.asLookup(), reader);

			BlockPos destination = BlockPos.containing(x, y, z);
			ItemStack stack = new ItemInput(itemResult.item(), itemResult.nbt()).createItemStack(1, false);

			return new ItemSignalParticleOption(new BlockPositionSource(destination), arrivalInTicks, stack);
		}

		@Override
		public VibrationParticleOption fromNetwork(ParticleType<VibrationParticleOption> particleType, FriendlyByteBuf buffer) {
			PositionSource destination = PositionSourceType.fromNetwork(buffer);
			int arrivalInTicks = buffer.readVarInt();
			ItemStack stack = buffer.readItem();

			return new ItemSignalParticleOption(destination, arrivalInTicks, stack);
		}
	};
	private final ItemStack stack;

	public ItemSignalParticleOption(PositionSource destination, int arrivalInTicks, ItemStack stack) {
		super(destination, arrivalInTicks);
		this.stack = stack.copy();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		super.writeToNetwork(buffer);
		buffer.writeItem(stack);
	}

	@Override
	public String writeToString() {
		return super.writeToString() + " " + new ItemInput(stack.getItemHolder(), stack.getTag()).serialize();
	}

	@Override
	public ParticleType<VibrationParticleOption> getType() {
		return STParticleTypes.ITEM_SIGNAL.get();
	}

	public ItemStack getItem() {
		return stack;
	}
}
