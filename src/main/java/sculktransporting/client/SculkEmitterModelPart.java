package sculktransporting.client;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.BakedQuad.MaterialInfo;
import net.minecraft.client.resources.model.sprite.Material.Baked;
import net.minecraft.core.Direction;
import sculktransporting.items.ModifierTier;
import sculktransporting.items.QuantityModifierItem.QuantityTier;
import sculktransporting.items.SpeedModifierItem.SpeedTier;

public record SculkEmitterModelPart(BlockStateModelPart originalModel, Direction modelDirection, SpeedTier speedTier, QuantityTier quantityTier) implements BlockStateModelPart {
	@Override
	public List<BakedQuad> getQuads(Direction side) {
		List<BakedQuad> originalQuads = new ArrayList<>(originalModel.getQuads(side));

		if (side != null) {
			for (int i = 0; i < originalQuads.size(); i++) {
				BakedQuad quad = originalQuads.get(i);
				MaterialInfo info = quad.materialInfo();

				if (info.isTinted()) {
					int tintIndex = info.tintIndex();

					if (tintIndex == 0)
						originalQuads.set(i, bakeLeftQuad(Direction.NORTH, new Vector3f(8.0F, 0.0F, 0.0F), new Vector3f(16.0F, 8.0F, 0.0F), speedTier, quad));
					else if (tintIndex == 1)
						originalQuads.set(i, bakeRightQuad(Direction.NORTH, new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(8.0F, 8.0F, 0.0F), quantityTier, quad));
					else if (tintIndex == 2)
						originalQuads.set(i, bakeLeftQuad(Direction.EAST, new Vector3f(16.0F, 0.0F, 8.0F), new Vector3f(16.0F, 8.0F, 16.0F), speedTier, quad));
					else if (tintIndex == 3)
						originalQuads.set(i, bakeRightQuad(Direction.EAST, new Vector3f(16.0F, 0.0F, 0.0F), new Vector3f(16.0F, 8.0F, 8.0F), quantityTier, quad));
					else if (tintIndex == 4)
						originalQuads.set(i, bakeLeftQuad(Direction.SOUTH, new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(8.0F, 8.0F, 16.0F), speedTier, quad));
					else if (tintIndex == 5)
						originalQuads.set(i, bakeRightQuad(Direction.SOUTH, new Vector3f(8.0F, 0.0F, 16.0F), new Vector3f(16.0F, 8.0F, 16.0F), quantityTier, quad));
					else if (tintIndex == 6)
						originalQuads.set(i, bakeLeftQuad(Direction.WEST, new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 8.0F, 8.0F), speedTier, quad));
					else if (tintIndex == 7)
						originalQuads.set(i, bakeRightQuad(Direction.WEST, new Vector3f(0.0F, 0.0F, 8.0F), new Vector3f(0.0F, 8.0F, 16.0F), quantityTier, quad));
				}
			}
		}

		return originalQuads;
	}

	private BakedQuad bakeLeftQuad(Direction quadDirection, Vector3f from, Vector3f to, ModifierTier modifierTier, BakedQuad originalQuad) {
		return bakeQuad(quadDirection, from, to, modifierTier, originalQuad, 0.0F, 8.0F, 8.0F, 16.0F);
	}

	private BakedQuad bakeRightQuad(Direction quadDirection, Vector3f from, Vector3f to, ModifierTier modifierTier, BakedQuad originalQuad) {
		return bakeQuad(quadDirection, from, to, modifierTier, originalQuad, 8.0F, 8.0F, 16.0F, 16.0F);
	}

	private BakedQuad bakeQuad(Direction quadDirection, Vector3f from, Vector3f to, ModifierTier modifierTier, BakedQuad originalQuad, float u0, float u1, float v0, float v1) {
		return ClientHandler.bakeQuad(quadDirection, modelDirection, "sculk_emitter", from, to, modifierTier, originalQuad, u0, u1, v0, v1);
	}

	@Override
	public int materialFlags() {
		return originalModel.materialFlags();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return originalModel.useAmbientOcclusion();
	}

	@Override
	public Baked particleMaterial() {
		return originalModel.particleMaterial();
	}
}
