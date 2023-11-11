package sculktransporting.client;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.registries.ForgeRegistries;
import sculktransporting.SculkTransporting;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem.SpeedTier;
import sculktransporting.registration.STBlockEntityTypes;
import sculktransporting.registration.STBlocks;
import sculktransporting.registration.STParticleTypes;

import java.util.stream.Collectors;

@EventBusSubscriber(modid = SculkTransporting.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientHandler {
	public static final ModelProperty<SpeedTier> SPEED_TIER = new ModelProperty<>();
	public static final ModelProperty<QuantityTier> QUANTITY_TIER = new ModelProperty<>();

	private ClientHandler() {}

	@SubscribeEvent
	public static void onModelBakingCompleted(ModelEvent.ModifyBakingResult event) {
		Block sculkReceiver = STBlocks.SCULK_RECEIVER.get();
		Block sculkEmitter = STBlocks.SCULK_EMITTER.get();

		for (BlockState state : sculkReceiver.getStateDefinition().getPossibleStates()) {
			String stateString = state.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(","));
			ModelResourceLocation mrl = new ModelResourceLocation(ForgeRegistries.BLOCKS.getKey(sculkReceiver), stateString);

			event.getModels().put(mrl, new SculkReceiverModel(event.getModels().get(mrl)));
		}

		for (BlockState state : sculkEmitter.getStateDefinition().getPossibleStates()) {
			String stateString = state.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(","));
			ModelResourceLocation mrl = new ModelResourceLocation(ForgeRegistries.BLOCKS.getKey(sculkEmitter), stateString);

			event.getModels().put(mrl, new SculkEmitterModel(event.getModels().get(mrl)));
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
}
