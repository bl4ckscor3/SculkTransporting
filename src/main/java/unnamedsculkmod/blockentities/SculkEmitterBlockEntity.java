package unnamedsculkmod.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import unnamedsculkmod.registration.USBlockEntityTypes;

public class SculkEmitterBlockEntity extends BaseSculkItemTransporterBlockEntity {
	private LazyOptional<IItemHandler> inventoryBelow;

	public SculkEmitterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SculkEmitterBlockEntity be) {
		BaseSculkItemTransporterBlockEntity.serverTick(level, pos, state, be);

		if (level.getGameTime() % 5 == 0 && be.inventoryBelow == null) {
			BlockEntity beBelow = level.getBlockEntity(pos.below());

			if (beBelow != null)
				be.inventoryBelow = beBelow.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
		}

		//TODO: Modify to allow for quicker item sending depending on installed upgrades -b
		if (!be.hasStoredItemSignal()) {
			be.inventoryBelow.ifPresent(itemHandler -> {
				for (int i = 0; i < itemHandler.getSlots(); i++) {
					//TODO: Modify to extract different item amounts depending on installed upgrades -b
					ItemStack extracted = itemHandler.extractItem(i, 1, false);

					if (!extracted.isEmpty()) {
						be.setItemSignal(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), extracted), 0);
						break;
					}
				}
			});
		}
	}

	public void forgetInventoryBelow() {
		inventoryBelow = null;
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
