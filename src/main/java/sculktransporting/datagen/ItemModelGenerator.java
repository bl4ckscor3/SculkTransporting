package sculktransporting.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sculktransporting.SculkTransporting;
import sculktransporting.registration.STBlocks;
import sculktransporting.registration.STItems;

public class ItemModelGenerator extends ItemModelProvider {
	public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, SculkTransporting.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		simpleParent(STBlocks.SCULK_EMITTER.get(), "sculk_emitter_active");
		simpleParent(STBlocks.SCULK_TRANSMITTER.get(), "sculk_transmitter_active");
		simpleParent(STBlocks.SCULK_RECEIVER.get(), "sculk_receiver_active");
		simpleParent(STBlocks.SCULK_BARREL.get());

		for (RegistryObject<Item> item : STItems.ITEMS.getEntries()) {
			simpleItem(item.get(), "item/generated");
		}
	}

	public ItemModelBuilder simpleItem(Item item, String parent) {
		String path = name(item);

		return uncheckedSingleTexture(path, mcLoc(parent), "layer0", modLoc(ITEM_FOLDER + "/" + path));
	}

	public ItemModelBuilder uncheckedSingleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture) {
		return parent(name, parent).texture(textureKey, texture);
	}

	public ItemModelBuilder simpleParent(Block block) {
		return simpleParent(block, name(block));
	}

	public ItemModelBuilder simpleParent(Block block, String parent) {
		return parent(name(block), modLoc(BLOCK_FOLDER + "/" + parent));
	}

	public ItemModelBuilder parent(String name, ResourceLocation parent) {
		return getBuilder(name).parent(new UncheckedModelFile(parent));
	}

	private String name(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block).getPath();
	}

	private String name(Item item) {
		return ForgeRegistries.ITEMS.getKey(item).getPath();
	}

	@Override
	public String getName() {
		return SculkTransporting.MODID + " Item Models";
	}
}
