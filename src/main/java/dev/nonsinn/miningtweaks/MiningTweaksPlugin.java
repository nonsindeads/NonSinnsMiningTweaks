package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class MiningTweaksPlugin extends JavaPlugin {
    public MiningTweaksPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        super.setup();
        OpenCustomUIInteraction.registerSimple(
            this,
            ModifierAssemblyPage.class,
            "NonSinnModifierAssembly",
            ModifierAssemblyPage::new
        );
        getCommandRegistry().registerCommand(new NonSinnCommand());
        getCommandRegistry().registerCommand(new ToolRepairCommand());
        getCommandRegistry().registerCommand(new ToolUpgradeCommand());
        getCommandRegistry().registerCommand(new ToolModCommand());
        getCommandRegistry().registerCommand(new ResonanceCommand());
        getCommandRegistry().registerCommand(new ResonanceFocusCommand());
        getCommandRegistry().registerCommand(new MiningGuideCommand());
        getEntityStoreRegistry().registerSystem(new MiningPreviewSystem());
        getEntityStoreRegistry().registerSystem(new ToolTooltipSystem());
        getEntityStoreRegistry().registerSystem(new ModifierDamageSystem());
        getEntityStoreRegistry().registerSystem(new AreaBreakSystem());
        getEntityStoreRegistry().registerSystem(new ForgeCraftSystem());
        getEntityStoreRegistry().registerSystem(new EntityCombatModifierSystem());
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Player player = event.getPlayer();
            if (player == null || player.getWorld() == null || player.getReference() == null) return;
            player.getWorld().execute(() -> {
                Ref<EntityStore> ref = player.getReference();
                if (ref == null || !ref.isValid()) return;
                Store<EntityStore> store = ref.getStore();
                ToolTooltipSystem.syncInventory(ref, store);
            });
        });
        getLogger().atInfo().log("NonSinn's Mining Tweaks 2.6.0-beta4 loaded: in-hand 3D models for all items, 3x3 in-world drops, and authentic pickaxe models enabled.");
    }

    @Override
    protected void shutdown() {
        MiningMode.clear();
        super.shutdown();
    }
}
