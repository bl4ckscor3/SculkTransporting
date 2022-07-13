package unnamedsculkmod.blocks;

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
import unnamedsculkmod.blockentities.SculkEmitterBlockEntity;
import unnamedsculkmod.registration.USBlockEntityTypes;
import unnamedsculkmod.registration.USItems;

public class SculkEmitterBlock extends BaseSculkItemTransporterBlock {
	public SculkEmitterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof SculkEmitterBlockEntity be) {
			ItemStack heldItem = player.getItemInHand(hand);
			boolean isQuantityModifier = heldItem.is(USItems.QUANTITY_MODIFIER.get());

			if ((isQuantityModifier || heldItem.is(USItems.SPEED_MODIFIER.get()))) {
				if (!level.isClientSide) {
					boolean modifierAdded = false;

					if (isQuantityModifier)
						modifierAdded = be.addQuantityModifier();
					else
						modifierAdded = be.addSpeedModifier();

					if (modifierAdded) {
						if (!player.isCreative())
							heldItem.shrink(1);
					}
				}

				return InteractionResult.sidedSuccess(level.isClientSide);
			}

			//TODO: properly handle removal -b
			System.out.println("quantity: " + be.getQuantityModifier());
			System.out.println("speed: " + be.getSpeedModifier());

			if (player.isShiftKeyDown()) {
				if (be.removeQuantityModifier())
					Block.popResource(level, pos, new ItemStack(USItems.QUANTITY_MODIFIER.get()));

				if (be.removeSpeedModifier())
					Block.popResource(level, pos, new ItemStack(USItems.SPEED_MODIFIER.get()));
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return InteractionResult.PASS;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (level.getBlockEntity(pos) instanceof SculkEmitterBlockEntity be)
			be.forgetInventoryBelow();
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
