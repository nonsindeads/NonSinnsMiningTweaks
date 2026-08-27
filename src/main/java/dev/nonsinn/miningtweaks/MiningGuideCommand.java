package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public final class MiningGuideCommand extends AbstractPlayerCommand {
    public MiningGuideCommand() {
        super("bergbauhilfe", "Kurzanleitung für NonSinn's Mining Tweaks");
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
        playerRef.sendMessage(Message.raw("[Bergbauhilfe] 1/5 Bergbauwerkbank: Stufe 1 Kupfer/Eisen, Stufe 2 Thorium + Resonanzschmiede, Stufe 3 Kobalt.").color("#66ddff"));
        playerRef.sendMessage(Message.raw("[Bergbauhilfe] 2/5 Resonanzschmiede, Reiter Kalibrieren: Rohling + Katalysator werden direkt zum Bauteil (60/30/10).").color("#66ddff"));
        playerRef.sendMessage(Message.raw("[Bergbauhilfe] 3/5 Montagebank: rechts ein echtes Bauteil wählen. Mit Werkzeug kombinieren oder direkt in 1/2/3 Splitter zerlegen.").color("#55ff88"));
        playerRef.sendMessage(Message.raw("[Bergbauhilfe] 4/5 Schmiede Stufe 2: fokussierte Kalibrierung (75/25). Recycling erfolgt an der Montagebank.").color("#66ddff"));
        playerRef.sendMessage(Message.raw("[Bergbauhilfe] 5/5 Pflege: /werkzeugpflege. Aufwertung an passender Bankstufe: /werkzeugupgrade weiter. /nonsinn ist nur Testmodus.").color("#55ff88"));
    }
}
