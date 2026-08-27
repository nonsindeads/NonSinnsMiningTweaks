package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.ecs.CraftRecipeEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.protocol.GameMode;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/** Executes Resonance Forge recipe cards without losing per-tool metadata. */
public final class ForgeCraftSystem extends EntityEventSystem<EntityStore, CraftRecipeEvent.Pre> {
    private static final String PREFIX = "NonSinn_Action_";
    private static final String BLANK = "NonSinn_Resonanzrohling";
    private static final String SPLINTER = "NonSinn_Resonanzsplitter";

    public ForgeCraftSystem() {
        super(CraftRecipeEvent.Pre.class);
    }

    @Override
    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> chunk,
        @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer,
        @Nonnull CraftRecipeEvent.Pre event) {
        MaterialQuantity output = event.getCraftedRecipe().getPrimaryOutput();
        String id = output == null ? null : output.getItemId();
        if (id == null || !id.startsWith(PREFIX)) return;
        event.setCancelled(true);
        Ref<EntityStore> player = chunk.getReferenceTo(index);
        PlayerRef playerRef = store.getComponent(player, PlayerRef.getComponentType());
        if (playerRef == null) return;
        String action = id.substring(PREFIX.length());
        if (action.startsWith("Calibrate_")) {
            calibrateAction(player, playerRef, store, action.substring(10));
        } else if (action.startsWith("Focus_")) {
            calibrateAction(player, playerRef, store, action.substring(6) + "_Thorium");
        } else if (action.startsWith("Fuse_")) {
            fuseAction(player, playerRef, store, action.substring(5));
        }
    }

    private static void calibrateAction(Ref<EntityStore> player, PlayerRef playerRef,
        Store<EntityStore> store, String parameter) {
        String[] parts = parameter.split("_");
        ToolModifiers.Type type = ToolModifiers.Type.parse(parts[0]);
        ToolModifiers.BlankTier tier = parts.length > 1
            ? ToolModifiers.BlankTier.fromItemId("NonSinn_Resonanzrohling_" + parts[1])
            : ToolModifiers.BlankTier.COPPER;
        if (tier == null) tier = ToolModifiers.BlankTier.COPPER;
        if (type == null) return;

        CombinedItemContainer inventory = combined(store, player);
        if (inventory == null) return;
        boolean creative = isCreative(store, player);

        if (tier == ToolModifiers.BlankTier.THORIUM && !creative) {
            World world = ((EntityStore) store.getExternalData()).getWorld();
            int benchTier = ModifierBench.modifierBenchTier(player, store, world);
            if (benchTier < 2) {
                message(playerRef, "Thorium-Kalibrierung benötigt eine Resonanzschmiede Stufe 2.", "#ff7777");
                return;
            }
        }

        if (!creative && !hasBlankAndCatalyst(inventory, tier, type)) {
            message(playerRef, "Materialien für diese Kalibrierung fehlen (" + tier.displayName()
                + "-Rohling & Katalysator).", "#ffcc55");
            return;
        }
        List<ItemStack> removed = creative ? List.of() : removeBlankAndCatalyst(inventory, tier, type);
        if (!creative && removed == null) return;

        ToolModifiers.Grade grade = tier.roll();
        ItemStack result = new ItemStack(type.moduleItemId(grade), 1);
        if (!inventory.addItemStack(result).succeeded()) {
            refund(inventory, removed);
            message(playerRef, "Kein Platz; Materialien zurückgegeben.", "#ff7777");
            return;
        }
        message(playerRef, grade.displayName() + "es " + type.commandName() + "-Bauteil kalibriert ("
            + tier.displayName() + "-Rohling).",
            grade == ToolModifiers.Grade.MEISTERLICH ? "#d98cff"
            : grade == ToolModifiers.Grade.PRAEZISE ? "#66ddff" : "#55ff88");
    }

    private static void fuseAction(Ref<EntityStore> player, PlayerRef playerRef,
        Store<EntityStore> store, String parameter) {
        String[] parts = parameter.split("_");
        if (parts.length < 2) return;
        ToolModifiers.Type type = ToolModifiers.Type.parse(parts[0]);
        ToolModifiers.Grade targetGrade = ToolModifiers.Grade.parse(parts[1]);
        if (type == null || targetGrade == null) return;

        ToolModifiers.Grade sourceGrade = targetGrade == ToolModifiers.Grade.MEISTERLICH
            ? ToolModifiers.Grade.PRAEZISE : ToolModifiers.Grade.STANDARD;
        int splinterCost = sourceGrade.fuseSplinterCost();

        CombinedItemContainer inventory = combined(store, player);
        if (inventory == null) return;
        boolean creative = isCreative(store, player);

        List<ItemStack> costs = new ArrayList<>();
        costs.add(new ItemStack(type.moduleItemId(sourceGrade), 2));
        if (splinterCost > 0) {
            costs.add(new ItemStack(SPLINTER, splinterCost));
        }

        if (!creative && !hasAll(inventory, costs)) {
            message(playerRef, "Materialien zum Verschmelzen fehlen (2x " + sourceGrade.displayName()
                + " & " + splinterCost + "x Splitter).", "#ffcc55");
            return;
        }
        List<ItemStack> removed = creative ? List.of() : removeAll(inventory, costs);
        if (!creative && removed == null) return;

        ItemStack result = new ItemStack(type.moduleItemId(targetGrade), 1);
        if (!inventory.addItemStack(result).succeeded()) {
            refund(inventory, removed);
            message(playerRef, "Kein Platz; Materialien zurückgegeben.", "#ff7777");
            return;
        }
        message(playerRef, "2x " + sourceGrade.displayName() + " erfolgreich zu 1x "
            + targetGrade.displayName() + " (" + type.commandName() + ") verschmolzen!", "#d98cff");
    }

    private static boolean hasBlankAndCatalyst(CombinedItemContainer inventory,
        ToolModifiers.BlankTier tier, ToolModifiers.Type type) {
        boolean hasBlank = inventory.canRemoveItemStack(new ItemStack(tier.itemId(), 1))
            || (tier == ToolModifiers.BlankTier.COPPER && inventory.canRemoveItemStack(new ItemStack(BLANK, 1)));
        boolean hasCatalyst = inventory.canRemoveItemStack(new ItemStack(type.catalystItemId(), 1));
        return hasBlank && hasCatalyst;
    }

    private static List<ItemStack> removeBlankAndCatalyst(CombinedItemContainer inventory,
        ToolModifiers.BlankTier tier, ToolModifiers.Type type) {
        List<ItemStack> removed = new ArrayList<>();
        ItemStack blankStack = new ItemStack(tier.itemId(), 1);
        if (!inventory.canRemoveItemStack(blankStack) && tier == ToolModifiers.BlankTier.COPPER) {
            blankStack = new ItemStack(BLANK, 1);
        }
        ItemStackTransaction t1 = inventory.removeItemStack(blankStack);
        if (!t1.succeeded()) return null;
        removed.add(blankStack);

        ItemStack catStack = new ItemStack(type.catalystItemId(), 1);
        ItemStackTransaction t2 = inventory.removeItemStack(catStack);
        if (!t2.succeeded()) {
            refund(inventory, removed);
            return null;
        }
        removed.add(catStack);
        return removed;
    }

    private static CombinedItemContainer combined(Store<EntityStore> store, Ref<EntityStore> player) {
        return InventoryComponent.getCombined(store, player, InventoryComponent.HOTBAR_STORAGE_BACKPACK);
    }

    private static boolean isCreative(Store<EntityStore> store, Ref<EntityStore> player) {
        Player component = store.getComponent(player, Player.getComponentType());
        return component != null && component.getGameMode() == GameMode.Creative;
    }

    private static boolean hasAll(CombinedItemContainer inventory, List<ItemStack> costs) {
        for (ItemStack cost : costs) if (!inventory.canRemoveItemStack(cost)) return false;
        return true;
    }

    private static List<ItemStack> removeAll(CombinedItemContainer inventory, List<ItemStack> costs) {
        List<ItemStack> removed = new ArrayList<>();
        for (ItemStack cost : costs) {
            ItemStackTransaction transaction = inventory.removeItemStack(cost);
            if (!transaction.succeeded()) {
                refund(inventory, removed);
                return null;
            }
            removed.add(cost);
        }
        return removed;
    }

    private static void refund(CombinedItemContainer inventory, List<ItemStack> removed) {
        for (ItemStack stack : removed) inventory.addItemStack(stack);
    }

    private static void message(PlayerRef player, String text, String color) {
        player.sendMessage(Message.raw("[Resonanzschmiede] " + text).color(color));
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
