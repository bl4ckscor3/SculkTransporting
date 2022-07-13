package unnamedsculkmod.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import unnamedsculkmod.blocks.BaseSculkItemTransporterBlock;
import unnamedsculkmod.registration.USBlockEntityTypes;

public class SculkReceiverBlockEntity extends SculkTransmitterBlockEntity {
	private LazyOptional<IItemHandler> inventoryBelow;
	private int speedModifier = 0;

	public SculkReceiverBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SculkReceiverBlockEntity be) {
		be.getListener().tick(level);

		if (level.getGameTime() % 5 == 0 && be.inventoryBelow == null) {
			BlockEntity beBelow = level.getBlockEntity(pos.below());

			if (beBelow != null)
				be.inventoryBelow = beBelow.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
		}

		//every tick, or only every 5, 10, 15, 20 ticks depending on 4, 3, 2, 1, or no modifiers installed
		if (be.shouldPerformAction(level)) {
			if (be.hasStoredItemSignal() && be.inventoryBelow != null) {
				be.inventoryBelow.ifPresent(itemHandler -> {
					ItemStack inserted = be.storedItemSignal;

					for (int i = 0; i < itemHandler.getSlots(); i++) {
						inserted = itemHandler.insertItem(i, inserted, false);

						if (inserted.isEmpty()) {
							BaseSculkItemTransporterBlock.deactivate(level, be.worldPosition, be.getBlockState());
							break;
						}
					}

					be.storedItemSignal = inserted;
				});
			}
		}
	}

	@Override
	public boolean shouldPerformAction(Level level) {
		//every tick, or only every 5, 10, 15, 20 ticks
		return speedModifier == 4 || level.getGameTime() % (20 - (speedModifier * 5)) == 0;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		speedModifier = tag.getInt("SpeedModifier");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("SpeedModifier", speedModifier);
	}

	public boolean addSpeedModifier() {
		if (speedModifier == 4)
			return false;

		speedModifier++;
		return true;
	}

	public boolean removeSpeedModifier() {
		if (speedModifier == 0)
			return false;

		speedModifier--;
		return true;
	}

	public int getSpeedModifier() {
		return speedModifier;
	}

	public void forgetInventoryBelow() {
		inventoryBelow = null;
	}

	@Override
	public BlockEntityType<?> getType() {
		return USBlockEntityTypes.SCULK_RECEIVER_BLOCK_ENTITY.get();
	}
}
