package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.InventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/** Keeps per-tool modifier and free-capacity details visible in native inventory tooltips. */
public final class ToolTooltipSystem extends EntityEventSystem<EntityStore, InventoryChangeEvent> {
    private static final ThreadLocal<Boolean> SYNCING = ThreadLocal.withInitial(() -> false);

    public ToolTooltipSystem() {
        super(InventoryChangeEvent.class);
    }

    @Override
    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> chunk,
        @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer,
        @Nonnull InventoryChangeEvent event) {
        syncContainer(event.getItemContainer());
    }

    public static void syncInventory(Ref<EntityStore> playerRef, Store<EntityStore> store) {
        CombinedItemContainer inventory = InventoryComponent.getCombined(
            store, playerRef, InventoryComponent.EVERYTHING);
        syncContainer(inventory);
    }

    private static void syncContainer(ItemContainer inventory) {
        if (inventory == null || Boolean.TRUE.equals(SYNCING.get())) return;
        SYNCING.set(true);
        try {
            for (short slot = 0; slot < inventory.getCapacity(); slot++) {
                ItemStack current = inventory.getItemStack(slot);
                if (ItemStack.isEmpty(current) || !ToolModifiers.isCustomAreaTool(current)) continue;
                ItemStack updated = ToolModifiers.refreshDisplay(current);
                if (!updated.isEquivalentType(current)) {
                    inventory.replaceItemStackInSlot(slot, current, updated);
                }
            }
        } finally {
            SYNCING.set(false);
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
