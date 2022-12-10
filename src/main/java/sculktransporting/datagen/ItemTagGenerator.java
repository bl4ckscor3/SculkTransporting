package sculktransporting.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import sculktransporting.STTags;
import sculktransporting.SculkTransporting;
import sculktransporting.registration.STItems;

public class ItemTagGenerator extends ItemTagsProvider {
	public ItemTagGenerator(PackOutput output, CompletableFuture<Provider> lookupProvider, TagsProvider<Block> blockTagsProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTagsProvider, SculkTransporting.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(STTags.Items.QUANTITY_MODIFIERS).add(STItems.QUANTITY_MODIFIER_TIER_1.get(), STItems.QUANTITY_MODIFIER_TIER_2.get(), STItems.QUANTITY_MODIFIER_TIER_3.get());
		tag(STTags.Items.SPEED_MODIFIERS).add(STItems.SPEED_MODIFIER_TIER_1.get(), STItems.SPEED_MODIFIER_TIER_2.get(), STItems.SPEED_MODIFIER_TIER_3.get(), STItems.SPEED_MODIFIER_TIER_4.get());
	}

	@Override
	public String getName() {
		return SculkTransporting.MODID + " Item Tags";
	}
}
