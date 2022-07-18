package sculktransporting.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import sculktransporting.blocks.BaseSculkItemTransporterBlock;
import sculktransporting.registration.STGameEvents;

public abstract class BaseSculkItemTransporterBlockEntity extends SculkSensorBlockEntity {
	protected ItemStack storedItemSignal = ItemStack.EMPTY;
	protected BlockPos signalOrigin;
	protected ItemEntity cachedItemEntity;

	public BaseSculkItemTransporterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, BaseSculkItemTransporterBlockEntity be) {
		be.getListener().tick(level);

		if (!be.storedItemSignal.isEmpty()) {
			if (be.cachedItemEntity == null)
				be.cachedItemEntity = new ItemEntity(level, be.signalOrigin.getX(), be.signalOrigin.getY(), be.signalOrigin.getZ(), be.storedItemSignal);

			if (be.shouldPerformAction(level))
				level.gameEvent(be.cachedItemEntity, STGameEvents.ITEM_TRANSMITTABLE.get(), pos);
		}
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
		}
		else {
			if (storedItemSignal.isEmpty())
				BaseSculkItemTransporterBlock.activate(itemSignal, level, worldPosition, getBlockState(), power);

			storedItemSignal = itemSignal.getItem().copy();
			signalOrigin = itemSignal.blockPosition();
		}

		cachedItemEntity = null;
	}

	@Override
	public abstract BlockEntityType<?> getType();
}
