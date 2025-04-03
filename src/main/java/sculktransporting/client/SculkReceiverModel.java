package sculktransporting.client;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import sculktransporting.items.SpeedModifierItem.SpeedTier;

public class SculkReceiverModel implements BlockStateModel {
	private final BlockStateModel originalModel;
	private final Direction blockStateFacing;
	private final Map<SpeedTier, SculkReceiverModelPart> modelPartCache = new ConcurrentHashMap<>();

	public SculkReceiverModel(BlockStateModel originalModel, Direction blockStateFacing) {
		this.originalModel = originalModel;
		this.blockStateFacing = blockStateFacing;
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
		ModelData data = level.getModelData(pos);
		SpeedTier speedTier = data.get(ClientHandler.SPEED_TIER);

		if (speedTier != null) {
			for (BlockModelPart oldModelPart : originalModel.collectParts(level, pos, state, random)) {
				SculkReceiverModelPart newModelPart = modelPartCache.computeIfAbsent(speedTier, k -> new SculkReceiverModelPart(oldModelPart, blockStateFacing, speedTier));

				parts.add(newModelPart);
			}

			return;
		}

		parts.addAll(originalModel.collectParts(level, pos, state, random));
	}

	@Override
	public void collectParts(RandomSource random, List<BlockModelPart> modelList) {
		modelList.addAll(originalModel.collectParts(random));
	}

	@Override
	public TextureAtlasSprite particleIcon(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return originalModel.particleIcon(level, pos, state);
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return originalModel.particleIcon();
	}
}
