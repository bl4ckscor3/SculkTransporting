package sculktransporting.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Vector3f;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import sculktransporting.items.ModifierTier;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem.SpeedTier;

public class SculkEmitterModel implements IDynamicBakedModel {
	private final BakedModel originalModel;
	private final Direction modelDirection;
	private final Map<CacheKey, List<BakedQuad>> quadCache = new ConcurrentHashMap<>();

	public SculkEmitterModel(BakedModel originalModel, Direction direction) {
		this.originalModel = originalModel;
		this.modelDirection = direction;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
		if (side != null) {
			SpeedTier speedTier = data.get(ClientHandler.SPEED_TIER);
			QuantityTier quantityTier = data.get(ClientHandler.QUANTITY_TIER);

			if (speedTier != null && quantityTier != null) {
				return quadCache.computeIfAbsent(new CacheKey(side, speedTier, quantityTier), k -> {
					List<BakedQuad> originalQuads = new ArrayList<>(originalModel.getQuads(state, side, rand, data, renderType));

					for (int i = 0; i < originalQuads.size(); i++) {
						BakedQuad quad = originalQuads.get(i);

						if (quad.isTinted()) {
							int tintIndex = quad.getTintIndex();

							if (tintIndex == 0)
								originalQuads.set(i, bakeLeftQuad(Direction.NORTH, new Vector3f(8.0F, 0.0F, 0.0F), new Vector3f(16.0F, 8.0F, 0.0F), speedTier, quad));
							else if (tintIndex == 1)
								originalQuads.set(i, bakeRightQuad(Direction.NORTH, new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(8.0F, 8.0F, 0.0F), quantityTier, quad));
							else if (tintIndex == 2)
								originalQuads.set(i, bakeLeftQuad(Direction.EAST, new Vector3f(16.0F, 0.0F, 8.0F), new Vector3f(16.0F, 8.0F, 16.0F), speedTier, quad));
							else if (tintIndex == 3)
								originalQuads.set(i, bakeRightQuad(Direction.EAST, new Vector3f(16.0F, 0.0F, 0.0F), new Vector3f(16.0F, 8.0F, 8.0F), quantityTier, quad));
							else if (tintIndex == 4)
								originalQuads.set(i, bakeLeftQuad(Direction.SOUTH, new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(8.0F, 8.0F, 16.0F), speedTier, quad));
							else if (tintIndex == 5)
								originalQuads.set(i, bakeRightQuad(Direction.SOUTH, new Vector3f(8.0F, 0.0F, 16.0F), new Vector3f(16.0F, 8.0F, 16.0F), quantityTier, quad));
							else if (tintIndex == 6)
								originalQuads.set(i, bakeLeftQuad(Direction.WEST, new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 8.0F, 8.0F), speedTier, quad));
							else if (tintIndex == 7)
								originalQuads.set(i, bakeRightQuad(Direction.WEST, new Vector3f(0.0F, 0.0F, 8.0F), new Vector3f(0.0F, 8.0F, 16.0F), quantityTier, quad));
						}
					}

					return originalQuads;
				});
			}
		}

		return originalModel.getQuads(state, side, rand, data, renderType);
	}

	private BakedQuad bakeLeftQuad(Direction quadDirection, Vector3f from, Vector3f to, ModifierTier modifierTier, BakedQuad originalQuad) {
		return bakeQuad(quadDirection, from, to, modifierTier, originalQuad, 0.0F, 8.0F, 8.0F, 16.0F);
	}

	private BakedQuad bakeRightQuad(Direction quadDirection, Vector3f from, Vector3f to, ModifierTier modifierTier, BakedQuad originalQuad) {
		return bakeQuad(quadDirection, from, to, modifierTier, originalQuad, 8.0F, 8.0F, 16.0F, 16.0F);
	}

	private BakedQuad bakeQuad(Direction quadDirection, Vector3f from, Vector3f to, ModifierTier modifierTier, BakedQuad originalQuad, float u0, float u1, float v0, float v1) {
		return ClientHandler.bakeQuad(quadDirection, modelDirection, "sculk_emitter", from, to, modifierTier, originalQuad, u0, u1, v0, v1);
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
	public boolean isCustomRenderer() {
		return originalModel.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return originalModel.getParticleIcon();
	}

	@Override
	public ItemOverrides getOverrides() {
		return originalModel.getOverrides();
	}

	record CacheKey(Direction side, SpeedTier speedTier, QuantityTier quantityTier) {}
}
