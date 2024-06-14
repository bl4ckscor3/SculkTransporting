package sculktransporting.client;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import sculktransporting.SculkTransporting;
import sculktransporting.blocks.BaseSculkItemTransporterBlock;
import sculktransporting.items.ModifierTier;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem.SpeedTier;
import sculktransporting.registration.STBlockEntityTypes;
import sculktransporting.registration.STBlocks;
import sculktransporting.registration.STParticleTypes;

@EventBusSubscriber(modid = SculkTransporting.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientHandler {
	private static final FaceBakery FACE_BAKERY = new FaceBakery();
	public static final ModelProperty<SpeedTier> SPEED_TIER = new ModelProperty<>();
	public static final ModelProperty<QuantityTier> QUANTITY_TIER = new ModelProperty<>();

	private ClientHandler() {}

	@SubscribeEvent
	public static void onModelBakingCompleted(ModelEvent.ModifyBakingResult event) {
		Map<ModelResourceLocation, BakedModel> models = event.getModels();

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

	private static void replaceModels(Map<ModelResourceLocation, BakedModel> models, Block block, BiFunction<BakedModel, Direction, BakedModel> modelFactory) {
		ResourceLocation blockName = BuiltInRegistries.BLOCK.getKey(block);

		for (BlockState state : block.getStateDefinition().getPossibleStates()) {
			String stateString = state.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(","));
			ModelResourceLocation mrl = new ModelResourceLocation(blockName, stateString);

			models.put(mrl, modelFactory.apply(models.get(mrl), state.getValue(BaseSculkItemTransporterBlock.FACING)));
		}
	}

	public static BakedQuad bakeQuad(Direction quadDirection, Direction modelDirection, String blockName, Vector3f from, Vector3f to, ModifierTier modifierTier, BakedQuad originalQuad, float u0, float u1, float v0, float v1) {
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ResourceLocation.fromNamespaceAndPath(SculkTransporting.MODID, "block/" + blockName + "_side_" + modifierTier.getValue()));

		return FACE_BAKERY.bakeQuad(from, to, new BlockElementFace(null, originalQuad.getTintIndex(), sprite.contents().name().toString(), new BlockFaceUV(new float[] {
				u0, u1, v0, v1
		}, 0)), sprite, quadDirection, getModelRotation(modelDirection), null, originalQuad.isShade());
	}

	private static BlockModelRotation getModelRotation(Direction dir) {
		return switch (dir) {
			case DOWN -> BlockModelRotation.X180_Y0;
			case UP -> BlockModelRotation.X0_Y0;
			case NORTH -> BlockModelRotation.X90_Y0;
			case SOUTH -> BlockModelRotation.X90_Y180;
			case WEST -> BlockModelRotation.X90_Y270;
			case EAST -> BlockModelRotation.X90_Y90;
		};
	}
}
