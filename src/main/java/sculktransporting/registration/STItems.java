package sculktransporting.registration;

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
import sculktransporting.SculkTransporting;
import sculktransporting.items.QuantityModifierItem;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem;
import sculktransporting.items.SpeedModifierItem.SpeedTier;

@EventBusSubscriber(modid = SculkTransporting.MODID, bus = Bus.MOD)
public class STItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SculkTransporting.MODID);
	public static final RegistryObject<QuantityModifierItem> QUANTITY_MODIFIER_TIER_1 = ITEMS.register("quantity_modifier_tier_1", () -> new QuantityModifierItem(QuantityTier.ONE, new Item.Properties().tab(SculkTransporting.TAB)));
	public static final RegistryObject<QuantityModifierItem> QUANTITY_MODIFIER_TIER_2 = ITEMS.register("quantity_modifier_tier_2", () -> new QuantityModifierItem(QuantityTier.TWO, new Item.Properties().tab(SculkTransporting.TAB)));
	public static final RegistryObject<QuantityModifierItem> QUANTITY_MODIFIER_TIER_3 = ITEMS.register("quantity_modifier_tier_3", () -> new QuantityModifierItem(QuantityTier.THREE, new Item.Properties().tab(SculkTransporting.TAB)));
	public static final RegistryObject<SpeedModifierItem> SPEED_MODIFIER_TIER_1 = ITEMS.register("speed_modifier_tier_1", () -> new SpeedModifierItem(SpeedTier.ONE, new Item.Properties().tab(SculkTransporting.TAB)));
	public static final RegistryObject<SpeedModifierItem> SPEED_MODIFIER_TIER_2 = ITEMS.register("speed_modifier_tier_2", () -> new SpeedModifierItem(SpeedTier.TWO, new Item.Properties().tab(SculkTransporting.TAB)));
	public static final RegistryObject<SpeedModifierItem> SPEED_MODIFIER_TIER_3 = ITEMS.register("speed_modifier_tier_3", () -> new SpeedModifierItem(SpeedTier.THREE, new Item.Properties().tab(SculkTransporting.TAB)));
	public static final RegistryObject<SpeedModifierItem> SPEED_MODIFIER_TIER_4 = ITEMS.register("speed_modifier_tier_4", () -> new SpeedModifierItem(SpeedTier.FOUR, new Item.Properties().tab(SculkTransporting.TAB)));

	@SubscribeEvent
	public static void automaticallyRegisterBlockItems(RegisterEvent event) {
		event.register(Keys.ITEMS, helper -> {
			//register block items from blocks
			for (RegistryObject<Block> ro : STBlocks.BLOCKS.getEntries()) {
				Block block = ro.get();

				helper.register(ForgeRegistries.BLOCKS.getKey(block), new BlockItem(block, new Item.Properties().tab(SculkTransporting.TAB)));
			}
		});
	}
}
