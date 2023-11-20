package sculktransporting.registration;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import sculktransporting.SculkTransporting;
import sculktransporting.items.QuantityModifierItem;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem;
import sculktransporting.items.SpeedModifierItem.SpeedTier;

@EventBusSubscriber(modid = SculkTransporting.MODID, bus = Bus.MOD)
public class STItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SculkTransporting.MODID);
	public static final DeferredItem<QuantityModifierItem> QUANTITY_MODIFIER_TIER_1 = ITEMS.register("quantity_modifier_tier_1", () -> new QuantityModifierItem(QuantityTier.ONE, new Item.Properties()));
	public static final DeferredItem<QuantityModifierItem> QUANTITY_MODIFIER_TIER_2 = ITEMS.register("quantity_modifier_tier_2", () -> new QuantityModifierItem(QuantityTier.TWO, new Item.Properties()));
	public static final DeferredItem<QuantityModifierItem> QUANTITY_MODIFIER_TIER_3 = ITEMS.register("quantity_modifier_tier_3", () -> new QuantityModifierItem(QuantityTier.THREE, new Item.Properties()));
	public static final DeferredItem<SpeedModifierItem> SPEED_MODIFIER_TIER_1 = ITEMS.register("speed_modifier_tier_1", () -> new SpeedModifierItem(SpeedTier.ONE, new Item.Properties()));
	public static final DeferredItem<SpeedModifierItem> SPEED_MODIFIER_TIER_2 = ITEMS.register("speed_modifier_tier_2", () -> new SpeedModifierItem(SpeedTier.TWO, new Item.Properties()));
	public static final DeferredItem<SpeedModifierItem> SPEED_MODIFIER_TIER_3 = ITEMS.register("speed_modifier_tier_3", () -> new SpeedModifierItem(SpeedTier.THREE, new Item.Properties()));
	public static final DeferredItem<SpeedModifierItem> SPEED_MODIFIER_TIER_4 = ITEMS.register("speed_modifier_tier_4", () -> new SpeedModifierItem(SpeedTier.FOUR, new Item.Properties()));

	private STItems() {}

	@SubscribeEvent
	public static void automaticallyRegisterBlockItems(RegisterEvent event) {
		event.register(Registries.ITEM, helper -> {
			//register block items from blocks
			for (DeferredHolder<Block, ? extends Block> ro : STBlocks.BLOCKS.getEntries()) {
				Block block = ro.get();

				helper.register(BuiltInRegistries.BLOCK.getKey(block), new BlockItem(block, new Item.Properties()));
			}
		});
	}
}
