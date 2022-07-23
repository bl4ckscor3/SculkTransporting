package sculktransporting.registration;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sculktransporting.SculkTransporting;
import sculktransporting.blockentities.SculkBarrelBlockEntity;
import sculktransporting.blockentities.SculkEmitterBlockEntity;
import sculktransporting.blockentities.SculkReceiverBlockEntity;
import sculktransporting.blockentities.SculkTransmitterBlockEntity;

public class STBlockEntityTypes {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SculkTransporting.MODID);
	public static final RegistryObject<BlockEntityType<SculkEmitterBlockEntity>> SCULK_EMITTER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sculk_emitter", () -> BlockEntityType.Builder.of(SculkEmitterBlockEntity::new, STBlocks.SCULK_EMITTER.get()).build(null));
	public static final RegistryObject<BlockEntityType<SculkReceiverBlockEntity>> SCULK_RECEIVER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sculk_receiver", () -> BlockEntityType.Builder.of(SculkReceiverBlockEntity::new, STBlocks.SCULK_RECEIVER.get()).build(null));
	public static final RegistryObject<BlockEntityType<SculkTransmitterBlockEntity>> SCULK_TRANSMITTER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sculk_transmitter", () -> BlockEntityType.Builder.of(SculkTransmitterBlockEntity::new, STBlocks.SCULK_TRANSMITTER.get()).build(null));
	public static final RegistryObject<BlockEntityType<SculkBarrelBlockEntity>> SCULK_BARREL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sculk_barrel", () -> BlockEntityType.Builder.of(SculkBarrelBlockEntity::new, STBlocks.SCULK_BARREL.get()).build(null));
}
