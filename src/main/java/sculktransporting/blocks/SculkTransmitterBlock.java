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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import sculktransporting.blockentities.BaseSculkItemTransporterBlockEntity;
import sculktransporting.blockentities.SculkTransmitterBlockEntity;
import sculktransporting.registration.STBlockEntityTypes;

public class SculkTransmitterBlock extends BaseSculkItemTransporterBlock {
	public static final BooleanProperty INVERTED = BooleanProperty.create("inverted");

	public SculkTransmitterBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(INVERTED, false));
	}

	@Override
	public InteractionResult useItemOn(ItemStack heldStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!heldStack.isEmpty() && level.getBlockEntity(pos) instanceof SculkTransmitterBlockEntity be) {
			if (!level.isClientSide())
				be.setFilteredItem(heldStack);

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof SculkTransmitterBlockEntity be) {
			if (player.isShiftKeyDown()) {
				if (!level.isClientSide())
					be.removeFilteredItem();
			}
			else
				level.setBlockAndUpdate(pos, state.setValue(INVERTED, !state.getValue(INVERTED)));

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SculkTransmitterBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return !level.isClientSide() ? createTickerHelper(type, STBlockEntityTypes.SCULK_TRANSMITTER_BLOCK_ENTITY.get(), BaseSculkItemTransporterBlockEntity::serverTick) : null;
	}

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(INVERTED);
	}
}
