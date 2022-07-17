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
import sculktransporting.USTags;
import sculktransporting.blockentities.SculkEmitterBlockEntity;
import sculktransporting.items.QuantityModifierItem;
import sculktransporting.items.SpeedModifierItem;
import sculktransporting.registration.USBlockEntityTypes;

public class SculkEmitterBlock extends BaseSculkItemTransporterBlock {
	public SculkEmitterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof SculkEmitterBlockEntity be) {
			ItemStack heldStack = player.getItemInHand(hand);
			boolean isQuantityModifier = heldStack.is(USTags.Items.QUANTITY_MODIFIERS);

			if ((isQuantityModifier || heldStack.is(USTags.Items.SPEED_MODIFIERS))) {
				if (!level.isClientSide) {
					boolean modifierAdded = false;

					if (isQuantityModifier)
						modifierAdded = be.setQuantityModifier(((QuantityModifierItem) heldStack.getItem()).tier);
					else
						modifierAdded = be.setSpeedModifier(((SpeedModifierItem) heldStack.getItem()).tier);

					if (modifierAdded) {
						if (!player.isCreative())
							heldStack.shrink(1);
					}
				}

				return InteractionResult.sidedSuccess(level.isClientSide);
			}

			System.out.println("quantity: " + be.getQuantityModifier());
			System.out.println("speed: " + be.getSpeedModifier());

			if (player.isShiftKeyDown()) {
				//TODO: properly handle removal -b
				be.removeQuantityModifier();
				be.removeSpeedModifier();
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return InteractionResult.PASS;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (fromPos.getY() == pos.getY() - 1 && level.getBlockEntity(pos) instanceof SculkEmitterBlockEntity be) {
			//SculkSensorBlock#updateNeighbours calls Level#updateNeighborsAt for the position below itself, calling this method again.
			//thus there's a need to check if the block below has changed, before updating the item handler
			BlockState stateBelow = level.getBlockState(fromPos);

			if (be.getLastKnownStateBelow() != stateBelow)
				be.forgetInventoryBelow(stateBelow);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SculkEmitterBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return !level.isClientSide ? createTickerHelper(type, USBlockEntityTypes.SCULK_EMITTER_BLOCK_ENTITY.get(), SculkEmitterBlockEntity::serverTick) : null;
	}
}
