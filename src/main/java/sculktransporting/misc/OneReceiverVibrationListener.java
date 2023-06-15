package sculktransporting.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;

public class OneReceiverVibrationListener extends VibrationSystem.Listener {
	public OneReceiverVibrationListener(VibrationSystem system) {
		super(system);
	}

	@Override
	public void scheduleVibration(ServerLevel level, VibrationSystem.Data data, GameEvent gameEvent, GameEvent.Context ctx, Vec3 from, Vec3 to) {
		super.scheduleVibration(level, data, gameEvent, ctx, from, to);

		if (ctx.sourceEntity() instanceof ItemEntity item && system.getVibrationData().getSelectionStrategy().currentVibrationData.isPresent() && ctx.sourceEntity().equals(system.getVibrationData().getSelectionStrategy().currentVibrationData.get().getLeft().entity()))
			item.discard(); //If this item signal is scheduled for one receiver, mark it as such to prevent it from getting sent to another one
	}

	@Override
	public DeliveryMode getDeliveryMode() {
		return DeliveryMode.BY_DISTANCE;
	}
}
