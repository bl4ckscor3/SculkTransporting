package sculktransporting.mixin;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V1460;

/**
 * Registers SculkTransporting's block entities to datafixers, so the item stacks in it can be fixed.
 */
@Mixin(V1460.class)
public class V1460Mixin {
	@Shadow
	protected static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
		throw new IllegalStateException("Shadowing registerInventoy failed!");
	}

	@Inject(method = "registerBlockEntities", at = @At("TAIL"))
	private void sculktransporting$registerBlockEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> ci, @Local Map<String, Supplier<TypeTemplate>> map) {
		Arrays.asList(
		//@formatter:off
				"sculktransporting:sculk_emitter",
				"sculktransporting:sculk_receiver",
				"sculktransporting:sculk_transmitter"
		//@formatter:on
		).forEach(blockEntityType -> schema.register(map, blockEntityType, name -> DSL.optionalFields("StoredItemSignal", References.ITEM_STACK.in(schema))));
		registerInventory(schema, map, "sculktransporting:sculk_barrel");
	}
}
