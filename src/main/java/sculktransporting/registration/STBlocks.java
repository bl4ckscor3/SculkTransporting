package sculktransporting.registration;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import sculktransporting.SculkTransporting;
import sculktransporting.blocks.SculkBarrelBlock;
import sculktransporting.blocks.SculkEmitterBlock;
import sculktransporting.blocks.SculkReceiverBlock;
import sculktransporting.blocks.SculkTransmitterBlock;

public class STBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SculkTransporting.MODID);
	public static final DeferredBlock<SculkEmitterBlock> SCULK_EMITTER = BLOCKS.register("sculk_emitter", () -> new SculkEmitterBlock(defaultProperties().lightLevel(state -> 1)));
	public static final DeferredBlock<SculkReceiverBlock> SCULK_RECEIVER = BLOCKS.register("sculk_receiver", () -> new SculkReceiverBlock(defaultProperties()));
	public static final DeferredBlock<SculkTransmitterBlock> SCULK_TRANSMITTER = BLOCKS.register("sculk_transmitter", () -> new SculkTransmitterBlock(defaultProperties()));
	public static final DeferredBlock<SculkBarrelBlock> SCULK_BARREL = BLOCKS.register("sculk_barrel", () -> new SculkBarrelBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.BARREL).mapColor(MapColor.COLOR_CYAN)));

	private STBlocks() {}

	public static BlockBehaviour.Properties defaultProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(1.5F).sound(SoundType.SCULK_SENSOR).noOcclusion();
	}
}
