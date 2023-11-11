package sculktransporting.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import sculktransporting.STTags;
import sculktransporting.SculkTransporting;
import sculktransporting.registration.STBlocks;

import java.util.concurrent.CompletableFuture;

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
