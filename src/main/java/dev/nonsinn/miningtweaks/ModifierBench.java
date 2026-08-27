package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.builtin.crafting.component.BenchBlock;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import org.joml.Vector3d;

public final class ModifierBench {
    private static final String MODIFIER_BENCH_ID = "NonSinn_Modifikatorwerkbank";
    private static final String MINING_BENCH_ID = "NonSinn_Bergbauwerkbank";

    private ModifierBench() {
    }

    public static boolean isNearby(Ref<EntityStore> player, Store<EntityStore> store, World world) {
        return nearbyTier(player, store, world, MODIFIER_BENCH_ID) > 0;
    }

    public static boolean isMiningBenchNearby(Ref<EntityStore> player, Store<EntityStore> store, World world) {
        return miningBenchTier(player, store, world) > 0;
    }

    public static int modifierBenchTier(Ref<EntityStore> player, Store<EntityStore> store, World world) {
        return nearbyTier(player, store, world, MODIFIER_BENCH_ID);
    }

    public static int miningBenchTier(Ref<EntityStore> player, Store<EntityStore> store, World world) {
        return nearbyTier(player, store, world, MINING_BENCH_ID);
    }

    private static int nearbyTier(
        Ref<EntityStore> player,
        Store<EntityStore> store,
        World world,
        String benchId
    ) {
        Transform transform = TargetUtil.getLook(player, store);
        if (transform == null) return 0;
        Vector3d position = transform.getPosition();
        int centerX = (int) Math.floor(position.x());
        int centerY = (int) Math.floor(position.y());
        int centerZ = (int) Math.floor(position.z());
        int maxTier = 0;
        for (int x = centerX - 4; x <= centerX + 4; x++) {
            for (int y = centerY - 3; y <= centerY + 3; y++) {
                for (int z = centerZ - 4; z <= centerZ + 4; z++) {
                    try {
                        BlockType type = world.getBlockType(x, y, z);
                        if (type != null && type.getId() != null && type.getId().startsWith(benchId)) {
                            int tier = benchTier(world, x, y, z);
                            if (tier > maxTier) {
                                maxTier = tier;
                            }
                        }
                    } catch (RuntimeException ignored) {
                        // Unloaded edge chunks are not valid nearby benches.
                    }
                }
            }
        }
        return maxTier;
    }

    private static int benchTier(World world, int x, int y, int z) {
        try {
            ChunkStore chunks = world.getChunkStore();
            Store<ChunkStore> store = chunks.getStore();
            Ref<ChunkStore> chunkRef = chunks.getChunkReference(ChunkUtil.indexChunkFromBlock(x, z));
            if (chunkRef == null || !chunkRef.isValid()) return 1;
            WorldChunk chunk = store.getComponent(chunkRef, WorldChunk.getComponentType());
            if (chunk == null) return 1;
            Ref<ChunkStore> benchRef = chunk.getBlockComponentEntity(x, y, z);
            if (benchRef == null || !benchRef.isValid()) return 1;
            BenchBlock bench = store.getComponent(benchRef, BenchBlock.getComponentType());
            return bench == null ? 1 : Math.max(1, bench.getTierLevel());
        } catch (RuntimeException ignored) {
            return 1;
        }
    }
}
