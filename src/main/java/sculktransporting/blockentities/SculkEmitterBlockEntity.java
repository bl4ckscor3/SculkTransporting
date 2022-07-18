package sculktransporting.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem.SpeedTier;
import sculktransporting.registration.STBlockEntityTypes;

public class SculkEmitterBlockEntity extends BaseSculkItemTransporterBlockEntity {
	private BlockState lastKnownStateBelow;
	private LazyOptional<IItemHandler> inventoryBelow;
	private QuantityTier quantityModifier = QuantityTier.ZERO;
	private SpeedTier speedModifier = SpeedTier.ZERO;

	public SculkEmitterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SculkEmitterBlockEntity be) {
		if (level.getGameTime() % 5 == 0 && be.inventoryBelow == null) {
			BlockEntity beBelow = level.getBlockEntity(pos.below());

			if (beBelow != null)
				be.inventoryBelow = beBelow.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
			else
				be.inventoryBelow = LazyOptional.empty();
		}

		if (be.shouldPerformAction(level)) {
			if (!be.hasStoredItemSignal() && be.inventoryBelow != null) {
				//from 0 to 3 installed modifiers: 1, 4, 16, 64
				final int amountToExtract = (int) Math.pow(4, be.quantityModifier.getValue());

				be.inventoryBelow.ifPresent(itemHandler -> {
					for (int i = 0; i < itemHandler.getSlots(); i++) {
						ItemStack extracted = itemHandler.extractItem(i, amountToExtract, false);

						if (!extracted.isEmpty()) {
							be.setItemSignal(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), extracted), 0);
							break;
						}
					}
				});
			}

			BaseSculkItemTransporterBlockEntity.serverTick(level, pos, state, be);
		}
	}

	@Override
	public boolean shouldPerformAction(Level level) {
		//every tick, or only every 5, 10, 15, 20 ticks
		return speedModifier == SpeedTier.FOUR || level.getGameTime() % (20 - (speedModifier.getValue() * 5)) == 0;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		quantityModifier = QuantityTier.values()[tag.getInt("QuantityModifier")];
		speedModifier = SpeedTier.values()[tag.getInt("SpeedModifier")];
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("QuantityModifier", quantityModifier.ordinal());
		tag.putInt("SpeedModifier", speedModifier.ordinal());
	}

	public boolean setQuantityModifier(QuantityTier quantityModifier) {
		if (this.quantityModifier != QuantityTier.ZERO)
			return false;

		this.quantityModifier = quantityModifier;
		return true;
	}

	public void removeQuantityModifier() {
		if (quantityModifier == QuantityTier.ZERO)
			return;

		Block.popResource(level, worldPosition, new ItemStack(quantityModifier.getItem()));
		quantityModifier = QuantityTier.ZERO;
		return;
	}

	public QuantityTier getQuantityModifier() {
		return quantityModifier;
	}

	public boolean setSpeedModifier(SpeedTier speedModifier) {
		if (this.speedModifier != SpeedTier.ZERO)
			return false;

		this.speedModifier = speedModifier;
		return true;
	}

	public void removeSpeedModifier() {
		if (speedModifier == SpeedTier.ZERO)
			return;

		Block.popResource(level, worldPosition, new ItemStack(speedModifier.getItem()));
		speedModifier = SpeedTier.ZERO;
		return;
	}

	public SpeedTier getSpeedModifier() {
		return speedModifier;
	}

	public BlockState getLastKnownStateBelow() {
		return lastKnownStateBelow;
	}

	public void forgetInventoryBelow(BlockState stateBelow) {
		inventoryBelow = null;
		lastKnownStateBelow = stateBelow;
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
		return STBlockEntityTypes.SCULK_EMITTER_BLOCK_ENTITY.get();
	}
}
