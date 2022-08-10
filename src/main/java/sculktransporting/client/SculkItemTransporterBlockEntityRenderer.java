package sculktransporting.client;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sculktransporting.blockentities.BaseSculkItemTransporterBlockEntity;

public class SculkItemTransporterBlockEntityRenderer<T extends BaseSculkItemTransporterBlockEntity> implements BlockEntityRenderer<T> {
	private static final Quaternion XP_90 = new Quaternion(90.0F, 0.0F, 0.0F, true);

	public SculkItemTransporterBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(T be, float partialTick, PoseStack pose, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (be.hasStoredItemSignal()) {
			ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
			ItemStack signal = be.getStoredItemSignal();
			Random random = new Random(Item.getId(signal.getItem()));
			BakedModel itemModel = itemRenderer.getModel(signal, be.getLevel(), null, 0);
			boolean isGui3d = itemModel.isGui3d();
			int renderAmount = 1;

			if (signal.getCount() > 48)
				renderAmount = 5;
			else if (signal.getCount() > 32)
				renderAmount = 4;
			else if (signal.getCount() > 16)
				renderAmount = 3;
			else if (signal.getCount() > 1)
				renderAmount = 2;

			pose.pushPose();

			if (signal.getItem() instanceof BlockItem)
				pose.translate(0.5D, 0.44D, 0.5D);
			else {
				pose.translate(0.5D, 0.52D, 0.375D);
				pose.mulPose(XP_90);
			}

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

				itemRenderer.render(signal, TransformType.GROUND, false, pose, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, itemModel);
				pose.popPose();

				if (!isGui3d)
					pose.translate(0.0, 0.0, 0.032F);
			}

			pose.popPose();
		}
	}
}
