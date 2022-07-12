package unnamedsculkmod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import unnamedsculkmod.blockentities.BaseSculkItemTransporterBlockEntity;
import unnamedsculkmod.blockentities.SculkEmitterBlockEntity;
import unnamedsculkmod.registration.USBlockEntityTypes;

public class SculkEmitterBlock extends BaseSculkItemTransporterBlock {
	public SculkEmitterBlock(Properties properties) {
		super(properties);
	}

	@Override //TODO Remove, only for initial testing -R
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof SculkEmitterBlockEntity be)
			be.setItemSignal(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.DIAMOND, 5)), 0);

		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SculkEmitterBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return !level.isClientSide ? createTickerHelper(type, USBlockEntityTypes.SCULK_EMITTER_BLOCK_ENTITY.get(), BaseSculkItemTransporterBlockEntity::tick) : null;
	}
}
