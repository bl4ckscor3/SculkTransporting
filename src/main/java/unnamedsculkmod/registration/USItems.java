package unnamedsculkmod.registration;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import unnamedsculkmod.UnnamedSculkMod;
import unnamedsculkmod.items.QuantityModifierItem;
import unnamedsculkmod.items.QuantityModifierItem.QuantityTier;
import unnamedsculkmod.items.SpeedModifierItem;
import unnamedsculkmod.items.SpeedModifierItem.SpeedTier;

@EventBusSubscriber(modid = UnnamedSculkMod.MODID, bus = Bus.MOD)
public class USItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, UnnamedSculkMod.MODID);
	public static final RegistryObject<QuantityModifierItem> TIER_1_QUANTITY_MODIFIER = ITEMS.register("tier_1_quantity_modifier", () -> new QuantityModifierItem(QuantityTier.ONE, new Item.Properties().tab(UnnamedSculkMod.TAB)));
	public static final RegistryObject<QuantityModifierItem> TIER_2_QUANTITY_MODIFIER = ITEMS.register("tier_2_quantity_modifier", () -> new QuantityModifierItem(QuantityTier.TWO, new Item.Properties().tab(UnnamedSculkMod.TAB)));
	public static final RegistryObject<QuantityModifierItem> TIER_3_QUANTITY_MODIFIER = ITEMS.register("tier_3_quantity_modifier", () -> new QuantityModifierItem(QuantityTier.THREE, new Item.Properties().tab(UnnamedSculkMod.TAB)));
	public static final RegistryObject<SpeedModifierItem> TIER_1_SPEED_MODIFIER = ITEMS.register("tier_1_speed_modifier", () -> new SpeedModifierItem(SpeedTier.ONE, new Item.Properties().tab(UnnamedSculkMod.TAB)));
	public static final RegistryObject<SpeedModifierItem> TIER_2_SPEED_MODIFIER = ITEMS.register("tier_2_speed_modifier", () -> new SpeedModifierItem(SpeedTier.TWO, new Item.Properties().tab(UnnamedSculkMod.TAB)));
	public static final RegistryObject<SpeedModifierItem> TIER_3_SPEED_MODIFIER = ITEMS.register("tier_3_speed_modifier", () -> new SpeedModifierItem(SpeedTier.THREE, new Item.Properties().tab(UnnamedSculkMod.TAB)));
	public static final RegistryObject<SpeedModifierItem> TIER_4_SPEED_MODIFIER = ITEMS.register("tier_4_speed_modifier", () -> new SpeedModifierItem(SpeedTier.FOUR, new Item.Properties().tab(UnnamedSculkMod.TAB)));

	@SubscribeEvent
	public static void automaticallyRegisterBlockItems(RegisterEvent event) {
		event.register(Keys.ITEMS, helper -> {
			//register block items from blocks
			for (RegistryObject<Block> ro : USBlocks.BLOCKS.getEntries()) {
				Block block = ro.get();

				helper.register(ForgeRegistries.BLOCKS.getKey(block), new BlockItem(block, new Item.Properties().tab(UnnamedSculkMod.TAB)));
			}
		});
	}
}
