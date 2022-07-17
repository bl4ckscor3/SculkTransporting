package sculktransporting.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.Vec3;
import sculktransporting.registration.USBlockEntityTypes;
import sculktransporting.registration.USGameEvents;

public class SculkTransmitterBlockEntity extends BaseSculkItemTransporterBlockEntity {
	public SculkTransmitterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public boolean shouldPerformAction(Level level) {
		return true;
	}

	@Override
	public boolean isValidVibration(GameEvent gameEvent, Context ctx) {
		return gameEvent == USGameEvents.ITEM_TRANSMITTABLE.get() && ctx.sourceEntity() instanceof ItemEntity item && item.isAlive();
	}

	@Override
	public boolean shouldListen(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Context ctx) {
		return storedItemSignal.isEmpty() && ctx.sourceEntity() instanceof ItemEntity item && !item.blockPosition().equals(worldPosition) && super.shouldListen(level, listener, pos, event, ctx);
	}

	@Override
	public void onSignalSchedule() {
		super.onSignalSchedule();

		if (getListener().receivingEvent != null && getListener().receivingEvent.entity() instanceof ItemEntity item) {
			Vec3 originVec = getListener().receivingEvent.pos();
			BlockPos originPos = new BlockPos(originVec);

			if (level.getBlockEntity(originPos) instanceof BaseSculkItemTransporterBlockEntity be && be.hasStoredItemSignal()) {
				be.setItemSignal(null, 0);
				level.scheduleTick(originPos, be.getBlockState().getBlock(), 0);
				item.setPos(originVec); //set the position of the item entity to the origin of the signal as a marker, so the transmitter doesn't send the item back where it came from
				item.discard(); //marks this item signal as already scheduled for one receiver, so it doesn't get sent to another one
			}
		}
	}

	@Override
	public void onSignalReceive(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, Entity entity, Entity projectileOwner, float distance) {
		if (event == USGameEvents.ITEM_TRANSMITTABLE.get() && entity instanceof ItemEntity item)
			setItemSignal(item, getRedstoneStrengthForDistance(distance, listener.getListenerRadius()));
	}

	@Override
	public BlockEntityType<?> getType() {
		return USBlockEntityTypes.SCULK_TRANSMITTER_BLOCK_ENTITY.get();
	}
}
