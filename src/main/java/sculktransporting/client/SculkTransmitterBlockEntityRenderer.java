package sculktransporting.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import sculktransporting.blockentities.SculkTransmitterBlockEntity;

public class SculkTransmitterBlockEntityRenderer extends SculkItemTransporterBlockEntityRenderer<SculkTransmitterBlockEntity> {
	public SculkTransmitterBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(SculkTransmitterBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		super.render(be, partialTick, pose, bufferSource, packedLight, packedOverlay);

		ItemStack filteredItem = be.getFilteredItem();

		if (!filteredItem.isEmpty()) {
			pose.pushPose();
			pose.translate(1.0F, 0.25F, 1.0F);
			renderItem(Direction.NORTH, be, pose, -0.5F, 0.0F, -1.0F, 0.0F, filteredItem, bufferSource, packedOverlay);
			renderItem(Direction.WEST, be, pose, -1.0F, 0.0F, -0.5F, 90.0F, filteredItem, bufferSource, packedOverlay);
			renderItem(Direction.SOUTH, be, pose, -0.5F, 0.0F, 0.0F, 180.0F, filteredItem, bufferSource, packedOverlay);
			renderItem(Direction.EAST, be, pose, 0.0F, 0.0F, -0.5F, 270.0F, filteredItem, bufferSource, packedOverlay);
			pose.popPose();
		}
	}

	private void renderItem(Direction direction, BlockEntity be, PoseStack pose, float translateX, float translateY, float translateZ, float degrees, ItemStack filteredItem, MultiBufferSource bufferSource, int packedOverlay) {
		pose.pushPose();
		pose.translate(translateX, translateY, translateZ);
		pose.mulPose(Vector3f.YP.rotationDegrees(degrees));
		pose.scale(0.35F, 0.35F, 0.35F);
		Minecraft.getInstance().getItemRenderer().renderStatic(filteredItem, TransformType.FIXED, LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(direction)), packedOverlay, pose, bufferSource, 0);
		pose.popPose();
	}
}
