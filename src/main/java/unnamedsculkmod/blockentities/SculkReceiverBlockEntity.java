package unnamedsculkmod.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import unnamedsculkmod.registration.USBlockEntityTypes;

public class SculkReceiverBlockEntity extends SculkTransmitterBlockEntity {
	private LazyOptional<IItemHandler> inventoryBelow;

	public SculkReceiverBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SculkReceiverBlockEntity be) {
		if (level.getGameTime() % 5 == 0 && be.inventoryBelow == null) {
			BlockEntity beBelow = level.getBlockEntity(pos.below());

			if (beBelow != null)
				be.inventoryBelow = beBelow.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
		}

		//TODO: Modification needed to allow for quicker item depositing depending on installed upgrades? -b
		if (be.hasStoredItemSignal() && be.inventoryBelow != null) {
			be.inventoryBelow.ifPresent(itemHandler -> {
				ItemStack inserted = be.storedItemSignal;

				for (int i = 0; i < itemHandler.getSlots(); i++) {
					inserted = itemHandler.insertItem(i, inserted, false);

					if (inserted.isEmpty()) {
						level.scheduleTick(be.worldPosition, be.getBlockState().getBlock(), 0);
						break;
					}
				}

				be.storedItemSignal = inserted;
			});
		}

		be.getListener().tick(level);
	}

	public void forgetInventoryBelow() {
		inventoryBelow = null;
	}

	@Override
	public BlockEntityType<?> getType() {
		return USBlockEntityTypes.SCULK_RECEIVER_BLOCK_ENTITY.get();
	}
}
