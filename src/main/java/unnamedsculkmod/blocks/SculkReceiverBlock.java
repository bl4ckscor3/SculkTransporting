package unnamedsculkmod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import unnamedsculkmod.blockentities.SculkReceiverBlockEntity;
import unnamedsculkmod.registration.USBlockEntityTypes;

public class SculkReceiverBlock extends BaseSculkItemTransporterBlock {
	public SculkReceiverBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (level.getBlockEntity(pos) instanceof SculkReceiverBlockEntity be)
			be.forgetInventoryBelow();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SculkReceiverBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return !level.isClientSide ? createTickerHelper(type, USBlockEntityTypes.SCULK_RECEIVER_BLOCK_ENTITY.get(), SculkReceiverBlockEntity::serverTick) : null;
	}
}
