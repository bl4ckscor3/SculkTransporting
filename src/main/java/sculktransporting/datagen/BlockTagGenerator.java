package sculktransporting.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import sculktransporting.SculkTransporting;
import sculktransporting.registration.USBlocks;

public class BlockTagGenerator extends BlockTagsProvider {
	protected BlockTagGenerator(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, SculkTransporting.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(BlockTags.MINEABLE_WITH_HOE).add(USBlocks.SCULK_EMITTER.get(), USBlocks.SCULK_TRANSMITTER.get(), USBlocks.SCULK_RECEIVER.get());
	}

	@Override
	public String getName() {
		return SculkTransporting.MODID + " Block Tags";
	}
}
