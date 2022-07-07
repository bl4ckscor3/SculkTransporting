package unnamedsculkmod;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import unnamedsculkmod.blocks.SculkEmitterBlock;
import unnamedsculkmod.blocks.SculkTransmitterBlock;

public class USBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, UnnamedSculkMod.MODID);

	public static final RegistryObject<Block> SCULK_EMITTER = BLOCKS.register("sculk_emitter", () -> new SculkEmitterBlock(Properties.of(Material.SCULK, MaterialColor.COLOR_CYAN).strength(1.5F).sound(SoundType.SCULK_SENSOR)));
	public static final RegistryObject<Block> SCULK_TRANSMITTER = BLOCKS.register("sculk_transmitter", () -> new SculkTransmitterBlock(Properties.of(Material.SCULK, MaterialColor.COLOR_CYAN).strength(1.5F).sound(SoundType.SCULK_SENSOR)));
}
