package sculktransporting.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class SpeedModifierItem extends Item {
	public final SpeedTier tier;

	public SpeedModifierItem(SpeedTier tier, Properties properties) {
		super(properties);
		this.tier = tier;
		tier.item = this;
	}

	public static enum SpeedTier implements ModifierTier {
		ZERO(0),
		ONE(1),
		TWO(2),
		THREE(3),
		FOUR(4);

		private final int value;
		private Item item = Items.AIR;

		private SpeedTier(int value) {
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
