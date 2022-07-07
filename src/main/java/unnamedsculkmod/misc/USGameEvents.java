package unnamedsculkmod.misc;

import net.minecraft.core.Registry;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import unnamedsculkmod.UnnamedSculkMod;

public class USGameEvents {
	public static final DeferredRegister<GameEvent> GAME_EVENTS = DeferredRegister.create(Registry.GAME_EVENT_REGISTRY, UnnamedSculkMod.MODID);

	public static final RegistryObject<GameEvent> ITEM_TRANSMITTABLE = GAME_EVENTS.register("item_transmittable", () -> new GameEvent("item_transmittable", 16));
}
