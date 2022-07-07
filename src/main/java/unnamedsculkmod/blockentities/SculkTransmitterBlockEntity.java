package unnamedsculkmod.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.GameEventListener;
import unnamedsculkmod.USBlockEntityTypes;
import unnamedsculkmod.USBlocks;
import unnamedsculkmod.blocks.SculkTransmitterBlock;
import unnamedsculkmod.misc.USGameEvents;

public class SculkTransmitterBlockEntity extends SculkSensorBlockEntity {
	public SculkTransmitterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public boolean shouldListen(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Context ctx) {
		return !getBlockState().is(USBlocks.SCULK_ENCODER.get()) && super.shouldListen(level, listener, pos, event, ctx);
	}

	@Override
	public void onSignalReceive(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, Entity entity, Entity projectileOwner, float distance) {
		BlockState state = getBlockState();

		if (!state.is(USBlocks.SCULK_ENCODER.get()))
			SculkTransmitterBlock.activate(entity, level, worldPosition, state, getRedstoneStrengthForDistance(distance, listener.getListenerRadius()));
	}

	@Override
	public BlockEntityType<?> getType() {
		return USBlockEntityTypes.SCULK_TRANSMITTER_BLOCK_ENTITY.get();
	}

	@Override
	public boolean isValidVibration(GameEvent gameEvent, Context ctx) {
		return gameEvent == USGameEvents.ITEM_TRANSMITTABLE.get();
	}
}
