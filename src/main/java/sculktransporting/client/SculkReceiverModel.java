package sculktransporting.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import sculktransporting.items.SpeedModifierItem.SpeedTier;

public class SculkReceiverModel implements IDynamicBakedModel {
	private static final FaceBakery FACE_BAKERY = new FaceBakery();
	private BakedModel originalModel;
	private Map<SpeedTier, List<BakedQuad>> quadCache = new HashMap<>();

	public SculkReceiverModel(BakedModel originalModel) {
		this.originalModel = originalModel;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
		if (side == null) {
			SpeedTier speedTier = data.get(ClientHandler.SPEED_TIER);

			if (speedTier != null) {
				return quadCache.computeIfAbsent(speedTier, k -> {
					List<BakedQuad> originalQuads = originalModel.getQuads(state, side, rand, data, renderType);

					for (int i = 0; i < originalQuads.size(); i++) {
						BakedQuad quad = originalQuads.get(i);

						if (quad.getTintIndex() == 0) {
							Direction quadDirection = quad.getDirection();

							if (quadDirection == Direction.NORTH)
								originalQuads.set(i, bakeQuad(new Vector3f(0.0F, 3.0F, 0.0F), new Vector3f(16.0F, 8.0F, 0.0F), speedTier, quad));
							else if (quadDirection == Direction.EAST)
								originalQuads.set(i, bakeQuad(new Vector3f(16.0F, 3.0F, 0.0F), new Vector3f(16.0F, 8.0F, 16.0F), speedTier, quad));
							else if (quadDirection == Direction.SOUTH)
								originalQuads.set(i, bakeQuad(new Vector3f(0.0F, 3.0F, 16.0F), new Vector3f(16.0F, 8.0F, 16.0F), speedTier, quad));
							else if (quadDirection == Direction.WEST)
								originalQuads.set(i, bakeQuad(new Vector3f(0.0F, 3.0F, 0.0F), new Vector3f(0.0F, 8.0F, 16.0F), speedTier, quad));
						}
					}
					return new ArrayList<>(originalQuads);
				});
			}
		}

		return originalModel.getQuads(state, side, rand, data, renderType);
	}

	private BakedQuad bakeQuad(Vector3f from, Vector3f to, SpeedTier speedTier, BakedQuad originalQuad) {
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation(SculkTransporting.MODID, "block/sculk_receiver_side_" + speedTier.getValue()));

		return FACE_BAKERY.bakeQuad(from, to, new BlockElementFace(null, originalQuad.getTintIndex(), sprite.getName().toString(), new BlockFaceUV(new float[] {
				0.0F, 8.0F, 16.0F, 13.0F
		}, 0)), sprite, originalQuad.getDirection(), BlockModelRotation.X0_Y0, null, originalQuad.isShade(), new ResourceLocation(SculkTransporting.MODID, "sculk_receiver"));
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
