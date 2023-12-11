package sculktransporting.blockentities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.IItemHandler;
import sculktransporting.STTags;
import sculktransporting.client.ClientHandler;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem.SpeedTier;
import sculktransporting.registration.STBlockEntityTypes;

public class SculkEmitterBlockEntity extends BaseSculkItemTransporterBlockEntity {
	private BlockState lastKnownStateBelow;
	private BlockCapabilityCache<IItemHandler, Direction> inventoryBelow;
	private QuantityTier quantityTier = QuantityTier.ZERO;
	private SpeedTier speedTier = SpeedTier.ZERO;

	public SculkEmitterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SculkEmitterBlockEntity be) {
		if (be.shouldPerformAction(level)) {
			if (!be.hasStoredItemSignal() && be.inventoryBelow != null && be.getLastKnownStateBelow().is(STTags.Blocks.SCULK_EMITTER_CAN_EXTRACT_FROM)) {
				IItemHandler itemHandler = be.inventoryBelow.getCapability();

				if (itemHandler != null) {
					//from 0 to 3 installed modifiers: 1, 4, 16, 64
					final int amountToExtract = (int) Math.pow(4, be.quantityTier.getValue());

					for (int i = 0; i < itemHandler.getSlots(); i++) {
						ItemStack extracted = itemHandler.extractItem(i, amountToExtract, false);

						if (!extracted.isEmpty()) {
							be.setItemSignal(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), extracted), 15);
							break;
						}
					}
				}
			}

			BaseSculkItemTransporterBlockEntity.serverTick(level, pos, state, be);
		}
	}

	@Override
	public User createVibrationUser() {
		return new SculkEmitterVibrationUser(getBlockPos());
	}

	@Override
	public boolean shouldPerformAction(Level level) {
		//every tick, or only every 5, 10, 15, 20 ticks
		return speedTier == SpeedTier.FOUR || level.getGameTime() % (20 - (speedTier.getValue() * 5)) == 0;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		quantityTier = QuantityTier.values()[tag.getInt("QuantityTier")];
		speedTier = SpeedTier.values()[tag.getInt("SpeedTier")];
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("QuantityTier", quantityTier.ordinal());
		tag.putInt("SpeedTier", speedTier.ordinal());
	}

	public boolean setQuantityTier(QuantityTier quantityTier) {
		if (this.quantityTier == quantityTier)
			return false;

		this.quantityTier = quantityTier;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		return true;
	}

	public void removeQuantityModifier() {
		if (quantityTier == QuantityTier.ZERO)
			return;

		setQuantityTier(QuantityTier.ZERO);
	}

	public QuantityTier getQuantityTier() {
		return quantityTier;
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
		if (lastKnownStateBelow == null) {
			if (level != null)
				lastKnownStateBelow = level.getBlockState(worldPosition.below());
			else
				return Blocks.AIR.defaultBlockState();
		}

		return lastKnownStateBelow;
	}

	public void setLastKnownStateBelow(BlockState lastKnownStateBelow) {
		this.lastKnownStateBelow = lastKnownStateBelow;
	}

	@Override
	public BlockEntityType<?> getType() {
		return STBlockEntityTypes.SCULK_EMITTER_BLOCK_ENTITY.get();
	}

	@Override
	public ModelData getModelData() {
		return ModelData.builder().with(ClientHandler.SPEED_TIER, speedTier).with(ClientHandler.QUANTITY_TIER, quantityTier).build();
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		requestModelDataUpdate();
		Minecraft.getInstance().levelRenderer.setBlocksDirty(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
	}

	@Override
	public void onLoad() {
		super.onLoad();

		if (level != null && !level.isClientSide)
			inventoryBelow = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, (ServerLevel) level, worldPosition.below(), Direction.UP);
	}

	public class SculkEmitterVibrationUser extends BaseVibrationUser {
		public SculkEmitterVibrationUser(BlockPos pos) {
			super(pos);
		}

		@Override
		public boolean isValidVibration(GameEvent gameEvent, GameEvent.Context ctx) {
			return false;
		}

		@Override
		public boolean canReceiveVibration(ServerLevel level, BlockPos pos, GameEvent event, GameEvent.Context ctx) {
			return false;
		}

		@Override
		public void onDataChanged() {}

		@Override
		public void onReceiveVibration(ServerLevel level, BlockPos pos, GameEvent event, Entity entity, Entity projectileOwner, float distance) {}
	}
}
