package sculktransporting.client;

import org.joml.Quaternionfc;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;

public class SculkItemTransporterRenderState extends BlockEntityRenderState {
	public boolean hasStoredItemSignal;
	public ItemClusterRenderState itemClusterRenderState = new ItemClusterRenderState();
	public Quaternionfc rotation;
}
