package sculktransporting;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sculktransporting.registration.USBlockEntityTypes;
import sculktransporting.registration.USBlocks;
import sculktransporting.registration.USGameEvents;
import sculktransporting.registration.USItems;

@Mod(SculkTransporting.MODID)
public class SculkTransporting {
	public static final String MODID = "sculktransporting";
	public static final CreativeModeTab TAB = new CreativeModeTab(SculkTransporting.MODID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(USBlocks.SCULK_TRANSMITTER.get().asItem()); //TODO: Placeholder -R
		}
	};

	public SculkTransporting() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		USBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
		USBlocks.BLOCKS.register(modEventBus);
		USGameEvents.GAME_EVENTS.register(modEventBus);
		USItems.ITEMS.register(modEventBus);
	}
}
