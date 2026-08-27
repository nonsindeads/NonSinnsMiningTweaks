package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockBreakingDropType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.BlockHarvestUtils;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import org.joml.Vector3d;
import org.joml.Vector3i;

public final class AreaBreakSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    private static final ThreadLocal<Boolean> PROCESSING = ThreadLocal.withInitial(() -> false);
    private static final String SPLINTER = "NonSinn_Resonanzsplitter";

    public AreaBreakSystem() {
        super(BreakBlockEvent.class);
    }

    @Override
    public void handle(
        int index,
        @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
        @Nonnull Store<EntityStore> store,
        @Nonnull CommandBuffer<EntityStore> commandBuffer,
        @Nonnull BreakBlockEvent event
    ) {
        MiningRules.ToolKind toolKind = MiningRules.toolKind(event.getItemInHand());
        if (event.isCancelled() || PROCESSING.get() || toolKind == MiningRules.ToolKind.NONE) {
            return;
        }
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);
        PlayerRef playerRef = store.getComponent(entityRef, PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }
        if (!MiningRules.usesAreaMining(event.getItemInHand(), MiningMode.isEnabled(playerRef.getUuid()))) {
            return;
        }
        BlockBreakingDropType centerBreaking = MiningRules.breaking(event.getBlockType());
        if (centerBreaking == null || centerBreaking.getGatherType() == null
            || !MiningRules.canHarvest(event.getItemInHand(), event.getBlockType(), toolKind)) {
            return;
        }
        World world = ((EntityStore) store.getExternalData()).getWorld();
        if (world == null || !world.isAlive()) {
            return;
        }
        Transform look = TargetUtil.getLook(entityRef, store);
        int axis = MiningGeometry.depthAxis(event.getTargetBlock(), look.getPosition(), look.getDirection());
        ItemStack heldItem = event.getItemInHand();
        double durabilityLoss = 0.0;
        double centerLoss = heldItem.isUnbreakable()
            ? 0.0
            : BlockHarvestUtils.calculateDurabilityUse(heldItem.getItem(), event.getBlockType());
        double remainingDurability = heldItem.isUnbreakable()
            ? Double.POSITIVE_INFINITY
            : Math.max(0.0, heldItem.getDurability() - centerLoss);
        PROCESSING.set(true);
        try {
            tryDropSplinters(commandBuffer, event.getTargetBlock(), event.getBlockType());
            ChunkStore chunks = world.getChunkStore();
            Store<ChunkStore> chunkStore = chunks.getStore();
            for (Vector3i position : MiningGeometry.area(event.getTargetBlock(), axis, false)) {
                BlockType type;
                try {
                    type = world.getBlockType(position.x(), position.y(), position.z());
                } catch (RuntimeException ignored) {
                    continue;
                }
                if (!MiningRules.canHarvest(heldItem, type, toolKind)) {
                    continue;
                }
                long chunkIndex = ChunkUtil.indexChunkFromBlock(position.x(), position.z());
                Ref<ChunkStore> chunkRef = chunks.getChunkReference(chunkIndex);
                if (chunkRef == null || !chunkRef.isValid()) {
                    continue;
                }
                WorldChunk worldChunk = chunkStore.getComponent(chunkRef, WorldChunk.getComponentType());
                if (worldChunk == null
                    || worldChunk.getBlockComponentEntity(position.x(), position.y(), position.z()) != null) {
                    continue;
                }
                double thisLoss = heldItem.isUnbreakable()
                    ? 0.0
                    : BlockHarvestUtils.calculateDurabilityUse(heldItem.getItem(), type)
                        * ToolModifiers.areaDurabilityMultiplier(heldItem);
                if (thisLoss > remainingDurability) {
                    continue;
                }
                BlockHarvestUtils.performBlockBreak(entityRef, heldItem, position, chunkRef, store, chunkStore);
                BlockType after = world.getBlockType(position.x(), position.y(), position.z());
                if (after == null || after == BlockType.EMPTY || "Empty".equals(after.getId())) {
                    giveDrops(commandBuffer, position, type, heldItem, look.getPosition());
                    tryDropSplinters(commandBuffer, position, type);
                    durabilityLoss += thisLoss;
                    remainingDurability -= thisLoss;
                }
            }
        } finally {
            PROCESSING.set(false);
        }
        if (durabilityLoss > 0.0) {
            applyDurability(store, entityRef, heldItem, durabilityLoss);
        }
    }

    private static void tryDropSplinters(
        CommandBuffer<EntityStore> buffer,
        Vector3i position,
        BlockType blockType
    ) {
        if (blockType == null || blockType.getId() == null || buffer == null) return;
        String id = blockType.getId().toLowerCase(Locale.ROOT);
        boolean isOre = id.contains("ore") || id.contains("erz") || id.contains("crystal") || id.contains("gem");
        double chance = isOre ? 0.40 : 0.04;
        if (ThreadLocalRandom.current().nextDouble() < chance) {
            int count = isOre ? (ThreadLocalRandom.current().nextInt(1, 4)) : 1;
            List<ItemStack> splinterDrops = List.of(new ItemStack(SPLINTER, count));
            Vector3d dropPos = new Vector3d(position.x() + 0.5, position.y() + 0.2, position.z() + 0.5);
            Holder<EntityStore>[] holders = ItemComponent.generateItemDrops(buffer, splinterDrops, dropPos, Rotation3f.IDENTITY);
            if (holders != null && holders.length > 0) {
                buffer.addEntities(holders, AddReason.SPAWN);
            }
        }
    }

    private static void giveDrops(
        CommandBuffer<EntityStore> buffer,
        Vector3i position,
        BlockType blockType,
        ItemStack heldItem,
        Vector3d playerPos
    ) {
        if (blockType == null || buffer == null) return;
        List<ItemStack> drops;
        boolean silkTouch = ToolModifiers.level(heldItem, ToolModifiers.Type.BEHUTSAMKEIT) > 0;
        if (silkTouch && blockType.getId() != null && !blockType.getId().isBlank()) {
            drops = List.of(new ItemStack(blockType.getId(), 1));
        } else {
            BlockBreakingDropType breaking = MiningRules.breaking(blockType);
            int quantity = 1;
            String itemId = null;
            String dropListId = null;
            if (breaking != null) {
                quantity = breaking.getQuantity() > 0 ? breaking.getQuantity() : 1;
                itemId = breaking.getItemId();
                dropListId = breaking.getDropListId();
            }
            drops = BlockHarvestUtils.getDrops(
                blockType,
                quantity,
                itemId,
                dropListId
            );
        }

        if (drops != null && !drops.isEmpty()) {
            int fortuneLevel = ToolModifiers.level(heldItem, ToolModifiers.Type.GLUECK);
            boolean autoSmelt = ToolModifiers.level(heldItem, ToolModifiers.Type.SCHMELZEN) > 0;
            boolean magnet = ToolModifiers.level(heldItem, ToolModifiers.Type.MAGNETISMUS) > 0;

            java.util.ArrayList<ItemStack> processed = new java.util.ArrayList<>();
            for (ItemStack drop : drops) {
                if (drop == null || drop.getItemId() == null) continue;
                ItemStack stack = drop;
                if (autoSmelt) {
                    stack = smeltDrop(stack);
                }
                if (fortuneLevel > 0) {
                    int extra = (int) Math.round(stack.getQuantity() * (0.25 * fortuneLevel));
                    if (extra > 0) {
                        stack = new ItemStack(stack.getItemId(), stack.getQuantity() + extra);
                    }
                }
                processed.add(stack);
            }

            Vector3d dropPos = magnet && playerPos != null
                ? new Vector3d(playerPos.x(), playerPos.y() + 0.5, playerPos.z())
                : new Vector3d(position.x() + 0.5, position.y() + 0.2, position.z() + 0.5);
            Holder<EntityStore>[] holders = ItemComponent.generateItemDrops(buffer, processed, dropPos, Rotation3f.IDENTITY);
            if (holders != null && holders.length > 0) {
                buffer.addEntities(holders, AddReason.SPAWN);
            }
        }
    }

    private static ItemStack smeltDrop(ItemStack drop) {
        if (drop == null || drop.getItemId() == null) return drop;
        String id = drop.getItemId().toLowerCase(Locale.ROOT);
        String smeltedId = null;
        if (id.contains("copper")) smeltedId = "Ingredient_Bar_Copper";
        else if (id.contains("iron")) smeltedId = "Ingredient_Bar_Iron";
        else if (id.contains("gold")) smeltedId = "Ingredient_Bar_Gold";
        else if (id.contains("thorium")) smeltedId = "Ingredient_Bar_Thorium";
        else if (id.contains("cobalt")) smeltedId = "Ingredient_Bar_Cobalt";
        else if (id.contains("silver")) smeltedId = "Ingredient_Bar_Silver";

        if (smeltedId != null) {
            return new ItemStack(smeltedId, drop.getQuantity());
        }
        return drop;
    }

    private static void applyDurability(
        Store<EntityStore> store,
        Ref<EntityStore> playerRef,
        ItemStack originallyHeld,
        double loss
    ) {
        if (originallyHeld.isUnbreakable()) {
            return;
        }
        InventoryComponent.Hotbar hotbar = store.getComponent(playerRef, InventoryComponent.Hotbar.getComponentType());
        if (hotbar == null || hotbar.getActiveSlot() < 0) {
            return;
        }
        ItemContainer inventory = hotbar.getInventory();
        short slot = hotbar.getActiveSlot();
        ItemStack current = inventory.getItemStack(slot);
        if (current == null || current.getItemId() == null
            || !current.getItemId().equalsIgnoreCase(originallyHeld.getItemId())) {
            return;
        }
        inventory.setItemStackForSlot(slot, current.withDurability(Math.max(0.0, current.getDurability() - loss)));
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
