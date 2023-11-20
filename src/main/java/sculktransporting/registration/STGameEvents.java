package sculktransporting.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sculktransporting.SculkTransporting;

public class STGameEvents {
	public static final DeferredRegister<GameEvent> GAME_EVENTS = DeferredRegister.create(Registries.GAME_EVENT, SculkTransporting.MODID);
	public static final DeferredHolder<GameEvent, GameEvent> ITEM_TRANSMITTABLE = GAME_EVENTS.register("item_transmittable", () -> new GameEvent(16));

	private STGameEvents() {}
}
