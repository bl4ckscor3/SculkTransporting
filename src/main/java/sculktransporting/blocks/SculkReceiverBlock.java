package sculktransporting.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import sculktransporting.STTags;
import sculktransporting.blockentities.SculkReceiverBlockEntity;
import sculktransporting.items.SpeedModifierItem;
import sculktransporting.registration.STBlockEntityTypes;

public class SculkReceiverBlock extends BaseSculkItemTransporterBlock {
	public SculkReceiverBlock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof SculkReceiverBlockEntity be) {
			ItemStack heldStack = player.getItemInHand(hand);

			if (heldStack.is(STTags.Items.SPEED_MODIFIERS)) {
				if (!level.isClientSide && be.setSpeedModifier(((SpeedModifierItem) heldStack.getItem()).tier)) {
					if (!player.isCreative())
						heldStack.shrink(1);
				}

				return InteractionResult.sidedSuccess(level.isClientSide);
			}

			System.out.println("speed: " + be.getSpeedModifier());

			if (!level.isClientSide && player.isShiftKeyDown())
				be.removeSpeedModifier();

			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return InteractionResult.PASS;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (fromPos.getY() == pos.getY() - 1 && level.getBlockEntity(pos) instanceof SculkReceiverBlockEntity be) {
			//SculkSensorBlock#updateNeighbours calls Level#updateNeighborsAt for the position below itself, calling this method again.
			//thus there's a need to check if the block below has changed, before updating the item handler
			BlockState stateBelow = level.getBlockState(fromPos);

			if (be.getLastKnownStateBelow() != stateBelow)
				be.forgetInventoryBelow(stateBelow);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SculkReceiverBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return !level.isClientSide ? createTickerHelper(type, STBlockEntityTypes.SCULK_RECEIVER_BLOCK_ENTITY.get(), SculkReceiverBlockEntity::serverTick) : null;
	}
}
