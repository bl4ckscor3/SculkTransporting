package sculktransporting.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import sculktransporting.registration.STBlocks;
import sculktransporting.registration.STItems;

public class RecipeGenerator extends RecipeProvider {
	public RecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected final void buildRecipes(RecipeOutput recipeOutput) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, STBlocks.SCULK_EMITTER.get())
				.pattern("DSD")
				.pattern("GBG")
				.define('D', Blocks.DEEPSLATE_BRICKS)
				.define('S', Blocks.SCULK_SENSOR)
				.define('B', Blocks.SCULK)
				.define('G', Tags.Items.INGOTS_GOLD)
				.unlockedBy("has_sculk_sensor", has(Blocks.SCULK_SENSOR))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, STBlocks.SCULK_TRANSMITTER.get())
				.pattern("DBD")
				.pattern("IRI")
				.define('D', Blocks.DEEPSLATE_BRICKS)
				.define('B', Blocks.SCULK)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.unlockedBy("has_sculk", has(Blocks.SCULK))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, STBlocks.SCULK_RECEIVER.get())
				.pattern("DSD")
				.pattern("BHB")
				.define('D', Blocks.DEEPSLATE_BRICKS)
				.define('S', Blocks.SCULK_SENSOR)
				.define('B', Blocks.SCULK)
				.define('H', Blocks.HOPPER)
				.unlockedBy("has_sculk_sensor", has(Blocks.SCULK_SENSOR))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, STBlocks.SCULK_BARREL.get())
				.pattern(" S ")
				.pattern("SBS")
				.pattern(" S ")
				.define('S', Blocks.SCULK_VEIN)
				.define('B', Blocks.BARREL)
				.unlockedBy("has_barrel", has(Blocks.BARREL))
				.save(recipeOutput);
		//@formatter:on

		addQuantityModifierRecipe(recipeOutput, Items.ITEM_FRAME, Blocks.SCULK, STItems.QUANTITY_MODIFIER_TIER_1.get());
		addQuantityModifierRecipe(recipeOutput, STItems.QUANTITY_MODIFIER_TIER_1.get(), Blocks.SCULK_CATALYST, STItems.QUANTITY_MODIFIER_TIER_2.get());
		addModifierRecipe(recipeOutput, STItems.QUANTITY_MODIFIER_TIER_2.get(), Blocks.SMOOTH_BASALT, Items.ECHO_SHARD, Items.SCULK_SHRIEKER, STItems.QUANTITY_MODIFIER_TIER_3.get());
		addSpeedModifierRecipe(recipeOutput, Items.ITEM_FRAME, Blocks.SCULK, STItems.SPEED_MODIFIER_TIER_1.get());
		addSpeedModifierRecipe(recipeOutput, STItems.SPEED_MODIFIER_TIER_1.get(), Blocks.RAW_IRON_BLOCK, STItems.SPEED_MODIFIER_TIER_2.get());
		addSpeedModifierRecipe(recipeOutput, STItems.SPEED_MODIFIER_TIER_2.get(), Blocks.SCULK_CATALYST, STItems.SPEED_MODIFIER_TIER_3.get());
		addModifierRecipe(recipeOutput, STItems.SPEED_MODIFIER_TIER_3.get(), Blocks.TUFF, Items.ECHO_SHARD, Items.SCULK_SHRIEKER, STItems.SPEED_MODIFIER_TIER_4.get());
	}

	protected final void addQuantityModifierRecipe(RecipeOutput recipeOutput, ItemLike previous, ItemLike material, ItemLike result) {
		addModifierRecipe(recipeOutput, previous, Blocks.SMOOTH_BASALT, material, null, result);
	}

	protected final void addSpeedModifierRecipe(RecipeOutput recipeOutput, ItemLike previous, ItemLike material, ItemLike result) {
		addModifierRecipe(recipeOutput, previous, Blocks.TUFF, material, null, result);
	}

	protected final void addModifierRecipe(RecipeOutput recipeOutput, ItemLike previous, ItemLike frameMaterial, ItemLike material1, ItemLike material2, ItemLike result) {
		if (material2 == null) {
			//@formatter:off
			ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, result)
					.pattern("FMF")
					.pattern("MPM")
					.pattern("FMF")
					.define('F', frameMaterial)
					.define('M', material1)
					.define('P', previous)
					.unlockedBy("has_previous", has(previous))
					.save(recipeOutput);
			//@formatter:on
		}
		else {
			ResourceLocation resultName = BuiltInRegistries.ITEM.getKey(result.asItem());

			//@formatter:off
			ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, result)
					.group(resultName.getPath())
					.pattern("FMF")
					.pattern("NPN")
					.pattern("FMF")
					.define('F', frameMaterial)
					.define('M', material1)
					.define('N', material2)
					.define('P', previous)
					.unlockedBy("has_previous", has(previous))
					.save(recipeOutput);
			ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, result)
					.group(resultName.getPath())
					.pattern("FNF")
					.pattern("MPM")
					.pattern("FNF")
					.define('F', frameMaterial)
					.define('M', material1)
					.define('N', material2)
					.define('P', previous)
					.unlockedBy("has_previous", has(previous))
					.save(recipeOutput, resultName + "_alt");
			//@formatter:on
		}
	}
}
