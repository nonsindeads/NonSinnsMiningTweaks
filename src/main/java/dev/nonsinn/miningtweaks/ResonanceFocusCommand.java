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
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public final class ResonanceFocusCommand extends AbstractPlayerCommand {
    private static final String BLANK_ID = "NonSinn_Resonanzrohling";
    private static final String FOCUS_ID = "NonSinn_Resonanzfokuskern";
    private final RequiredArg<String> typeArg;

    public ResonanceFocusCommand() {
        super("resonanzfokus", "Kalibriert garantiert mindestens ein präzises Resonanzbauteil");
        typeArg = withRequiredArg("typ", "tempo, haltbarkeit oder stabilitaet", ArgTypes.STRING);
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
        ToolModifiers.Type type = ToolModifiers.Type.parse(typeArg.get(context));
        if (type == null) {
            playerRef.sendMessage(Message.raw("[Resonanzfokus] Typ: tempo, haltbarkeit oder stabilitaet.").color("#ffcc55"));
            return;
        }
        int benchTier = ModifierBench.modifierBenchTier(ref, store, world);
        if (benchTier < 2) {
            playerRef.sendMessage(Message.raw("[Resonanzfokus] Benötigt eine Resonanzschmiede Stufe 2"
                + (benchTier == 0 ? "." : " (aktuell Stufe " + benchTier + ").")).color("#ff7777"));
            return;
        }
        CombinedItemContainer inventory = InventoryComponent.getCombined(
            store,
            ref,
            InventoryComponent.HOTBAR_STORAGE_BACKPACK
        );
        if (inventory == null) return;

        ItemStack[] costs = {
            new ItemStack(BLANK_ID, 1),
            new ItemStack(type.catalystItemId(), 1),
            new ItemStack(FOCUS_ID, 1)
        };
        for (ItemStack cost : costs) {
            if (!inventory.canRemoveItemStack(cost)) {
                playerRef.sendMessage(Message.raw("[Resonanzfokus] Benötigt: 1 Resonanzrohling, 1 passenden Katalysator und 1 Resonanzfokuskern.").color("#ffcc55"));
                return;
            }
        }

        List<ItemStack> removed = new ArrayList<>();
        for (ItemStack cost : costs) {
            ItemStackTransaction removal = inventory.removeItemStack(cost);
            if (!removal.succeeded()) {
                refund(inventory, removed);
                return;
            }
            removed.add(cost);
        }

        ToolModifiers.Grade grade = ToolModifiers.Grade.rollFocused();
        ItemStack result = new ItemStack(type.moduleItemId(grade), 1);
        if (!inventory.addItemStack(result).succeeded()) {
            refund(inventory, removed);
            playerRef.sendMessage(Message.raw("[Resonanzfokus] Kein Platz; Materialien wurden zurückgegeben.").color("#ff7777"));
            return;
        }
        playerRef.sendMessage(Message.raw("[Resonanzfokus] " + grade.displayName() + " kalibriert: "
            + type.commandName() + " (Wirkung " + grade.power() + ", Belastung " + grade.strain() + ").")
            .color(grade == ToolModifiers.Grade.MEISTERLICH ? "#d98cff" : "#66ddff"));
    }

    private static void refund(CombinedItemContainer inventory, List<ItemStack> removed) {
        for (ItemStack cost : removed) inventory.addItemStack(cost);
    }
}
