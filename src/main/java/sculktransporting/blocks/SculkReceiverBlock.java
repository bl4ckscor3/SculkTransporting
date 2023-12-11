package sculktransporting.blocks;

import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sculktransporting.STTags;
import sculktransporting.blockentities.SculkReceiverBlockEntity;
import sculktransporting.items.SpeedModifierItem;
import sculktransporting.items.SpeedModifierItem.SpeedTier;
import sculktransporting.registration.STBlockEntityTypes;

public class SculkReceiverBlock extends BaseSculkItemTransporterBlock {
	private static final VoxelShape SHAPE = Stream.of(Block.box(0, 0, 0, 1, 3, 1), Block.box(0, 0, 15, 1, 3, 16), Block.box(15, 0, 0, 16, 3, 1), Block.box(15, 0, 15, 16, 3, 16), Block.box(6, 1, 6, 10, 2, 10), Block.box(5, 2, 5, 11, 3, 11), Block.box(7, 0, 7, 9, 1, 9), Block.box(0, 3, 0, 16, 8, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

	public SculkReceiverBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
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
