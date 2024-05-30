package sculktransporting.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import sculktransporting.blockentities.BaseSculkItemTransporterBlockEntity;
import sculktransporting.misc.OneReceiverVibrationListener;
import sculktransporting.registration.STGameEvents;

/**
 * Resolves inconsistencies in sculk receiver priorities when they are an equal distance from an emitter by also considering
 * when they were placed down (prioritizing older sculk receivers) when sorting all potential receivers
 */
@Mixin(GameEvent.ListenerInfo.class)
public class GameEventListenerInfoMixin {
	@Shadow
	@Final
	private GameEventListener recipient;
	@Shadow
	@Final
	private GameEvent gameEvent;

	@Redirect(method = "compareTo", at = @At(value = "INVOKE", target = "Ljava/lang/Double;compare(DD)I"))
	public int sculktransporting$compareListeners(double thisDistance, double otherDistance, GameEvent.ListenerInfo other) {
		if (gameEvent == STGameEvents.ITEM_TRANSMITTABLE.get() && thisDistance == otherDistance) {
			if (recipient instanceof OneReceiverVibrationListener thisListener && other.recipient() instanceof OneReceiverVibrationListener otherListener) {
				if (thisListener.getSystem() instanceof BaseSculkItemTransporterBlockEntity thisBe && otherListener.getSystem() instanceof BaseSculkItemTransporterBlockEntity otherBe)
					return Long.compare(thisBe.getPlacedDownTick(), otherBe.getPlacedDownTick());
			}
		}

		return Double.compare(thisDistance, otherDistance);
	}
}
