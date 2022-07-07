package unnamedsculkmod.datagen;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import unnamedsculkmod.UnnamedSculkMod;

public class RecipeGenerator extends RecipeProvider {
	public RecipeGenerator(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected final void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {}

	@Override
	public String getName() {
		return UnnamedSculkMod.MODID + " Recipes";
	}
}
