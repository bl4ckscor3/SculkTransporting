package sculktransporting.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import sculktransporting.SculkTransporting;

public class BlockModelAndStateGenerator extends BlockStateProvider {
	public BlockModelAndStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, SculkTransporting.MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {}

	@Override
	public String getName() {
		return SculkTransporting.MODID + " Block States/Models";
	}
}
