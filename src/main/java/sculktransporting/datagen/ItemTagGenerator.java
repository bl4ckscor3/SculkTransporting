package sculktransporting.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;
import sculktransporting.STTags;
import sculktransporting.SculkTransporting;
import sculktransporting.registration.STItems;

public class ItemTagGenerator extends BlockTagCopyingItemTagProvider {
	public ItemTagGenerator(PackOutput output, CompletableFuture<Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsProvider) {
		super(output, lookupProvider, blockTagsProvider, SculkTransporting.MODID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(STTags.Items.QUANTITY_MODIFIERS).add(STItems.QUANTITY_MODIFIER_TIER_1.getKey(), STItems.QUANTITY_MODIFIER_TIER_2.getKey(), STItems.QUANTITY_MODIFIER_TIER_3.getKey());
		tag(STTags.Items.SPEED_MODIFIERS).add(STItems.SPEED_MODIFIER_TIER_1.getKey(), STItems.SPEED_MODIFIER_TIER_2.getKey(), STItems.SPEED_MODIFIER_TIER_3.getKey(), STItems.SPEED_MODIFIER_TIER_4.getKey());
	}

	@Override
	public String getName() {
		return SculkTransporting.MODID + " Item Tags";
	}
}
