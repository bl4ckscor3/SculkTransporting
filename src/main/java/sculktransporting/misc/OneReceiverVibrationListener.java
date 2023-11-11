package sculktransporting.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public class OneReceiverVibrationListener extends VibrationSystem.Listener {
	public OneReceiverVibrationListener(VibrationSystem system) {
		super(system);
	}

	@Override
	public void scheduleVibration(ServerLevel level, VibrationSystem.Data data, GameEvent gameEvent, GameEvent.Context ctx, Vec3 from, Vec3 to) {
		Optional<Pair<VibrationInfo, Long>> oldVibrationData = data.getSelectionStrategy().currentVibrationData;
		Optional<Pair<VibrationInfo, Long>> newVibrationData;

		super.scheduleVibration(level, data, gameEvent, ctx, from, to);
		newVibrationData = data.getSelectionStrategy().currentVibrationData;

		if (ctx.sourceEntity() instanceof ItemEntity item && newVibrationData.isPresent() && ctx.sourceEntity().equals(newVibrationData.get().getLeft().entity())) {
			item.discard(); //If this item signal is scheduled for one receiver, mark it as such to prevent it from getting sent to another one

			if (oldVibrationData.isPresent() && oldVibrationData.get().getLeft().entity() instanceof ItemEntity oldItem && !oldVibrationData.get().getLeft().equals(newVibrationData.get().getLeft()))
				oldItem.revive(); //If this item signal overrode a previously assigned one, mark it to be valid again
		}
	}

	@Override
	public DeliveryMode getDeliveryMode() {
		return DeliveryMode.BY_DISTANCE;
	}
}
