package sculktransporting.client;

import java.util.Random;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import sculktransporting.blockentities.BaseSculkItemTransporterBlockEntity;
import sculktransporting.blocks.BaseSculkItemTransporterBlock;

public class SculkItemTransporterBlockEntityRenderer<T extends BaseSculkItemTransporterBlockEntity> implements BlockEntityRenderer<T> {
	private static final Quaternionf XP_90 = new Quaternionf().rotateXYZ(90.0F * ((float) Math.PI / 180.0F), 0.0F, 0.0F);
	private final ItemModelResolver itemModelResolver;
	private final ItemStackRenderState itemStackRenderState = new ItemStackRenderState();

	public SculkItemTransporterBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		itemModelResolver = ctx.getItemModelResolver();
	}

	@Override
	public void render(T be, float partialTick, PoseStack pose, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
		if (be.hasStoredItemSignal()) {
			ItemStack signal = be.getStoredItemSignal();
			int seed = ItemClusterRenderState.getSeedForItemStack(signal);
			Random random = new Random(seed);
			boolean isGui3d = shouldRenderGui3d(itemStackRenderState); //TODO: isGui3d might behave differently compared to 1.21.4, check item rendering
			int renderAmount = ItemClusterRenderState.getRenderedAmount(signal.getCount());

			itemModelResolver.updateForTopItem(itemStackRenderState, signal, ItemDisplayContext.FIXED, be.getLevel(), null, seed);
			pose.pushPose();
			adjustForRotation(pose, be);

			if (isGui3d)
				pose.translate(0.5D, 0.63D, 0.5D);
			else {
				pose.translate(0.5D, 0.52D, 0.375D);
				pose.mulPose(XP_90);
			}

			pose.scale(0.5F, 0.5F, 0.5F);
			//translate the item stack so it sits on top of the block, or closer to the middle of it (for when isGui3d is true)
			pose.translate(0.0F, 0.0F, (renderAmount - 1) * -0.032F);

			for (int k = 0; k < renderAmount; ++k) {
				pose.pushPose();

				if (k > 0) {
					if (isGui3d) {
						float translateX = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
						float translateY = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
						float translateZ = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;

						pose.translate(translateX, translateY, translateZ);
					}
					else {
						float translateX = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
						float translateY = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;

						pose.translate(translateX, translateY, 0.0D);
					}
				}

				itemStackRenderState.render(pose, bufferSource, packedLight, packedOverlay);
				pose.popPose();

				if (!isGui3d)
					pose.translate(0.0, 0.0, 0.032F);
			}

			pose.popPose();
		}
	}

	protected boolean shouldRenderGui3d(ItemStackRenderState renderState) {
		AABB.Builder builder = new AABB.Builder();

		renderState.visitExtents(builder::include);
		return builder.build().getZsize() > 0.0625F;
	}

	protected void adjustForRotation(PoseStack pose, T be) {
		pose.translate(0.5D, 0.5D, 0.5D);
		pose.mulPose(be.getBlockState().getValue(BaseSculkItemTransporterBlock.FACING).getRotation());
		pose.translate(-0.5D, -0.5D, -0.5D);
	}
}
