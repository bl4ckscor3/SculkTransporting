package sculktransporting.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sculktransporting.registration.STBlockEntityTypes;
import sculktransporting.registration.STBlocks;

public class SculkBarrelBlockEntity extends BarrelBlockEntity {
	public SculkBarrelBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public BlockEntityType<?> getType() {
		return STBlockEntityTypes.SCULK_BARREL_BLOCK_ENTITY.get();
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable(STBlocks.SCULK_BARREL.get().getDescriptionId());
	}
}
