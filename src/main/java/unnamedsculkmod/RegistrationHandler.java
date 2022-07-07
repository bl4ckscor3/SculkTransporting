package unnamedsculkmod;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = UnnamedSculkMod.MODID, bus = Bus.MOD)
public class RegistrationHandler {
	@SubscribeEvent
	public static void registerItems(RegisterEvent event) {
		event.register(Keys.ITEMS, helper-> {
			//register block items from blocks
			for(RegistryObject<Block> ro : USBlocks.BLOCKS.getEntries()) {
				Block block = ro.get();

				helper.register(ForgeRegistries.BLOCKS.getKey(block), new BlockItem(block, new Item.Properties().tab(UnnamedSculkMod.TAB)));
			}
		});
	}
}
