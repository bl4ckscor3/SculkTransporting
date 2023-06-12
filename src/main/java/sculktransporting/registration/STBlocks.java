package sculktransporting.registration;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sculktransporting.SculkTransporting;
import sculktransporting.blocks.SculkBarrelBlock;
import sculktransporting.blocks.SculkEmitterBlock;
import sculktransporting.blocks.SculkReceiverBlock;
import sculktransporting.blocks.SculkTransmitterBlock;

public class STBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SculkTransporting.MODID);
	public static final RegistryObject<Block> SCULK_EMITTER = BLOCKS.register("sculk_emitter", () -> new SculkEmitterBlock(Properties.of().mapColor(MapColor.COLOR_CYAN).strength(1.5F).sound(SoundType.SCULK_SENSOR).lightLevel(state -> 1)));
	public static final RegistryObject<Block> SCULK_RECEIVER = BLOCKS.register("sculk_receiver", () -> new SculkReceiverBlock(Properties.of().mapColor(MapColor.COLOR_CYAN).strength(1.5F).sound(SoundType.SCULK_SENSOR)));
	public static final RegistryObject<Block> SCULK_TRANSMITTER = BLOCKS.register("sculk_transmitter", () -> new SculkTransmitterBlock(Properties.of().mapColor(MapColor.COLOR_CYAN).strength(1.5F).sound(SoundType.SCULK_SENSOR)));
	public static final RegistryObject<Block> SCULK_BARREL = BLOCKS.register("sculk_barrel", () -> new SculkBarrelBlock(Properties.copy(Blocks.BARREL).mapColor(MapColor.COLOR_CYAN)));
}
