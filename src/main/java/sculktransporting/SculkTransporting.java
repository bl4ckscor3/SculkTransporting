package sculktransporting;

import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import sculktransporting.registration.STBlockEntityTypes;
import sculktransporting.registration.STBlocks;
import sculktransporting.registration.STGameEvents;
import sculktransporting.registration.STItems;
import sculktransporting.registration.STParticleTypes;

@Mod(SculkTransporting.MODID)
public class SculkTransporting {
	public static final String MODID = "sculktransporting";
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
	public static final RegistryObject<CreativeModeTab> TECHNICAL_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
	//@formatter:off
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.icon(() -> new ItemStack(STBlocks.SCULK_EMITTER.get()))
			.title(Component.translatable("itemGroup.sculktransporting"))
			.displayItems((displayParameters, output) -> {
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
			}).build());
	//@formatter:on

	public SculkTransporting() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		STBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
		STBlocks.BLOCKS.register(modEventBus);
		STGameEvents.GAME_EVENTS.register(modEventBus);
		STItems.ITEMS.register(modEventBus);
		STParticleTypes.PARTICLE_TYPES.register(modEventBus);
		CREATIVE_MODE_TABS.register(modEventBus);
	}
}
