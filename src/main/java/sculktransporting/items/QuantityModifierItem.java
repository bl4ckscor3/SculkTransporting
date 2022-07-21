package sculktransporting.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class QuantityModifierItem extends Item {
	public final QuantityTier tier;

	public QuantityModifierItem(QuantityTier tier, Properties properties) {
		super(properties);
		this.tier = tier;
		tier.item = this;
	}

	public static enum QuantityTier implements ModifierTier {
		ZERO(0),
		ONE(1),
		TWO(2),
		THREE(3);

		private final int value;
		private Item item = Items.AIR;

		private QuantityTier(int value) {
			this.value = value;
		}

		@Override
		public int getValue() {
			return value;
		}

		public Item getItem() {
			return item;
		}
	}
}
