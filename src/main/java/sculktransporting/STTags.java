package sculktransporting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class STTags {
	public static class Blocks {
		public static final TagKey<Block> SCULK_EMITTER_CAN_EXTRACT_FROM = tag("sculk_emitter_can_extract_from");

		private static TagKey<Block> tag(String name) {
			return BlockTags.create(new ResourceLocation(SculkTransporting.MODID, name));
		}
	}

	public static class Items {
		public static final TagKey<Item> QUANTITY_MODIFIERS = tag("quantity_modifiers");
		public static final TagKey<Item> SPEED_MODIFIERS = tag("speed_modifiers");

		private static TagKey<Item> tag(String name) {
			return ItemTags.create(new ResourceLocation(SculkTransporting.MODID, name));
		}
	}
}
