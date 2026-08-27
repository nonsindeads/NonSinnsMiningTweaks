package dev.nonsinn.miningtweaks.scripts;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class GenerateFullToolTextures {

    public static void main(String[] args) throws Exception {
        String baseDir = "Common/Items/NonSinn/MiningTweaks";
        new File(baseDir).mkdirs();

        // 1. Hammers
        renderHammerTexture(new File(baseDir, "Bergbauhammer_Copper_Texture.png"), Tier.COPPER);
        renderHammerTexture(new File(baseDir, "Bergbauhammer_Iron_Texture.png"), Tier.IRON);
        renderHammerTexture(new File(baseDir, "Bergbauhammer_Thorium_Texture.png"), Tier.THORIUM);
        renderHammerTexture(new File(baseDir, "Bergbauhammer_Cobalt_Texture.png"), Tier.COBALT);

        // 2. Shovels
        renderShovelTexture(new File(baseDir, "Flaechenschaufel_Copper_Texture.png"), Tier.COPPER);
        renderShovelTexture(new File(baseDir, "Flaechenschaufel_Iron_Texture.png"), Tier.IRON);
        renderShovelTexture(new File(baseDir, "Flaechenschaufel_Thorium_Texture.png"), Tier.THORIUM);
        renderShovelTexture(new File(baseDir, "Flaechenschaufel_Cobalt_Texture.png"), Tier.COBALT);

        System.out.println("In-world 3D tool textures generation complete.");
    }

    enum Tier {
        COPPER, IRON, THORIUM, COBALT
    }

    private static void renderHammerTexture(File file, Tier tier) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // 1. Wood Handle at top (0, 0, 64, 12)
        Color woodBase = tier == Tier.COPPER ? new Color(95, 55, 30) :
                         tier == Tier.IRON ? new Color(60, 42, 28) :
                         tier == Tier.THORIUM ? new Color(42, 28, 22) : new Color(35, 38, 48);
        for (int y = 0; y < 12; y++) {
            for (int x = 0; x < 64; x++) {
                int var = (x * 3 + y * 5) % 6;
                g.setColor(new Color(
                    Math.min(255, woodBase.getRed() + var * 3),
                    Math.min(255, woodBase.getGreen() + var * 2),
                    Math.min(255, woodBase.getBlue() + var)
                ));
                g.fillRect(x, y, 1, 1);
            }
        }
        // Leather grip wraps on handle
        g.setColor(new Color(130, 85, 45));
        for (int x = 8; x < 56; x += 6) {
            g.fillRect(x, 1, 2, 10);
        }
        // Pommel metal ring
        g.setColor(getAccentColor(tier));
        g.fillRect(56, 0, 8, 12);
        g.fillRect(0, 0, 4, 12);

        // 2. Hammer Head (0, 12, 54, 46)
        Color metalMain = getMetalMain(tier);
        Color metalLight = getMetalLight(tier);
        Color metalDark = getMetalDark(tier);

        for (int y = 12; y < 58; y++) {
            for (int x = 0; x < 54; x++) {
                int shade = (x * 2 + y * 4) % 10;
                float f = (float) shade / 10.0f;
                int r = (int) (metalDark.getRed() * (1 - f) + metalLight.getRed() * f);
                int gr = (int) (metalDark.getGreen() * (1 - f) + metalLight.getGreen() * f);
                int b = (int) (metalDark.getBlue() * (1 - f) + metalLight.getBlue() * f);
                g.setColor(new Color(r, gr, b));
                g.fillRect(x, y, 1, 1);
            }
        }

        // Metal Bevel Edges
        g.setColor(metalLight);
        g.drawRect(2, 14, 48, 40);
        g.setColor(metalDark);
        g.drawRect(3, 15, 46, 38);

        // Tier-Specific Inlay / Resonator Core on Hammer Face
        Color runeColor = getRuneColor(tier);
        g.setColor(runeColor);
        // Striking face reinforced matrix (center face at 14, 24, 24, 20)
        g.drawRoundRect(14, 24, 24, 20, 4, 4);
        g.fillRect(23, 31, 6, 6);
        g.setColor(Color.WHITE);
        g.fillRect(25, 33, 2, 2);

        // Rivets in corners
        g.setColor(getAccentColor(tier));
        g.fillRect(6, 18, 3, 3);
        g.fillRect(42, 18, 3, 3);
        g.fillRect(6, 46, 3, 3);
        g.fillRect(42, 46, 3, 3);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void renderShovelTexture(File file, Tier tier) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // 1. Wood Handle regions (top: 0, 0, 64, 18; bottom: 0, 52, 24, 12)
        Color woodBase = tier == Tier.COPPER ? new Color(95, 55, 30) :
                         tier == Tier.IRON ? new Color(60, 42, 28) :
                         tier == Tier.THORIUM ? new Color(42, 28, 22) : new Color(35, 38, 48);

        for (int y = 0; y < 18; y++) {
            for (int x = 0; x < 64; x++) {
                int var = (x * 4 + y * 3) % 6;
                g.setColor(new Color(
                    Math.min(255, woodBase.getRed() + var * 3),
                    Math.min(255, woodBase.getGreen() + var * 2),
                    Math.min(255, woodBase.getBlue() + var)
                ));
                g.fillRect(x, y, 1, 1);
            }
        }
        for (int y = 52; y < 64; y++) {
            for (int x = 0; x < 24; x++) {
                g.setColor(woodBase);
                g.fillRect(x, y, 1, 1);
            }
        }

        // Metal Bands on Handle Grip
        g.setColor(getAccentColor(tier));
        g.fillRect(22, 2, 4, 14);
        g.fillRect(38, 2, 4, 14);

        // 2. Scoop Blade (8, 18, 52, 34)
        Color metalMain = getMetalMain(tier);
        Color metalLight = getMetalLight(tier);
        Color metalDark = getMetalDark(tier);

        for (int y = 18; y < 52; y++) {
            for (int x = 6; x < 58; x++) {
                int shade = (x * 3 + y * 3) % 8;
                float f = (float) shade / 8.0f;
                int r = (int) (metalDark.getRed() * (1 - f) + metalLight.getRed() * f);
                int gr = (int) (metalDark.getGreen() * (1 - f) + metalLight.getGreen() * f);
                int b = (int) (metalDark.getBlue() * (1 - f) + metalLight.getBlue() * f);
                g.setColor(new Color(r, gr, b));
                g.fillRect(x, y, 1, 1);
            }
        }

        // Scoop Outer Rim & Sharp Cutting Edge
        g.setColor(metalLight);
        g.drawRoundRect(8, 20, 48, 30, 6, 6);
        g.setColor(new Color(255, 255, 255, 180));
        g.drawLine(14, 48, 50, 48); // Sharp leading edge

        // Central Spine Reinforcement
        g.setColor(getAccentColor(tier));
        g.fillRect(29, 20, 6, 26);

        // Glowing Tier Rune Core on Spine
        g.setColor(getRuneColor(tier));
        g.fillOval(30, 28, 4, 10);
        g.setColor(Color.WHITE);
        g.fillOval(31, 31, 2, 4);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static Color getMetalMain(Tier tier) {
        switch (tier) {
            case COPPER: return new Color(195, 100, 50);
            case IRON: return new Color(85, 92, 105);
            case THORIUM: return new Color(105, 80, 120);
            case COBALT: return new Color(40, 85, 150);
        }
        return Color.GRAY;
    }

    private static Color getMetalLight(Tier tier) {
        switch (tier) {
            case COPPER: return new Color(245, 160, 100);
            case IRON: return new Color(165, 175, 195);
            case THORIUM: return new Color(175, 140, 195);
            case COBALT: return new Color(90, 160, 245);
        }
        return Color.WHITE;
    }

    private static Color getMetalDark(Tier tier) {
        switch (tier) {
            case COPPER: return new Color(130, 55, 25);
            case IRON: return new Color(45, 48, 55);
            case THORIUM: return new Color(55, 40, 65);
            case COBALT: return new Color(20, 45, 85);
        }
        return Color.BLACK;
    }

    private static Color getAccentColor(Tier tier) {
        switch (tier) {
            case COPPER: return new Color(225, 180, 90); // Bronze
            case IRON: return new Color(200, 205, 215);  // Polished Steel
            case THORIUM: return new Color(245, 185, 55); // Amber Gold
            case COBALT: return new Color(0, 230, 255);   // Electric Cyan
        }
        return Color.LIGHT_GRAY;
    }

    private static Color getRuneColor(Tier tier) {
        switch (tier) {
            case COPPER: return new Color(255, 130, 60, 230);
            case IRON: return new Color(100, 220, 255, 230);
            case THORIUM: return new Color(255, 190, 60, 240);
            case COBALT: return new Color(0, 245, 255, 255);
        }
        return Color.CYAN;
    }
}
