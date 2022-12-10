package sculktransporting;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sculktransporting.registration.STBlockEntityTypes;
import sculktransporting.registration.STBlocks;
import sculktransporting.registration.STGameEvents;
import sculktransporting.registration.STItems;
import sculktransporting.registration.STParticleTypes;

@Mod(SculkTransporting.MODID)
@EventBusSubscriber(modid = SculkTransporting.MODID, bus = Bus.MOD)
public class SculkTransporting {
	public static final String MODID = "sculktransporting";
	public static CreativeModeTab tab;

	public SculkTransporting() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		STBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
		STBlocks.BLOCKS.register(modEventBus);
		STGameEvents.GAME_EVENTS.register(modEventBus);
		STItems.ITEMS.register(modEventBus);
		STParticleTypes.PARTICLE_TYPES.register(modEventBus);
	}

	@SubscribeEvent
	public static void onCreativeModeTabRegister(CreativeModeTabEvent.Register event) {
		//@formatter:off
		tab = event.registerCreativeModeTab(new ResourceLocation(MODID, "tab"), builder -> builder
				.icon(() -> new ItemStack(STBlocks.SCULK_EMITTER.get()))
				.title(Component.translatable("itemGroup.sculktransporting"))
				.displayItems((features, output, hasPermissions) -> {
					output.acceptAll(List.of(
							new ItemStack(STBlocks.SCULK_BARREL.get()),
							new ItemStack(STBlocks.SCULK_EMITTER.get()),
							new ItemStack(STBlocks.SCULK_TRANSMITTER.get()),
							new ItemStack(STBlocks.SCULK_RECEIVER.get()),
							new ItemStack(STItems.QUANTITY_MODIFIER_TIER_1.get()),
							new ItemStack(STItems.QUANTITY_MODIFIER_TIER_2.get()),
							new ItemStack(STItems.QUANTITY_MODIFIER_TIER_3.get()),
							new ItemStack(STItems.SPEED_MODIFIER_TIER_1.get()),
							new ItemStack(STItems.SPEED_MODIFIER_TIER_2.get()),
							new ItemStack(STItems.SPEED_MODIFIER_TIER_3.get()),
							new ItemStack(STItems.SPEED_MODIFIER_TIER_4.get())));
				}));
		//@formatter:on
	}
}
