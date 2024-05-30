package sculktransporting.blocks;

import java.util.stream.Stream;

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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import sculktransporting.STTags;
import sculktransporting.blockentities.SculkReceiverBlockEntity;
import sculktransporting.items.SpeedModifierItem;
import sculktransporting.items.SpeedModifierItem.SpeedTier;
import sculktransporting.registration.STBlockEntityTypes;

public class SculkReceiverBlock extends BaseSculkItemTransporterBlock {
	public SculkReceiverBlock(BlockBehaviour.Properties properties) {
		super(properties);
		upShape = Stream.of(Block.box(6, 1, 6, 10, 2, 10), Block.box(7, 0, 7, 9, 1, 9), Block.box(5, 2, 5, 11, 3, 11), Block.box(0, 3, 0, 16, 8, 16), Block.box(0, 0, 0, 1, 3, 1), Block.box(0, 0, 15, 1, 3, 16), Block.box(15, 0, 15, 16, 3, 16), Block.box(15, 0, 0, 16, 3, 1)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
		downShape = Stream.of(Block.box(6, 14, 6, 10, 15, 10), Block.box(7, 15, 7, 9, 16, 9), Block.box(5, 13, 5, 11, 14, 11), Block.box(0, 8, 0, 16, 13, 16), Block.box(15, 13, 15, 16, 16, 16), Block.box(0, 13, 15, 1, 16, 16), Block.box(0, 13, 0, 1, 16, 1), Block.box(15, 13, 0, 16, 16, 1)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
		northShape = Stream.of(Block.box(6, 6, 14, 10, 10, 15), Block.box(7, 7, 15, 9, 9, 16), Block.box(5, 5, 13, 11, 11, 14), Block.box(0, 0, 8, 16, 16, 13), Block.box(0, 0, 13, 1, 1, 16), Block.box(0, 15, 13, 1, 16, 16), Block.box(15, 15, 13, 16, 16, 16), Block.box(15, 0, 13, 16, 1, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
		eastShape = Stream.of(Block.box(1, 6, 6, 2, 10, 10), Block.box(0, 7, 7, 1, 9, 9), Block.box(2, 5, 5, 3, 11, 11), Block.box(3, 0, 0, 8, 16, 16), Block.box(0, 0, 0, 3, 1, 1), Block.box(0, 15, 0, 3, 16, 1), Block.box(0, 15, 15, 3, 16, 16), Block.box(0, 0, 15, 3, 1, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
		southShape = Stream.of(Block.box(6, 6, 1, 10, 10, 2), Block.box(7, 7, 0, 9, 9, 1), Block.box(5, 5, 2, 11, 11, 3), Block.box(0, 0, 3, 16, 16, 8), Block.box(15, 0, 0, 16, 1, 3), Block.box(15, 15, 0, 16, 16, 3), Block.box(0, 15, 0, 1, 16, 3), Block.box(0, 0, 0, 1, 1, 3)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
		westShape = Stream.of(Block.box(14, 6, 6, 15, 10, 10), Block.box(15, 7, 7, 16, 9, 9), Block.box(13, 5, 5, 14, 11, 11), Block.box(8, 0, 0, 13, 16, 16), Block.box(13, 0, 15, 16, 1, 16), Block.box(13, 15, 15, 16, 16, 16), Block.box(13, 15, 0, 16, 16, 1), Block.box(13, 0, 0, 16, 1, 1)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof SculkReceiverBlockEntity be) {
			ItemStack heldStack = player.getItemInHand(hand);

			if (heldStack.is(STTags.Items.SPEED_MODIFIERS)) {
				if (!level.isClientSide && be.getSpeedTier() == SpeedTier.ZERO && be.setSpeedTier(((SpeedModifierItem) heldStack.getItem()).tier) && !player.isCreative())
					heldStack.shrink(1);

				return InteractionResult.sidedSuccess(level.isClientSide);
			}

			if (player.isShiftKeyDown()) {
				if (!level.isClientSide) {
					SpeedTier speedTier = be.getSpeedTier();

					be.removeSpeedModifier();
					player.getInventory().placeItemBackInInventory(new ItemStack(speedTier.getItem()));
				}

				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (fromPos.equals(pos.relative(state.getValue(FACING).getOpposite())) && level.getBlockEntity(pos) instanceof SculkReceiverBlockEntity be) {
			//SculkSensorBlock#updateNeighbours calls Level#updateNeighborsAt for the position below itself, calling this method again.
			//thus there's a need to check if the block below has changed, before updating the item handler
			BlockState stateBelow = level.getBlockState(fromPos);

			if (be.getLastKnownStateBelow() != stateBelow)
				be.forgetInventoryBelow(stateBelow);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof SculkReceiverBlockEntity be)
			Block.popResource(level, pos, new ItemStack(be.getSpeedTier().getItem()));

		super.onRemove(state, level, pos, newState, isMoving);
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
