package sculktransporting.mixin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.TypeRewriteRule;

import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.fixes.BlockPosFormatAndRenamesFix;
import net.minecraft.util.datafix.fixes.References;

/**
 * Fixes the block positions in the sculk blocks so they can be written and read using {@link NbtUtils}
 */
@Mixin(BlockPosFormatAndRenamesFix.class)
public class BlockPosFormatAndRenamesFixMixin {
	@Shadow
	private TypeRewriteRule createEntityFixer(TypeReference reference, String entityId, Map<String, String> renames) {
		throw new IllegalStateException("Shadowing createEntityFixer failed!");
	}

	@Inject(method = "addBlockEntityRules", at = @At("TAIL"))
	private void sculktransporting$addBlockEntityRules(List<TypeRewriteRule> output, CallbackInfo ci) {
		Arrays.asList(
		//@formatter:off
				"sculktransporting:sculk_emitter",
				"sculktransporting:sculk_receiver",
				"sculktransporting:sculk_transmitter"
		//@formatter:on
		).forEach(blockEntityType -> output.add(createEntityFixer(References.BLOCK_ENTITY, blockEntityType, Map.of("SignalOrigin", "signal_origin"))));
	}
}
