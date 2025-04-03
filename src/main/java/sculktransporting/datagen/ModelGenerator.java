package sculktransporting.datagen;

import java.util.stream.Stream;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import sculktransporting.SculkTransporting;
import sculktransporting.registration.STBlocks;
import sculktransporting.registration.STItems;

public class ModelGenerator extends ModelProvider {
	public ModelGenerator(PackOutput output) {
		super(output, SculkTransporting.MODID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		BlockStateGenerator.run(blockModels);

		for (DeferredHolder<Item, ? extends Item> item : STItems.ITEMS.getEntries()) {
			itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
		}
	}

	@Override
	protected Stream<? extends Holder<Block>> getKnownBlocks() {
		return Stream.of(STBlocks.SCULK_BARREL);
	}
}
