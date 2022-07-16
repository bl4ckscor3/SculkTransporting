package unnamedsculkmod.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import unnamedsculkmod.UnnamedSculkMod;
import unnamedsculkmod.registration.USBlocks;
import unnamedsculkmod.registration.USItems;

public class ItemModelGenerator extends ItemModelProvider {
	public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, UnnamedSculkMod.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		for (RegistryObject<Block> block : USBlocks.BLOCKS.getEntries()) {
			simpleParent(block.get());
		}

		for (RegistryObject<Item> item : USItems.ITEMS.getEntries()) {
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
		return UnnamedSculkMod.MODID + " Item Models";
	}
}
