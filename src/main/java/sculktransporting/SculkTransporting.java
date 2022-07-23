package sculktransporting;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sculktransporting.registration.STBlockEntityTypes;
import sculktransporting.registration.STBlocks;
import sculktransporting.registration.STGameEvents;
import sculktransporting.registration.STItems;
import sculktransporting.registration.STParticleTypes;

@Mod(SculkTransporting.MODID)
public class SculkTransporting {
	public static final String MODID = "sculktransporting";
	public static final CreativeModeTab TAB = new CreativeModeTab(SculkTransporting.MODID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(STBlocks.SCULK_EMITTER.get().asItem());
		}
	};

	public SculkTransporting() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		STBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
		STBlocks.BLOCKS.register(modEventBus);
		STGameEvents.GAME_EVENTS.register(modEventBus);
		STItems.ITEMS.register(modEventBus);
		STParticleTypes.PARTICLE_TYPES.register(modEventBus);
	}
}
