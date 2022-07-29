package sculktransporting.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.registries.ForgeRegistries;
import sculktransporting.blocks.SculkTransmitterBlock;
import sculktransporting.registration.STBlockEntityTypes;

public class SculkTransmitterBlockEntity extends BaseSculkItemTransporterBlockEntity {
	private ItemStack filteredItem = ItemStack.EMPTY;

	public SculkTransmitterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public boolean shouldPerformAction(Level level) {
		return true;
	}

	@Override
	public boolean isValidVibration(GameEvent gameEvent, Context ctx) {
		return super.isValidVibration(gameEvent, ctx) && (filteredItem.is(Items.AIR) || (!getBlockState().getValue(SculkTransmitterBlock.INVERTED) == ((ItemEntity) ctx.sourceEntity()).getItem().is(filteredItem.getItem())));
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putString("FilteredItem", ForgeRegistries.ITEMS.getKey(filteredItem.getItem()).toString());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag.getString("FilteredItem")));

		if (item == Items.AIR)
			filteredItem = ItemStack.EMPTY;
		else
			filteredItem = new ItemStack(item);
	}

	public void setFilteredItem(ItemStack stack) {
		if (stack.is(filteredItem.getItem()))
			return;

		filteredItem = new ItemStack(stack.getItem());
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		return;
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
}
