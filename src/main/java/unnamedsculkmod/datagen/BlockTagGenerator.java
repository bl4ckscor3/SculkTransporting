package unnamedsculkmod.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import unnamedsculkmod.UnnamedSculkMod;

public class BlockTagGenerator extends BlockTagsProvider {
	protected BlockTagGenerator(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, UnnamedSculkMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {}

	@Override
	public String getName() {
		return UnnamedSculkMod.MODID + " Block Tags";
	}
}
