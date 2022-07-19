package sculktransporting.client;

import java.util.stream.Collectors;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import sculktransporting.SculkTransporting;
import sculktransporting.items.SpeedModifierItem.SpeedTier;
import sculktransporting.registration.STBlocks;

@EventBusSubscriber(modid = SculkTransporting.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientHandler {
	public static final ModelProperty<SpeedTier> SPEED_TIER = new ModelProperty<>();

	@SubscribeEvent
	public static void onModelBakingCompleted(ModelEvent.BakingCompleted event) {
		Block sculkReceiver = STBlocks.SCULK_RECEIVER.get();

		for (BlockState state : sculkReceiver.getStateDefinition().getPossibleStates()) {
			String stateString = state.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(","));
			ModelResourceLocation mrl = new ModelResourceLocation(ForgeRegistries.BLOCKS.getKey(sculkReceiver), stateString);

			event.getModels().put(mrl, new SculkReceiverModel(event.getModels().get(mrl)));
		}
	}

	@SubscribeEvent
	public static void onTextureStitchPre(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
			event.addSprite(new ResourceLocation(SculkTransporting.MODID, "block/sculk_receiver_side_1"));
			event.addSprite(new ResourceLocation(SculkTransporting.MODID, "block/sculk_receiver_side_2"));
			event.addSprite(new ResourceLocation(SculkTransporting.MODID, "block/sculk_receiver_side_3"));
			event.addSprite(new ResourceLocation(SculkTransporting.MODID, "block/sculk_receiver_side_4"));
		}
	}
}
