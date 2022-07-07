package unnamedsculkmod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import unnamedsculkmod.misc.USGameEvents;

public class SculkEncoderBlock extends SculkTransmitterBlock {
	public SculkEncoderBlock(Properties properties) {
		super(properties);
	}

	@Override //TODO Remove, only for initial testing -R
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		level.gameEvent(null, USGameEvents.ITEM_TRANSMITTABLE.get(), pos);
		return InteractionResult.SUCCESS;
	}
}
