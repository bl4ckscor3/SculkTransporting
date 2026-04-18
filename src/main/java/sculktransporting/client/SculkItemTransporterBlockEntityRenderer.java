package sculktransporting.client;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import sculktransporting.blockentities.BaseSculkItemTransporterBlockEntity;
import sculktransporting.blocks.BaseSculkItemTransporterBlock;

public class SculkItemTransporterBlockEntityRenderer<T extends BaseSculkItemTransporterBlockEntity, S extends SculkItemTransporterRenderState> implements BlockEntityRenderer<T, S> {
	private static final Quaternionf XP_90 = new Quaternionf().rotateXYZ(90.0F * ((float) Math.PI / 180.0F), 0.0F, 0.0F);
	protected final ItemModelResolver itemModelResolver;
	private final RandomSource random = RandomSource.create();

	public SculkItemTransporterBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		itemModelResolver = ctx.itemModelResolver();
	}

	@Override
	public void submit(S state, PoseStack pose, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		if (state.hasStoredItemSignal) {
			pose.pushPose();
			adjustForRotation(pose, state.rotation);

			if (state.itemClusterRenderState.item.getModelBoundingBox().getZsize() <= 0.0625F) {
				pose.translate(0.5D, 0.52D, 0.375D);
				pose.mulPose(XP_90);
			}
			else
				pose.translate(0.5D, 0.63D, 0.5D);

			pose.scale(0.5F, 0.5F, 0.5F);
			ItemEntityRenderer.renderMultipleFromCount(pose, submitNodeCollector, state.lightCoords, state.itemClusterRenderState, random);
			pose.popPose();
		}
	}

	@Override
	public S createRenderState() {
		return (S) new SculkItemTransporterRenderState();
	}

	@Override
	public void extractRenderState(T be, S state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
		state.hasStoredItemSignal = be.hasStoredItemSignal();

		if (state.hasStoredItemSignal) {
			ItemStack signal = be.getStoredItemSignal();

			itemModelResolver.updateForTopItem(state.itemClusterRenderState.item, signal, ItemDisplayContext.FIXED, be.getLevel(), null, ItemClusterRenderState.getSeedForItemStack(signal));
			state.itemClusterRenderState.count = ItemClusterRenderState.getRenderedAmount(signal.getCount());
			state.itemClusterRenderState.seed = ItemClusterRenderState.getSeedForItemStack(signal);
		}

		state.rotation = be.getBlockState().getValue(BaseSculkItemTransporterBlock.FACING).getRotation();
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPosition, breakProgress);
	}

	protected void adjustForRotation(PoseStack pose, Quaternionfc rotation) {
		pose.translate(0.5D, 0.5D, 0.5D);
		pose.mulPose(rotation);
		pose.translate(-0.5D, -0.5D, -0.5D);
	}
}
