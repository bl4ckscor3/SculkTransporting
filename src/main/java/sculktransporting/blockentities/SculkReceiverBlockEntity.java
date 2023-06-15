package sculktransporting.blockentities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import sculktransporting.blocks.BaseSculkItemTransporterBlock;
import sculktransporting.client.ClientHandler;
import sculktransporting.items.SpeedModifierItem.SpeedTier;
import sculktransporting.registration.STBlockEntityTypes;

public class SculkReceiverBlockEntity extends BaseSculkItemTransporterBlockEntity {
	private BlockState lastKnownStateBelow;
	private LazyOptional<IItemHandler> inventoryBelow;
	private SpeedTier speedTier = SpeedTier.ZERO;

	public SculkReceiverBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SculkReceiverBlockEntity be) {
		VibrationSystem.Ticker.tick(level, be.getVibrationData(), be.getVibrationUser());

		if (level.getGameTime() % 5 == 0 && be.inventoryBelow == null) {
			BlockEntity beBelow = level.getBlockEntity(pos.below());

			if (beBelow != null)
				be.inventoryBelow = beBelow.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
			else
				be.inventoryBelow = LazyOptional.empty();
		}

		if (be.shouldPerformAction(level)) {
			if (be.hasStoredItemSignal() && be.inventoryBelow != null) {
				be.inventoryBelow.ifPresent(itemHandler -> {
					ItemStack inserted = be.storedItemSignal;

					for (int i = 0; i < itemHandler.getSlots(); i++) {
						inserted = itemHandler.insertItem(i, inserted, false);

						if (inserted.isEmpty()) {
							be.storedItemSignal = ItemStack.EMPTY;
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
		return speedTier == SpeedTier.FOUR || level.getGameTime() % (20 - (speedTier.getValue() * 5)) == 0;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		speedTier = SpeedTier.values()[tag.getInt("SpeedTier")];
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("SpeedTier", speedTier.ordinal());
	}

	public boolean setSpeedTier(SpeedTier speedTier) {
		if (this.speedTier == speedTier)
			return false;

		this.speedTier = speedTier;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		return true;
	}

	public void removeSpeedModifier() {
		if (speedTier == SpeedTier.ZERO)
			return;

		setSpeedTier(SpeedTier.ZERO);
	}

	public SpeedTier getSpeedTier() {
		return speedTier;
	}

	public BlockState getLastKnownStateBelow() {
		return lastKnownStateBelow;
	}

	public void forgetInventoryBelow(BlockState stateBelow) {
		inventoryBelow = null;
		lastKnownStateBelow = stateBelow;
	}

	@Override
	public BlockEntityType<?> getType() {
		return STBlockEntityTypes.SCULK_RECEIVER_BLOCK_ENTITY.get();
	}

	@Override
	public ModelData getModelData() {
		return ModelData.builder().with(ClientHandler.SPEED_TIER, speedTier).build();
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		requestModelDataUpdate();
		Minecraft.getInstance().levelRenderer.setBlocksDirty(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
	}
}
