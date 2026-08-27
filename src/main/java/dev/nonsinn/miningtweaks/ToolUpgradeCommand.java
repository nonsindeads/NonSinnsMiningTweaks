package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
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
import com.hypixel.hytale.protocol.GameMode;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public final class ToolUpgradeCommand extends AbstractPlayerCommand {
    private final RequiredArg<String> tierArg;

    public ToolUpgradeCommand() {
        super("werkzeugupgrade", "Wertet ein NonSinn-Werkzeug auf und erhaelt seine Resonanz");
        tierArg = withRequiredArg("stufe", "weiter, eisen, thorium oder kobalt", ArgTypes.STRING);
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
        if (!ModifierBench.isMiningBenchNearby(ref, store, world)) {
            playerRef.sendMessage(Message.raw("[Werkzeugupgrade] Du musst an einer Bergbauwerkbank stehen.").color("#ff7777"));
            return;
        }
        InventoryComponent.Hotbar hotbar = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
        if (hotbar == null || hotbar.getActiveSlot() < 0) return;
        ItemContainer hotbarInventory = hotbar.getInventory();
        short slot = hotbar.getActiveSlot();
        ItemStack tool = hotbarInventory.getItemStack(slot);
        if (!ToolModifiers.isCustomAreaTool(tool)) {
            playerRef.sendMessage(Message.raw("[Werkzeugupgrade] Halte einen NonSinn-Hammer oder eine Flächenschaufel.").color("#ffcc55"));
            return;
        }

        ToolProgression.UpgradePlan plan = ToolProgression.plan(tool.getItemId(), tierArg.get(context));
        if (plan == null) {
            ToolProgression.Tier current = ToolProgression.tier(tool.getItemId());
            if (current == ToolProgression.Tier.COBALT) {
                playerRef.sendMessage(Message.raw("[Werkzeugupgrade] Kobalt ist bereits die höchste Stufe.").color("#66ddff"));
            } else {
                String next = current == null || current.next() == null ? "unbekannt" : current.next().displayName();
                playerRef.sendMessage(Message.raw("[Werkzeugupgrade] Es ist nur die direkte nächste Stufe möglich: "
                    + next + " (oder /werkzeugupgrade weiter).").color("#ffcc55"));
            }
            return;
        }
        Player player = store.getComponent(ref, Player.getComponentType());
        boolean creative = player != null && player.getGameMode() == GameMode.Creative;
        int requiredBenchTier = plan.to() == ToolProgression.Tier.COBALT ? 3
            : plan.to() == ToolProgression.Tier.THORIUM ? 2 : 1;
        int benchTier = ModifierBench.miningBenchTier(ref, store, world);
        if (!creative && benchTier < requiredBenchTier) {
            playerRef.sendMessage(Message.raw("[Werkzeugupgrade] Dafür muss die Bergbauwerkbank Stufe "
                + requiredBenchTier + " erreichen (aktuell " + benchTier + ").").color("#ffcc55"));
            return;
        }

        CombinedItemContainer inventory = InventoryComponent.getCombined(
            store,
            ref,
            InventoryComponent.HOTBAR_STORAGE_BACKPACK
        );
        if (inventory == null) return;
        for (ToolProgression.Cost cost : creative ? new ToolProgression.Cost[0] : plan.costs()) {
            if (!inventory.canRemoveItemStack(new ItemStack(cost.itemId(), cost.quantity()))) {
                playerRef.sendMessage(Message.raw("[Werkzeugupgrade] Fehlt: " + cost.quantity() + "x "
                    + cost.displayName() + ".").color("#ffcc55"));
                return;
            }
        }

        List<ItemStack> removed = new ArrayList<>();
        for (ToolProgression.Cost cost : creative ? new ToolProgression.Cost[0] : plan.costs()) {
            ItemStack material = new ItemStack(cost.itemId(), cost.quantity());
            ItemStackTransaction transaction = inventory.removeItemStack(material);
            if (!transaction.succeeded()) {
                refund(inventory, removed);
                return;
            }
            removed.add(material);
        }

        ItemStack upgraded = ToolModifiers.transferToTier(tool, plan.targetItemId());
        ItemStackSlotTransaction replacement = hotbarInventory.setItemStackForSlot(slot, upgraded);
        if (!replacement.succeeded()) {
            refund(inventory, removed);
            playerRef.sendMessage(Message.raw("[Werkzeugupgrade] Aufwertung abgebrochen; Materialien wurden zurückgegeben.").color("#ff7777"));
            return;
        }
        playerRef.sendMessage(Message.raw("[Werkzeugupgrade] Auf " + plan.to().displayName()
            + " aufgewertet. Resonanz, Belastung und relativer Zustand wurden übernommen.").color("#55ff88"));
    }

    private static void refund(CombinedItemContainer inventory, List<ItemStack> removed) {
        for (ItemStack stack : removed) {
            inventory.addItemStack(stack);
        }
    }
}
