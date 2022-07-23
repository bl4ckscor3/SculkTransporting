package sculktransporting.datagen;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import sculktransporting.SculkTransporting;
import sculktransporting.registration.STBlocks;
import sculktransporting.registration.STItems;

public class RecipeGenerator extends RecipeProvider {
	public RecipeGenerator(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected final void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(STBlocks.SCULK_EMITTER.get())
				.pattern("DSD")
				.pattern("GBG")
				.define('D', Blocks.DEEPSLATE_BRICKS)
				.define('S', Blocks.SCULK_SENSOR)
				.define('B', Blocks.SCULK)
				.define('G', Tags.Items.INGOTS_GOLD)
				.unlockedBy("has_sculk_sensor", has(Blocks.SCULK_SENSOR))
				.save(consumer);
		ShapedRecipeBuilder.shaped(STBlocks.SCULK_TRANSMITTER.get())
				.pattern("DBD")
				.pattern("IRI")
				.define('D', Blocks.DEEPSLATE_BRICKS)
				.define('B', Blocks.SCULK)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.unlockedBy("has_sculk", has(Blocks.SCULK))
				.save(consumer);
		ShapedRecipeBuilder.shaped(STBlocks.SCULK_RECEIVER.get())
				.pattern("DSD")
				.pattern("BHB")
				.define('D', Blocks.DEEPSLATE_BRICKS)
				.define('S', Blocks.SCULK_SENSOR)
				.define('B', Blocks.SCULK)
				.define('H', Blocks.HOPPER)
				.unlockedBy("has_sculk_sensor", has(Blocks.SCULK_SENSOR))
				.save(consumer);
		ShapedRecipeBuilder.shaped(STBlocks.SCULK_BARREL.get())
				.pattern(" S ")
				.pattern("SBS")
				.pattern(" S ")
				.define('S', Blocks.SCULK_VEIN)
				.define('B', Blocks.BARREL)
				.unlockedBy("has_barrel", has(Blocks.BARREL))
				.save(consumer);
		//@formatter:on

		addQuantityModifierRecipe(consumer, Items.ITEM_FRAME, Blocks.SCULK, STItems.QUANTITY_MODIFIER_TIER_1.get());
		addQuantityModifierRecipe(consumer, STItems.QUANTITY_MODIFIER_TIER_1.get(), Blocks.SCULK_CATALYST, STItems.QUANTITY_MODIFIER_TIER_2.get());
		addQuantityModifierRecipe(consumer, STItems.QUANTITY_MODIFIER_TIER_2.get(), Items.ECHO_SHARD, STItems.QUANTITY_MODIFIER_TIER_3.get());
		addSpeedModifierRecipe(consumer, Items.ITEM_FRAME, Blocks.SCULK, STItems.SPEED_MODIFIER_TIER_1.get());
		addSpeedModifierRecipe(consumer, STItems.SPEED_MODIFIER_TIER_1.get(), Blocks.RAW_IRON_BLOCK, STItems.SPEED_MODIFIER_TIER_2.get());
		addSpeedModifierRecipe(consumer, STItems.SPEED_MODIFIER_TIER_2.get(), Blocks.SCULK_CATALYST, STItems.SPEED_MODIFIER_TIER_3.get());
		addSpeedModifierRecipe(consumer, STItems.SPEED_MODIFIER_TIER_3.get(), Items.ECHO_SHARD, STItems.SPEED_MODIFIER_TIER_4.get());
	}

	protected final void addQuantityModifierRecipe(Consumer<FinishedRecipe> consumer, ItemLike previous, ItemLike material, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(result)
				.pattern("BMB")
				.pattern("MPM")
				.pattern("BMB")
				.define('B', Blocks.SMOOTH_BASALT)
				.define('M', material)
				.define('P', previous)
				.unlockedBy("has_previous", has(previous))
				.save(consumer);
		//@formatter:on
	}

	protected final void addSpeedModifierRecipe(Consumer<FinishedRecipe> consumer, ItemLike previous, ItemLike material, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(result)
				.pattern("TMT")
				.pattern("MPM")
				.pattern("TMT")
				.define('T', Blocks.TUFF)
				.define('M', material)
				.define('P', previous)
				.unlockedBy("has_previous", has(previous))
				.save(consumer);
		//@formatter:on
	}

	@Override
	public String getName() {
		return SculkTransporting.MODID + " Recipes";
	}
}
