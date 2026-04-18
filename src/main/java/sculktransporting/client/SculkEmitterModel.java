package sculktransporting.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material.Baked;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
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
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
		ModelData data = level.getModelData(pos);
		SpeedTier speedTier = data.get(ClientHandler.SPEED_TIER);
		QuantityTier quantityTier = data.get(ClientHandler.QUANTITY_TIER);
		List<BlockStateModelPart> originalParts = new ArrayList<>();

		originalModel.collectParts(level, pos, state, random, originalParts);

		if (speedTier != null && quantityTier != null) {
			for (BlockStateModelPart oldModelPart : originalParts) {
				SculkEmitterModelPart newModelPart = modelPartCache.computeIfAbsent(Pair.of(speedTier, quantityTier), k -> new SculkEmitterModelPart(oldModelPart, blockStateFacing, speedTier, quantityTier));

				parts.add(newModelPart);
			}
		}
		else
			parts.addAll(originalParts);
	}

	@Override
	public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
		originalModel.collectParts(random, parts);
	}

	@Override
	public Baked particleMaterial() {
		return originalModel.particleMaterial();
	}

	@Override
	public int materialFlags() {
		return originalModel.materialFlags();
	}
}
