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
		tag(USTags.Items.QUANTITY_MODIFIERS).add(USItems.QUANTITY_MODIFIER_TIER_1.get(), USItems.QUANTITY_MODIFIER_TIER_2.get(), USItems.QUANTITY_MODIFIER_TIER_3.get());
		tag(USTags.Items.SPEED_MODIFIERS).add(USItems.SPEED_MODIFIER_TIER_1.get(), USItems.SPEED_MODIFIER_TIER_2.get(), USItems.SPEED_MODIFIER_TIER_3.get(), USItems.SPEED_MODIFIER_TIER_4.get());
	}

	@Override
	public String getName() {
		return UnnamedSculkMod.MODID + " Item Tags";
	}
}
