package sculktransporting.datagen;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import sculktransporting.SculkTransporting;
import sculktransporting.registration.USBlocks;

public class RecipeGenerator extends RecipeProvider {
	public RecipeGenerator(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected final void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(USBlocks.SCULK_EMITTER.get())
				.pattern("DSD")
				.pattern("BGB")
				.define('D', Blocks.DEEPSLATE_BRICKS)
				.define('S', Blocks.SCULK_SENSOR)
				.define('B', Blocks.SCULK)
				.define('G', Tags.Items.INGOTS_GOLD)
				.unlockedBy("has_sculk_sensor", has(Blocks.SCULK_SENSOR))
				.save(consumer);
		ShapedRecipeBuilder.shaped(USBlocks.SCULK_TRANSMITTER.get())
				.pattern("DBD")
				.pattern("IRI")
				.define('D', Blocks.DEEPSLATE_BRICKS)
				.define('B', Blocks.SCULK)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.unlockedBy("has_sculk", has(Blocks.SCULK))
				.save(consumer);
		ShapedRecipeBuilder.shaped(USBlocks.SCULK_RECEIVER.get())
				.pattern("DSD")
				.pattern("BHB")
				.define('D', Blocks.DEEPSLATE_BRICKS)
				.define('S', Blocks.SCULK_SENSOR)
				.define('B', Blocks.SCULK)
				.define('H', Blocks.HOPPER)
				.unlockedBy("has_sculk_sensor", has(Blocks.SCULK_SENSOR))
				.save(consumer);
		//@formatter:on
	}

	@Override
	public String getName() {
		return SculkTransporting.MODID + " Recipes";
	}
}
