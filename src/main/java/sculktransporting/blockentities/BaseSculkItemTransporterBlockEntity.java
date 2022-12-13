package sculktransporting.blockentities;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.Vec3;
import sculktransporting.blocks.BaseSculkItemTransporterBlock;
import sculktransporting.client.ItemSignalParticleOption;
import sculktransporting.misc.OneReceiverVibrationListener;
import sculktransporting.registration.STGameEvents;

public abstract class BaseSculkItemTransporterBlockEntity extends SculkSensorBlockEntity {
	private static final Logger LOGGER = LogUtils.getLogger();
	protected ItemStack storedItemSignal = ItemStack.EMPTY;
	protected BlockPos signalOrigin;
	protected ItemEntity cachedItemEntity;

	public BaseSculkItemTransporterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
		this.listener = new OneReceiverVibrationListener(new BlockPositionSource(worldPosition), ((SculkSensorBlock) state.getBlock()).getListenerRange(), this);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, BaseSculkItemTransporterBlockEntity be) {
		be.getListener().tick(level);

		if (!be.storedItemSignal.isEmpty()) {
			if (be.cachedItemEntity == null) {
				be.level.updateNeighborsAt(pos, state.getBlock());
				be.cachedItemEntity = new ItemEntity(level, be.signalOrigin.getX(), be.signalOrigin.getY(), be.signalOrigin.getZ(), be.storedItemSignal);
			}

			if (be.shouldPerformAction(level) && be.cachedItemEntity.isAlive())
				level.gameEvent(be.cachedItemEntity, STGameEvents.ITEM_TRANSMITTABLE.get(), pos);
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		if (tag.contains("listener", 10))
			OneReceiverVibrationListener.oneReceiverCodec(this).parse(new Dynamic<>(NbtOps.INSTANCE, tag.getCompound("listener"))).resultOrPartial(LOGGER::error).ifPresent(listener -> this.listener = listener);

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
		else if (getListener().currentVibration != null && getListener().currentVibration.entity() instanceof ItemEntity item) {
			tag.put("StoredItemSignal", item.getItem().save(new CompoundTag()));
			tag.put("SignalOrigin", NbtUtils.writeBlockPos(item.blockPosition()));
		}
	}

	public abstract boolean shouldPerformAction(Level level);

	@Override
	public boolean isValidVibration(GameEvent gameEvent, Context ctx) {
		return gameEvent == STGameEvents.ITEM_TRANSMITTABLE.get() && ctx.sourceEntity() instanceof ItemEntity item && item.isAlive();
	}

	@Override
	public boolean shouldListen(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Context ctx) {
		return storedItemSignal.isEmpty() && ctx.sourceEntity() instanceof ItemEntity item && !item.blockPosition().equals(worldPosition) && super.shouldListen(level, listener, pos, event, ctx);
	}

	@Override
	public void onSignalSchedule() {
		super.onSignalSchedule();

		if (getListener().currentVibration != null && getListener().currentVibration.entity() instanceof ItemEntity item) {
			Vec3 originVec = getListener().currentVibration.pos();
			BlockPos originPos = new BlockPos(originVec);

			if (level.getBlockEntity(originPos) instanceof BaseSculkItemTransporterBlockEntity be && be.hasStoredItemSignal()) {
				be.setItemSignal(null, 0);
				level.scheduleTick(originPos, be.getBlockState().getBlock(), 0);
				item.setPos(originVec); //set the position of the item entity to the origin of the signal as a marker, so the transmitter doesn't send the item back where it came from
			}

			((ServerLevel) level).sendParticles(new ItemSignalParticleOption(getListener().getListenerSource(), getListener().travelTimeInTicks, item.getItem()), originVec.x, originVec.y, originVec.z, (item.getItem().getCount() + 15) / 16 * 5, 0, 0, 0, 0);
		}
	}

	@Override
	public void onSignalReceive(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, Entity entity, Entity projectileOwner, float distance) {
		if (event == STGameEvents.ITEM_TRANSMITTABLE.get() && entity instanceof ItemEntity item)
			setItemSignal(item, getRedstoneStrengthForDistance(distance, listener.getListenerRadius()));
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
			level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
		}
		else {
			boolean shouldActivate = storedItemSignal.isEmpty();

			storedItemSignal = itemSignal.getItem().copy();
			signalOrigin = itemSignal.blockPosition();

			if (shouldActivate)
				BaseSculkItemTransporterBlock.activate(itemSignal, level, worldPosition, getBlockState(), power);
		}

		cachedItemEntity = null;
	}

	@Override
	public abstract BlockEntityType<?> getType();

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();

		saveAdditional(tag);
		return tag;
	}
}
