package dev.nonsinn.miningtweaks.scripts;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class GenerateFaithfulHytaleAssets {

    public static void main(String[] args) throws Exception {
        String vDir = "/tmp/vanilla_assets";
        String outBlocks = "Common/Blocks/NonSinn/MiningTweaks";
        String outItems = "Common/Items/NonSinn/MiningTweaks";
        String outIcons = "Common/Icons/ItemsGenerated";

        new File(outBlocks).mkdirs();
        new File(outItems).mkdirs();
        new File(outIcons).mkdirs();

        // -------------------------------------------------------------
        // 1. IN-WORLD 3D WORKBENCH TEXTURES (Derived from Vanilla)
        // -------------------------------------------------------------

        // A) Bergbauwerkbank: Based on Workbench3_Texture.png
        BufferedImage wb3 = ImageIO.read(new File(vDir, "Common/Blocks/Benches/Workbench3_Texture.png"));
        BufferedImage bergbauTex = copyImage(wb3);
        Graphics2D gBerg = bergbauTex.createGraphics();
        // Warm copper corner plates on the tabletop (0, 0, 64, 32)
        gBerg.setColor(new Color(185, 105, 50, 210)); // Copper
        gBerg.fillRect(0, 0, 8, 4);
        gBerg.fillRect(0, 0, 4, 8);
        gBerg.fillRect(56, 0, 8, 4);
        gBerg.fillRect(60, 0, 4, 8);
        gBerg.fillRect(0, 28, 8, 4);
        gBerg.fillRect(0, 24, 4, 8);
        gBerg.fillRect(56, 28, 8, 4);
        gBerg.fillRect(60, 24, 4, 8);
        // Copper Rivets
        gBerg.setColor(new Color(235, 175, 110));
        gBerg.fillRect(2, 2, 2, 2);
        gBerg.fillRect(60, 2, 2, 2);
        gBerg.fillRect(2, 28, 2, 2);
        gBerg.fillRect(60, 28, 2, 2);
        gBerg.dispose();
        ImageIO.write(bergbauTex, "png", new File(outBlocks, "Bergbauwerkbank_Texture.png"));

        // B) Modifikatorwerkbank (Resonanzschmiede): Based on Workbench3_Texture.png
        BufferedImage modTex = copyImage(wb3);
        Graphics2D gMod = modTex.createGraphics();
        // Subtle engraved carved circle in the wood tabletop (32, 16)
        gMod.setColor(new Color(30, 20, 15, 160)); // Dark wood carving
        gMod.drawOval(26, 10, 12, 12);
        gMod.drawOval(28, 12, 8, 8);
        // Small glowing amethyst crystal dust in the carving
        gMod.setColor(new Color(160, 80, 220, 180));
        gMod.fillRect(31, 15, 2, 2);
        gMod.setColor(new Color(80, 200, 240, 180));
        gMod.fillRect(32, 16, 1, 1);
        gMod.dispose();
        ImageIO.write(modTex, "png", new File(outBlocks, "Modifikatorwerkbank_Texture.png"));

        // C) Montagebank: Based on Weapon_Texture.png
        BufferedImage wepTex = ImageIO.read(new File(vDir, "Common/Blocks/Benches/Weapon_Texture.png"));
        BufferedImage montageTex = copyImage(wepTex);
        Graphics2D gMont = montageTex.createGraphics();
        // Brass corner trims
        gMont.setColor(new Color(205, 165, 65, 210));
        gMont.fillRect(2, 2, 6, 4);
        gMont.fillRect(56, 2, 6, 4);
        gMont.fillRect(2, 42, 6, 4);
        gMont.fillRect(56, 42, 6, 4);
        // Small center socket ring on table
        gMont.setColor(new Color(70, 190, 230, 180));
        gMont.drawOval(30, 22, 6, 6);
        gMont.dispose();
        ImageIO.write(montageTex, "png", new File(outBlocks, "Montagebank_Texture.png"));

        // -------------------------------------------------------------
        // 2. IN-WORLD 3D TOOL TEXTURES & BLOCKYMODELS (Direct Vanilla Pickaxe/Shovel Files)
        // -------------------------------------------------------------
        copyFile(new File(vDir, "Common/Items/Tools/Pickaxe/Copper.blockymodel"), new File(outItems, "Bergbauhammer_Copper.blockymodel"));
        copyFile(new File(vDir, "Common/Items/Tools/Pickaxe/Iron.blockymodel"), new File(outItems, "Bergbauhammer_Iron.blockymodel"));
        copyFile(new File(vDir, "Common/Items/Tools/Pickaxe/Thorium.blockymodel"), new File(outItems, "Bergbauhammer_Thorium.blockymodel"));
        copyFile(new File(vDir, "Common/Items/Tools/Pickaxe/Cobalt.blockymodel"), new File(outItems, "Bergbauhammer_Cobalt.blockymodel"));

        copyFile(new File(vDir, "Common/Items/Tools/Pickaxe/Copper_Texture.png"), new File(outItems, "Bergbauhammer_Copper_Texture.png"));
        copyFile(new File(vDir, "Common/Items/Tools/Pickaxe/Iron_Texture.png"), new File(outItems, "Bergbauhammer_Iron_Texture.png"));
        copyFile(new File(vDir, "Common/Items/Tools/Pickaxe/Thorium_Texture.png"), new File(outItems, "Bergbauhammer_Thorium_Texture.png"));
        copyFile(new File(vDir, "Common/Items/Tools/Pickaxe/Cobalt_Texture.png"), new File(outItems, "Bergbauhammer_Cobalt_Texture.png"));

        copyFile(new File(vDir, "Common/Items/Tools/Shovel/Copper.blockymodel"), new File(outItems, "Flaechenschaufel_Copper.blockymodel"));
        copyFile(new File(vDir, "Common/Items/Tools/Shovel/Iron.blockymodel"), new File(outItems, "Flaechenschaufel_Iron.blockymodel"));
        copyFile(new File(vDir, "Common/Items/Tools/Shovel/Thorium.blockymodel"), new File(outItems, "Flaechenschaufel_Thorium.blockymodel"));
        copyFile(new File(vDir, "Common/Items/Tools/Shovel/Cobalt.blockymodel"), new File(outItems, "Flaechenschaufel_Cobalt.blockymodel"));

        copyFile(new File(vDir, "Common/Items/Tools/Shovel/Copper_Texture.png"), new File(outItems, "Flaechenschaufel_Copper_Texture.png"));
        copyFile(new File(vDir, "Common/Items/Tools/Shovel/Iron_Texture.png"), new File(outItems, "Flaechenschaufel_Iron_Texture.png"));
        copyFile(new File(vDir, "Common/Items/Tools/Shovel/Thorium_Texture.png"), new File(outItems, "Flaechenschaufel_Thorium_Texture.png"));
        copyFile(new File(vDir, "Common/Items/Tools/Shovel/Cobalt_Texture.png"), new File(outItems, "Flaechenschaufel_Cobalt_Texture.png"));

        // -------------------------------------------------------------
        // 3. ITEM ICONS (Derived from authentic Vanilla Icons)
        // -------------------------------------------------------------

        // A) Rohlinge: Based on vanilla metal bar icons
        createResonanzrohlingIcon(new File(vDir, "Common/Icons/ItemsGenerated/Ingredient_Bar_Copper.png"), new File(outIcons, "NonSinn_Resonanzrohling_Copper.png"), new Color(255, 140, 70));
        createResonanzrohlingIcon(new File(vDir, "Common/Icons/ItemsGenerated/Ingredient_Bar_Iron.png"), new File(outIcons, "NonSinn_Resonanzrohling_Iron.png"), new Color(100, 210, 255));
        createResonanzrohlingIcon(new File(vDir, "Common/Icons/ItemsGenerated/Ingredient_Bar_Thorium.png"), new File(outIcons, "NonSinn_Resonanzrohling_Thorium.png"), new Color(255, 200, 80));
        // Fallback default rohling
        copyFile(new File(outIcons, "NonSinn_Resonanzrohling_Copper.png"), new File(outIcons, "NonSinn_Resonanzrohling.png"));

        // B) Resonanzsplitter: Authentic vanilla amethyst crystal fragment icon
        copyFile(new File(vDir, "Common/Icons/ItemsGenerated/Ingredient_Crystal_Fragments_Purple.png"), new File(outIcons, "NonSinn_Resonanzsplitter.png"));

        // C) Modifier Modules: Authentic carved slate rune tablets
        generateRuneModuleIcons(outIcons);
        // Base action icons (used by crafting action cards)
        copyFile(new File(outIcons, "NonSinn_Mod_Tempo_Standard.png"), new File(outIcons, "NonSinn_Mod_Tempo_Base.png"));
        copyFile(new File(outIcons, "NonSinn_Mod_Haltbarkeit_Standard.png"), new File(outIcons, "NonSinn_Mod_Haltbarkeit_Base.png"));
        copyFile(new File(outIcons, "NonSinn_Mod_Stabilitaet_Standard.png"), new File(outIcons, "NonSinn_Mod_Stabilitaet_Base.png"));
        copyFile(new File(outIcons, "NonSinn_Resonanzrohling_Thorium.png"), new File(outIcons, "NonSinn_Resonanzfokuskern.png"));

        // D) Hammers Icons: Based on vanilla pickaxe icons with reinforced head silhouette
        createHammerIcon(new File(vDir, "Common/Icons/ItemsGenerated/Tool_Pickaxe_Copper.png"), new File(outIcons, "NonSinn_Bergbauhammer_Copper.png"), new Color(195, 100, 50));
        createHammerIcon(new File(vDir, "Common/Icons/ItemsGenerated/Tool_Pickaxe_Iron.png"), new File(outIcons, "NonSinn_Bergbauhammer_Iron.png"), new Color(175, 185, 200));
        createHammerIcon(new File(vDir, "Common/Icons/ItemsGenerated/Tool_Pickaxe_Thorium.png"), new File(outIcons, "NonSinn_Bergbauhammer_Thorium.png"), new Color(155, 120, 175));
        createHammerIcon(new File(vDir, "Common/Icons/ItemsGenerated/Tool_Pickaxe_Cobalt.png"), new File(outIcons, "NonSinn_Bergbauhammer_Cobalt.png"), new Color(75, 140, 230));

        // E) Shovels Icons: Based on vanilla shovel icons
        createShovelIcon(new File(vDir, "Common/Icons/ItemsGenerated/Tool_Shovel_Copper.png"), new File(outIcons, "NonSinn_Flaechenschaufel_Copper.png"), new Color(195, 100, 50));
        createShovelIcon(new File(vDir, "Common/Icons/ItemsGenerated/Tool_Shovel_Iron.png"), new File(outIcons, "NonSinn_Flaechenschaufel_Iron.png"), new Color(175, 185, 200));
        createShovelIcon(new File(vDir, "Common/Icons/ItemsGenerated/Tool_Shovel_Thorium.png"), new File(outIcons, "NonSinn_Flaechenschaufel_Thorium.png"), new Color(155, 120, 175));
        createShovelIcon(new File(vDir, "Common/Icons/ItemsGenerated/Tool_Shovel_Cobalt.png"), new File(outIcons, "NonSinn_Flaechenschaufel_Cobalt.png"), new Color(75, 140, 230));

        // F) Workbench Icons: Based on authentic vanilla bench icons
        copyFile(new File(vDir, "Common/Icons/ItemsGenerated/Bench_WorkBench.png"), new File(outIcons, "NonSinn_Bergbauwerkbank.png"));
        copyFile(new File(vDir, "Common/Icons/ItemsGenerated/Bench_Arcane.png"), new File(outIcons, "NonSinn_Modifikatorwerkbank.png"));
        copyFile(new File(vDir, "Common/Icons/ItemsGenerated/Bench_Weapon.png"), new File(outIcons, "NonSinn_Montagebank.png"));

        // G) Crafting Category Icons:
        String outCat = "Common/Icons/CraftingCategories/MiningTweaks";
        new File(outCat).mkdirs();
        copyFile(new File(outIcons, "NonSinn_Bergbauhammer_Iron.png"), new File(outCat, "Hammers.png"));
        copyFile(new File(outIcons, "NonSinn_Flaechenschaufel_Iron.png"), new File(outCat, "Shovels.png"));
        copyFile(new File(vDir, "Common/Icons/ItemsGenerated/Bench_WorkBench.png"), new File(outCat, "Stations.png"));
        copyFile(new File(outIcons, "NonSinn_Resonanzrohling_Iron.png"), new File(outCat, "ResonanceTechnology.png"));
        copyFile(new File(vDir, "Common/Icons/ItemsGenerated/Ingredient_Crystal_Fragments_Cyan.png"), new File(outCat, "Calibration.png"));
        copyFile(new File(vDir, "Common/Icons/ItemsGenerated/Ingredient_Crystal_Fragments_Purple.png"), new File(outCat, "Recycling.png"));
        copyFile(new File(vDir, "Common/Icons/ItemsGenerated/Bench_Weapon.png"), new File(outCat, "Installation.png"));

        System.out.println("Faithful Hytale vanilla-derived assets generation complete.");
    }

    private static void createResonanzrohlingIcon(File srcFile, File dstFile, Color glowColor) throws Exception {
        BufferedImage src = ImageIO.read(srcFile);
        BufferedImage out = copyImage(src);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = out.getWidth() / 2;
        int cy = out.getHeight() / 2;

        // Subtle etched rune circle on the bar
        g.setColor(new Color(20, 20, 25, 180));
        g.drawOval(cx - 7, cy - 7, 14, 14);
        g.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 200));
        g.drawOval(cx - 5, cy - 5, 10, 10);
        g.fillOval(cx - 2, cy - 2, 4, 4);

        g.dispose();
        ImageIO.write(out, "png", dstFile);
    }

    private static void createHammerIcon(File srcFile, File dstFile, Color headColor) throws Exception {
        BufferedImage src = ImageIO.read(srcFile);
        BufferedImage out = copyImage(src);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Subtly widen the pickaxe head into a heavy forged hammer face
        int w = out.getWidth();
        int h = out.getHeight();
        g.setColor(headColor);
        // Reinforced striking back
        g.fillRect(w / 2 - 8, 10, 12, 10);
        g.setColor(new Color(30, 32, 38, 180));
        g.drawRect(w / 2 - 8, 10, 12, 10);

        g.dispose();
        ImageIO.write(out, "png", dstFile);
    }

    private static void createShovelIcon(File srcFile, File dstFile, Color bladeColor) throws Exception {
        BufferedImage src = ImageIO.read(srcFile);
        BufferedImage out = copyImage(src);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = out.getWidth();
        int h = out.getHeight();
        // Reinforced spine in center
        g.setColor(bladeColor.brighter());
        g.fillRect(w / 2 - 2, h / 2 - 2, 4, 14);
        g.setColor(new Color(30, 32, 38, 140));
        g.drawRect(w / 2 - 2, h / 2 - 2, 4, 14);

        g.dispose();
        ImageIO.write(out, "png", dstFile);
    }

    private static void generateRuneModuleIcons(String outIcons) throws Exception {
        String[] types = {"Tempo", "Haltbarkeit", "Stabilitaet"};
        String[] grades = {"Standard", "Praezise", "Meisterlich"};

        for (String type : types) {
            for (int gIdx = 0; gIdx < 3; gIdx++) {
                String grade = grades[gIdx];
                int size = 64;
                BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. Handcrafted Slate Stone Tablet (Hytale cozy fantasy palette)
                RoundRectangle2D tablet = new RoundRectangle2D.Float(10, 8, 44, 48, 8, 8);
                // Dark slate gradient
                GradientPaint stoneGrad = new GradientPaint(10, 8, new Color(75, 80, 90), 54, 56, new Color(42, 45, 52));
                g.setPaint(stoneGrad);
                g.fill(tablet);

                // Chiseled stone border
                g.setColor(new Color(95, 102, 115));
                g.draw(tablet);
                g.setColor(new Color(30, 32, 38));
                g.drawRoundRect(11, 9, 42, 46, 6, 6);

                // 2. Carved Fantasy Emblem
                Color emblemColor = type.equals("Tempo") ? new Color(70, 205, 235) :
                                    type.equals("Haltbarkeit") ? new Color(175, 95, 230) :
                                    new Color(235, 185, 75);

                Color darkCarve = new Color(25, 28, 32, 220);

                if (type.equals("Tempo")) {
                    // Feather / Wing Emblem
                    Polygon wing = new Polygon();
                    wing.addPoint(32, 16);
                    wing.addPoint(42, 24);
                    wing.addPoint(36, 32);
                    wing.addPoint(24, 40);
                    wing.addPoint(28, 30);
                    wing.addPoint(22, 24);
                    g.setColor(darkCarve);
                    g.draw(wing);
                    g.setColor(emblemColor);
                    g.fill(wing);
                } else if (type.equals("Haltbarkeit")) {
                    // Shield Emblem
                    Polygon shield = new Polygon();
                    shield.addPoint(32, 16);
                    shield.addPoint(42, 20);
                    shield.addPoint(40, 34);
                    shield.addPoint(32, 42);
                    shield.addPoint(24, 34);
                    shield.addPoint(22, 20);
                    g.setColor(darkCarve);
                    g.draw(shield);
                    g.setColor(emblemColor);
                    g.fill(shield);
                    g.setColor(darkCarve);
                    g.fillRect(31, 22, 2, 16);
                } else {
                    // 3x3 Mountain / Anchor Matrix Emblem
                    g.setColor(darkCarve);
                    g.fillRect(21, 18, 22, 22);
                    for (int r = 0; r < 3; r++) {
                        for (int c = 0; c < 3; c++) {
                            g.setColor(emblemColor);
                            g.fillRect(23 + c * 6, 20 + r * 6, 4, 4);
                        }
                    }
                }

                // 3. Rank Pips (1 pip = Standard, 2 pips = Präzise, 3 pips = Meisterlich)
                Color pipColor = gIdx == 0 ? new Color(205, 135, 75) : // Bronze
                                 gIdx == 1 ? new Color(220, 225, 235) : // Silver
                                 new Color(255, 215, 60); // Gold

                int pips = gIdx + 1;
                int startX = 32 - (pips * 4);
                for (int p = 0; p < pips; p++) {
                    g.setColor(new Color(20, 20, 20, 200));
                    g.fillOval(startX + p * 8 - 1, 46, 6, 6);
                    g.setColor(pipColor);
                    g.fillOval(startX + p * 8, 47, 4, 4);
                }

                g.dispose();
                ImageIO.write(img, "png", new File(outIcons, "NonSinn_Mod_" + type + "_" + grade + ".png"));
            }
        }
    }

    private static BufferedImage copyImage(BufferedImage src) {
        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copy.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return copy;
    }

    private static void copyFile(File src, File dst) throws Exception {
        if (!src.exists()) return;
        dst.getParentFile().mkdirs();
        java.nio.file.Files.copy(src.toPath(), dst.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
}
