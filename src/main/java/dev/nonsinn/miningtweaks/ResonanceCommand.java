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
import javax.annotation.Nonnull;

public final class ResonanceCommand extends AbstractPlayerCommand {
    private static final String BLANK_ID = "NonSinn_Resonanzrohling";
    private final RequiredArg<String> typeArg;

    public ResonanceCommand() {
        super("resonanz", "Kalibriert einen Resonanzrohling an NonSinns Resonanzschmiede");
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
            playerRef.sendMessage(Message.raw("[Resonanz] Typ: tempo, haltbarkeit oder stabilitaet.").color("#ffcc55"));
            return;
        }
        if (!ModifierBench.isNearby(ref, store, world)) {
            playerRef.sendMessage(Message.raw("[Resonanz] Du musst höchstens 4 Blöcke von einer Resonanzschmiede entfernt sein.").color("#ff7777"));
            return;
        }
        CombinedItemContainer inventory = InventoryComponent.getCombined(store, ref, InventoryComponent.HOTBAR_STORAGE_BACKPACK);
        if (inventory == null) {
            playerRef.sendMessage(Message.raw("[Resonanz] Inventar nicht verfügbar.").color("#ff7777"));
            return;
        }
        ItemStack blank = new ItemStack(BLANK_ID, 1);
        ItemStack catalyst = new ItemStack(type.catalystItemId(), 1);
        if (!inventory.canRemoveItemStack(blank) || !inventory.canRemoveItemStack(catalyst)) {
            playerRef.sendMessage(Message.raw("[Resonanz] Benötigt: 1 Resonanzrohling und 1 Katalysator ("
                + catalystName(type) + ").").color("#ffcc55"));
            return;
        }

        ToolModifiers.Grade grade = ToolModifiers.Grade.roll();
        ItemStack result = new ItemStack(type.moduleItemId(grade), 1);
        ItemStackTransaction blankRemoval = inventory.removeItemStack(blank);
        if (!blankRemoval.succeeded()) return;
        ItemStackTransaction catalystRemoval = inventory.removeItemStack(catalyst);
        if (!catalystRemoval.succeeded()) {
            inventory.addItemStack(blank);
            return;
        }
        ItemStackTransaction addition = inventory.addItemStack(result);
        if (!addition.succeeded()) {
            inventory.addItemStack(blank);
            inventory.addItemStack(catalyst);
            playerRef.sendMessage(Message.raw("[Resonanz] Kein Platz für das kalibrierte Bauteil.").color("#ff7777"));
            return;
        }
        playerRef.sendMessage(Message.raw("[Resonanz] " + grade.displayName() + " kalibriert: "
            + type.commandName() + " (Wirkung " + grade.power() + ", Belastung " + grade.strain() + ").")
            .color(grade == ToolModifiers.Grade.MEISTERLICH ? "#d98cff" : "#66ddff"));
    }

    private static String catalystName(ToolModifiers.Type type) {
        if (type == null) return "Resonanz-Katalysator";
        return switch (type) {
            case TEMPO -> "Sumpfkrokodil-Schuppe";
            case HALTBARKEIT -> "Sumpfbarren";
            case STABILITAET -> "Sumpfjuwel";
            case GLUECK -> "Blauer Kristall";
            case SCHMELZEN -> "Roter Kristall";
            case MAGNETISMUS -> "Grüner Kristall";
            case BEHUTSAMKEIT -> "Kristall-Staub";
            case SCHADEN -> "Amethyst-Kristall";
            case LEBENSSAUGER -> "Dunkler Kristall";
            default -> "Resonanz-Katalysator";
        };
    }
}
