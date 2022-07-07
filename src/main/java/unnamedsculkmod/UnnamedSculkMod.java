package unnamedsculkmod;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import unnamedsculkmod.misc.USGameEvents;

@Mod(UnnamedSculkMod.MODID)
public class UnnamedSculkMod {
	public static final String MODID = "unnamedsculkmod";
	public static final CreativeModeTab TAB = new CreativeModeTab(UnnamedSculkMod.MODID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(USBlocks.SCULK_TRANSMITTER.get().asItem()); //TODO: Placeholder -R
		}
	};

	public UnnamedSculkMod() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		USBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
		USBlocks.BLOCKS.register(modEventBus);
		USGameEvents.GAME_EVENTS.register(modEventBus);
		USItems.ITEMS.register(modEventBus);
	}
}
