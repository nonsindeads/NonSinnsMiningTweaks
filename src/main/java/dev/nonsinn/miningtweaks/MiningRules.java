package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockBreakingDropType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockGathering;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.BlockHarvestUtils;
import java.util.Locale;
import java.util.Set;

public final class MiningRules {
    public enum ToolKind {
        HAMMER,
        AREA_SHOVEL,
        PICKAXE,
        SHOVEL,
        NONE
    }

    private static final Set<String> PICKAXE_GATHER_TYPES = Set.of(
        "Rocks", "VolcanicRocks",
        "OreCopper", "OreIron", "OreSilver", "OreGold", "OreThorium",
        "OreCobalt", "OreAdamantite", "OreMithril"
    );
    private static final Set<String> SHOVEL_GATHER_TYPES = Set.of("Soils", "SoftBlocks");

    private MiningRules() {
    }

    public static ToolKind toolKind(ItemStack stack) {
        return toolKind(stack == null ? null : stack.getItemId());
    }

    static ToolKind toolKind(String rawItemId) {
        if (rawItemId == null) {
            return ToolKind.NONE;
        }
        String itemId = rawItemId.toLowerCase(Locale.ROOT);
        if (itemId.contains("nonsinn_bergbauhammer")) {
            return ToolKind.HAMMER;
        }
        if (itemId.contains("nonsinn_flaechenschaufel")) {
            return ToolKind.AREA_SHOVEL;
        }
        if (itemId.contains("pickaxe")) {
            return ToolKind.PICKAXE;
        }
        if (itemId.contains("shovel")) {
            return ToolKind.SHOVEL;
        }
        return ToolKind.NONE;
    }

    public static BlockBreakingDropType breaking(BlockType type) {
        if (type == null || type.getGathering() == null) {
            return null;
        }
        return type.getGathering().getBreaking();
    }

    public static boolean isSafeMiningBlock(BlockType type, int maximumQuality, ToolKind toolKind) {
        if (type == null || type == BlockType.EMPTY || "Empty".equals(type.getId())) {
            return false;
        }
        if (type.getBlockEntity() != null || type.getConnectedBlockRuleSet() != null || type.isState()) {
            return false;
        }
        BlockBoundingBoxes hitbox = BlockBoundingBoxes.getAssetMap().getAsset(type.getHitboxTypeIndex());
        if (hitbox != null && hitbox.protrudesUnitBox()) {
            return false;
        }
        BlockGathering gathering = type.getGathering();
        BlockBreakingDropType breaking = gathering == null ? null : gathering.getBreaking();
        if (breaking == null || breaking.getGatherType() == null || breaking.getQuality() > maximumQuality) {
            return false;
        }
        return switch (toolKind) {
            case HAMMER -> PICKAXE_GATHER_TYPES.contains(breaking.getGatherType());
            case AREA_SHOVEL -> SHOVEL_GATHER_TYPES.contains(breaking.getGatherType());
            case PICKAXE -> PICKAXE_GATHER_TYPES.contains(breaking.getGatherType());
            case SHOVEL -> SHOVEL_GATHER_TYPES.contains(breaking.getGatherType());
            case NONE -> false;
        };
    }

    public static boolean canHarvest(ItemStack stack, BlockType type, ToolKind toolKind) {
        if (stack == null || stack.getItem() == null
            || !isSafeMiningBlock(type, Integer.MAX_VALUE, toolKind)) {
            return false;
        }
        return BlockHarvestUtils.getSpecPowerDamageBlock(
            stack.getItem(),
            type,
            stack.getItem().getTool()
        ) != null;
    }

    public static boolean usesAreaMining(ItemStack stack, boolean commandModeEnabled) {
        return usesAreaMining(toolKind(stack), commandModeEnabled);
    }

    static boolean usesAreaMining(ToolKind kind, boolean commandModeEnabled) {
        return kind == ToolKind.HAMMER || kind == ToolKind.AREA_SHOVEL
            || (commandModeEnabled && (kind == ToolKind.PICKAXE || kind == ToolKind.SHOVEL));
    }
}
