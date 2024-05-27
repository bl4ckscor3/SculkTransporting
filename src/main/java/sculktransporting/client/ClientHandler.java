package sculktransporting.client;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

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
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem.SpeedTier;
import sculktransporting.registration.STBlockEntityTypes;
import sculktransporting.registration.STBlocks;
import sculktransporting.registration.STParticleTypes;

@EventBusSubscriber(modid = SculkTransporting.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientHandler {
	public static final ModelProperty<SpeedTier> SPEED_TIER = new ModelProperty<>();
	public static final ModelProperty<QuantityTier> QUANTITY_TIER = new ModelProperty<>();

	private ClientHandler() {}

	@SubscribeEvent
	public static void onModelBakingCompleted(ModelEvent.ModifyBakingResult event) {
		Map<ResourceLocation, BakedModel> models = event.getModels();
		Block sculkReceiver = STBlocks.SCULK_RECEIVER.get();
		ResourceLocation receiverName = BuiltInRegistries.BLOCK.getKey(sculkReceiver);
		Block sculkEmitter = STBlocks.SCULK_EMITTER.get();
		ResourceLocation emitterName = BuiltInRegistries.BLOCK.getKey(sculkEmitter);

		for (BlockState state : sculkReceiver.getStateDefinition().getPossibleStates()) {
			Pair<ModelResourceLocation, BakedModel> model = getModel(models, state, receiverName);

			event.getModels().put(model.getLeft(), new SculkReceiverModel(model.getRight(), state.getValue(BaseSculkItemTransporterBlock.FACING)));
		}

		for (BlockState state : sculkEmitter.getStateDefinition().getPossibleStates()) {
			Pair<ModelResourceLocation, BakedModel> model = getModel(models, state, emitterName);

			event.getModels().put(model.getLeft(), new SculkEmitterModel(model.getRight(), state.getValue(BaseSculkItemTransporterBlock.FACING)));
		}
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

	private static Pair<ModelResourceLocation, BakedModel> getModel(Map<ResourceLocation, BakedModel> models, BlockState state, ResourceLocation blockName) {
		String stateString = state.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(","));
		ModelResourceLocation mrl = new ModelResourceLocation(blockName, stateString);

		return Pair.of(mrl, models.get(mrl));
	}

	public static BlockModelRotation getModelRotation(Direction dir) {
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
