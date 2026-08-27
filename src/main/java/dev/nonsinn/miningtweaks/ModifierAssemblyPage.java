package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.ui.ItemGridSlot;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.protocol.GameMode;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Two-slot, inventory-backed assembly page for modifier installation and recycling. */
public final class ModifierAssemblyPage extends InteractiveCustomUIPage<ModifierAssemblyPage.PageData> {
    private static final String PAGE = "NonSinn/ModifierAssembly.ui";
    private static final String CHOICE = "NonSinn/ModifierAssemblyChoice.ui";
    private static final String TOOL_ROWS = "#ToolChoices";
    private static final String MODULE_ROWS = "#ModuleChoices";
    private static final String SPLINTER = "NonSinn_Resonanzsplitter";
    private boolean templateAppended;
    private short selectedToolSlot = -1;
    private String expectedToolItemId;
    private int expectedToolStrain = -1;
    private short selectedModuleSlot = -1;
    private String expectedModuleItemId;

    public ModifierAssemblyPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, PageData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commands,
        @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        if (!templateAppended) {
            commands.append(PAGE);
            templateAppended = true;
        }

        CombinedItemContainer inventory = inventory(store, ref);
        commands.clear(TOOL_ROWS);
        commands.clear(MODULE_ROWS);
        if (inventory == null) {
            clearSelection(commands, "Inventar konnte nicht geöffnet werden.");
            return;
        }

        ItemStack selectedTool = selectedTool(inventory, selectedToolSlot, expectedToolItemId, expectedToolStrain);
        if (selectedTool == null) {
            selectedToolSlot = -1;
            expectedToolItemId = null;
            expectedToolStrain = -1;
        }
        ItemStack selectedModule = selectedModule(inventory, selectedModuleSlot, expectedModuleItemId);
        if (selectedModule == null) {
            selectedModuleSlot = -1;
            expectedModuleItemId = null;
        }

        setSlot(commands, "#ToolSlot", selectedTool);
        setSlot(commands, "#ModuleSlot", selectedModule);
        appendChoices(commands, events, inventory);

        ToolModifiers.Grade grade = selectedModule == null ? null
            : ToolModifiers.moduleGrade(selectedModule.getItemId());
        boolean isCustomTool = selectedTool != null && ToolModifiers.isCustomAreaTool(selectedTool);
        boolean ready = isCustomTool && grade != null && ToolModifiers.canInstall(selectedTool, grade);
        commands.set("#Combine.Disabled", !ready);

        if (grade != null) {
            commands.set("#Recycle.Disabled", false);
            commands.set("#Recycle.Text", "Bauteil zerlegen (" + grade.recyclingYield() + " Splitter)");
            events.addEventBinding(CustomUIEventBindingType.Activating, "#Recycle",
                new EventData().append("Action", "Recycle"), false);
        } else if (selectedTool != null) {
            ToolModifiers.RecycleYield toolYield = ToolModifiers.calculateToolRecycleYield(selectedTool);
            commands.set("#Recycle.Disabled", false);
            commands.set("#Recycle.Text", "Werkzeug zerlegen (" + toolYield.summary() + ")");
            events.addEventBinding(CustomUIEventBindingType.Activating, "#Recycle",
                new EventData().append("Action", "RecycleTool"), false);
        } else {
            commands.set("#Recycle.Disabled", true);
            commands.set("#Recycle.Text", "Zerlegen");
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        boolean creative = player != null && player.getGameMode() == GameMode.Creative;
        int repairCost = selectedTool == null ? 0 : ToolModifiers.repairCost(selectedTool);
        String repairMatId = selectedTool == null ? null : ToolModifiers.repairMaterial(selectedTool);
        String repairMatName = selectedTool == null ? "" : ToolModifiers.repairMaterialDisplay(selectedTool);
        boolean hasRepairMats = creative || (inventory != null && repairMatId != null
            && inventory.canRemoveItemStack(new ItemStack(repairMatId, repairCost)));

        if (selectedTool != null && repairCost > 0) {
            if (hasRepairMats) {
                commands.set("#Repair.Disabled", false);
                commands.set("#Repair.Text", creative ? "Reparieren (Kreativ)"
                    : "Reparieren (" + repairCost + "x " + repairMatName + ")");
                events.addEventBinding(CustomUIEventBindingType.Activating, "#Repair",
                    new EventData().append("Action", "Repair"), false);
            } else {
                commands.set("#Repair.Disabled", true);
                commands.set("#Repair.Text", "Reparieren (" + repairCost + "x " + repairMatName + ")");
            }
        } else if (selectedTool != null) {
            commands.set("#Repair.Disabled", true);
            commands.set("#Repair.Text", "Haltbarkeit 100%");
        } else {
            commands.set("#Repair.Disabled", true);
            commands.set("#Repair.Text", "Reparieren");
        }

        ToolModifiers.Grade nextGrade = grade == null ? null : grade.nextGrade();
        int fuseSplinterCost = grade == null ? 0 : grade.fuseSplinterCost();
        boolean hasModulesForFuse = false;
        boolean hasSplintersForFuse = false;
        if (selectedModule != null && nextGrade != null) {
            int moduleCount = 0;
            if (inventory != null) {
                for (short s = 0; s < inventory.getCapacity(); s++) {
                    ItemStack st = inventory.getItemStack(s);
                    if (!ItemStack.isEmpty(st) && selectedModule.getItemId().equalsIgnoreCase(st.getItemId())) {
                        moduleCount += st.getQuantity();
                    }
                }
            }
            hasModulesForFuse = creative || moduleCount >= 2;
            hasSplintersForFuse = creative || (inventory != null && inventory.canRemoveItemStack(new ItemStack(SPLINTER, fuseSplinterCost)));
        }

        if (selectedModule != null && nextGrade != null) {
            if (hasModulesForFuse && hasSplintersForFuse) {
                commands.set("#Fuse.Disabled", false);
                commands.set("#Fuse.Text", "Aufwerten (" + nextGrade.displayName() + ")");
                events.addEventBinding(CustomUIEventBindingType.Activating, "#Fuse",
                    new EventData().append("Action", "Fuse"), false);
            } else {
                commands.set("#Fuse.Disabled", true);
                commands.set("#Fuse.Text", "Aufwerten (2x + " + fuseSplinterCost + " Splitter)");
            }
        } else if (selectedModule != null && grade == ToolModifiers.Grade.MEISTERLICH) {
            commands.set("#Fuse.Disabled", true);
            commands.set("#Fuse.Text", "Maximaler Rang");
        } else {
            commands.set("#Fuse.Disabled", true);
            commands.set("#Fuse.Text", "Aufwerten");
        }

        if (selectedTool == null && selectedModule == null) {
            commands.set("#Status.TextSpans", Message.raw("Links ein Werkzeug oder rechts ein Bauteil auswählen."));
        } else if (selectedTool == null) {
            ToolModifiers.Type type = ToolModifiers.moduleType(selectedModule.getItemId());
            commands.set("#Status.TextSpans", Message.raw(grade.displayName() + "es "
                + type.commandName() + "-Bauteil ausgewählt.\nEinbau: links ein 3×3-Werkzeug wählen. "
                + "Zerlegen: ergibt " + grade.recyclingYield() + " Resonanzsplitter."));
        } else if (selectedModule == null) {
            if (isCustomTool) {
                commands.set("#Status.TextSpans", Message.raw(ToolModifiers.buildSummary(selectedTool)
                    + "\nRechts ein Bauteil wählen, oder unten das Werkzeug zerlegen."));
            } else {
                ToolModifiers.RecycleYield toolYield = ToolModifiers.calculateToolRecycleYield(selectedTool);
                commands.set("#Status.TextSpans", Message.raw("Vanilla-Werkzeug ausgewählt.\n"
                    + "Zerlegen ergibt: " + toolYield.summary() + "."));
            }
        } else if (!isCustomTool) {
            commands.set("#Status.TextSpans", Message.raw("Item kann nicht verändert werden."));
        } else if (selectedModule != null) {
            ToolModifiers.Type type = ToolModifiers.moduleType(selectedModule.getItemId());
            if (!ToolModifiers.canInstall(selectedTool, type)) {
                commands.set("#Status.TextSpans", Message.raw(ToolModifiers.buildSummary(selectedTool)
                    + "\n[Ungültiges Bauteil] " + (type != null ? type.commandName() : "Bauteil")
                    + " passt nicht auf diesen Gegenstand (z. B. Auto-Schmelzen nur auf Bergbau-Werkzeuge, Federfall nur auf Stiefel)."));
            } else if (!ready) {
                commands.set("#Status.TextSpans", Message.raw(ToolModifiers.buildSummary(selectedTool)
                    + "\nZu hohe Belastung: "
                    + ToolModifiers.strain(selectedTool) + "/" + ToolModifiers.capacity(selectedTool)
                    + " + " + grade.strain() + ". Ein stärkeres Werkzeug oder kleineres Bauteil wählen."));
            } else {
                commands.set("#Status.TextSpans", Message.raw(ToolModifiers.buildSummary(selectedTool)
                    + "\nBereit: " + grade.displayName() + "es "
                    + (type != null ? type.commandName() : "") + "-Bauteil · Belastung " + ToolModifiers.strain(selectedTool)
                    + "/" + ToolModifiers.capacity(selectedTool) + " → "
                    + (ToolModifiers.strain(selectedTool) + grade.strain()) + "/"
                    + ToolModifiers.capacity(selectedTool) + "."));
                events.addEventBinding(CustomUIEventBindingType.Activating, "#Combine",
                    new EventData().append("Action", "Combine"), false);
            }
        }
    }

    private void appendChoices(UICommandBuilder commands, UIEventBuilder events,
        CombinedItemContainer inventory) {
        int toolIndex = 0;
        int moduleIndex = 0;
        for (short slot = 0; slot < inventory.getCapacity(); slot++) {
            ItemStack stack = inventory.getItemStack(slot);
            if (ItemStack.isEmpty(stack)) continue;
            if (ToolModifiers.isRecyclableTool(stack)) {
                commands.append(TOOL_ROWS, CHOICE);
                String row = TOOL_ROWS + "[" + toolIndex++ + "]";
                setSlot(commands, row + " #ChoiceIcon", stack);
                String desc;
                if (ToolModifiers.isCustomAreaTool(stack)) {
                    desc = ToolModifiers.buildCompactSummary(stack);
                } else {
                    double maxD = stack.getMaxDurability();
                    double curD = stack.getDurability();
                    int pct = maxD <= 0 ? 100 : (int) Math.round((curD / maxD) * 100.0);
                    desc = "Haltbarkeit " + pct + "%";
                }
                commands.set(row + " #ChoiceText.TextSpans", stack.getDisplayName().insert(Message.raw("\n" + desc)));
                events.addEventBinding(CustomUIEventBindingType.Activating, row,
                    new EventData().append("Action", "SelectTool").append("Slot", String.valueOf(slot)), false);
            } else if (ToolModifiers.isModifierModule(stack)) {
                commands.append(MODULE_ROWS, CHOICE);
                String row = MODULE_ROWS + "[" + moduleIndex++ + "]";
                setSlot(commands, row + " #ChoiceIcon", stack);
                ToolModifiers.Grade grade = ToolModifiers.moduleGrade(stack.getItemId());
                commands.set(row + " #ChoiceText.TextSpans", stack.getDisplayName().insert(Message.raw(
                    "\nBelastung +" + grade.strain() + " · Menge " + stack.getQuantity())));
                events.addEventBinding(CustomUIEventBindingType.Activating, row,
                    new EventData().append("Action", "SelectModule").append("Slot", String.valueOf(slot)), false);
            }
        }
        commands.set("#NoTools.Visible", toolIndex == 0);
        commands.set("#NoModules.Visible", moduleIndex == 0);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store,
        @Nonnull PageData data) {
        if (data.action == null) return;
        CombinedItemContainer inventory = inventory(store, ref);
        if ("SelectTool".equalsIgnoreCase(data.action)) {
            selectedToolSlot = parseSlot(data.slot);
            ItemStack stack = (inventory != null && selectedToolSlot >= 0 && selectedToolSlot < inventory.getCapacity())
                ? inventory.getItemStack(selectedToolSlot) : null;
            if (stack != null && ToolModifiers.isRecyclableTool(stack)) {
                expectedToolItemId = stack.getItemId();
                expectedToolStrain = ToolModifiers.isCustomAreaTool(stack) ? ToolModifiers.strain(stack) : 0;
            } else {
                selectedToolSlot = -1;
                expectedToolItemId = null;
                expectedToolStrain = -1;
            }
            refresh(ref, store);
        } else if ("SelectModule".equalsIgnoreCase(data.action)) {
            selectedModuleSlot = parseSlot(data.slot);
            ItemStack stack = (inventory != null && selectedModuleSlot >= 0 && selectedModuleSlot < inventory.getCapacity())
                ? inventory.getItemStack(selectedModuleSlot) : null;
            if (stack != null && ToolModifiers.isModifierModule(stack)) {
                expectedModuleItemId = stack.getItemId();
            } else {
                selectedModuleSlot = -1;
                expectedModuleItemId = null;
            }
            refresh(ref, store);
        } else if ("Combine".equalsIgnoreCase(data.action)) {
            combine(ref, store);
        } else if ("Repair".equalsIgnoreCase(data.action)) {
            repair(ref, store);
        } else if ("Fuse".equalsIgnoreCase(data.action)) {
            fuse(ref, store);
        } else if ("Recycle".equalsIgnoreCase(data.action)) {
            recycle(ref, store);
        } else if ("RecycleTool".equalsIgnoreCase(data.action)) {
            recycleTool(ref, store);
        }
    }

    private void combine(Ref<EntityStore> ref, Store<EntityStore> store) {
        CombinedItemContainer inventory = inventory(store, ref);
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (inventory == null || playerRef == null) return;
        ItemStack tool = selectedTool(inventory, selectedToolSlot, expectedToolItemId, expectedToolStrain);
        ItemStack module = selectedModule(inventory, selectedModuleSlot, expectedModuleItemId);
        ToolModifiers.Type type = module == null ? null : ToolModifiers.moduleType(module.getItemId());
        ToolModifiers.Grade grade = module == null ? null : ToolModifiers.moduleGrade(module.getItemId());
        if (tool == null || type == null || grade == null || !ToolModifiers.isCustomAreaTool(tool)) {
            playerRef.sendMessage(Message.raw("[Montagebank] Auswahl ist nicht mehr im Inventar.").color("#ffcc55"));
            refresh(ref, store);
            return;
        }
        if (!ToolModifiers.canInstall(tool, grade)) {
            playerRef.sendMessage(Message.raw("[Montagebank] Belastungsgrenze überschritten.").color("#ff7777"));
            refresh(ref, store);
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        boolean creative = player != null && player.getGameMode() == GameMode.Creative;
        ItemStackSlotTransaction removed = creative ? null
            : inventory.removeItemStackFromSlot(selectedModuleSlot, 1);
        if (!creative && (removed == null || !removed.succeeded())) {
            playerRef.sendMessage(Message.raw("[Montagebank] Bauteil konnte nicht entnommen werden.").color("#ff7777"));
            refresh(ref, store);
            return;
        }
        ItemStack modifiedTool = ToolModifiers.install(tool, type, grade);
        ItemStackSlotTransaction installed = inventory.replaceItemStackInSlot(
            selectedToolSlot, tool, modifiedTool);
        if (!installed.succeeded()) {
            if (!creative) inventory.addItemStack(new ItemStack(module.getItemId(), 1));
            playerRef.sendMessage(Message.raw("[Montagebank] Einbau abgebrochen; Bauteil zurückgegeben.").color("#ff7777"));
            refresh(ref, store);
            return;
        }

        selectedModuleSlot = -1;
        expectedModuleItemId = null;
        expectedToolStrain = ToolModifiers.strain(modifiedTool);
        playerRef.sendMessage(Message.raw("[Montagebank] " + grade.displayName() + "es "
            + type.commandName() + "-Bauteil eingebaut.").color("#55ff88"));
        refresh(ref, store);
    }

    private void recycle(Ref<EntityStore> ref, Store<EntityStore> store) {
        CombinedItemContainer inventory = inventory(store, ref);
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (inventory == null || playerRef == null) return;
        ItemStack module = selectedModule(inventory, selectedModuleSlot, expectedModuleItemId);
        ToolModifiers.Type type = module == null ? null : ToolModifiers.moduleType(module.getItemId());
        ToolModifiers.Grade grade = module == null ? null : ToolModifiers.moduleGrade(module.getItemId());
        if (type == null || grade == null) {
            playerRef.sendMessage(Message.raw("[Montagebank] Das ausgewählte Bauteil ist nicht mehr im Inventar.")
                .color("#ffcc55"));
            refresh(ref, store);
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        boolean creative = player != null && player.getGameMode() == GameMode.Creative;
        ItemStackSlotTransaction removed = creative ? null
            : inventory.removeItemStackFromSlot(selectedModuleSlot, 1);
        if (!creative && (removed == null || !removed.succeeded())) {
            playerRef.sendMessage(Message.raw("[Montagebank] Bauteil konnte nicht entnommen werden.")
                .color("#ff7777"));
            refresh(ref, store);
            return;
        }

        ItemStack result = new ItemStack(SPLINTER, grade.recyclingYield());
        if (!inventory.addItemStack(result).succeeded()) {
            if (!creative) inventory.addItemStack(new ItemStack(module.getItemId(), 1));
            playerRef.sendMessage(Message.raw("[Montagebank] Kein Platz; Bauteil zurückgegeben.")
                .color("#ff7777"));
            refresh(ref, store);
            return;
        }

        selectedModuleSlot = -1;
        expectedModuleItemId = null;
        playerRef.sendMessage(Message.raw("[Montagebank] " + grade.displayName() + "es "
            + type.commandName() + "-Bauteil zerlegt: " + grade.recyclingYield()
            + " Resonanzsplitter.").color("#55ff88"));
        refresh(ref, store);
    }

    private void recycleTool(Ref<EntityStore> ref, Store<EntityStore> store) {
        CombinedItemContainer inventory = inventory(store, ref);
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (inventory == null || playerRef == null) return;
        ItemStack tool = selectedTool(inventory, selectedToolSlot, expectedToolItemId, expectedToolStrain);
        if (tool == null) {
            playerRef.sendMessage(Message.raw("[Montagebank] Das ausgewählte Werkzeug ist nicht mehr im Inventar.").color("#ffcc55"));
            refresh(ref, store);
            return;
        }

        ToolModifiers.RecycleYield yield = ToolModifiers.calculateToolRecycleYield(tool);
        Player player = store.getComponent(ref, Player.getComponentType());
        boolean creative = player != null && player.getGameMode() == GameMode.Creative;

        ItemStackSlotTransaction removed = creative ? null
            : inventory.removeItemStackFromSlot(selectedToolSlot, 1);
        if (!creative && (removed == null || !removed.succeeded())) {
            playerRef.sendMessage(Message.raw("[Montagebank] Werkzeug konnte nicht entnommen werden.").color("#ff7777"));
            refresh(ref, store);
            return;
        }

        if (yield.barCount() > 0 && yield.barItemId() != null) {
            inventory.addItemStack(new ItemStack(yield.barItemId(), yield.barCount()));
        }
        if (yield.splinterCount() > 0) {
            inventory.addItemStack(new ItemStack(SPLINTER, yield.splinterCount()));
        }

        selectedToolSlot = -1;
        expectedToolItemId = null;
        expectedToolStrain = -1;
        playerRef.sendMessage(Message.raw("[Montagebank] Werkzeug erfolgreich zerlegt: " + yield.summary() + " erhalten.").color("#55ff88"));
        refresh(ref, store);
    }

    private void repair(Ref<EntityStore> ref, Store<EntityStore> store) {
        CombinedItemContainer inventory = inventory(store, ref);
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (inventory == null || playerRef == null) return;
        ItemStack tool = selectedTool(inventory, selectedToolSlot, expectedToolItemId, expectedToolStrain);
        if (tool == null) {
            playerRef.sendMessage(Message.raw("[Montagebank] Das Werkzeug ist nicht mehr im Inventar.").color("#ffcc55"));
            refresh(ref, store);
            return;
        }
        int cost = ToolModifiers.repairCost(tool);
        if (cost <= 0) {
            playerRef.sendMessage(Message.raw("[Montagebank] Das Werkzeug ist bereits vollständig repariert.").color("#66ddff"));
            refresh(ref, store);
            return;
        }
        String materialId = ToolModifiers.repairMaterial(tool);
        if (materialId == null) {
            playerRef.sendMessage(Message.raw("[Montagebank] Unbekanntes Reparaturmaterial.").color("#ff7777"));
            refresh(ref, store);
            return;
        }
        ItemStack material = new ItemStack(materialId, cost);
        Player player = store.getComponent(ref, Player.getComponentType());
        boolean creative = player != null && player.getGameMode() == GameMode.Creative;
        if (!creative && !inventory.canRemoveItemStack(material)) {
            playerRef.sendMessage(Message.raw("[Montagebank] Benötigt " + cost + "x "
                + ToolModifiers.repairMaterialDisplay(tool) + ".").color("#ffcc55"));
            refresh(ref, store);
            return;
        }
        ItemStackTransaction removal = creative ? null : inventory.removeItemStack(material);
        if (!creative && (removal == null || !removal.succeeded())) {
            refresh(ref, store);
            return;
        }
        ItemStack repaired = tool.withDurability(tool.getMaxDurability());
        ItemStackSlotTransaction replacement = inventory.replaceItemStackInSlot(selectedToolSlot, tool, repaired);
        if (!replacement.succeeded()) {
            if (!creative) inventory.addItemStack(material);
            playerRef.sendMessage(Message.raw("[Montagebank] Reparatur fehlgeschlagen; Material zurückgegeben.").color("#ff7777"));
            refresh(ref, store);
            return;
        }
        String price = creative ? "ohne Materialverbrauch im Creative-Modus"
            : "für " + cost + "x " + ToolModifiers.repairMaterialDisplay(tool);
        playerRef.sendMessage(Message.raw("[Montagebank] Werkzeug vollständig repariert " + price + ".").color("#55ff88"));
        refresh(ref, store);
    }

    private void fuse(Ref<EntityStore> ref, Store<EntityStore> store) {
        CombinedItemContainer inventory = inventory(store, ref);
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (inventory == null || playerRef == null) return;
        ItemStack module = selectedModule(inventory, selectedModuleSlot, expectedModuleItemId);
        ToolModifiers.Type type = module == null ? null : ToolModifiers.moduleType(module.getItemId());
        ToolModifiers.Grade grade = module == null ? null : ToolModifiers.moduleGrade(module.getItemId());
        ToolModifiers.Grade nextGrade = grade == null ? null : grade.nextGrade();
        if (type == null || grade == null || nextGrade == null) {
            playerRef.sendMessage(Message.raw("[Montagebank] Bauteil kann nicht weiter aufgewertet werden.").color("#ffcc55"));
            refresh(ref, store);
            return;
        }
        int splinterCost = grade.fuseSplinterCost();
        Player player = store.getComponent(ref, Player.getComponentType());
        boolean creative = player != null && player.getGameMode() == GameMode.Creative;
        if (!creative) {
            if (!inventory.canRemoveItemStack(new ItemStack(module.getItemId(), 2))) {
                playerRef.sendMessage(Message.raw("[Montagebank] Benötigt 2x " + grade.displayName() + "es " + type.commandName() + "-Bauteil.").color("#ffcc55"));
                refresh(ref, store);
                return;
            }
            if (!inventory.canRemoveItemStack(new ItemStack(SPLINTER, splinterCost))) {
                playerRef.sendMessage(Message.raw("[Montagebank] Benötigt " + splinterCost + "x Resonanzsplitter.").color("#ffcc55"));
                refresh(ref, store);
                return;
            }
            ItemStackTransaction moduleRemoval = inventory.removeItemStack(new ItemStack(module.getItemId(), 2));
            if (moduleRemoval == null || !moduleRemoval.succeeded()) {
                refresh(ref, store);
                return;
            }
            ItemStackTransaction splinterRemoval = inventory.removeItemStack(new ItemStack(SPLINTER, splinterCost));
            if (splinterRemoval == null || !splinterRemoval.succeeded()) {
                inventory.addItemStack(new ItemStack(module.getItemId(), 2));
                refresh(ref, store);
                return;
            }
        }
        String targetModuleId = type.moduleItemId(nextGrade);
        ItemStack upgraded = new ItemStack(targetModuleId, 1);
        if (!inventory.addItemStack(upgraded).succeeded()) {
            if (!creative) {
                inventory.addItemStack(new ItemStack(module.getItemId(), 2));
                inventory.addItemStack(new ItemStack(SPLINTER, splinterCost));
            }
            playerRef.sendMessage(Message.raw("[Montagebank] Kein Platz im Inventar.").color("#ff7777"));
            refresh(ref, store);
            return;
        }
        selectedModuleSlot = -1;
        expectedModuleItemId = null;
        playerRef.sendMessage(Message.raw("[Montagebank] Erfolgreich zu 1x " + nextGrade.displayName() + "es " + type.commandName() + "-Bauteil verschmolzen!").color("#55ff88"));
        refresh(ref, store);
    }

    private static CombinedItemContainer inventory(Store<EntityStore> store, Ref<EntityStore> ref) {
        return InventoryComponent.getCombined(store, ref, InventoryComponent.HOTBAR_STORAGE_BACKPACK);
    }

    private static ItemStack selectedTool(CombinedItemContainer inventory, short slot, String expectedId, int expectedStrain) {
        if (inventory == null || slot < 0 || slot >= inventory.getCapacity() || expectedId == null) return null;
        ItemStack stack = inventory.getItemStack(slot);
        if (ItemStack.isEmpty(stack) || !ToolModifiers.isRecyclableTool(stack)) return null;
        int strain = ToolModifiers.isCustomAreaTool(stack) ? ToolModifiers.strain(stack) : 0;
        if (!expectedId.equalsIgnoreCase(stack.getItemId()) || strain != expectedStrain) return null;
        return stack;
    }

    private static ItemStack selectedModule(CombinedItemContainer inventory, short slot, String expectedId) {
        if (inventory == null || slot < 0 || slot >= inventory.getCapacity() || expectedId == null) return null;
        ItemStack stack = inventory.getItemStack(slot);
        if (ItemStack.isEmpty(stack) || !ToolModifiers.isModifierModule(stack)) return null;
        if (!expectedId.equalsIgnoreCase(stack.getItemId())) return null;
        return stack;
    }

    private static void setSlot(UICommandBuilder commands, String selector, ItemStack stack) {
        ItemGridSlot[] slots = stack == null
            ? new ItemGridSlot[0]
            : new ItemGridSlot[] {new ItemGridSlot(new ItemStack(stack.getItemId(), 1))};
        commands.set(selector + ".Slots", slots);
    }

    private static void clearSelection(UICommandBuilder commands, String status) {
        setSlot(commands, "#ToolSlot", null);
        setSlot(commands, "#ModuleSlot", null);
        commands.set("#Status.TextSpans", Message.raw(status));
        commands.set("#Combine.Disabled", true);
        commands.set("#Repair.Disabled", true);
        commands.set("#Repair.Text", "Reparieren");
        commands.set("#Fuse.Disabled", true);
        commands.set("#Fuse.Text", "Aufwerten");
        commands.set("#Recycle.Disabled", true);
        commands.set("#NoTools.Visible", true);
        commands.set("#NoModules.Visible", true);
    }

    private static short parseSlot(String raw) {
        if (raw == null || raw.isBlank()) return -1;
        try {
            return Short.parseShort(raw.trim());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private void refresh(Ref<EntityStore> ref, Store<EntityStore> store) {
        UICommandBuilder commands = new UICommandBuilder();
        UIEventBuilder events = new UIEventBuilder();
        build(ref, commands, events, store);
        sendUpdate(commands, events, false);
    }

    public static final class PageData {
        public static final BuilderCodec<PageData> CODEC = BuilderCodec.builder(PageData.class, PageData::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (data, value) -> data.action = value,
                data -> data.action).add()
            .append(new KeyedCodec<>("Slot", Codec.STRING), (data, value) -> data.slot = value,
                data -> data.slot).add()
            .build();

        @Nullable private String action;
        @Nullable private String slot;
    }
}
