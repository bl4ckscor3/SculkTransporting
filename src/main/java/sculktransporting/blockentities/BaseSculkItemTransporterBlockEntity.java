package sculktransporting.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import sculktransporting.blocks.BaseSculkItemTransporterBlock;
import sculktransporting.client.ItemSignalParticleOption;
import sculktransporting.misc.OneReceiverVibrationListener;
import sculktransporting.registration.STGameEvents;

public abstract class BaseSculkItemTransporterBlockEntity extends SculkSensorBlockEntity {
	protected ItemStack storedItemSignal = ItemStack.EMPTY;
	protected BlockPos signalOrigin;
	protected ItemEntity cachedItemEntity;

	protected BaseSculkItemTransporterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
		this.vibrationListener = new OneReceiverVibrationListener(this);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, BaseSculkItemTransporterBlockEntity be) {
		VibrationSystem.Ticker.tick(level, be.getVibrationData(), be.getVibrationUser());

		if (!be.storedItemSignal.isEmpty()) {
			if (be.cachedItemEntity == null) {
				be.level.updateNeighborsAt(pos, state.getBlock());
				be.cachedItemEntity = new ItemEntity(level, be.signalOrigin.getX(), be.signalOrigin.getY(), be.signalOrigin.getZ(), be.storedItemSignal);
			}

			if (be.shouldPerformAction(level) && be.cachedItemEntity.isAlive())
				level.gameEvent(be.cachedItemEntity, STGameEvents.ITEM_TRANSMITTABLE, pos);
		}
	}

	@Override
	public User createVibrationUser() {
		return new BaseVibrationUser(getBlockPos());
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);

		storedItemSignal = ItemStack.parseOptional(lookupProvider, tag.getCompound("StoredItemSignal"));
		signalOrigin = NbtUtils.readBlockPos(tag, "SignalOrigin").orElse(null);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);

		if (!storedItemSignal.isEmpty()) {
			tag.put("StoredItemSignal", storedItemSignal.save(lookupProvider));
			tag.put("SignalOrigin", NbtUtils.writeBlockPos(signalOrigin));
		}
		else if (getVibrationData().getCurrentVibration() != null && getVibrationData().getCurrentVibration().entity() instanceof ItemEntity item) {
			tag.put("StoredItemSignal", item.getItem().save(lookupProvider));
			tag.put("SignalOrigin", NbtUtils.writeBlockPos(item.blockPosition()));
		}
	}

	public abstract boolean shouldPerformAction(Level level);

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

			if (shouldActivate) {
				Block block = getBlockState().getBlock();

				if (block instanceof BaseSculkItemTransporterBlock sculkItemTransporterBlock)
					sculkItemTransporterBlock.activate(itemSignal, level, worldPosition, getBlockState(), power, 0);
			}
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
	public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
		return saveCustomOnly(lookupProvider);
	}

	public class BaseVibrationUser extends SculkSensorBlockEntity.VibrationUser {
		public BaseVibrationUser(BlockPos pos) {
			super(pos);
		}

		@Override
		public boolean isValidVibration(Holder<GameEvent> gameEvent, Context ctx) {
			return gameEvent.is(STGameEvents.ITEM_TRANSMITTABLE) && ctx.sourceEntity() instanceof ItemEntity item && item.isAlive();
		}

		@Override
		public boolean canReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> event, GameEvent.Context ctx) {
			return storedItemSignal.isEmpty() && ctx.sourceEntity() instanceof ItemEntity item && !item.blockPosition().equals(worldPosition) && super.canReceiveVibration(level, pos, event, ctx);
		}

		@Override
		public void onDataChanged() {
			super.onDataChanged();

			if (getVibrationData().getCurrentVibration() != null && getVibrationData().getCurrentVibration().entity() instanceof ItemEntity item) {
				Vec3 originVec = getVibrationData().getCurrentVibration().pos();
				BlockPos originPos = BlockPos.containing(originVec);

				if (level.getBlockEntity(originPos) instanceof BaseSculkItemTransporterBlockEntity be && be.hasStoredItemSignal() && be.getStoredItemSignal() == item.getItem()) {
					be.setItemSignal(null, 0);
					level.scheduleTick(originPos, be.getBlockState().getBlock(), 0);
					item.setPos(originVec); //set the position of the item entity to the origin of the signal as a marker, so the transmitter doesn't send the item back where it came from
					((ServerLevel) level).sendParticles(new ItemSignalParticleOption(getListener().getListenerSource(), getVibrationData().getTravelTimeInTicks(), item.getItem().copy()), originVec.x, originVec.y, originVec.z, (item.getItem().getCount() + 15) / 16 * 5, 0, 0, 0, 0);
				}
			}
		}

		@Override
		public void onReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> event, Entity entity, Entity projectileOwner, float distance) {
			super.onReceiveVibration(level, pos, event, entity, projectileOwner, distance);

			if (event.is(STGameEvents.ITEM_TRANSMITTABLE) && entity instanceof ItemEntity item)
				setItemSignal(item, VibrationSystem.getRedstoneStrengthForDistance(distance, getListenerRadius()));
		}
	}
}
