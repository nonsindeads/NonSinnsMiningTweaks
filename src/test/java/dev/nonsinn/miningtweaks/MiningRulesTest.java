package dev.nonsinn.miningtweaks;

public final class MiningRulesTest {
    public static void main(String[] args) {
        require(MiningRules.toolKind("NonSinn_Bergbauhammer_Iron") == MiningRules.ToolKind.HAMMER, "Hammer ID not recognized");
        require(MiningRules.toolKind("Tool_Pickaxe_Iron") == MiningRules.ToolKind.PICKAXE, "Vanilla pickaxe not recognized");
        require(MiningRules.toolKind("nat20:tool__pickaxe__iron_rare") == MiningRules.ToolKind.PICKAXE, "Natural20 pickaxe not recognized");
        require(MiningRules.toolKind("Tool_Shovel_Iron") == MiningRules.ToolKind.SHOVEL, "Vanilla shovel not recognized");
        require(MiningRules.toolKind("NonSinn_Flaechenschaufel_Cobalt") == MiningRules.ToolKind.AREA_SHOVEL, "Area shovel not recognized");
        require(MiningRules.toolKind("Weapon_Sword_Iron") == MiningRules.ToolKind.NONE, "Unrelated tool recognized");
        require(MiningRules.toolKind((String) null) == MiningRules.ToolKind.NONE, "Null ID recognized");
        require(MiningRules.usesAreaMining(MiningRules.ToolKind.HAMMER, false), "Hammer must always use area mining");
        require(MiningRules.usesAreaMining(MiningRules.ToolKind.AREA_SHOVEL, false), "Area shovel must always use area mining");
        require(!MiningRules.usesAreaMining(MiningRules.ToolKind.PICKAXE, false), "Pickaxe must need test mode");
        require(MiningRules.usesAreaMining(MiningRules.ToolKind.PICKAXE, true), "Pickaxe test mode did not activate");
        System.out.println("MiningRulesTest passed");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
