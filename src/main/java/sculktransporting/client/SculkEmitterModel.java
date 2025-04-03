package sculktransporting.client;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem.SpeedTier;

public class SculkEmitterModel implements BlockStateModel {
	private final BlockStateModel originalModel;
	private final Direction blockStateFacing;
	private final Map<Pair<SpeedTier, QuantityTier>, SculkEmitterModelPart> modelPartCache = new ConcurrentHashMap<>();

	public SculkEmitterModel(BlockStateModel originalModel, Direction blockStateFacing) {
		this.originalModel = originalModel;
		this.blockStateFacing = blockStateFacing;
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
		ModelData data = level.getModelData(pos);
		SpeedTier speedTier = data.get(ClientHandler.SPEED_TIER);
		QuantityTier quantityTier = data.get(ClientHandler.QUANTITY_TIER);

		if (speedTier != null && quantityTier != null) {
			for (BlockModelPart oldModelPart : originalModel.collectParts(level, pos, state, random)) {
				SculkEmitterModelPart newModelPart = modelPartCache.computeIfAbsent(Pair.of(speedTier, quantityTier),k -> new SculkEmitterModelPart(oldModelPart, blockStateFacing, speedTier, quantityTier));

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
