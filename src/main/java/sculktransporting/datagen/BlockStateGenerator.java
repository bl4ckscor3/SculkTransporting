package sculktransporting.datagen;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import sculktransporting.blocks.BaseSculkItemTransporterBlock;
import sculktransporting.blocks.SculkTransmitterBlock;
import sculktransporting.registration.STBlocks;

public class BlockStateGenerator {
	public static final TextureSlot OVERLAY_TEXTURE_SLOT = TextureSlot.create("overlay");
	public static final TextureSlot OVERLAY_TOP_TEXTURE_SLOT = TextureSlot.create("overlayTop");
	public static final ModelTemplate SCULK_BARREL_MODEL_TEMPLATE = ModelTemplates.create("sculktransporting:template_sculk_barrel", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE, OVERLAY_TEXTURE_SLOT, OVERLAY_TOP_TEXTURE_SLOT);
	public static final TexturedModel.Provider SCULK_BARREL_TEXTURED_MODEL = TexturedModel.createDefault(TextureMapping::cubeBottomTop, SCULK_BARREL_MODEL_TEMPLATE);
	static BlockModelGenerators blockModelGenerators;
	static Consumer<BlockModelDefinitionGenerator> blockStateOutput;
	static BiConsumer<Identifier, ModelInstance> modelOutput;

	protected static void run(BlockModelGenerators blockModels) {
		blockModelGenerators = blockModels;
		blockStateOutput = blockModelGenerators.blockStateOutput;
		modelOutput = blockModelGenerators.modelOutput;
		createSculkBarrel();
		createSculkTransmissionEndBlock(STBlocks.SCULK_EMITTER.get());
		createSculkTransmissionEndBlock(STBlocks.SCULK_RECEIVER.get());
		createSculkTransmitter();
	}

	public static void createSculkBarrel() {
		Block sculkBarrel = STBlocks.SCULK_BARREL.get();
		Identifier topOpenTexture = TextureMapping.getBlockTexture(Blocks.BARREL, "_top_open");
		Identifier overlayTexture = TextureMapping.getBlockTexture(Blocks.SCULK_VEIN);
		Identifier overlayTopTexture = TextureMapping.getBlockTexture(sculkBarrel, "_overlay_top");
		//@formatter:off
		MultiVariant closedVariant = BlockModelGenerators.plainVariant(
			SCULK_BARREL_TEXTURED_MODEL
				.get(Blocks.BARREL)
				.updateTextures(textureMapping -> textureMapping.put(OVERLAY_TEXTURE_SLOT, overlayTexture).put(OVERLAY_TOP_TEXTURE_SLOT, overlayTexture))
				.create(sculkBarrel, modelOutput));
		MultiVariant openVariant = BlockModelGenerators.plainVariant(
			SCULK_BARREL_TEXTURED_MODEL
				.get(Blocks.BARREL)
				.updateTextures(textureMapping ->
					textureMapping
					.put(TextureSlot.TOP, topOpenTexture)
					.put(OVERLAY_TEXTURE_SLOT, overlayTexture)
					.put(OVERLAY_TOP_TEXTURE_SLOT, overlayTopTexture))
				.createWithSuffix(sculkBarrel, "_open", modelOutput)
		);
		blockStateOutput.accept(
			MultiVariantGenerator.dispatch(sculkBarrel)
				.with(PropertyDispatch.initial(BlockStateProperties.OPEN)
					.select(false, closedVariant)
					.select(true, openVariant))
				.with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING)
		);
		//@formatter:on
	}

	public static void createSculkTransmissionEndBlock(BaseSculkItemTransporterBlock block) {
		Identifier activeLocation = ModelLocationUtils.getModelLocation(block, "_active");
		MultiVariant activeVariant = BlockModelGenerators.plainVariant(activeLocation);
		MultiVariant inactiveVariant = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(block, "_inactive"));
		blockModelGenerators.registerSimpleItemModel(block, activeLocation);
		//@formatter:off
		blockStateOutput.accept(
			MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(BlockStateProperties.SCULK_SENSOR_PHASE)
					.generate(phase -> phase == SculkSensorPhase.ACTIVE ? activeVariant : inactiveVariant))
				.with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING)
		);
		//@formatter:on
	}

	public static void createSculkTransmitter() {
		Block block = STBlocks.SCULK_TRANSMITTER.get();
		Identifier activeLocation = ModelLocationUtils.getModelLocation(block, "_active");
		MultiVariant inactiveVariant = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(block, "_inactive"));
		MultiVariant activeVariant = BlockModelGenerators.plainVariant(activeLocation);
		MultiVariant inactiveInvertedVariant = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(block, "_inactive_inverted"));
		MultiVariant activeInvertedVariant = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(block, "_active_inverted"));
		blockModelGenerators.registerSimpleItemModel(block, activeLocation);
		//@formatter:off
		blockStateOutput.accept(
			MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(SculkTransmitterBlock.INVERTED, BlockStateProperties.SCULK_SENSOR_PHASE)
					.generate((inverted, phase) ->
						inverted
							? phase == SculkSensorPhase.ACTIVE ? activeInvertedVariant : inactiveInvertedVariant
							: phase == SculkSensorPhase.ACTIVE ? activeVariant : inactiveVariant))
				.with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING)
		);
		//@formatter:on
	}
}
