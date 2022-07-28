package sculktransporting.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import sculktransporting.SculkTransporting;
import sculktransporting.items.ModifierTier;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem.SpeedTier;

public class SculkEmitterModel implements IDynamicBakedModel {
	private static final FaceBakery FACE_BAKERY = new FaceBakery();
	private BakedModel originalModel;
	private Map<Pair<SpeedTier, QuantityTier>, List<BakedQuad>> quadCache = new HashMap<>();

	public SculkEmitterModel(BakedModel originalModel) {
		this.originalModel = originalModel;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
		if (side == null) {
			SpeedTier speedTier = data.get(ClientHandler.SPEED_TIER);
			QuantityTier quantityTier = data.get(ClientHandler.QUANTITY_TIER);

			if (speedTier != null && quantityTier != null) {
				return quadCache.computeIfAbsent(Pair.of(speedTier, quantityTier), k -> {
					List<BakedQuad> originalQuads = originalModel.getQuads(state, side, rand, data, renderType);

					for (int i = 0; i < originalQuads.size(); i++) {
						BakedQuad quad = originalQuads.get(i);

						if (quad.isTinted()) {
							int tintIndex = quad.getTintIndex();

							if (quad.getDirection() == Direction.NORTH) {
								if (tintIndex == 0)
									originalQuads.set(i, bakeLeftQuad(new Vector3f(8.0F, 0.0F, 0.0F), new Vector3f(16.0F, 8.0F, 0.0F), speedTier, quad));
								else if (tintIndex == 1)
									originalQuads.set(i, bakeRightQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(8.0F, 8.0F, 0.0F), quantityTier, quad));
							}
							else if (quad.getDirection() == Direction.EAST) {
								if (tintIndex == 0)
									originalQuads.set(i, bakeLeftQuad(new Vector3f(16.0F, 0.0F, 8.0F), new Vector3f(16.0F, 8.0F, 16.0F), speedTier, quad));
								else if (tintIndex == 1)
									originalQuads.set(i, bakeRightQuad(new Vector3f(16.0F, 0.0F, 0.0F), new Vector3f(16.0F, 8.0F, 8.0F), quantityTier, quad));
							}
							else if (quad.getDirection() == Direction.SOUTH) {
								if (tintIndex == 0)
									originalQuads.set(i, bakeLeftQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(8.0F, 8.0F, 16.0F), speedTier, quad));
								else if (tintIndex == 1)
									originalQuads.set(i, bakeRightQuad(new Vector3f(8.0F, 0.0F, 16.0F), new Vector3f(16.0F, 8.0F, 16.0F), quantityTier, quad));
							}
							else if (quad.getDirection() == Direction.WEST) {
								if (tintIndex == 0)
									originalQuads.set(i, bakeLeftQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 8.0F, 8.0F), speedTier, quad));
								else if (tintIndex == 1)
									originalQuads.set(i, bakeRightQuad(new Vector3f(0.0F, 0.0F, 8.0F), new Vector3f(0.0F, 8.0F, 16.0F), quantityTier, quad));
							}
						}
					}

					return new ArrayList<>(originalQuads);
				});
			}
		}

		return originalModel.getQuads(state, side, rand, data, renderType);
	}

	private BakedQuad bakeLeftQuad(Vector3f from, Vector3f to, ModifierTier modifierTier, BakedQuad originalQuad) {
		return bakeQuad(from, to, modifierTier, originalQuad, 0.0F, 8.0F, 8.0F, 16.0F);
	}

	private BakedQuad bakeRightQuad(Vector3f from, Vector3f to, ModifierTier modifierTier, BakedQuad originalQuad) {
		return bakeQuad(from, to, modifierTier, originalQuad, 8.0F, 8.0F, 16.0F, 16.0F);
	}

	private BakedQuad bakeQuad(Vector3f from, Vector3f to, ModifierTier modifierTier, BakedQuad originalQuad, float u0, float u1, float v0, float v1) {
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation(SculkTransporting.MODID, "block/sculk_emitter_side_" + modifierTier.getValue()));

		return FACE_BAKERY.bakeQuad(from, to, new BlockElementFace(null, originalQuad.getTintIndex(), sprite.getName().toString(), new BlockFaceUV(new float[] {
				u0, u1, v0, v1
		}, 0)), sprite, originalQuad.getDirection(), BlockModelRotation.X0_Y0, null, originalQuad.isShade(), new ResourceLocation(SculkTransporting.MODID, "sculk_emitter"));
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
}
