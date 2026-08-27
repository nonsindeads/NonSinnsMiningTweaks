package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public final class ToolModCommand extends AbstractPlayerCommand {
    private final RequiredArg<String> actionArg;

    public ToolModCommand() {
        super("werkzeugmod", "Zeigt oder installiert Resonanzbauteile am gehaltenen Werkzeug");
        actionArg = withRequiredArg("typ", "info, tempo, haltbarkeit oder stabilitaet", ArgTypes.STRING);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(
        @Nonnull CommandContext context,
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> ref,
        @Nonnull PlayerRef playerRef,
        @Nonnull World world
    ) {
        InventoryComponent.Hotbar hotbar = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
        if (hotbar == null || hotbar.getActiveSlot() < 0) return;
        ItemContainer hotbarInventory = hotbar.getInventory();
        short slot = hotbar.getActiveSlot();
        ItemStack tool = hotbarInventory.getItemStack(slot);
        if (!ToolModifiers.isCustomAreaTool(tool)) {
            playerRef.sendMessage(Message.raw("[Werkzeugmod] Halte einen NonSinn-Bergbauhammer oder eine Flächenschaufel.").color("#ffcc55"));
            return;
        }

        String action = actionArg.get(context);
        if ("info".equalsIgnoreCase(action)) {
            showInfo(playerRef, tool);
            return;
        }
        ToolModifiers.Type type = ToolModifiers.Type.parse(action);
        if (type == null) {
            playerRef.sendMessage(Message.raw("[Werkzeugmod] Typ: info, tempo, haltbarkeit oder stabilitaet.").color("#ffcc55"));
            return;
        }
        if (!ModifierBench.isNearby(ref, store, world)) {
            playerRef.sendMessage(Message.raw("[Werkzeugmod] Zum Einsetzen musst du an einer Resonanzschmiede stehen.").color("#ff7777"));
            return;
        }

        CombinedItemContainer inventory = InventoryComponent.getCombined(store, ref, InventoryComponent.HOTBAR_STORAGE_BACKPACK);
        ToolModifiers.Grade grade = bestInstallable(inventory, type, tool);
        if (grade == null) {
            ToolModifiers.Grade available = bestAvailable(inventory, type);
            String message = available == null
                ? "[Werkzeugmod] Kein passendes kalibriertes Bauteil im Inventar."
                : "[Werkzeugmod] Kein vorhandenes Bauteil passt noch in die Belastungsgrenze "
                    + ToolModifiers.strain(tool) + "/" + ToolModifiers.capacity(tool) + ".";
            playerRef.sendMessage(Message.raw(message).color(available == null ? "#ffcc55" : "#ff7777"));
            return;
        }

        ItemStack module = new ItemStack(type.moduleItemId(grade), 1);
        ItemStackTransaction removal = inventory.removeItemStack(module);
        if (!removal.succeeded()) return;
        ItemStack modified = ToolModifiers.install(tool, type, grade);
        ItemStackSlotTransaction replacement = hotbarInventory.setItemStackForSlot(slot, modified);
        if (!replacement.succeeded()) {
            inventory.addItemStack(module);
            playerRef.sendMessage(Message.raw("[Werkzeugmod] Werkzeug konnte nicht aktualisiert werden; Bauteil wurde zurückgegeben.").color("#ff7777"));
            return;
        }
        playerRef.sendMessage(Message.raw("[Werkzeugmod] " + grade.displayName() + "es Bauteil eingesetzt. Belastung: "
            + ToolModifiers.strain(modified) + "/" + ToolModifiers.capacity(modified) + ".").color("#66ddff"));
    }

    private static ToolModifiers.Grade bestAvailable(CombinedItemContainer inventory, ToolModifiers.Type type) {
        if (inventory == null) return null;
        ToolModifiers.Grade[] order = {
            ToolModifiers.Grade.MEISTERLICH,
            ToolModifiers.Grade.PRAEZISE,
            ToolModifiers.Grade.STANDARD
        };
        for (ToolModifiers.Grade grade : order) {
            if (inventory.canRemoveItemStack(new ItemStack(type.moduleItemId(grade), 1))) return grade;
        }
        return null;
    }

    private static ToolModifiers.Grade bestInstallable(
        CombinedItemContainer inventory,
        ToolModifiers.Type type,
        ItemStack tool
    ) {
        if (inventory == null) return null;
        ToolModifiers.Grade[] order = {
            ToolModifiers.Grade.MEISTERLICH,
            ToolModifiers.Grade.PRAEZISE,
            ToolModifiers.Grade.STANDARD
        };
        for (ToolModifiers.Grade grade : order) {
            if (ToolModifiers.canInstall(tool, grade)
                && inventory.canRemoveItemStack(new ItemStack(type.moduleItemId(grade), 1))) {
                return grade;
            }
        }
        return null;
    }

    private static void showInfo(PlayerRef playerRef, ItemStack tool) {
        int tempo = ToolModifiers.level(tool, ToolModifiers.Type.TEMPO);
        int durability = ToolModifiers.level(tool, ToolModifiers.Type.HALTBARKEIT);
        int stability = ToolModifiers.level(tool, ToolModifiers.Type.STABILITAET);
        playerRef.sendMessage(Message.raw("[Werkzeugmod] Belastung " + ToolModifiers.strain(tool) + "/"
            + ToolModifiers.capacity(tool) + " | Teile " + ToolModifiers.partCount(tool)
            + " | Zustand " + Math.round(tool.getDurability()) + "/" + Math.round(tool.getMaxDurability())).color("#66ddff"));
        int stabilityReduction = (int) Math.round((1.0 - ToolModifiers.areaDurabilityMultiplier(tool)) * 100.0);
        playerRef.sendMessage(Message.raw("[Werkzeugmod] Tempo " + tempo + " (+" + (tempo * 8)
            + "%) | Haltbarkeit " + durability + " (+" + (durability * 15)
            + "%) | Stabilität " + stability + " (-" + stabilityReduction
            + "% Flächenverschleiß) | Reparatur jetzt " + ToolModifiers.repairCost(tool) + "x "
            + ToolModifiers.repairMaterialDisplay(tool)).color("#66ddff"));
    }
}
