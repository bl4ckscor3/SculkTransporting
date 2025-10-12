package sculktransporting.client;

import net.minecraft.client.renderer.item.ItemStackRenderState;

public class SculkTransmitterRenderState extends SculkItemTransporterRenderState {
	public boolean hasFilter;
	public ItemStackRenderState filter = new ItemStackRenderState();
}
