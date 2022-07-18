package sculktransporting.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import sculktransporting.STTags;
import sculktransporting.SculkTransporting;
import sculktransporting.registration.STItems;

public class ItemTagGenerator extends ItemTagsProvider {
	protected ItemTagGenerator(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagsProvider, SculkTransporting.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(STTags.Items.QUANTITY_MODIFIERS).add(STItems.QUANTITY_MODIFIER_TIER_1.get(), STItems.QUANTITY_MODIFIER_TIER_2.get(), STItems.QUANTITY_MODIFIER_TIER_3.get());
		tag(STTags.Items.SPEED_MODIFIERS).add(STItems.SPEED_MODIFIER_TIER_1.get(), STItems.SPEED_MODIFIER_TIER_2.get(), STItems.SPEED_MODIFIER_TIER_3.get(), STItems.SPEED_MODIFIER_TIER_4.get());
	}

	@Override
	public String getName() {
		return SculkTransporting.MODID + " Item Tags";
	}
}
