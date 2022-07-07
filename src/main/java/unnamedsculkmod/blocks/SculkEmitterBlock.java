package unnamedsculkmod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import unnamedsculkmod.blockentities.SculkTransmitterBlockEntity;

public class SculkEmitterBlock extends SculkTransmitterBlock {
	public SculkEmitterBlock(Properties properties) {
		super(properties);
	}

	@Override //TODO Remove, only for initial testing -R
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof SculkTransmitterBlockEntity be)
			be.setItemSignal(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.DIAMOND, 5)), 0);

		return InteractionResult.SUCCESS;
	}
}
