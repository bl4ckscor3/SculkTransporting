package sculktransporting.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.gameevent.GameEvent;
import sculktransporting.blockentities.BaseSculkItemTransporterBlockEntity;

public abstract class BaseSculkItemTransporterBlock extends SculkSensorBlock {
	private static final float CONVERSION_FACTOR = 14.0F / 63.0F;

	protected BaseSculkItemTransporterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		SculkSensorPhase phase = getPhase(state);

		if (phase == SculkSensorPhase.COOLDOWN)
			level.setBlockAndUpdate(pos, state.setValue(PHASE, SculkSensorPhase.INACTIVE));
		else if (phase == SculkSensorPhase.ACTIVE && level.getBlockEntity(pos) instanceof BaseSculkItemTransporterBlockEntity be && !be.hasStoredItemSignal())
			deactivate(level, pos, state);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {}

	@Override
	public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

	@Override
	public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type);

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof BaseSculkItemTransporterBlockEntity be) {
				if (be.hasStoredItemSignal())
					Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.getStoredItemSignal());
				else if (be.getVibrationData().getCurrentVibration() != null && be.getVibrationData().getCurrentVibration().entity() instanceof ItemEntity item)
					Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item.getItem());
			}

			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	public static void deactivate(Level level, BlockPos pos, BlockState state) {
		SculkSensorBlock.deactivate(level, pos, state);
		level.setBlockAndUpdate(pos, state.setValue(PHASE, SculkSensorPhase.INACTIVE).setValue(POWER, 0)); //skip SculkSensorPhase.COOLDOWN to reduce delay
	}

	@Override
	public void activate(Entity entity, Level level, BlockPos pos, BlockState state, int distance, int resonanceFrequency) { //copied from SculkSensorBlock to remove vibration resonance
		level.setBlock(pos, state.setValue(PHASE, SculkSensorPhase.ACTIVE).setValue(POWER, distance), 3);
		level.scheduleTick(pos, state.getBlock(), 0);
		updateNeighbours(level, pos, state);
		level.gameEvent(entity, GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, pos);

		if (!state.getValue(WATERLOGGED))
			level.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.SCULK_CLICKING, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.2F + 0.8F);
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof BaseSculkItemTransporterBlockEntity be && be.hasStoredItemSignal())
			return (int) (1 + Math.floor(CONVERSION_FACTOR * (be.getStoredItemSignal().getCount() - 1))); //mapping 1 to 64 items onto 1 to 15 power output

		return 0;
	}

	@Override
	public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
		return 0;
	}
}
