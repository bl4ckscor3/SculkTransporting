package sculktransporting.client;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import sculktransporting.items.SpeedModifierItem.SpeedTier;

public record SculkReceiverModelPart(BlockModelPart originalModel, Direction modelDirection, SpeedTier speedTier) implements BlockModelPart {
	private static final ModelBakery.PartCacheImpl PART_CACHE = new ModelBakery.PartCacheImpl();

	@Override
	public List<BakedQuad> getQuads(Direction side) {
		List<BakedQuad> originalQuads = new ArrayList<>(originalModel.getQuads(side));

		if (side != null) {
			for (int i = 0; i < originalQuads.size(); i++) {
				BakedQuad quad = originalQuads.get(i);

				if (quad.isTinted()) {
					int tintIndex = quad.tintIndex();

					if (tintIndex == 0)
						originalQuads.set(i, bakeQuad(Direction.NORTH, new Vector3f(0.0F, 3.0F, 0.0F), new Vector3f(16.0F, 8.0F, 0.0F), speedTier, quad));
					else if (tintIndex == 1)
						originalQuads.set(i, bakeQuad(Direction.EAST, new Vector3f(16.0F, 3.0F, 0.0F), new Vector3f(16.0F, 8.0F, 16.0F), speedTier, quad));
					else if (tintIndex == 2)
						originalQuads.set(i, bakeQuad(Direction.SOUTH, new Vector3f(0.0F, 3.0F, 16.0F), new Vector3f(16.0F, 8.0F, 16.0F), speedTier, quad));
					else if (tintIndex == 3)
						originalQuads.set(i, bakeQuad(Direction.WEST, new Vector3f(0.0F, 3.0F, 0.0F), new Vector3f(0.0F, 8.0F, 16.0F), speedTier, quad));
				}
			}
		}

		return originalQuads;
	}

	private BakedQuad bakeQuad(Direction quadDirection, Vector3f from, Vector3f to, SpeedTier speedTier, BakedQuad originalQuad) {
		return ClientHandler.bakeQuad(PART_CACHE, quadDirection, modelDirection, "sculk_receiver", from, to, speedTier, originalQuad, 0.0F, 8.0F, 16.0F, 13.0F);
	}

	@Override
	public ChunkSectionLayer getRenderType(BlockState state) {
		return ChunkSectionLayer.CUTOUT;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return originalModel.useAmbientOcclusion();
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return originalModel.particleIcon();
	}
}
