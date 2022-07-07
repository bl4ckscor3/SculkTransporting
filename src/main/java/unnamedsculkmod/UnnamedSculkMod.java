package unnamedsculkmod;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(UnnamedSculkMod.MODID)
public class UnnamedSculkMod {
	public static final String MODID = "unnamedsculkmod";

	public UnnamedSculkMod() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		USBlocks.BLOCKS.register(modEventBus);
		USItems.ITEMS.register(modEventBus);
	}
}
