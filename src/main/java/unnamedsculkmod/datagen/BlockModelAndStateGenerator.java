package unnamedsculkmod.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import unnamedsculkmod.UnnamedSculkMod;

public class BlockModelAndStateGenerator extends BlockStateProvider {
	public BlockModelAndStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, UnnamedSculkMod.MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {}

	@Override
	public String getName() {
		return UnnamedSculkMod.MODID + " Block States/Models";
	}
}
