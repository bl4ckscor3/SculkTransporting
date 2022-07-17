package sculktransporting.datagen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import sculktransporting.SculkTransporting;
import sculktransporting.registration.USBlocks;

public class BlockLootTableGenerator implements DataProvider {
	protected final Map<Supplier<Block>, LootTable.Builder> lootTables = new HashMap<>();
	private final DataGenerator generator;

	public BlockLootTableGenerator(DataGenerator generator) {
		this.generator = generator;
	}

	private void addTables() {
		putStandardBlockLootTable(USBlocks.SCULK_EMITTER);
		putStandardBlockLootTable(USBlocks.SCULK_TRANSMITTER);
		putStandardBlockLootTable(USBlocks.SCULK_RECEIVER);
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

	@Override
	public void run(CachedOutput cache) throws IOException {
		Map<ResourceLocation, LootTable> tables = new HashMap<>();

		addTables();

		for (Map.Entry<Supplier<Block>, LootTable.Builder> entry : lootTables.entrySet()) {
			tables.put(entry.getKey().get().getLootTable(), entry.getValue().setParamSet(LootContextParamSets.BLOCK).build());
		}

		tables.forEach((key, lootTable) -> {
			try {
				DataProvider.saveStable(cache, LootTables.serialize(lootTable), generator.getOutputFolder().resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public String getName() {
		return SculkTransporting.MODID + " Block Loot Tables";
	}
}
