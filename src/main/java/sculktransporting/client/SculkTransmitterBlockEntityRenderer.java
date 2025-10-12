package sculktransporting.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import sculktransporting.blockentities.SculkTransmitterBlockEntity;

public class SculkTransmitterBlockEntityRenderer extends SculkItemTransporterBlockEntityRenderer<SculkTransmitterBlockEntity, SculkTransmitterRenderState> {
	public SculkTransmitterBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void submit(SculkTransmitterRenderState state, PoseStack pose, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		super.submit(state, pose, submitNodeCollector, camera);

		if (!state.hasFilter) {
			pose.pushPose();
			adjustForRotation(pose, state.rotation);
			pose.translate(1.0F, 0.25F, 1.0F);
			renderItem(state, pose, -0.5F, 0.0F, -1.0F, 0.0F, submitNodeCollector);
			renderItem(state, pose, -1.0F, 0.0F, -0.5F, 90.0F, submitNodeCollector);
			renderItem(state, pose, -0.5F, 0.0F, 0.0F, 180.0F, submitNodeCollector);
			renderItem(state, pose, 0.0F, 0.0F, -0.5F, 270.0F, submitNodeCollector);
			pose.popPose();
		}
	}

	@Override
	public SculkTransmitterRenderState createRenderState() {
		return new SculkTransmitterRenderState();
	}

	@Override
	public void extractRenderState(SculkTransmitterBlockEntity be, SculkTransmitterRenderState state, float partialTick, Vec3 cameraPosition, CrumblingOverlay breakProgress) {
		ItemStack filteredItem = be.getFilteredItem();

		state.hasFilter = !filteredItem.isEmpty();

		if (state.hasFilter)
			itemModelResolver.updateForTopItem(state.filter, filteredItem, ItemDisplayContext.FIXED, be.getLevel(), null, ItemClusterRenderState.getSeedForItemStack(filteredItem));

		super.extractRenderState(be, state, partialTick, cameraPosition, breakProgress);
	}

	private void renderItem(SculkTransmitterRenderState state, PoseStack pose, float translateX, float translateY, float translateZ, float degrees, SubmitNodeCollector submitNodeCollector) {
		pose.pushPose();
		pose.translate(translateX, translateY, translateZ);
		pose.mulPose(Axis.YP.rotationDegrees(degrees));
		pose.scale(0.35F, 0.35F, 0.35F);
		state.filter.submit(pose, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
		pose.popPose();
	}
}
