package unnamedsculkmod.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.GameEventListener;
import unnamedsculkmod.registration.USBlockEntityTypes;

public class SculkEmitterBlockEntity extends BaseSculkItemTransporterBlockEntity {
	public SculkEmitterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public boolean isValidVibration(GameEvent gameEvent, Context ctx) {
		return false;
	}

	@Override
	public boolean shouldListen(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Context ctx) {
		return false;
	}

	@Override
	public void onSignalSchedule() {}

	@Override
	public void onSignalReceive(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, Entity entity, Entity projectileOwner, float distance) {}

	@Override
	public BlockEntityType<?> getType() {
		return USBlockEntityTypes.SCULK_EMITTER_BLOCK_ENTITY.get();
	}
}
