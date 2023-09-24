package sculktransporting.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import sculktransporting.SculkTransporting;

public class STGameEvents {
	public static final DeferredRegister<GameEvent> GAME_EVENTS = DeferredRegister.create(Registries.GAME_EVENT, SculkTransporting.MODID);
	public static final RegistryObject<GameEvent> ITEM_TRANSMITTABLE = GAME_EVENTS.register("item_transmittable", () -> new GameEvent("item_transmittable", 16));

	private STGameEvents() {}
}
