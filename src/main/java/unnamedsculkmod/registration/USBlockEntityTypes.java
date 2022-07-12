package unnamedsculkmod.registration;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import unnamedsculkmod.UnnamedSculkMod;
import unnamedsculkmod.blockentities.SculkEmitterBlockEntity;
import unnamedsculkmod.blockentities.SculkTransmitterBlockEntity;

public class USBlockEntityTypes {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, UnnamedSculkMod.MODID);
	public static final RegistryObject<BlockEntityType<SculkEmitterBlockEntity>> SCULK_EMITTER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sculk_emitter", () -> BlockEntityType.Builder.of(SculkEmitterBlockEntity::new, USBlocks.SCULK_EMITTER.get()).build(null));
	public static final RegistryObject<BlockEntityType<SculkTransmitterBlockEntity>> SCULK_TRANSMITTER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sculk_transmitter", () -> BlockEntityType.Builder.of(SculkTransmitterBlockEntity::new, USBlocks.SCULK_TRANSMITTER.get()).build(null));
}
