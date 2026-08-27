package dev.nonsinn.miningtweaks;

public final class ToolProgressionTest {
    public static void main(String[] args) {
        ToolProgression.UpgradePlan iron = ToolProgression.plan("NonSinn_Bergbauhammer_Copper", "weiter");
        require(iron != null, "Copper hammer must upgrade");
        require(iron.to() == ToolProgression.Tier.IRON, "Copper must upgrade to iron");
        require("NonSinn_Bergbauhammer_Iron".equals(iron.targetItemId()), "Hammer target ID wrong");
        require(iron.costs().length == 4, "Hammer upgrade costs incomplete");

        ToolProgression.UpgradePlan shovel = ToolProgression.plan("NonSinn_Flaechenschaufel_Iron", "thorium");
        require(shovel != null, "Iron shovel must upgrade to thorium");
        require(shovel.family() == ToolProgression.Family.SHOVEL, "Shovel family lost");
        require("NonSinn_Flaechenschaufel_Thorium".equals(shovel.targetItemId()), "Shovel target ID wrong");

        require(ToolProgression.plan("NonSinn_Bergbauhammer_Copper", "kobalt") == null,
            "Skipping tiers must not be possible");
        require(ToolProgression.plan("NonSinn_Bergbauhammer_Cobalt", "weiter") == null,
            "Cobalt must be final");
        require(ToolProgression.plan("Tool_Pickaxe_Iron", "thorium") == null,
            "Vanilla tool must not upgrade");
        System.out.println("ToolProgressionTest passed");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
