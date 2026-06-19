package sculktransporting.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import sculktransporting.STTags;
import sculktransporting.SculkTransporting;
import sculktransporting.registration.STBlocks;

public class BlockTagGenerator extends BlockTagsProvider {
	public BlockTagGenerator(PackOutput output, CompletableFuture<Provider> lookupProvider) {
		super(output, lookupProvider, SculkTransporting.MODID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(STTags.Blocks.SCULK_EMITTER_CAN_EXTRACT_FROM).add(STBlocks.SCULK_BARREL.getKey());
		tag(BlockTags.MINEABLE_WITH_AXE).add(STBlocks.SCULK_BARREL.getKey());
		tag(BlockTags.MINEABLE_WITH_HOE).add(STBlocks.SCULK_EMITTER.getKey(), STBlocks.SCULK_TRANSMITTER.getKey(), STBlocks.SCULK_RECEIVER.getKey());
	}

	@Override
	public String getName() {
		return SculkTransporting.MODID + " Block Tags";
	}
}
