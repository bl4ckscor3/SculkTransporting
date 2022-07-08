package unnamedsculkmod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import unnamedsculkmod.USBlockEntityTypes;
import unnamedsculkmod.blockentities.SculkTransmitterBlockEntity;

public class SculkTransmitterBlock extends SculkSensorBlock {
	public SculkTransmitterBlock(Properties properties) {
		super(properties, 8); //maybe make range configurable? -R
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (getPhase(state) == SculkSensorPhase.COOLDOWN)
			level.setBlock(pos, state.setValue(PHASE, SculkSensorPhase.INACTIVE), 3);
		else if (getPhase(state) == SculkSensorPhase.ACTIVE && level.getBlockEntity(pos) instanceof SculkTransmitterBlockEntity be && be.getStoredItemSignal() == null)
			deactivate(level, pos, state);
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
		return !level.isClientSide ? createTickerHelper(type, USBlockEntityTypes.SCULK_TRANSMITTER_BLOCK_ENTITY.get(), SculkTransmitterBlockEntity::tick) : null;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof SculkTransmitterBlockEntity be) {
				if (be.getStoredItemSignal() != null)
					Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.getStoredItemSignal().getItem());
				else if (be.getListener().receivingEvent != null && be.getListener().receivingEvent.entity() instanceof ItemEntity item)
					Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item.getItem());
			} 

			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	public static void activate(Entity entity, Level level, BlockPos pos, BlockState state, int distance) {
		level.scheduleTick(pos, state.getBlock(), 20); //TODO: should we tweak the transmission cooldown (here would be the place to do that)? Vanilla has 40 as default value -R
		SculkSensorBlock.activate(entity, level, pos, state, distance);
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
