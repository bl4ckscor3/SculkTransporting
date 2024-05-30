package sculktransporting.datagen;

import java.util.Set;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import sculktransporting.registration.STBlocks;

public class BlockLootTableGenerator extends BlockLootSubProvider {
	protected BlockLootTableGenerator() {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags());
	}

	@Override
	public void generate() {
		dropSelf(STBlocks.SCULK_EMITTER.get());
		dropSelf(STBlocks.SCULK_TRANSMITTER.get());
		dropSelf(STBlocks.SCULK_RECEIVER.get());
		add(STBlocks.SCULK_BARREL.get(), block -> createNameableBlockEntityTable(block));
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return STBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
	}
}
