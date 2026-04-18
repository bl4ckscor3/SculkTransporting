package sculktransporting.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material.Baked;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
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
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
		ModelData data = level.getModelData(pos);
		SpeedTier speedTier = data.get(ClientHandler.SPEED_TIER);
		List<BlockStateModelPart> originalParts = new ArrayList<>();

		originalModel.collectParts(level, pos, state, random, originalParts);

		if (speedTier != null) {
			for (BlockStateModelPart oldModelPart : originalParts) {
				SculkReceiverModelPart newModelPart = modelPartCache.computeIfAbsent(speedTier, k -> new SculkReceiverModelPart(oldModelPart, blockStateFacing, speedTier));

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
