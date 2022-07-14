package unnamedsculkmod.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import unnamedsculkmod.USTags;
import unnamedsculkmod.UnnamedSculkMod;
import unnamedsculkmod.registration.USItems;

public class ItemTagGenerator extends ItemTagsProvider {
	protected ItemTagGenerator(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagsProvider, UnnamedSculkMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(USTags.Items.QUANTITY_MODIFIERS).add(USItems.TIER_1_QUANTITY_MODIFIER.get(), USItems.TIER_2_QUANTITY_MODIFIER.get(), USItems.TIER_3_QUANTITY_MODIFIER.get());
		tag(USTags.Items.SPEED_MODIFIERS).add(USItems.TIER_1_SPEED_MODIFIER.get(), USItems.TIER_2_SPEED_MODIFIER.get(), USItems.TIER_3_SPEED_MODIFIER.get(), USItems.TIER_4_SPEED_MODIFIER.get());
	}

	@Override
	public String getName() {
		return UnnamedSculkMod.MODID + " Item Tags";
	}
}
