package dev.nonsinn.miningtweaks;

public final class ToolModifiersTest {
    public static void main(String[] args) {
        require(ToolModifiers.isCustomAreaTool("NonSinn_Bergbauhammer_Iron"), "Hammer compatibility failed");
        require(ToolModifiers.isCustomAreaTool("NonSinn_Flaechenschaufel_Cobalt"), "Shovel compatibility failed");
        require(ToolModifiers.capacity("Tool_Pickaxe_Iron") == 8, "Vanilla pickaxe capacity wrong");
        require(ToolModifiers.capacity("NonSinn_Bergbauhammer_Copper") == 6, "Copper capacity wrong");
        require(ToolModifiers.capacity("NonSinn_Bergbauhammer_Iron") == 8, "Iron capacity wrong");
        require(ToolModifiers.capacity("NonSinn_Bergbauhammer_Thorium") == 10, "Thorium capacity wrong");
        require(ToolModifiers.capacity("NonSinn_Bergbauhammer_Cobalt") == 12, "Cobalt capacity wrong");
        require(Math.abs(ToolModifiers.durabilityMultiplier(3) - 1.45) < 0.0001, "Durability multiplier wrong");

        // Type Parsing Tests
        require(ToolModifiers.Type.parse("stabilität") == ToolModifiers.Type.STABILITAET, "Umlaut parsing failed");
        require(ToolModifiers.Type.parse("glück") == ToolModifiers.Type.GLUECK, "Fortune parsing failed");
        require(ToolModifiers.Type.parse("schmelzen") == ToolModifiers.Type.SCHMELZEN, "Smelt parsing failed");
        require(ToolModifiers.Type.parse("magnet") == ToolModifiers.Type.MAGNETISMUS, "Magnet parsing failed");
        require(ToolModifiers.Type.parse("schaden") == ToolModifiers.Type.SCHADEN, "Damage parsing failed");
        require(ToolModifiers.Type.parse("lifesteal") == ToolModifiers.Type.LEBENSSAUGER, "Lifesteal parsing failed");
        require(ToolModifiers.Type.parse("featherfalling") == ToolModifiers.Type.FEDERFALL, "Feather falling parsing failed");
        require(ToolModifiers.Type.parse("swiftswim") == ToolModifiers.Type.SCHNELLSCHWIMMEN, "Swift swim parsing failed");
        require(ToolModifiers.Type.parse("reflection") == ToolModifiers.Type.REFLEXION, "Reflection parsing failed");
        require(ToolModifiers.Type.parse("secondstomach") == ToolModifiers.Type.ZWEITER_MAGEN, "Second stomach parsing failed");

        // Category Scoping Tests
        String pickaxe = "Tool_Pickaxe_Iron";
        String sword = "Weapon_Sword_Iron";
        String boots = "Armor_Boots_Iron";
        String helmet = "Armor_Helmet_Iron";
        String chest = "Armor_Chestplate_Iron";
        String gloves = "Armor_Gloves_Iron";
        String shield = "Weapon_Shield_Iron";

        require(ToolModifiers.canInstall(pickaxe, ToolModifiers.Type.SCHMELZEN), "AutoSmelt should fit pickaxe");
        require(!ToolModifiers.canInstall(sword, ToolModifiers.Type.SCHMELZEN), "AutoSmelt MUST NOT fit sword");

        require(ToolModifiers.canInstall(sword, ToolModifiers.Type.SCHADEN), "Sharpness should fit sword");
        require(!ToolModifiers.canInstall(pickaxe, ToolModifiers.Type.SCHADEN), "Sharpness MUST NOT fit pickaxe");

        require(ToolModifiers.canInstall(boots, ToolModifiers.Type.FEDERFALL), "Feather falling should fit boots");
        require(!ToolModifiers.canInstall(helmet, ToolModifiers.Type.FEDERFALL), "Feather falling MUST NOT fit helmet");

        require(ToolModifiers.canInstall(helmet, ToolModifiers.Type.WASSERATMUNG), "Waterbreathing should fit helmet");
        require(!ToolModifiers.canInstall(boots, ToolModifiers.Type.WASSERATMUNG), "Waterbreathing MUST NOT fit boots");

        require(ToolModifiers.canInstall(chest, ToolModifiers.Type.ZWEITER_MAGEN), "Second stomach should fit chestplate");
        require(!ToolModifiers.canInstall(boots, ToolModifiers.Type.ZWEITER_MAGEN), "Second stomach MUST NOT fit boots");

        require(ToolModifiers.canInstall(gloves, ToolModifiers.Type.SCHNELLSCHWIMMEN), "Swift swim should fit gloves");
        require(!ToolModifiers.canInstall(helmet, ToolModifiers.Type.SCHNELLSCHWIMMEN), "Swift swim MUST NOT fit helmet");

        require(ToolModifiers.canInstall(shield, ToolModifiers.Type.REFLEXION), "Reflection should fit shield");
        require(!ToolModifiers.canInstall(sword, ToolModifiers.Type.REFLEXION), "Reflection MUST NOT fit sword");

        // Grade Tests
        require(ToolModifiers.Grade.parse("präzise") == ToolModifiers.Grade.PRAEZISE, "Grade umlaut parsing failed");
        require(ToolModifiers.Grade.parse("praezise") == ToolModifiers.Grade.PRAEZISE, "Grade praezise parsing failed");
        require(ToolModifiers.BlankTier.fromItemId("NonSinn_Resonanzrohling_Thorium") == ToolModifiers.BlankTier.THORIUM, "Thorium blank lookup failed");

        // Repair Scaling
        require(ToolModifiers.repairCostFor(1.0, 4, 0, 0) == 2, "Base copper full repair wrong");
        require(ToolModifiers.repairCostFor(0.5, 10, 10, 5) == 8, "Half repair scaling wrong");

        System.out.println("ToolModifiersTest passed");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
