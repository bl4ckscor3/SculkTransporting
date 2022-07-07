package unnamedsculkmod.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import unnamedsculkmod.UnnamedSculkMod;

public class ItemTagGenerator extends ItemTagsProvider {
	protected ItemTagGenerator(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagsProvider, UnnamedSculkMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {}

	@Override
	public String getName() {
		return UnnamedSculkMod.MODID + " Item Tags";
	}
}
