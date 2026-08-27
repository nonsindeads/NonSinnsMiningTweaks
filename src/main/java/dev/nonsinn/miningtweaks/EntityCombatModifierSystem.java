package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public final class EntityCombatModifierSystem extends DamageEventSystem {
    public EntityCombatModifierSystem() {
        super();
    }

    @Override
    public void handle(
        int index,
        @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
        @Nonnull Store<EntityStore> store,
        @Nonnull CommandBuffer<EntityStore> commandBuffer,
        @Nonnull Damage damage
    ) {
        if (damage == null || damage.isCancelled()) {
            return;
        }

        // 1. Defender Modifications (Target Entity receiving damage)
        Ref<EntityStore> targetRef = archetypeChunk.getReferenceTo(index);
        if (targetRef != null && targetRef.isValid()) {
            InventoryComponent.Armor armor = store.getComponent(targetRef, InventoryComponent.Armor.getComponentType());
            if (armor != null && armor.getInventory() != null) {
                int totalProtection = 0;
                int totalRangedProtection = 0;
                int totalEnvProtection = 0;
                int featherFallingLevel = 0;

                for (short slot = 0; slot < armor.getInventory().getCapacity(); slot++) {
                    ItemStack piece = armor.getInventory().getItemStack(slot);
                    if (piece == null || ItemStack.isEmpty(piece)) continue;

                    totalProtection += ToolModifiers.level(piece, ToolModifiers.Type.SCHUTZ);
                    totalRangedProtection += ToolModifiers.level(piece, ToolModifiers.Type.FERNSCHUTZ);
                    totalEnvProtection += ToolModifiers.level(piece, ToolModifiers.Type.UMWELTSCHUTZ);

                    if (ToolModifiers.isBoots(piece)) {
                        featherFallingLevel += ToolModifiers.level(piece, ToolModifiers.Type.FEDERFALL);
                    }
                }

                // Check fall / environmental damage vs physical / ranged
                boolean isFallOrEnv = damage.getSource() == null || !(damage.getSource() instanceof Damage.EntitySource);
                if (isFallOrEnv) {
                    if (featherFallingLevel > 0) {
                        double reduction = Math.min(1.0, 0.20 * featherFallingLevel);
                        damage.setAmount((float) (damage.getAmount() * (1.0 - reduction)));
                    }
                    if (totalEnvProtection > 0) {
                        double reduction = Math.min(0.80, 0.04 * totalEnvProtection);
                        damage.setAmount((float) (damage.getAmount() * (1.0 - reduction)));
                    }
                } else {
                    if (totalProtection > 0) {
                        double reduction = Math.min(0.80, 0.04 * totalProtection);
                        damage.setAmount((float) (damage.getAmount() * (1.0 - reduction)));
                    }
                    if (totalRangedProtection > 0) {
                        double reduction = Math.min(0.80, 0.04 * totalRangedProtection);
                        damage.setAmount((float) (damage.getAmount() * (1.0 - reduction)));
                    }
                }
            }
        }

        // 2. Attacker Modifications (Attacking Entity dealing damage)
        if (damage.getSource() instanceof Damage.EntitySource entitySource) {
            Ref<EntityStore> attackerRef = entitySource.getRef();
            if (attackerRef == null || !attackerRef.isValid()) return;

            InventoryComponent.Hotbar hotbar = store.getComponent(attackerRef, InventoryComponent.Hotbar.getComponentType());
            if (hotbar == null || hotbar.getActiveSlot() < 0) return;

            ItemStack heldItem = hotbar.getInventory().getItemStack(hotbar.getActiveSlot());
            if (heldItem == null || ItemStack.isEmpty(heldItem)) return;

            // Melee & Ranged damage bonuses
            if (ToolModifiers.isMeleeWeapon(heldItem)) {
                int schadenLevel = ToolModifiers.level(heldItem, ToolModifiers.Type.SCHADEN);
                if (schadenLevel > 0) {
                    damage.setAmount((float) (damage.getAmount() * (1.0 + 0.10 * schadenLevel)));
                }
            } else if (ToolModifiers.isRangedWeapon(heldItem)) {
                int fernschussLevel = ToolModifiers.level(heldItem, ToolModifiers.Type.FERNSCHUSS);
                if (fernschussLevel > 0) {
                    damage.setAmount((float) (damage.getAmount() * (1.0 + 0.10 * fernschussLevel)));
                }
            }

            // Life Leech
            int lifestealLevel = ToolModifiers.level(heldItem, ToolModifiers.Type.LEBENSSAUGER);
            if (lifestealLevel > 0) {
                float heal = damage.getAmount() * (0.10f * lifestealLevel);
                try {
                    EntityStatMap statMap = store.getComponent(attackerRef, EntityStatsModule.get().getEntityStatMapComponentType());
                    if (statMap != null) {
                        EntityStatValue health = statMap.get("Health");
                        if (health != null) {
                            statMap.addStatValue(health.getIndex(), heal);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
