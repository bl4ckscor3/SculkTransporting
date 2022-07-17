package sculktransporting.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sculktransporting.blockentities.BaseSculkItemTransporterBlockEntity;
import sculktransporting.blockentities.SculkTransmitterBlockEntity;
import sculktransporting.registration.USBlockEntityTypes;

public class SculkTransmitterBlock extends BaseSculkItemTransporterBlock {
	public SculkTransmitterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SculkTransmitterBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return !level.isClientSide ? createTickerHelper(type, USBlockEntityTypes.SCULK_TRANSMITTER_BLOCK_ENTITY.get(), BaseSculkItemTransporterBlockEntity::serverTick) : null;
	}
}
