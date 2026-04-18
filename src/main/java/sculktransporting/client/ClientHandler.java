package sculktransporting.client;

import java.util.Map;
import java.util.function.BiFunction;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.blaze3d.platform.Transparency;
import com.mojang.math.OctahedralGroup;
import com.mojang.math.Quadrant;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.FaceBakery;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.BakedQuad.MaterialInfo;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.model.ExtraFaceData;
import net.neoforged.neoforge.client.model.quad.BakedColors;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import net.neoforged.neoforge.model.data.ModelProperty;
import sculktransporting.SculkTransporting;
import sculktransporting.blocks.BaseSculkItemTransporterBlock;
import sculktransporting.items.ModifierTier;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem.SpeedTier;
import sculktransporting.registration.STBlockEntityTypes;
import sculktransporting.registration.STBlocks;
import sculktransporting.registration.STParticleTypes;

@EventBusSubscriber(modid = SculkTransporting.MODID, value = Dist.CLIENT)
public class ClientHandler {
	private static final ModelBaker.Interner INTERNER = new ModelBaker.Interner() {
		private final Interner<Vector3fc> vectors = Interners.newStrongInterner();
		private final Interner<BakedQuad.MaterialInfo> materialInfos = Interners.newStrongInterner();
		private final Interner<BakedNormals> normals = Interners.newStrongInterner();
		private final Interner<BakedColors> colors = Interners.newStrongInterner();

		@Override
		public Vector3fc vector(Vector3fc v) {
			return vectors.intern(v);
		}

		@Override
		public BakedQuad.MaterialInfo materialInfo(BakedQuad.MaterialInfo material) {
			return materialInfos.intern(material);
		}

		@Override
		public BakedNormals normals(BakedNormals normals) {
			return this.normals.intern(normals);
		}

		@Override
		public BakedColors colors(BakedColors colors) {
			return this.colors.intern(colors);
		}
	};

	public static final ModelProperty<SpeedTier> SPEED_TIER = new ModelProperty<>();
	public static final ModelProperty<QuantityTier> QUANTITY_TIER = new ModelProperty<>();

	private ClientHandler() {}

	@SubscribeEvent
	public static void onModelBakingCompleted(ModelEvent.ModifyBakingResult event) {
		Map<BlockState, BlockStateModel> models = event.getBakingResult().blockStateModels();

		replaceModels(models, STBlocks.SCULK_RECEIVER.get(), SculkReceiverModel::new);
		replaceModels(models, STBlocks.SCULK_EMITTER.get(), SculkEmitterModel::new);
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(STBlockEntityTypes.SCULK_EMITTER_BLOCK_ENTITY.get(), SculkItemTransporterBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(STBlockEntityTypes.SCULK_TRANSMITTER_BLOCK_ENTITY.get(), SculkTransmitterBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(STBlockEntityTypes.SCULK_RECEIVER_BLOCK_ENTITY.get(), SculkItemTransporterBlockEntityRenderer::new);
	}

	@SubscribeEvent
	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpecial(STParticleTypes.ITEM_SIGNAL.get(), new ItemSignalParticle.Provider());
	}

	private static void replaceModels(Map<BlockState, BlockStateModel> models, Block block, BiFunction<BlockStateModel, Direction, BlockStateModel> modelFactory) {
		for (BlockState state : block.getStateDefinition().getPossibleStates()) {
			models.put(state, modelFactory.apply(models.get(state), state.getValue(BaseSculkItemTransporterBlock.FACING)));
		}
	}

	public static BakedQuad bakeQuad(Direction quadDirection, Direction modelDirection, String blockName, Vector3f from, Vector3f to, ModifierTier modifierTier, BakedQuad originalQuad, float u0, float v0, float u1, float v1) {
		TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(Identifier.fromNamespaceAndPath(SculkTransporting.MODID, "block/" + blockName + "_side_" + modifierTier.getValue()));
		MaterialInfo info = originalQuad.materialInfo();

		return bakeQuad(from, to, new CuboidFace(null, info.tintIndex(), sprite.contents().name().toString(), new CuboidFace.UVs(u0, v0, u1, v1), Quadrant.R0), new Material.Baked(sprite, false), quadDirection, getModelRotation(modelDirection), info.shade());
	}

	private static BakedQuad bakeQuad(Vector3fc from, Vector3fc to, CuboidFace face, Material.Baked material, Direction facing, ModelState modelState, boolean shade) {
		CuboidFace.UVs uvs = face.uvs();

		if (uvs == null)
			uvs = FaceBakery.defaultFaceUV(from, to, facing);

		Transparency transparency = FaceBakery.computeMaterialTransparency(material, uvs);
		ExtraFaceData faceData = face.faceData();
		BakedQuad.MaterialInfo materialInfo = INTERNER.materialInfo(BakedQuad.MaterialInfo.of(material, transparency, face.tintIndex(), shade, faceData.lightEmission(), faceData.ambientOcclusion()));

		return FaceBakery.bakeQuad(INTERNER, from, to, uvs, face.rotation(), materialInfo, facing, modelState, null, faceData);
	}

	private static BlockModelRotation getModelRotation(Direction dir) {
		return BlockModelRotation.get(switch (dir) {
			case DOWN -> OctahedralGroup.BLOCK_ROT_X_180;
			case UP -> OctahedralGroup.IDENTITY;
			case NORTH -> OctahedralGroup.BLOCK_ROT_X_90;
			case SOUTH -> OctahedralGroup.BLOCK_ROT_X_90.compose(OctahedralGroup.BLOCK_ROT_Y_180);
			case WEST -> OctahedralGroup.BLOCK_ROT_X_90.compose(OctahedralGroup.BLOCK_ROT_Y_270);
			case EAST -> OctahedralGroup.BLOCK_ROT_X_90.compose(OctahedralGroup.BLOCK_ROT_Y_90);
		});
	}
}
