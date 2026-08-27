package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.BlockParticleEvent;
import com.hypixel.hytale.protocol.Position;
import com.hypixel.hytale.protocol.packets.world.SpawnBlockParticleSystem;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockBreakingDropType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import org.joml.Vector3d;
import org.joml.Vector3i;

public final class MiningPreviewSystem extends EntityEventSystem<EntityStore, DamageBlockEvent> {
    private static final long PREVIEW_REFRESH_NANOS = 850_000_000L;
    private final Map<UUID, PreviewState> lastPreview = new ConcurrentHashMap<>();

    private record PreviewState(Vector3i center, int axis, long time) {
    }

    public MiningPreviewSystem() {
        super(DamageBlockEvent.class);
    }

    @Override
    public void handle(
        int index,
        @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
        @Nonnull Store<EntityStore> store,
        @Nonnull CommandBuffer<EntityStore> commandBuffer,
        @Nonnull DamageBlockEvent event
    ) {
        MiningRules.ToolKind toolKind = MiningRules.toolKind(event.getItemInHand());
        if (event.isCancelled() || toolKind == MiningRules.ToolKind.NONE) {
            return;
        }
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);
        PlayerRef playerRef = store.getComponent(entityRef, PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }
        UUID playerId = playerRef.getUuid();
        if (!MiningRules.usesAreaMining(event.getItemInHand(), MiningMode.isEnabled(playerId))) {
            return;
        }
        BlockBreakingDropType centerBreaking = MiningRules.breaking(event.getBlockType());
        if (centerBreaking == null
            || !MiningRules.canHarvest(event.getItemInHand(), event.getBlockType(), toolKind)) {
            return;
        }
        World world = ((EntityStore) store.getExternalData()).getWorld();
        Transform look = TargetUtil.getLook(entityRef, store);
        Vector3i center = new Vector3i(event.getTargetBlock());
        int axis = MiningGeometry.depthAxis(center, look.getPosition(), look.getDirection());
        long now = System.nanoTime();
        PreviewState previous = lastPreview.get(playerId);
        if (previous != null
            && previous.axis() == axis
            && previous.center().equals(center)
            && now - previous.time() < PREVIEW_REFRESH_NANOS) {
            return;
        }
        lastPreview.put(playerId, new PreviewState(center, axis, now));
        for (Vector3i position : MiningGeometry.area(center, axis, true)) {
            BlockType type;
            try {
                type = world.getBlockType(position.x(), position.y(), position.z());
            } catch (RuntimeException ignored) {
                continue;
            }
            if (!MiningRules.canHarvest(event.getItemInHand(), type, toolKind)) {
                continue;
            }
            int blockId = BlockType.getAssetMap().getIndex(type.getId());
            playerRef.getPacketHandler().write(new SpawnBlockParticleSystem(
                blockId,
                BlockParticleEvent.Hit,
                new Position(position.x() + 0.5, position.y() + 0.5, position.z() + 0.5)
            ));
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
