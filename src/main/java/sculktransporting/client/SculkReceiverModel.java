package sculktransporting.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import sculktransporting.items.SpeedModifierItem.SpeedTier;

public class SculkReceiverModel implements IDynamicBakedModel {
	private final BakedModel originalModel;
	private final Direction modelDirection;
	private final Map<Pair<Direction, SpeedTier>, List<BakedQuad>> quadCache = new ConcurrentHashMap<>();

	public SculkReceiverModel(BakedModel originalModel, Direction direction) {
		this.originalModel = originalModel;
		this.modelDirection = direction;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
		if (side != null) {
			SpeedTier speedTier = data.get(ClientHandler.SPEED_TIER);

			if (speedTier != null) {
				return quadCache.computeIfAbsent(Pair.of(side, speedTier), k -> {
					List<BakedQuad> originalQuads = new ArrayList<>(originalModel.getQuads(state, side, rand, data, renderType));

					for (int i = 0; i < originalQuads.size(); i++) {
						BakedQuad quad = originalQuads.get(i);

						if (quad.isTinted()) {
							int tintIndex = quad.getTintIndex();

							if (tintIndex == 0)
								originalQuads.set(i, bakeQuad(Direction.NORTH, new Vector3f(0.0F, 3.0F, 0.0F), new Vector3f(16.0F, 8.0F, 0.0F), speedTier, quad));
							else if (tintIndex == 1)
								originalQuads.set(i, bakeQuad(Direction.EAST, new Vector3f(16.0F, 3.0F, 0.0F), new Vector3f(16.0F, 8.0F, 16.0F), speedTier, quad));
							else if (tintIndex == 2)
								originalQuads.set(i, bakeQuad(Direction.SOUTH, new Vector3f(0.0F, 3.0F, 16.0F), new Vector3f(16.0F, 8.0F, 16.0F), speedTier, quad));
							else if (tintIndex == 3)
								originalQuads.set(i, bakeQuad(Direction.WEST, new Vector3f(0.0F, 3.0F, 0.0F), new Vector3f(0.0F, 8.0F, 16.0F), speedTier, quad));
						}
					}

					return originalQuads;
				});
			}
		}

		return originalModel.getQuads(state, side, rand, data, renderType);
	}

	private BakedQuad bakeQuad(Direction quadDirection, Vector3f from, Vector3f to, SpeedTier speedTier, BakedQuad originalQuad) {
		return ClientHandler.bakeQuad(quadDirection, modelDirection, "sculk_receiver", from, to, speedTier, originalQuad, 0.0F, 8.0F, 16.0F, 13.0F);
	}

	@Override
	public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
		return ChunkRenderTypeSet.of(RenderType.cutout());
	}

	@Override
	public boolean useAmbientOcclusion() {
		return originalModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return originalModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return originalModel.usesBlockLight();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return originalModel.getParticleIcon();
	}

	@Override
	public ItemTransforms getTransforms() {
		return originalModel.getTransforms();
	}
}
