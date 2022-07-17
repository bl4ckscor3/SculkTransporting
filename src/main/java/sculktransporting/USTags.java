package sculktransporting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class USTags {
	public static class Items {
		public static final TagKey<Item> QUANTITY_MODIFIERS = tag("quantity_modifiers");
		public static final TagKey<Item> SPEED_MODIFIERS = tag("speed_modifiers");

		private static TagKey<Item> tag(String name) {
			return ItemTags.create(new ResourceLocation(SculkTransporting.MODID, name));
		}
	}
}
