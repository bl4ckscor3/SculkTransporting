package sculktransporting.blockentities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.model.data.ModelData;
import sculktransporting.STTags;
import sculktransporting.blocks.BaseSculkItemTransporterBlock;
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
		if (!be.hasStoredItemSignal() && be.inventoryBelow != null && be.canExtractFromBelow()) {
			IItemHandler itemHandler = be.inventoryBelow.getCapability();

			if (itemHandler != null) {
				final int amountToExtract = be.getAmountToExtract();

				for (int i = 0; i < itemHandler.getSlots(); i++) {
					ItemStack extracted = itemHandler.extractItem(i, amountToExtract, false);

					if (!extracted.isEmpty()) {
						be.setItemSignal(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), extracted), 15);
						break;
					}
				}
			}
		}

		if (be.shouldPerformAction(level))
			BaseSculkItemTransporterBlockEntity.serverTick(level, pos, state, be);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		Block.popResource(level, pos, new ItemStack(getSpeedTier().getItem()));
		Block.popResource(level, pos, new ItemStack(getQuantityTier().getItem()));
		super.preRemoveSideEffects(pos, state);
	}

	public int getAmountToExtract() {
		//from 0 to 3 installed modifiers: 1, 4, 16, 64
		return quantityTier == QuantityTier.THREE ? Item.ABSOLUTE_MAX_STACK_SIZE : (int) Math.pow(4, quantityTier.getValue());
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
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);
		quantityTier = QuantityTier.values()[tag.getIntOr("QuantityTier", 0)];
		speedTier = SpeedTier.values()[tag.getIntOr("SpeedTier", 0)];
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
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
				lastKnownStateBelow = level.getBlockState(worldPosition.relative(getBlockState().getValue(BaseSculkItemTransporterBlock.FACING).getOpposite()));
			else
				return Blocks.AIR.defaultBlockState();
		}

		return lastKnownStateBelow;
	}

	public void setLastKnownStateBelow(BlockState lastKnownStateBelow) {
		this.lastKnownStateBelow = lastKnownStateBelow;
	}

	public boolean canExtractFromBelow() {
		return getLastKnownStateBelow().is(STTags.Blocks.SCULK_EMITTER_CAN_EXTRACT_FROM);
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
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
		super.onDataPacket(net, pkt, lookupProvider);
		requestModelDataUpdate();
		Minecraft.getInstance().levelRenderer.setBlocksDirty(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
	}

	@Override
	public void onLoad() {
		super.onLoad();

		if (level != null && !level.isClientSide) {
			Direction direction = getBlockState().getValue(BaseSculkItemTransporterBlock.FACING);

			inventoryBelow = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, (ServerLevel) level, worldPosition.relative(direction.getOpposite()), direction);
		}
	}

	public class SculkEmitterVibrationUser extends BaseVibrationUser {
		public SculkEmitterVibrationUser(BlockPos pos) {
			super(pos);
		}

		@Override
		public boolean isValidVibration(Holder<GameEvent> gameEvent, GameEvent.Context ctx) {
			return false;
		}

		@Override
		public boolean canReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> event, GameEvent.Context ctx) {
			return false;
		}

		@Override
		public void onDataChanged() {}

		@Override
		public void onReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> event, Entity entity, Entity projectileOwner, float distance) {}
	}
}
