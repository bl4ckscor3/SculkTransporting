package sculktransporting.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import sculktransporting.SculkTransporting;
import sculktransporting.blockentities.SculkBarrelBlockEntity;
import sculktransporting.blockentities.SculkEmitterBlockEntity;
import sculktransporting.blockentities.SculkReceiverBlockEntity;
import sculktransporting.blockentities.SculkTransmitterBlockEntity;

@EventBusSubscriber(modid = SculkTransporting.MODID)
public class STBlockEntityTypes {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SculkTransporting.MODID);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SculkEmitterBlockEntity>> SCULK_EMITTER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sculk_emitter", () -> new BlockEntityType<>(SculkEmitterBlockEntity::new, STBlocks.SCULK_EMITTER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SculkReceiverBlockEntity>> SCULK_RECEIVER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sculk_receiver", () -> new BlockEntityType<>(SculkReceiverBlockEntity::new, STBlocks.SCULK_RECEIVER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SculkTransmitterBlockEntity>> SCULK_TRANSMITTER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sculk_transmitter", () -> new BlockEntityType<>(SculkTransmitterBlockEntity::new, STBlocks.SCULK_TRANSMITTER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SculkBarrelBlockEntity>> SCULK_BARREL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sculk_barrel", () -> new BlockEntityType<>(SculkBarrelBlockEntity::new, STBlocks.SCULK_BARREL.get()));

	private STBlockEntityTypes() {}

	@SubscribeEvent
	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.Item.BLOCK, SCULK_BARREL_BLOCK_ENTITY.get(), (container, side) -> VanillaContainerWrapper.of(container));
	}
}
