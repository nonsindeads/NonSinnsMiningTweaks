package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.command.system.CommandContext;
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
import com.hypixel.hytale.protocol.GameMode;
import javax.annotation.Nonnull;

public final class ToolRepairCommand extends AbstractPlayerCommand {
    public ToolRepairCommand() {
        super("werkzeugpflege", "Repariert das gehaltene NonSinn-Werkzeug an der Resonanzschmiede");
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
        if (!ModifierBench.isNearby(ref, store, world)) {
            playerRef.sendMessage(Message.raw("[Werkzeugpflege] Du musst an einer Resonanzschmiede stehen.").color("#ff7777"));
            return;
        }
        InventoryComponent.Hotbar hotbar = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
        if (hotbar == null || hotbar.getActiveSlot() < 0) return;
        ItemContainer hotbarInventory = hotbar.getInventory();
        short slot = hotbar.getActiveSlot();
        ItemStack tool = hotbarInventory.getItemStack(slot);
        if (!ToolModifiers.isCustomAreaTool(tool)) {
            playerRef.sendMessage(Message.raw("[Werkzeugpflege] Halte ein NonSinn-Flächenwerkzeug.").color("#ffcc55"));
            return;
        }
        int cost = ToolModifiers.repairCost(tool);
        if (cost <= 0) {
            playerRef.sendMessage(Message.raw("[Werkzeugpflege] Das Werkzeug benötigt keine Reparatur.").color("#66ddff"));
            return;
        }
        String materialId = ToolModifiers.repairMaterial(tool);
        CombinedItemContainer inventory = InventoryComponent.getCombined(store, ref, InventoryComponent.HOTBAR_STORAGE_BACKPACK);
        ItemStack material = new ItemStack(materialId, cost);
        Player player = store.getComponent(ref, Player.getComponentType());
        boolean creative = player != null && player.getGameMode() == GameMode.Creative;
        if (inventory == null || (!creative && !inventory.canRemoveItemStack(material))) {
            playerRef.sendMessage(Message.raw("[Werkzeugpflege] Benötigt " + cost + "x "
                + ToolModifiers.repairMaterialDisplay(tool) + ".").color("#ffcc55"));
            return;
        }
        ItemStackTransaction removal = creative ? null : inventory.removeItemStack(material);
        if (!creative && (removal == null || !removal.succeeded())) return;
        ItemStackSlotTransaction replacement = hotbarInventory.setItemStackForSlot(slot,
            tool.withDurability(tool.getMaxDurability()));
        if (!replacement.succeeded()) {
            if (!creative) inventory.addItemStack(material);
            return;
        }
        String price = creative ? "ohne Materialverbrauch im Creative-Modus"
            : "für " + cost + "x " + ToolModifiers.repairMaterialDisplay(tool);
        playerRef.sendMessage(Message.raw("[Werkzeugpflege] Vollständig repariert " + price
            + ". Eingebaute Teile erhöhen im Überlebensmodus die Kosten proportional zum Schaden.").color("#55ff88"));
    }
}
