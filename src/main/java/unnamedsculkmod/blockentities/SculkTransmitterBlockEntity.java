package unnamedsculkmod.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSensorBlock;
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
	private ItemEntity storedItemSignal;

	public SculkTransmitterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SculkTransmitterBlockEntity be) {
		if (be.storedItemSignal != null) {
			level.gameEvent(be.storedItemSignal, USGameEvents.ITEM_TRANSMITTABLE.get(), pos); //TODO: how bad is that for performance? maybe delay? -R
		}

		be.getListener().tick(level);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.storedItemSignal = new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), ItemStack.of(tag.getCompound("StoredItemSignal")));
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		if (storedItemSignal != null)
			tag.put("StoredItemSignal", storedItemSignal.getItem().save(new CompoundTag()));
	}

	@Override
	public boolean shouldListen(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Context ctx) {
		return !getBlockState().is(USBlocks.SCULK_EMITTER.get()) && storedItemSignal == null && super.shouldListen(level, listener, pos, event, ctx);
	}

	@Override
	public void onSignalSchedule() {
		super.onSignalSchedule();

		if (getListener().receivingEvent != null && getListener().receivingEvent.entity() instanceof ItemEntity) {
			BlockPos originPos = new BlockPos(getListener().receivingEvent.pos());

			if (level.getBlockEntity(originPos) instanceof SculkTransmitterBlockEntity be && be.getStoredItemSignal() != null){
				be.setItemSignal(null);
				level.scheduleTick(originPos, level.getBlockState(originPos).getBlock(), 0);
			}
		}
	}

	@Override
	public void onSignalReceive(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, Entity entity, Entity projectileOwner, float distance) {
		BlockState state = getBlockState();

		if (event == USGameEvents.ITEM_TRANSMITTABLE.get() && entity instanceof ItemEntity item) {
			storedItemSignal = item;
			SculkTransmitterBlock.activate(item, level, worldPosition, state, getRedstoneStrengthForDistance(distance, listener.getListenerRadius()));
		}
	}

	@Override
	public BlockEntityType<?> getType() {
		return USBlockEntityTypes.SCULK_TRANSMITTER_BLOCK_ENTITY.get();
	}

	@Override
	public boolean isValidVibration(GameEvent gameEvent, Context ctx) {
		return gameEvent == USGameEvents.ITEM_TRANSMITTABLE.get();
	}

	public ItemEntity getStoredItemSignal() {
		return storedItemSignal;
	}

	public void setItemSignal(ItemEntity itemSignal) {
		if (storedItemSignal == null && itemSignal != null)
			SculkSensorBlock.activate(itemSignal, level, worldPosition, level.getBlockState(worldPosition), 0); //TODO: reevaluate that 0 please -R

		storedItemSignal = itemSignal;
	}
}
