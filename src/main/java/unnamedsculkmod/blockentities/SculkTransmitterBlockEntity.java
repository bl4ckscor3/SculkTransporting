package unnamedsculkmod.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.Vec3;
import unnamedsculkmod.blocks.SculkTransmitterBlock;
import unnamedsculkmod.registration.USBlockEntityTypes;
import unnamedsculkmod.registration.USBlocks;
import unnamedsculkmod.registration.USGameEvents;

public class SculkTransmitterBlockEntity extends SculkSensorBlockEntity {
	private ItemStack storedItemSignal = ItemStack.EMPTY;
	private BlockPos signalOrigin;
	private ItemEntity cachedItemEntity;

	public SculkTransmitterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SculkTransmitterBlockEntity be) {
		if (!be.storedItemSignal.isEmpty()) {
			if (be.cachedItemEntity == null)
				be.cachedItemEntity = new ItemEntity(level, be.signalOrigin.getX(), be.signalOrigin.getY(), be.signalOrigin.getZ(), be.storedItemSignal);

			level.gameEvent(be.cachedItemEntity, USGameEvents.ITEM_TRANSMITTABLE.get(), pos); //TODO: how bad is that for performance? maybe delay? -R
		}

		be.getListener().tick(level);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		storedItemSignal = ItemStack.of(tag.getCompound("StoredItemSignal"));
		signalOrigin = NbtUtils.readBlockPos(tag.getCompound("SignalOrigin"));
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		if (!storedItemSignal.isEmpty()) {
			tag.put("StoredItemSignal", storedItemSignal.save(new CompoundTag()));
			tag.put("SignalOrigin", NbtUtils.writeBlockPos(signalOrigin));
		}
		else if (getListener().receivingEvent != null && getListener().receivingEvent.entity() instanceof ItemEntity item) {
			tag.put("StoredItemSignal", item.getItem().save(new CompoundTag()));
			tag.put("SignalOrigin", NbtUtils.writeBlockPos(item.blockPosition()));
		}
	}

	@Override
	public boolean isValidVibration(GameEvent gameEvent, Context ctx) {
		return gameEvent == USGameEvents.ITEM_TRANSMITTABLE.get() && ctx.sourceEntity() instanceof ItemEntity item && item.isAlive();
	}

	@Override
	public boolean shouldListen(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Context ctx) {
		return !getBlockState().is(USBlocks.SCULK_EMITTER.get()) && storedItemSignal.isEmpty() && ctx.sourceEntity() instanceof ItemEntity item && !item.blockPosition().equals(worldPosition) && super.shouldListen(level, listener, pos, event, ctx);
	}

	@Override
	public void onSignalSchedule() {
		super.onSignalSchedule();

		if (getListener().receivingEvent != null && getListener().receivingEvent.entity() instanceof ItemEntity item) {
			Vec3 originVec = getListener().receivingEvent.pos();
			BlockPos originPos = new BlockPos(originVec);

			if (level.getBlockEntity(originPos) instanceof SculkTransmitterBlockEntity be && be.hasStoredItemSignal()) {
				be.setItemSignal(null, 0);
				level.scheduleTick(originPos, level.getBlockState(originPos).getBlock(), 0);
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

	public boolean hasStoredItemSignal() {
		return !storedItemSignal.isEmpty();
	}

	public ItemStack getStoredItemSignal() {
		return storedItemSignal;
	}

	public void setItemSignal(ItemEntity itemSignal, int power) {
		if (itemSignal == null) {
			storedItemSignal = ItemStack.EMPTY;
			signalOrigin = null;
		}
		else {
			if (storedItemSignal.isEmpty())
				SculkTransmitterBlock.activate(itemSignal, level, worldPosition, level.getBlockState(worldPosition), power);

			storedItemSignal = itemSignal.getItem().copy();
			signalOrigin = itemSignal.blockPosition();
		}

		cachedItemEntity = null;
	}
}
