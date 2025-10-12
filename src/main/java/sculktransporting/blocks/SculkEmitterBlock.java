package sculktransporting.blocks;

import org.joml.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import sculktransporting.STTags;
import sculktransporting.blockentities.SculkEmitterBlockEntity;
import sculktransporting.items.ModifierTier;
import sculktransporting.items.QuantityModifierItem;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem;
import sculktransporting.items.SpeedModifierItem.SpeedTier;
import sculktransporting.registration.STBlockEntityTypes;

public class SculkEmitterBlock extends BaseSculkItemTransporterBlock {
	public SculkEmitterBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useItemOn(ItemStack heldStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof SculkEmitterBlockEntity be) {
			boolean isQuantityModifier = heldStack.is(STTags.Items.QUANTITY_MODIFIERS);

			if ((isQuantityModifier || heldStack.is(STTags.Items.SPEED_MODIFIERS))) {
				if (!level.isClientSide()) {
					boolean modifierAdded = false;

					if (isQuantityModifier && be.getQuantityTier() == QuantityTier.ZERO)
						modifierAdded = be.setQuantityTier(((QuantityModifierItem) heldStack.getItem()).tier);
					if (!isQuantityModifier && be.getSpeedTier() == SpeedTier.ZERO)
						modifierAdded = be.setSpeedTier(((SpeedModifierItem) heldStack.getItem()).tier);

					if (modifierAdded && !player.isCreative())
						heldStack.shrink(1);
				}

				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof SculkEmitterBlockEntity be && player.isShiftKeyDown()) {
			Direction clickedFace = hit.getDirection();
			Direction emitterFacing = state.getValue(FACING);

			if (!clickedFace.getAxis().test(emitterFacing)) {
				if (!level.isClientSide()) {
					//Rotate hit vector to mimic up-facing emitter
					Vector3f rotatedHitVec = hit.getLocation().subtract(pos.getCenter()).toVector3f().rotate(emitterFacing.getRotation().invert());
					float hitCheck;

					if (rotatedHitVec.x == 0.5F) //East block face
						hitCheck = rotatedHitVec.z;
					else if (rotatedHitVec.x == -0.5F) //West block face
						hitCheck = -rotatedHitVec.z;
					else if (rotatedHitVec.z == 0.5F) //South block face
						hitCheck = -rotatedHitVec.x;
					else
						hitCheck = rotatedHitVec.x;

					removeModifer(player, be, hitCheck < 0.0F);
				}

				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	private void removeModifer(Player player, SculkEmitterBlockEntity be, boolean removeQuantityModifier) {
		ModifierTier modifierTier;

		if (removeQuantityModifier) {
			modifierTier = be.getQuantityTier();
			be.removeQuantityModifier();
		}
		else {
			modifierTier = be.getSpeedTier();
			be.removeSpeedModifier();
		}

		player.getInventory().placeItemBackInInventory(new ItemStack(modifierTier.getItem()));
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos fromPos, BlockState fromState, RandomSource random) {
		if (fromPos.equals(pos.relative(state.getValue(FACING).getOpposite())) && level.getBlockEntity(pos) instanceof SculkEmitterBlockEntity be) {
			BlockState stateBelow = level.getBlockState(fromPos);

			if (be.getLastKnownStateBelow() != stateBelow)
				be.setLastKnownStateBelow(stateBelow);
		}

		return super.updateShape(state, level, tickAccess, pos, direction, fromPos, fromState, random);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean stillInside) {
		if (entity instanceof ItemEntity item && level.getBlockEntity(pos) instanceof SculkEmitterBlockEntity be && !be.hasStoredItemSignal() && !be.canExtractFromBelow()) {
			ItemStack extracted = item.getItem().split(be.getAmountToExtract());

			be.setItemSignal(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), extracted), 15);

			if (item.getItem().isEmpty() && level instanceof ServerLevel serverLevel)
				item.kill(serverLevel);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SculkEmitterBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return !level.isClientSide() ? createTickerHelper(type, STBlockEntityTypes.SCULK_EMITTER_BLOCK_ENTITY.get(), SculkEmitterBlockEntity::serverTick) : null;
	}
}
