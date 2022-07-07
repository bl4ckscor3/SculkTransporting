package unnamedsculkmod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import unnamedsculkmod.USBlockEntityTypes;
import unnamedsculkmod.blockentities.SculkTransmitterBlockEntity;
import unnamedsculkmod.misc.USGameEvents;

public class SculkTransmitterBlock extends SculkSensorBlock {
	public SculkTransmitterBlock(Properties properties) {
		super(properties, 8); //maybe make range configurable? -R
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SculkTransmitterBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return !level.isClientSide ? createTickerHelper(type, USBlockEntityTypes.SCULK_TRANSMITTER_BLOCK_ENTITY.get(), (l, p, s, b) -> b.getListener().tick(l)) : null;
	}

	public static void activate(Entity entity, Level level, BlockPos pos, BlockState state, int signal) {
		SculkSensorBlock.activate(entity, level, pos, state, signal);
		level.gameEvent(entity, USGameEvents.ITEM_TRANSMITTABLE.get(), pos);
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return 0; //TODO: maybe comparator output signal should be how many items the block is currently holding? -R
	}

	@Override
	public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
		return 0;
	}
}
