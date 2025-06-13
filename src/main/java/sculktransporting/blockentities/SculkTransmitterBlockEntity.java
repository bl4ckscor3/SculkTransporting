package sculktransporting.blockentities;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import sculktransporting.blocks.SculkTransmitterBlock;
import sculktransporting.registration.STBlockEntityTypes;

public class SculkTransmitterBlockEntity extends BaseSculkItemTransporterBlockEntity {
	private ItemStack filteredItem = ItemStack.EMPTY;

	public SculkTransmitterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public User createVibrationUser() {
		return new SculkTransmitterVibrationUser(getBlockPos());
	}

	@Override
	public boolean shouldPerformAction(Level level) {
		return true;
	}

	@Override
	protected void saveAdditional(ValueOutput tag) {
		super.saveAdditional(tag);
		tag.putString("FilteredItem", BuiltInRegistries.ITEM.getKey(filteredItem.getItem()).toString());
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);

		Optional<Reference<Item>> item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(tag.getStringOr("FilteredItem", "air")));

		filteredItem = item.map(i -> new ItemStack(i.getDelegate().value())).orElse(ItemStack.EMPTY);
	}

	public void setFilteredItem(ItemStack stack) {
		if (stack.is(filteredItem.getItem()))
			return;

		filteredItem = new ItemStack(stack.getItem());
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	public void removeFilteredItem() {
		if (filteredItem.is(Items.AIR))
			return;

		setFilteredItem(ItemStack.EMPTY);
	}

	public ItemStack getFilteredItem() {
		return filteredItem;
	}

	@Override
	public BlockEntityType<?> getType() {
		return STBlockEntityTypes.SCULK_TRANSMITTER_BLOCK_ENTITY.get();
	}

	public class SculkTransmitterVibrationUser extends BaseVibrationUser {
		public SculkTransmitterVibrationUser(BlockPos pos) {
			super(pos);
		}

		@Override
		public boolean isValidVibration(Holder<GameEvent> gameEvent, Context ctx) {
			return super.isValidVibration(gameEvent, ctx) && (filteredItem.is(Items.AIR) || (getBlockState().getValue(SculkTransmitterBlock.INVERTED) ^ ((ItemEntity) ctx.sourceEntity()).getItem().is(filteredItem.getItem())));
		}
	}
}
