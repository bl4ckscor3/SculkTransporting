package sculktransporting.misc;

import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSelector.VibrationEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;

public class OneReceiverVibrationListener extends VibrationSystem.Listener {
	public OneReceiverVibrationListener(VibrationSystem system) {
		super(system);
	}

	@Override
	public void scheduleVibration(ServerLevel level, VibrationSystem.Data data, Holder<GameEvent> gameEvent, GameEvent.Context ctx, Vec3 from, Vec3 to) {
		Optional<VibrationEvent> oldVibrationData = data.getSelectionStrategy().currentVibrationData;
		Optional<VibrationEvent> newVibrationData;

		super.scheduleVibration(level, data, gameEvent, ctx, from, to);
		newVibrationData = data.getSelectionStrategy().currentVibrationData;

		//Special case: If the old vibration was read from data, it will always have a null entity, and such a broken vibration should always be overridable by one from the same origin but with a provided entity
		if (oldVibrationData.isPresent()) {
			VibrationInfo oldVibrationInfo = oldVibrationData.get().event();

			if (oldVibrationInfo.distance() == (float) from.distanceTo(to) && oldVibrationInfo.entity() == null && ctx.sourceEntity() != null) {
				data.getSelectionStrategy().currentVibrationData = Optional.of(new VibrationEvent(new VibrationInfo(oldVibrationInfo.gameEvent(), oldVibrationInfo.distance(), oldVibrationInfo.pos(), ctx.sourceEntity()), level.getGameTime()));
				newVibrationData = data.getSelectionStrategy().currentVibrationData;
			}
		}

		if (ctx.sourceEntity() instanceof ItemEntity item && newVibrationData.isPresent() && item.equals(newVibrationData.get().event().entity())) {
			item.discard(); //If this item signal is scheduled for one receiver, mark it as such to prevent it from getting sent to another one

			if (oldVibrationData.isPresent() && oldVibrationData.get().event().entity() instanceof ItemEntity oldItem && !oldVibrationData.get().event().equals(newVibrationData.get().event()))
				oldItem.revive(); //If this item signal overrode a previously assigned one, mark it to be valid again
		}
	}

	@Override
	public DeliveryMode getDeliveryMode() {
		return DeliveryMode.BY_DISTANCE;
	}

	public VibrationSystem getSystem() {
		return system;
	}
}
