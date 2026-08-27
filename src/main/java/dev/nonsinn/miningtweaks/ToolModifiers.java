package dev.nonsinn.miningtweaks;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.metadata.ItemDisplayMetadata;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public final class ToolModifiers {
    private static final String KEY_TEMPO = "nonsinn_mod_tempo";
    private static final String KEY_DURABILITY = "nonsinn_mod_haltbarkeit";
    private static final String KEY_STABILITY = "nonsinn_mod_stabilitaet";
    private static final String KEY_STRAIN = "nonsinn_mod_belastung";
    private static final String KEY_PARTS = "nonsinn_mod_teile";
    private static final String KEY_PART_GRADE_PREFIX = "nonsinn_mod_bauteil_";

    public enum Type {
        TEMPO("tempo", KEY_TEMPO, "Ingredient_Crystal_Cyan", "NonSinn_Mod_Tempo_"),
        HALTBARKEIT("haltbarkeit", KEY_DURABILITY, "Ingredient_Crystal_Purple", "NonSinn_Mod_Haltbarkeit_"),
        STABILITAET("stabilitaet", KEY_STABILITY, "Ingredient_Crystal_White", "NonSinn_Mod_Stabilitaet_"),
        GLUECK("glueck", "nonsinn_mod_glueck", "Ingredient_Crystal_Blue", "NonSinn_Mod_Glueck_"),
        SCHMELZEN("schmelzen", "nonsinn_mod_schmelzen", "Ingredient_Crystal_Red", "NonSinn_Mod_Schmelzen_"),
        MAGNETISMUS("magnetismus", "nonsinn_mod_magnetismus", "Ingredient_Crystal_Green", "NonSinn_Mod_Magnetismus_"),
        BEHUTSAMKEIT("behutsamkeit", "nonsinn_mod_behutsamkeit", "Ingredient_Crystal_White", "NonSinn_Mod_Behutsamkeit_"),
        SCHADEN("schaden", "nonsinn_mod_schaden", "Ingredient_Crystal_Purple", "NonSinn_Mod_Schaden_"),
        LEBENSSAUGER("lebenssauger", "nonsinn_mod_lebenssauger", "Ingredient_Crystal_Purple", "NonSinn_Mod_Lebenssauger_"),
        SCHUTZ("schutz", "nonsinn_mod_schutz", "Ingredient_Crystal_Cyan", "NonSinn_Mod_Schutz_"),
        FEDERFALL("federfall", "nonsinn_mod_federfall", "Ingredient_Crystal_Cyan", "NonSinn_Mod_Federfall_"),
        PLUENDERUNG("pluenderung", "nonsinn_mod_pluenderung", "Ingredient_Crystal_Blue", "NonSinn_Mod_Pluenderung_"),
        WASSERATMUNG("wasseratmung", "nonsinn_mod_wasseratmung", "Ingredient_Crystal_Blue", "NonSinn_Mod_Wasseratmung_"),
        FERNSCHUSS("fernschuss", "nonsinn_mod_fernschuss", "Ingredient_Crystal_Purple", "NonSinn_Mod_Fernschuss_"),
        ADLERAUGE("adlerauge", "nonsinn_mod_adlerauge", "Ingredient_Crystal_Purple", "NonSinn_Mod_Adlerauge_"),
        GESCHICKLICHKEIT("geschicklichkeit", "nonsinn_mod_geschicklichkeit", "Ingredient_Crystal_Green", "NonSinn_Mod_Geschicklichkeit_"),
        ROBUST("robust", "nonsinn_mod_robust", "Ingredient_Crystal_Purple", "NonSinn_Mod_Robust_"),
        BRAND("brand", "nonsinn_mod_brand", "Ingredient_Crystal_Red", "NonSinn_Mod_Brand_"),
        FROST("frost", "nonsinn_mod_frost", "Ingredient_Crystal_Cyan", "NonSinn_Mod_Frost_"),
        EWIGER_PFEIL("ewiger_pfeil", "nonsinn_mod_ewiger_pfeil", "Ingredient_Crystal_Green", "NonSinn_Mod_EwigerPfeil_"),
        ELEMENTARHERZ("elementarherz", "nonsinn_mod_elementarherz", "Ingredient_Crystal_White", "NonSinn_Mod_Elementarherz_"),
        SPARSAMKEIT("sparsamkeit", "nonsinn_mod_sparsamkeit", "Ingredient_Crystal_White", "NonSinn_Mod_Sparsamkeit_"),
        RUECKSTOSS("rueckstoss", "nonsinn_mod_rueckstoss", "Ingredient_Crystal_Red", "NonSinn_Mod_Rueckstoss_"),
        REFLEXION("reflexion", "nonsinn_mod_reflexion", "Ingredient_Crystal_Red", "NonSinn_Mod_Reflexion_"),
        ABSORPTION("absorption", "nonsinn_mod_absorption", "Ingredient_Crystal_Green", "NonSinn_Mod_Absorption_"),
        SCHNELLSCHWIMMEN("schnellschwimmen", "nonsinn_mod_schnellschwimmen", "Ingredient_Crystal_Cyan", "NonSinn_Mod_Schnellschwimmen_"),
        FERNSCHUTZ("fernschutz", "nonsinn_mod_fernschutz", "Ingredient_Crystal_Cyan", "NonSinn_Mod_Fernschutz_"),
        RAUSCH("rausch", "nonsinn_mod_rausch", "Ingredient_Crystal_Purple", "NonSinn_Mod_Rausch_"),
        NACHTSICHT("nachtsicht", "nonsinn_mod_nachtsicht", "Ingredient_Crystal_White", "NonSinn_Mod_Nachtsicht_"),
        GIFT("gift", "nonsinn_mod_gift", "Ingredient_Crystal_Green", "NonSinn_Mod_Gift_"),
        UMWELTSCHUTZ("umweltschutz", "nonsinn_mod_umweltschutz", "Ingredient_Crystal_Green", "NonSinn_Mod_Umweltschutz_"),
        ZWEITER_MAGEN("zweiter_magen", "nonsinn_mod_zweiter_magen", "Ingredient_Crystal_Red", "NonSinn_Mod_ZweiterMagen_"),
        REGENERATION("regeneration", "nonsinn_mod_regeneration", "Ingredient_Crystal_Red", "NonSinn_Mod_Regeneration_");

        private final String commandName;
        private final String metadataKey;
        private final String catalystItemId;
        private final String modulePrefix;

        Type(String commandName, String metadataKey, String catalystItemId, String modulePrefix) {
            this.commandName = commandName;
            this.metadataKey = metadataKey;
            this.catalystItemId = catalystItemId;
            this.modulePrefix = modulePrefix;
        }

        public String commandName() { return commandName; }
        public String metadataKey() { return metadataKey; }
        public String catalystItemId() { return catalystItemId; }
        public String moduleItemId(Grade grade) { return modulePrefix + grade.assetSuffix(); }

        public static Type parse(String raw) {
            if (raw == null) return null;
            String normalized = raw.toLowerCase(Locale.ROOT)
                .replace("ä", "ae").replace("ö", "oe").replace("ü", "ue");
            return switch (normalized) {
                case "tempo", "speed", "efficiency" -> TEMPO;
                case "haltbarkeit", "durability" -> HALTBARKEIT;
                case "stabilitaet", "stabilitat", "stability" -> STABILITAET;
                case "glueck", "gluck", "luck", "fortune" -> GLUECK;
                case "schmelzen", "smelt", "autosmelt" -> SCHMELZEN;
                case "magnetismus", "magnet", "vacuum" -> MAGNETISMUS;
                case "behutsamkeit", "silktouch", "pickperfect" -> BEHUTSAMKEIT;
                case "schaden", "damage", "sharpness" -> SCHADEN;
                case "lebenssauger", "vampirismus", "lifesteal", "lifeleech" -> LEBENSSAUGER;
                case "schutz", "protection" -> SCHUTZ;
                case "federfall", "featherfalling" -> FEDERFALL;
                case "pluenderung", "looting" -> PLUENDERUNG;
                case "wasseratmung", "waterbreathing" -> WASSERATMUNG;
                case "fernschuss", "strength", "power" -> FERNSCHUSS;
                case "adlerauge", "eagleseye" -> ADLERAUGE;
                case "geschicklichkeit", "dexterity" -> GESCHICKLICHKEIT;
                case "robust", "sturdy" -> ROBUST;
                case "brand", "burn", "fire" -> BRAND;
                case "frost", "freeze", "slow" -> FROST;
                case "ewiger_pfeil", "ewigerpfeil", "eternalshot" -> EWIGER_PFEIL;
                case "elementarherz", "elementalheart" -> ELEMENTARHERZ;
                case "sparsamkeit", "thrift" -> SPARSAMKEIT;
                case "rueckstoss", "knockback" -> RUECKSTOSS;
                case "reflexion", "reflection" -> REFLEXION;
                case "absorption" -> ABSORPTION;
                case "schnellschwimmen", "swiftswim" -> SCHNELLSCHWIMMEN;
                case "fernschutz", "rangedprotection" -> FERNSCHUTZ;
                case "rausch", "frenzy" -> RAUSCH;
                case "nachtsicht", "nightvision" -> NACHTSICHT;
                case "gift", "poison" -> GIFT;
                case "umweltschutz", "environmentalprotection" -> UMWELTSCHUTZ;
                case "zweiter_magen", "zweitermagen", "secondstomach" -> ZWEITER_MAGEN;
                case "regeneration", "regen" -> REGENERATION;
                default -> null;
            };
        }
    }

    public enum BlankTier {
        COPPER("Kupfer", "NonSinn_Resonanzrohling_Copper", 0.70, 0.25, 0.05),
        IRON("Eisen", "NonSinn_Resonanzrohling_Iron", 0.35, 0.50, 0.15),
        THORIUM("Thorium", "NonSinn_Resonanzrohling_Thorium", 0.00, 0.65, 0.35);

        private final String displayName;
        private final String itemId;
        private final double standardChance;
        private final double praeziseChance;
        private final double meisterlichChance;

        BlankTier(String displayName, String itemId, double standardChance, double praeziseChance, double meisterlichChance) {
            this.displayName = displayName;
            this.itemId = itemId;
            this.standardChance = standardChance;
            this.praeziseChance = praeziseChance;
            this.meisterlichChance = meisterlichChance;
        }

        public String displayName() { return displayName; }
        public String itemId() { return itemId; }
        public double standardChance() { return standardChance; }
        public double praeziseChance() { return praeziseChance; }
        public double meisterlichChance() { return meisterlichChance; }

        public static BlankTier fromItemId(String id) {
            if (id == null) return null;
            if (id.equalsIgnoreCase("NonSinn_Resonanzrohling_Thorium")) return THORIUM;
            if (id.equalsIgnoreCase("NonSinn_Resonanzrohling_Iron")) return IRON;
            if (id.equalsIgnoreCase("NonSinn_Resonanzrohling_Copper") || id.equalsIgnoreCase("NonSinn_Resonanzrohling")) return COPPER;
            return null;
        }

        public Grade roll(double rollValue) {
            if (rollValue < meisterlichChance) return Grade.MEISTERLICH;
            if (rollValue < meisterlichChance + praeziseChance) return Grade.PRAEZISE;
            return Grade.STANDARD;
        }

        public Grade roll() {
            return roll(ThreadLocalRandom.current().nextDouble());
        }
    }

    public enum Grade {
        STANDARD("Standard", "Standard", 1, 2),
        PRAEZISE("Präzise", "Praezise", 2, 3),
        MEISTERLICH("Meisterlich", "Meisterlich", 3, 4);

        private final String displayName;
        private final String assetSuffix;
        private final int power;
        private final int strain;

        Grade(String displayName, String assetSuffix, int power, int strain) {
            this.displayName = displayName;
            this.assetSuffix = assetSuffix;
            this.power = power;
            this.strain = strain;
        }

        public String displayName() { return displayName; }
        public String assetSuffix() { return assetSuffix; }
        public int power() { return power; }
        public int strain() { return strain; }

        public Grade nextGrade() {
            return switch (this) {
                case STANDARD -> PRAEZISE;
                case PRAEZISE -> MEISTERLICH;
                case MEISTERLICH -> null;
            };
        }

        public int fuseSplinterCost() {
            return switch (this) {
                case STANDARD -> 6;
                case PRAEZISE -> 16;
                case MEISTERLICH -> 0;
            };
        }

        public static Grade roll() { return BlankTier.COPPER.roll(); }
        public static Grade rollFocused() { return BlankTier.THORIUM.roll(); }
        static Grade roll(double value, boolean focused) {
            return focused ? BlankTier.THORIUM.roll(value) : BlankTier.COPPER.roll(value);
        }

        public int recyclingYield() {
            return switch (this) {
                case STANDARD -> 1;
                case PRAEZISE -> 2;
                case MEISTERLICH -> 3;
            };
        }

        public static Grade parse(String raw) {
            if (raw == null) return null;
            String normalized = raw.toLowerCase(Locale.ROOT)
                .replace("ä", "ae").replace("ö", "oe").replace("ü", "ue");
            return switch (normalized) {
                case "standard", "normal" -> STANDARD;
                case "praezise", "prazise", "precise" -> PRAEZISE;
                case "meisterlich", "masterwork" -> MEISTERLICH;
                default -> null;
            };
        }
    }

    public static final class RecycleYield {
        private final String barItemId;
        private final String barDisplayName;
        private final int barCount;
        private final int splinterCount;

        public RecycleYield(String barItemId, String barDisplayName, int barCount, int splinterCount) {
            this.barItemId = barItemId;
            this.barDisplayName = barDisplayName;
            this.barCount = barCount;
            this.splinterCount = splinterCount;
        }

        public String barItemId() { return barItemId; }
        public String barDisplayName() { return barDisplayName; }
        public int barCount() { return barCount; }
        public int splinterCount() { return splinterCount; }

        public String summary() {
            if (barCount > 0 && splinterCount > 0) {
                return barCount + "x " + barDisplayName + ", " + splinterCount + "x Splitter";
            } else if (barCount > 0) {
                return barCount + "x " + barDisplayName;
            } else if (splinterCount > 0) {
                return splinterCount + "x Splitter";
            }
            return "1x Splitter";
        }
    }

    private ToolModifiers() {}

    public static boolean isMiningTool(ItemStack stack) {
        return stack != null && isMiningTool(stack.getItemId());
    }

    public static boolean isMiningTool(String rawItemId) {
        if (rawItemId == null) return false;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return id.contains("pickaxe") || id.contains("spitzhacke")
            || id.contains("shovel") || id.contains("schaufel")
            || id.contains("bergbauhammer") || id.contains("flaechenschaufel")
            || (id.contains("axe") && !id.contains("battleaxe"))
            || (id.contains("axt") && !id.contains("kriegaxt"));
    }

    public static boolean isMeleeWeapon(ItemStack stack) {
        return stack != null && isMeleeWeapon(stack.getItemId());
    }

    public static boolean isMeleeWeapon(String rawItemId) {
        if (rawItemId == null) return false;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return id.contains("sword") || id.contains("schwert")
            || id.contains("dagger") || id.contains("dolch")
            || id.contains("mace") || id.contains("streitkolben")
            || id.contains("spear") || id.contains("speer")
            || id.contains("battleaxe") || id.contains("kriegaxt")
            || id.contains("warhammer") || id.contains("kriegshammer")
            || id.contains("scythe") || id.contains("sensen");
    }

    public static boolean isRangedWeapon(ItemStack stack) {
        return stack != null && isRangedWeapon(stack.getItemId());
    }

    public static boolean isRangedWeapon(String rawItemId) {
        return isBowOrCrossbow(rawItemId) || isStaff(rawItemId);
    }

    public static boolean isBowOrCrossbow(ItemStack stack) {
        return stack != null && isBowOrCrossbow(stack.getItemId());
    }

    public static boolean isBowOrCrossbow(String rawItemId) {
        if (rawItemId == null) return false;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return id.contains("bow") || id.contains("bogen")
            || id.contains("crossbow") || id.contains("armbrust");
    }

    public static boolean isStaff(ItemStack stack) {
        return stack != null && isStaff(stack.getItemId());
    }

    public static boolean isStaff(String rawItemId) {
        if (rawItemId == null) return false;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return id.contains("staff") || id.contains("stab")
            || id.contains("wand") || id.contains("zauberstab");
    }

    public static boolean isShield(ItemStack stack) {
        return stack != null && isShield(stack.getItemId());
    }

    public static boolean isShield(String rawItemId) {
        if (rawItemId == null) return false;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return id.contains("shield") || id.contains("schild");
    }

    public static boolean isHelmet(ItemStack stack) {
        return stack != null && isHelmet(stack.getItemId());
    }

    public static boolean isHelmet(String rawItemId) {
        if (rawItemId == null) return false;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return id.contains("helmet") || id.contains("helm")
            || id.contains("head") || id.contains("hood")
            || id.contains("cap") || id.contains("coif")
            || id.contains("hat") || id.contains("crown")
            || id.contains("circlet") || id.contains("mask");
    }

    public static boolean isChestplate(ItemStack stack) {
        return stack != null && isChestplate(stack.getItemId());
    }

    public static boolean isChestplate(String rawItemId) {
        if (rawItemId == null) return false;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return id.contains("chestplate") || id.contains("brust")
            || id.contains("harnisch") || id.contains("cuirass")
            || id.contains("tunic") || id.contains("robe")
            || id.contains("breastplate") || id.contains("vest")
            || id.contains("jacket") || id.contains("coat");
    }

    public static boolean isLeggings(ItemStack stack) {
        return stack != null && isLeggings(stack.getItemId());
    }

    public static boolean isLeggings(String rawItemId) {
        if (rawItemId == null) return false;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return id.contains("leggings") || id.contains("hose")
            || id.contains("legs") || id.contains("pants")
            || id.contains("greaves") || id.contains("trousers")
            || id.contains("kilt") || id.contains("skirt");
    }

    public static boolean isBoots(ItemStack stack) {
        return stack != null && isBoots(stack.getItemId());
    }

    public static boolean isBoots(String rawItemId) {
        if (rawItemId == null) return false;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return id.contains("boots") || id.contains("stiefel")
            || id.contains("schuh") || id.contains("feet")
            || id.contains("foot") || id.contains("sabatons");
    }

    public static boolean isGloves(ItemStack stack) {
        return stack != null && isGloves(stack.getItemId());
    }

    public static boolean isGloves(String rawItemId) {
        if (rawItemId == null) return false;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return id.contains("gloves") || id.contains("handschuh")
            || id.contains("gauntlet") || id.contains("hand")
            || id.contains("mitts") || id.contains("bracer");
    }

    public static boolean isArmor(ItemStack stack) {
        return stack != null && isArmor(stack.getItemId());
    }

    public static boolean isArmor(String rawItemId) {
        if (rawItemId == null) return false;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return isHelmet(rawItemId) || isChestplate(rawItemId) || isLeggings(rawItemId) || isBoots(rawItemId) || isGloves(rawItemId)
            || id.contains("armor") || id.contains("ruestung");
    }

    public static boolean isCustomAreaTool(ItemStack stack) {
        return stack != null && isCustomAreaTool(stack.getItemId());
    }

    static boolean isCustomAreaTool(String rawItemId) {
        if (rawItemId == null) return false;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return id.contains("nonsinn_bergbauhammer_") || id.contains("nonsinn_flaechenschaufel_");
    }

    public static boolean isRecyclableTool(ItemStack stack) {
        return stack != null && isRecyclableTool(stack.getItemId());
    }

    public static boolean isRecyclableTool(String rawItemId) {
        if (rawItemId == null) return false;
        if (isCustomAreaTool(rawItemId)) return true;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        return isMiningTool(rawItemId) || isMeleeWeapon(rawItemId) || isRangedWeapon(rawItemId) || isShield(rawItemId) || isArmor(rawItemId)
            || id.startsWith("tool_") || id.startsWith("weapon_") || id.startsWith("armor_");
    }

    public static boolean isModifiableToolOrWeapon(ItemStack stack) {
        return isRecyclableTool(stack);
    }

    public static boolean isModifiableToolOrWeapon(String rawItemId) {
        return isRecyclableTool(rawItemId);
    }

    public static RecycleYield calculateToolRecycleYield(ItemStack stack) {
        if (!isRecyclableTool(stack)) return new RecycleYield(null, null, 0, 0);
        double maxD = stack.getMaxDurability();
        double curD = stack.getDurability();
        double ratio = (maxD <= 0.0) ? 1.0 : Math.max(0.0, Math.min(1.0, curD / maxD));

        String id = stack.getItemId().toLowerCase(Locale.ROOT);
        String barId = null;
        String barName = null;
        int baseSplitters = 1;
        int maxSplitters = 2;
        int maxBars = 1;

        if (id.contains("copper")) { barId = "Ingredient_Bar_Copper"; barName = "Kupferbarren"; baseSplitters = 1; maxSplitters = 2; }
        else if (id.contains("iron")) { barId = "Ingredient_Bar_Iron"; barName = "Eisenbarren"; baseSplitters = 1; maxSplitters = 3; }
        else if (id.contains("bronze")) { barId = "Ingredient_Bar_Bronze"; barName = "Bronzebarren"; baseSplitters = 1; maxSplitters = 2; }
        else if (id.contains("cobalt")) { barId = "Ingredient_Bar_Cobalt"; barName = "Kobaltbarren"; baseSplitters = 2; maxSplitters = 4; }
        else if (id.contains("thorium")) { barId = "Ingredient_Bar_Thorium"; barName = "Thoriumbarren"; baseSplitters = 2; maxSplitters = 5; }
        else if (id.contains("mithril")) { barId = "Ingredient_Bar_Mithril"; barName = "Mithrilbarren"; baseSplitters = 3; maxSplitters = 6; }
        else if (id.contains("adamant")) { barId = "Ingredient_Bar_Adamantite"; barName = "Adamantitbarren"; baseSplitters = 3; maxSplitters = 7; }
        else if (id.contains("onyxium")) { barId = "Ingredient_Bar_Onyxium"; barName = "Onyxiumbarren"; baseSplitters = 4; maxSplitters = 8; }

        if (isCustomAreaTool(stack)) { maxBars = 2; baseSplitters += 1; maxSplitters += 2; }

        int barCount = 0;
        if (barId != null) {
            if (maxBars > 1 && ratio >= 0.75) barCount = 2;
            else if (ratio >= 0.40) barCount = 1;
        }

        int splinterCount = baseSplitters + (int) Math.round((maxSplitters - baseSplitters) * ratio);

        for (Type type : Type.values()) {
            for (Grade grade : Grade.values()) {
                int installed = installedGradeCount(stack, type, grade);
                if (installed > 0) splinterCount += installed * grade.recyclingYield();
            }
        }
        return new RecycleYield(barId, barName, barCount, splinterCount);
    }

    public static int capacity(ItemStack stack) {
        return capacity(stack == null ? null : stack.getItemId());
    }

    static int capacity(String rawItemId) {
        if (rawItemId == null) return 4;
        String id = rawItemId.toLowerCase(Locale.ROOT);
        if (id.contains("cobalt") || id.contains("onyxium") || id.contains("adamant")) return 12;
        if (id.contains("thorium") || id.contains("mithril") || id.contains("gold")) return 10;
        if (id.contains("iron") || id.contains("silver")) return 8;
        if (id.contains("copper") || id.contains("bronze") || id.contains("stone")) return 6;
        return 4;
    }

    public static int level(ItemStack stack, Type type) {
        if (stack == null || type == null) return 0;
        Integer value = stack.getFromMetadataOrNull(type.metadataKey(), Codec.INTEGER);
        return value == null ? 0 : Math.max(0, value);
    }

    public static int strain(ItemStack stack) {
        if (stack == null) return 0;
        Integer value = stack.getFromMetadataOrNull(KEY_STRAIN, Codec.INTEGER);
        return value == null ? 0 : Math.max(0, value);
    }

    public static int partCount(ItemStack stack) {
        if (stack == null) return 0;
        Integer value = stack.getFromMetadataOrNull(KEY_PARTS, Codec.INTEGER);
        return value == null ? 0 : Math.max(0, value);
    }

    public static boolean canInstall(ItemStack stack, Grade grade) {
        return grade != null && isModifiableToolOrWeapon(stack) && strain(stack) + grade.strain() <= capacity(stack);
    }

    public static boolean canInstall(String rawItemId, Type type) {
        if (!isModifiableToolOrWeapon(rawItemId) || type == null) return false;

        return switch (type) {
            case TEMPO, GLUECK, SCHMELZEN, BEHUTSAMKEIT, MAGNETISMUS -> isMiningTool(rawItemId);
            case SCHADEN, LEBENSSAUGER -> isMeleeWeapon(rawItemId);
            case FERNSCHUSS -> isRangedWeapon(rawItemId);
            case ADLERAUGE, EWIGER_PFEIL -> isBowOrCrossbow(rawItemId);
            case ELEMENTARHERZ, SPARSAMKEIT -> isStaff(rawItemId);
            case REFLEXION, ABSORPTION -> isShield(rawItemId);
            case SCHUTZ, FERNSCHUTZ, UMWELTSCHUTZ -> isArmor(rawItemId);
            case FEDERFALL -> isBoots(rawItemId);
            case WASSERATMUNG, NACHTSICHT -> isHelmet(rawItemId);
            case ZWEITER_MAGEN, REGENERATION -> isChestplate(rawItemId);
            case SCHNELLSCHWIMMEN -> isGloves(rawItemId);
            case BRAND, GIFT, FROST, RUECKSTOSS, PLUENDERUNG, RAUSCH, GESCHICKLICHKEIT -> isMeleeWeapon(rawItemId) || isRangedWeapon(rawItemId);
            case HALTBARKEIT, STABILITAET, ROBUST -> isModifiableToolOrWeapon(rawItemId);
        };
    }

    public static boolean canInstall(ItemStack stack, Type type) {
        if (!isModifiableToolOrWeapon(stack) || type == null) return false;

        // Mutual exclusions
        if (type == Type.BRAND && level(stack, Type.GIFT) > 0) return false;
        if (type == Type.GIFT && level(stack, Type.BRAND) > 0) return false;
        if (type == Type.REFLEXION && level(stack, Type.ABSORPTION) > 0) return false;
        if (type == Type.ABSORPTION && level(stack, Type.REFLEXION) > 0) return false;

        return canInstall(stack.getItemId(), type);
    }

    public static boolean canInstall(ItemStack stack, Type type, Grade grade) {
        return canInstall(stack, grade) && canInstall(stack, type);
    }

    public static Type moduleType(String itemId) {
        if (itemId == null) return null;
        for (Type type : Type.values()) {
            for (Grade grade : Grade.values()) {
                if (type.moduleItemId(grade).equalsIgnoreCase(itemId)) return type;
            }
        }
        return null;
    }

    public static Grade moduleGrade(String itemId) {
        if (itemId == null) return null;
        for (Type type : Type.values()) {
            for (Grade grade : Grade.values()) {
                if (type.moduleItemId(grade).equalsIgnoreCase(itemId)) return grade;
            }
        }
        return null;
    }

    public static boolean isModifierModule(ItemStack stack) {
        return stack != null && moduleType(stack.getItemId()) != null && moduleGrade(stack.getItemId()) != null;
    }

    public static ItemStack install(ItemStack stack, Type type, Grade grade) {
        int oldPower = level(stack, type);
        int oldStrain = strain(stack);
        int oldParts = partCount(stack);
        double oldMax = stack.getMaxDurability();
        double oldDurability = stack.getDurability();

        ItemStack modified = stack
            .withMetadata(type.metadataKey(), Codec.INTEGER, oldPower + grade.power())
            .withMetadata(KEY_STRAIN, Codec.INTEGER, oldStrain + grade.strain())
            .withMetadata(KEY_PARTS, Codec.INTEGER, oldParts + 1)
            .withMetadata(partGradeKey(type, grade), Codec.INTEGER,
                installedGradeCount(stack, type, grade) + 1);

        if (type == Type.HALTBARKEIT && modified.getItem() != null) {
            double targetMax = modified.getItem().getMaxDurability()
                * durabilityMultiplier(oldPower + grade.power());
            double addedCapacity = Math.max(0.0, targetMax - oldMax);
            modified = modified.withMaxDurability(targetMax)
                .withDurability(Math.min(targetMax, oldDurability + addedCapacity));
        }
        return refreshDisplay(modified);
    }

    public static ItemStack transferToTier(ItemStack source, String targetItemId) {
        ItemStack target = new ItemStack(targetItemId, 1);
        int durabilityPower = level(source, Type.HALTBARKEIT);
        double baseMax = target.getItem() == null ? 100.0 : target.getItem().getMaxDurability();
        double targetMax = baseMax * durabilityMultiplier(durabilityPower);
        double durabilityRatio = source.getMaxDurability() <= 0.0
            ? 1.0
            : Math.max(0.0, Math.min(1.0, source.getDurability() / source.getMaxDurability()));

        for (Type type : Type.values()) {
            target = target.withMetadata(type.metadataKey(), Codec.INTEGER, level(source, type));
            for (Grade grade : Grade.values()) {
                int installed = installedGradeCount(source, type, grade);
                if (installed > 0) {
                    target = target.withMetadata(partGradeKey(type, grade), Codec.INTEGER, installed);
                }
            }
        }
        return refreshDisplay(target
            .withMetadata(KEY_STRAIN, Codec.INTEGER, strain(source))
            .withMetadata(KEY_PARTS, Codec.INTEGER, partCount(source))
            .withMaxDurability(targetMax)
            .withDurability(targetMax * durabilityRatio));
    }

    public static int freeCapacity(ItemStack stack) {
        return Math.max(0, capacity(stack) - strain(stack));
    }

    public static int installedGradeCount(ItemStack stack, Type type, Grade grade) {
        if (stack == null || type == null || grade == null) return 0;
        Integer value = stack.getFromMetadataOrNull(partGradeKey(type, grade), Codec.INTEGER);
        return value == null ? 0 : Math.max(0, value);
    }

    public static String buildCompactSummary(ItemStack stack) {
        StringBuilder sb = new StringBuilder();
        sb.append("Belastung ").append(strain(stack)).append("/").append(capacity(stack));
        if (level(stack, Type.TEMPO) > 0) sb.append(" · T+").append(level(stack, Type.TEMPO) * 20).append("%");
        if (level(stack, Type.HALTBARKEIT) > 0) sb.append(" · H+").append(level(stack, Type.HALTBARKEIT) * 15).append("%");
        if (level(stack, Type.GLUECK) > 0) sb.append(" · G+").append(level(stack, Type.GLUECK) * 25).append("%");
        if (level(stack, Type.SCHMELZEN) > 0) sb.append(" · AutoSmelt");
        if (level(stack, Type.MAGNETISMUS) > 0) sb.append(" · Magnet");
        if (level(stack, Type.BEHUTSAMKEIT) > 0) sb.append(" · SilkTouch");
        if (level(stack, Type.SCHADEN) > 0) sb.append(" · S+").append(level(stack, Type.SCHADEN) * 10).append("%");
        if (level(stack, Type.LEBENSSAUGER) > 0) sb.append(" · Lifesteal ").append(level(stack, Type.LEBENSSAUGER) * 10).append("%");
        if (level(stack, Type.SCHUTZ) > 0) sb.append(" · Schutz -").append(level(stack, Type.SCHUTZ) * 4).append("%");
        return sb.toString();
    }

    public static String buildSummary(ItemStack stack) {
        StringBuilder sb = new StringBuilder("Verbaut:");
        int count = 0;
        if (level(stack, Type.TEMPO) > 0) { sb.append(" Tempo +").append(level(stack, Type.TEMPO) * 20).append("%"); count++; }
        if (level(stack, Type.HALTBARKEIT) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Haltbarkeit +").append(level(stack, Type.HALTBARKEIT) * 15).append("%"); count++; }
        if (level(stack, Type.STABILITAET) > 0) { int red = (int) Math.round((1.0 - areaDurabilityMultiplier(stack)) * 100.0); if (count > 0) sb.append(" ·"); sb.append(" Verschleiß -").append(red).append("%"); count++; }
        if (level(stack, Type.GLUECK) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Glück +").append(level(stack, Type.GLUECK) * 25).append("%"); count++; }
        if (level(stack, Type.SCHMELZEN) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Auto-Schmelzen"); count++; }
        if (level(stack, Type.MAGNETISMUS) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Magnetismus"); count++; }
        if (level(stack, Type.BEHUTSAMKEIT) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Behutsamkeit (Silk Touch)"); count++; }
        if (level(stack, Type.SCHADEN) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Schärfe +").append(level(stack, Type.SCHADEN) * 10).append("%"); count++; }
        if (level(stack, Type.LEBENSSAUGER) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Lebenssauger ").append(level(stack, Type.LEBENSSAUGER) * 10).append("%"); count++; }
        if (level(stack, Type.SCHUTZ) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Schutz -").append(level(stack, Type.SCHUTZ) * 4).append("%"); count++; }
        if (level(stack, Type.FEDERFALL) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Federfall -").append(level(stack, Type.FEDERFALL) * 20).append("%"); count++; }
        if (level(stack, Type.PLUENDERUNG) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Plünderung +").append(level(stack, Type.PLUENDERUNG) * 25).append("%"); count++; }
        if (level(stack, Type.WASSERATMUNG) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Wasseratmung"); count++; }
        if (level(stack, Type.FERNSCHUSS) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Fernschuss +").append(level(stack, Type.FERNSCHUSS) * 10).append("%"); count++; }
        if (level(stack, Type.ADLERAUGE) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Adlerauge"); count++; }
        if (level(stack, Type.GESCHICKLICHKEIT) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Geschicklichkeit"); count++; }
        if (level(stack, Type.ROBUST) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Robust"); count++; }
        if (level(stack, Type.BRAND) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Brand"); count++; }
        if (level(stack, Type.FROST) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Frost (Slow 50%)"); count++; }
        if (level(stack, Type.EWIGER_PFEIL) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Ewiger Pfeil"); count++; }
        if (level(stack, Type.ELEMENTARHERZ) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Elementarherz"); count++; }
        if (level(stack, Type.SPARSAMKEIT) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Sparsamkeit -").append(level(stack, Type.SPARSAMKEIT) * 20).append("% Mana"); count++; }
        if (level(stack, Type.RUECKSTOSS) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Rückstoß +").append(level(stack, Type.RUECKSTOSS) * 25).append("%"); count++; }
        if (level(stack, Type.REFLEXION) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Reflexion ").append(level(stack, Type.REFLEXION) * 10).append("%"); count++; }
        if (level(stack, Type.ABSORPTION) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Absorption ").append(level(stack, Type.ABSORPTION) * 10).append("%"); count++; }
        if (level(stack, Type.SCHNELLSCHWIMMEN) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Schnellschwimmen +").append(level(stack, Type.SCHNELLSCHWIMMEN) * 25).append("%"); count++; }
        if (level(stack, Type.FERNSCHUTZ) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Fernschutz -").append(level(stack, Type.FERNSCHUTZ) * 4).append("%"); count++; }
        if (level(stack, Type.RAUSCH) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Rausch +").append(level(stack, Type.RAUSCH) * 15).append("%"); count++; }
        if (level(stack, Type.NACHTSICHT) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Nachtsicht"); count++; }
        if (level(stack, Type.GIFT) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Gift (3 Dmg/s)"); count++; }
        if (level(stack, Type.UMWELTSCHUTZ) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Umweltschutz -").append(level(stack, Type.UMWELTSCHUTZ) * 4).append("%"); count++; }
        if (level(stack, Type.ZWEITER_MAGEN) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Zweiter Magen +").append(level(stack, Type.ZWEITER_MAGEN) * 15).append("%"); count++; }
        if (level(stack, Type.REGENERATION) > 0) { if (count > 0) sb.append(" ·"); sb.append(" Regeneration (+0.5 HP/s)"); count++; }

        if (count == 0) sb.append(" Keine Modifikatoren");
        sb.append("\nBelastung ").append(strain(stack)).append("/").append(capacity(stack));
        return sb.toString();
    }

    public static ItemStack refreshDisplay(ItemStack stack) {
        if (!isModifiableToolOrWeapon(stack)) return stack;
        String summary = buildSummary(stack);
        return stack.withMetadata(ItemDisplayMetadata.KEYED_CODEC,
            new ItemDisplayMetadata(null, Message.raw(summary)));
    }

    private static String partGradeKey(Type type, Grade grade) {
        return KEY_PART_GRADE_PREFIX + type.commandName() + "_"
            + grade.assetSuffix().toLowerCase(Locale.ROOT);
    }

    public static int repairCostFor(double missingRatio, int baseCapacity, int strain, int partCount) {
        int baseCost = baseCapacity / 2;
        int strainCost = strain / 2;
        int partCost = partCount * 1;
        int fullCost = Math.max(1, baseCost + strainCost + partCost);
        return Math.max(1, (int) Math.ceil(fullCost * Math.max(0.0, Math.min(1.0, missingRatio))));
    }

    public static int repairCost(ItemStack tool) {
        if (tool == null) return 1;
        double maxD = tool.getMaxDurability();
        double curD = tool.getDurability();
        double missingRatio = (maxD <= 0.0) ? 0.0 : Math.max(0.0, Math.min(1.0, (maxD - curD) / maxD));
        int baseCap = capacity(tool);
        int curStrain = strain(tool);
        int curParts = partCount(tool);
        return repairCostFor(missingRatio, baseCap, curStrain, curParts);
    }

    public static String repairMaterial(ItemStack tool) {
        RecycleYield yield = calculateToolRecycleYield(tool);
        return (yield != null && yield.barItemId() != null) ? yield.barItemId() : "NonSinn_Resonanzsplitter";
    }

    public static String repairMaterialDisplay(ItemStack tool) {
        RecycleYield yield = calculateToolRecycleYield(tool);
        return (yield != null && yield.barDisplayName() != null) ? yield.barDisplayName() : "Resonanzsplitter";
    }

    public static double speedMultiplier(ItemStack stack) {
        return 1.0 + 0.20 * level(stack, Type.TEMPO);
    }

    public static double areaDurabilityMultiplier(ItemStack stack) {
        return Math.max(0.55, 1.0 - 0.10 * level(stack, Type.STABILITAET));
    }

    static double durabilityMultiplier(int power) {
        return 1.0 + 0.15 * Math.max(0, power);
    }
}
