package dev.nonsinn.miningtweaks;

import java.util.Locale;

public final class ToolProgression {
    public enum Family {
        HAMMER("NonSinn_Bergbauhammer_"),
        SHOVEL("NonSinn_Flaechenschaufel_");

        private final String prefix;

        Family(String prefix) {
            this.prefix = prefix;
        }

        String itemId(Tier tier) {
            return prefix + tier.assetSuffix;
        }
    }

    public enum Tier {
        COPPER("Copper", "kupfer"),
        IRON("Iron", "eisen"),
        THORIUM("Thorium", "thorium"),
        COBALT("Cobalt", "kobalt");

        private final String assetSuffix;
        private final String displayName;

        Tier(String assetSuffix, String displayName) {
            this.assetSuffix = assetSuffix;
            this.displayName = displayName;
        }

        public String displayName() {
            return displayName;
        }

        public Tier next() {
            return switch (this) {
                case COPPER -> IRON;
                case IRON -> THORIUM;
                case THORIUM -> COBALT;
                case COBALT -> null;
            };
        }

        public static Tier parse(String raw) {
            if (raw == null) return null;
            String normalized = raw.toLowerCase(Locale.ROOT)
                .replace("ä", "a").replace("ö", "o").replace("ü", "u");
            return switch (normalized) {
                case "kupfer", "copper" -> COPPER;
                case "eisen", "iron" -> IRON;
                case "thorium" -> THORIUM;
                case "kobalt", "cobalt" -> COBALT;
                default -> null;
            };
        }
    }

    public record Cost(String itemId, int quantity, String displayName) {
    }

    public record UpgradePlan(Family family, Tier from, Tier to, String targetItemId, Cost[] costs) {
    }

    private ToolProgression() {
    }

    public static UpgradePlan plan(String itemId, String requestedTier) {
        Family family = family(itemId);
        Tier from = tier(itemId);
        if (family == null || from == null || from.next() == null) return null;
        Tier requested = "weiter".equalsIgnoreCase(requestedTier) || "next".equalsIgnoreCase(requestedTier)
            ? from.next()
            : Tier.parse(requestedTier);
        if (requested != from.next()) return null;
        return new UpgradePlan(family, from, requested, family.itemId(requested), costs(family, requested));
    }

    static Family family(String itemId) {
        if (itemId == null) return null;
        String lower = itemId.toLowerCase(Locale.ROOT);
        if (lower.contains("nonsinn_bergbauhammer_")) return Family.HAMMER;
        if (lower.contains("nonsinn_flaechenschaufel_")) return Family.SHOVEL;
        return null;
    }

    static Tier tier(String itemId) {
        if (itemId == null) return null;
        String lower = itemId.toLowerCase(Locale.ROOT);
        if (lower.endsWith("_copper")) return Tier.COPPER;
        if (lower.endsWith("_iron")) return Tier.IRON;
        if (lower.endsWith("_thorium")) return Tier.THORIUM;
        if (lower.endsWith("_cobalt")) return Tier.COBALT;
        return null;
    }

    private static Cost[] costs(Family family, Tier target) {
        if (family == Family.HAMMER) {
            return switch (target) {
                case IRON -> new Cost[] {
                    cost("Ingredient_Bar_Iron", 10, "Eisenbarren"),
                    cost("Ingredient_Bar_Copper", 4, "Kupferbarren"),
                    cost("Ingredient_Crystal_Cyan", 2, "cyanfarbene Kristalle"),
                    cost("Ingredient_Leather_Light", 2, "leichtes Leder")
                };
                case THORIUM -> new Cost[] {
                    cost("Ingredient_Bar_Thorium", 12, "Thoriumbarren"),
                    cost("Ingredient_Bar_Iron", 5, "Eisenbarren"),
                    cost("Ingredient_Crystal_Purple", 3, "violette Kristalle"),
                    cost("Ingredient_Leather_Medium", 2, "mittleres Leder")
                };
                case COBALT -> new Cost[] {
                    cost("Ingredient_Bar_Cobalt", 14, "Kobaltbarren"),
                    cost("Ingredient_Bar_Thorium", 6, "Thoriumbarren"),
                    cost("Ingredient_Crystal_Blue", 3, "blaue Kristalle"),
                    cost("Ingredient_Leather_Heavy", 3, "schweres Leder")
                };
                default -> new Cost[0];
            };
        }
        return switch (target) {
            case IRON -> new Cost[] {
                cost("Ingredient_Bar_Iron", 6, "Eisenbarren"),
                cost("Ingredient_Bar_Copper", 2, "Kupferbarren"),
                cost("Ingredient_Crystal_Cyan", 1, "cyanfarbener Kristall"),
                cost("Ingredient_Leather_Light", 1, "leichtes Leder")
            };
            case THORIUM -> new Cost[] {
                cost("Ingredient_Bar_Thorium", 8, "Thoriumbarren"),
                cost("Ingredient_Bar_Iron", 3, "Eisenbarren"),
                cost("Ingredient_Crystal_Purple", 2, "violette Kristalle"),
                cost("Ingredient_Leather_Medium", 2, "mittleres Leder")
            };
            case COBALT -> new Cost[] {
                cost("Ingredient_Bar_Cobalt", 9, "Kobaltbarren"),
                cost("Ingredient_Bar_Thorium", 4, "Thoriumbarren"),
                cost("Ingredient_Crystal_Blue", 2, "blaue Kristalle"),
                cost("Ingredient_Leather_Heavy", 2, "schweres Leder")
            };
            default -> new Cost[0];
        };
    }

    private static Cost cost(String itemId, int quantity, String displayName) {
        return new Cost(itemId, quantity, displayName);
    }
}
