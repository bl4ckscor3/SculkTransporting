package sculktransporting.datagen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction.NameSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import sculktransporting.registration.STBlocks;

public class BlockLootTableGenerator implements LootTableSubProvider {
	protected final Map<Supplier<Block>, LootTable.Builder> lootTables = new HashMap<>();

	@Override
	public void generate(BiConsumer<ResourceLocation, Builder> consumer) {
		putStandardBlockLootTable(STBlocks.SCULK_EMITTER);
		putStandardBlockLootTable(STBlocks.SCULK_TRANSMITTER);
		putStandardBlockLootTable(STBlocks.SCULK_RECEIVER);
		//@formatter:off
		lootTables.put(STBlocks.SCULK_BARREL,
				LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(STBlocks.SCULK_BARREL.get())
								.apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)))
						.when(ExplosionCondition.survivesExplosion())));
		//@formatter:on

		lootTables.forEach((block, lootTable) -> consumer.accept(block.get().getLootTable(), lootTable.setParamSet(LootContextParamSets.BLOCK)));
	}

	protected final void putStandardBlockLootTable(Supplier<Block> block) {
		putStandardBlockLootTable(block, block.get());
	}

	protected final void putStandardBlockLootTable(Supplier<Block> block, ItemLike drop) {
		lootTables.put(block, createStandardBlockLootTable(drop));
	}

	protected final LootTable.Builder createStandardBlockLootTable(Supplier<Block> drop) {
		return createStandardBlockLootTable(drop.get());
	}

	protected final LootTable.Builder createStandardBlockLootTable(ItemLike drop) {
		//@formatter:off
		return LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(drop))
						.when(ExplosionCondition.survivesExplosion()));
		//@formatter:on
	}
}
