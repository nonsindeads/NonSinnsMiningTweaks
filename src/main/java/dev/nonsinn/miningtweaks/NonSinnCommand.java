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

public final class NonSinnCommand extends AbstractPlayerCommand {
    public NonSinnCommand() {
        super("nonsinn", "Schaltet NonSinn's 3x3-Bergbaumodus ein oder aus");
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
        boolean enabled = MiningMode.toggle(playerRef.getUuid());
        if (enabled) {
            playerRef.sendMessage(Message.raw("[NonSinn] Testmodus EIN - normale Spitzhacke: Gestein/Erz, normale Schaufel: Erde/Sand. NonSinn-Hammer und Flächenschaufeln arbeiten immer 3x3.").color("#55ff55"));
        } else {
            playerRef.sendMessage(Message.raw("[NonSinn] 3x3-Bergbau AUS - normaler 1x1-Abbau.").color("#ffcc55"));
        }
    }
}
