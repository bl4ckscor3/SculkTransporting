package sculktransporting.registration;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import sculktransporting.SculkTransporting;
import sculktransporting.items.QuantityModifierItem;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem;
import sculktransporting.items.SpeedModifierItem.SpeedTier;

@EventBusSubscriber(modid = SculkTransporting.MODID)
public class STItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SculkTransporting.MODID);
	public static final DeferredItem<QuantityModifierItem> QUANTITY_MODIFIER_TIER_1 = ITEMS.registerItem("quantity_modifier_tier_1", p -> new QuantityModifierItem(QuantityTier.ONE, p));
	public static final DeferredItem<QuantityModifierItem> QUANTITY_MODIFIER_TIER_2 = ITEMS.registerItem("quantity_modifier_tier_2", p -> new QuantityModifierItem(QuantityTier.TWO, p));
	public static final DeferredItem<QuantityModifierItem> QUANTITY_MODIFIER_TIER_3 = ITEMS.registerItem("quantity_modifier_tier_3", p -> new QuantityModifierItem(QuantityTier.THREE, p));
	public static final DeferredItem<SpeedModifierItem> SPEED_MODIFIER_TIER_1 = ITEMS.registerItem("speed_modifier_tier_1", p -> new SpeedModifierItem(SpeedTier.ONE, p));
	public static final DeferredItem<SpeedModifierItem> SPEED_MODIFIER_TIER_2 = ITEMS.registerItem("speed_modifier_tier_2", p -> new SpeedModifierItem(SpeedTier.TWO, p));
	public static final DeferredItem<SpeedModifierItem> SPEED_MODIFIER_TIER_3 = ITEMS.registerItem("speed_modifier_tier_3", p -> new SpeedModifierItem(SpeedTier.THREE, p));
	public static final DeferredItem<SpeedModifierItem> SPEED_MODIFIER_TIER_4 = ITEMS.registerItem("speed_modifier_tier_4", p -> new SpeedModifierItem(SpeedTier.FOUR, p));

	private STItems() {}

	@SubscribeEvent
	public static void automaticallyRegisterBlockItems(RegisterEvent event) {
		event.register(Registries.ITEM, helper -> {
			//register block items from blocks
			for (DeferredHolder<Block, ? extends Block> ro : STBlocks.BLOCKS.getEntries()) {
				Block block = ro.get();
				ResourceKey<Item> resourceKey = ResourceKey.create(Registries.ITEM, BuiltInRegistries.BLOCK.getKey(block));

				helper.register(resourceKey, new BlockItem(block, new Item.Properties().setId(resourceKey).useBlockDescriptionPrefix()));
			}
		});
	}
}
