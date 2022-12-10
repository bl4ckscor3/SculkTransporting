package sculktransporting.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import sculktransporting.STTags;
import sculktransporting.SculkTransporting;
import sculktransporting.registration.STBlocks;

public class BlockTagGenerator extends BlockTagsProvider {
	public BlockTagGenerator(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, SculkTransporting.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(STTags.Blocks.SCULK_EMITTER_CAN_EXTRACT_FROM).add(STBlocks.SCULK_BARREL.get());
		tag(BlockTags.MINEABLE_WITH_AXE).add(STBlocks.SCULK_BARREL.get());
		tag(BlockTags.MINEABLE_WITH_HOE).add(STBlocks.SCULK_EMITTER.get(), STBlocks.SCULK_TRANSMITTER.get(), STBlocks.SCULK_RECEIVER.get());
	}

	@Override
	public String getName() {
		return SculkTransporting.MODID + " Block Tags";
	}
}
