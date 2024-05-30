package sculktransporting.datagen;

import java.util.function.BiFunction;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import sculktransporting.SculkTransporting;
import sculktransporting.blocks.BaseSculkItemTransporterBlock;
import sculktransporting.blocks.SculkTransmitterBlock;
import sculktransporting.registration.STBlocks;

public class BlockStateGenerator extends BlockStateProvider {
	public BlockStateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, SculkTransporting.MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		getVariantBuilder(STBlocks.SCULK_BARREL.get()).forAllStates(state -> {
			int x = getXRotationBasedOnFacing(state);
			int y = getYRotationBasedOnFacing(state);
			ModelFile modelFile = new UncheckedModelFile(blockTexture(state.getBlock()) + (state.getValue(BarrelBlock.OPEN) ? "_open" : ""));

			return new ConfiguredModel[] {
					new ConfiguredModel(modelFile, x, y, false)
			};
		});
		createSculkItemTransporterState(STBlocks.SCULK_EMITTER.get());
		createSculkItemTransporterState(STBlocks.SCULK_RECEIVER.get());
		createSculkItemTransporterState(STBlocks.SCULK_TRANSMITTER.get(), (baseName, state) -> state.getValue(SculkTransmitterBlock.INVERTED) ? baseName + "_inverted" : baseName);
	}

	public void createSculkItemTransporterState(BaseSculkItemTransporterBlock block) {
		createSculkItemTransporterState(block, (baseName, state) -> baseName);
	}

	public void createSculkItemTransporterState(BaseSculkItemTransporterBlock block, BiFunction<String, BlockState, String> nameFunction) {
		getVariantBuilder(block).forAllStatesExcept(state -> {
			int x = getXRotationBasedOnFacing(state);
			int y = getYRotationBasedOnFacing(state);
			String baseName = blockTexture(block) + switch (state.getValue(SculkSensorBlock.PHASE)) {
				case ACTIVE -> "_active";
				default -> "_inactive";
			};

			return new ConfiguredModel[] {
					new ConfiguredModel(new UncheckedModelFile(nameFunction.apply(baseName, state)), x, y, false)
			};
		}, SculkSensorBlock.POWER, SculkSensorBlock.WATERLOGGED);
	}

	private int getXRotationBasedOnFacing(BlockState state) {
		return switch (state.getValue(BaseSculkItemTransporterBlock.FACING)) {
			case DOWN -> 180;
			case UP -> 0;
			default -> 90;
		};
	}

	private int getYRotationBasedOnFacing(BlockState state) {
		return switch (state.getValue(BaseSculkItemTransporterBlock.FACING)) {
			case EAST -> 90;
			case SOUTH -> 180;
			case WEST -> 270;
			default -> 0;
		};
	}
}
