package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public final class ModifierDamageSystem extends EntityEventSystem<EntityStore, DamageBlockEvent> {
    public ModifierDamageSystem() {
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
        if (!event.isCancelled() && ToolModifiers.isCustomAreaTool(event.getItemInHand())) {
            event.setDamage((float) (event.getDamage() * ToolModifiers.speedMultiplier(event.getItemInHand())));
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
