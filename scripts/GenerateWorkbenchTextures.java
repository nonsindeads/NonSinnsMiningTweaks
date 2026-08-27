package dev.nonsinn.miningtweaks.scripts;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class GenerateWorkbenchTextures {

    public static void main(String[] args) throws Exception {
        String baseBlocks = "Common/Blocks/NonSinn/MiningTweaks";
        String baseIcons = "Common/Icons/ItemsGenerated";
        new File(baseBlocks).mkdirs();
        new File(baseIcons).mkdirs();

        // 1. Montagebank Texture (from /tmp/Weapon_Texture.png if available, else synthesized)
        File weaponTex = new File("/tmp/Weapon_Texture.png");
        if (weaponTex.exists()) {
            BufferedImage src = ImageIO.read(weaponTex);
            BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = out.createGraphics();
            g.drawImage(src, 0, 0, null);

            // Add cyan resonance lines and brass highlights to the weapon bench texture
            g.setColor(new Color(0, 220, 255, 180));
            g.fillRect(10, 15, 40, 2);
            g.fillRect(10, 35, 40, 2);
            g.fillRect(60, 20, 2, 30);

            // Add gold/brass fittings
            g.setColor(new Color(220, 175, 60, 200));
            g.fillRect(8, 13, 6, 6);
            g.fillRect(46, 13, 6, 6);
            g.fillRect(8, 33, 6, 6);
            g.fillRect(46, 33, 6, 6);

            g.dispose();
            ImageIO.write(out, "png", new File(baseBlocks, "Montagebank_Texture.png"));
        }

        // 2. Modifikatorwerkbank Texture (add vivid crystal resonance glow)
        File modTex = new File(baseBlocks, "Modifikatorwerkbank_Texture.png");
        if (modTex.exists()) {
            BufferedImage src = ImageIO.read(modTex);
            Graphics2D g = src.createGraphics();
            // Crystal glow overlay
            g.setColor(new Color(0, 240, 255, 160));
            g.fillRect(50, 8, 12, 14);
            g.setColor(new Color(180, 70, 240, 160));
            g.fillRect(65, 8, 12, 14);
            g.dispose();
            ImageIO.write(src, "png", modTex);
        }

        // 3. Bergbauwerkbank Texture (heavy steel & copper)
        File bergTex = new File(baseBlocks, "Bergbauwerkbank_Texture.png");
        if (bergTex.exists()) {
            BufferedImage src = ImageIO.read(bergTex);
            Graphics2D g = src.createGraphics();
            // Copper corner braces
            g.setColor(new Color(195, 110, 55, 200));
            g.fillRect(2, 2, 14, 6);
            g.fillRect(60, 2, 14, 6);
            g.dispose();
            ImageIO.write(src, "png", bergTex);
        }

        // 4. Generate Workbench Icons
        generateMontagebankIcon(new File(baseIcons, "NonSinn_Montagebank.png"));
        generateModifikatorwerkbankIcon(new File(baseIcons, "NonSinn_Modifikatorwerkbank.png"));
        generateBergbauwerkbankIcon(new File(baseIcons, "NonSinn_Bergbauwerkbank.png"));

        System.out.println("Workbench textures & icons generation complete.");
    }

    private static void generateMontagebankIcon(File file) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Heavy dark oak bench legs
        g.setColor(new Color(65, 42, 25));
        g.fillRect(14, 38, 8, 18);
        g.fillRect(42, 38, 8, 18);

        // Steel base feet
        g.setColor(new Color(45, 48, 55));
        g.fillRect(12, 54, 12, 5);
        g.fillRect(40, 54, 12, 5);

        // Bench top slab (Heavy Dark Steel with Brass trim)
        GradientPaint gp = new GradientPaint(10, 28, new Color(70, 75, 85), 54, 38, new Color(35, 38, 45));
        g.setPaint(gp);
        g.fillRoundRect(8, 28, 48, 12, 4, 4);

        g.setColor(new Color(220, 175, 60));
        g.drawRoundRect(8, 28, 48, 12, 4, 4);

        // Anvil & Vise on top
        g.setColor(new Color(120, 130, 145));
        g.fillRect(16, 18, 16, 10);
        g.fillRect(12, 18, 6, 5);
        g.fillRect(30, 20, 6, 6);

        // Glowing Cyan Resonance Socket in Center
        g.setColor(new Color(0, 230, 255));
        g.fillOval(38, 18, 10, 10);
        g.setColor(Color.WHITE);
        g.fillOval(41, 21, 4, 4);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void generateModifikatorwerkbankIcon(File file) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dark Wood legs & shelf
        g.setColor(new Color(55, 35, 20));
        g.fillRect(14, 36, 8, 20);
        g.fillRect(42, 36, 8, 20);
        g.fillRect(18, 44, 28, 4);

        // Rich Rune tabletop
        GradientPaint gp = new GradientPaint(8, 24, new Color(90, 55, 35), 56, 36, new Color(45, 28, 18));
        g.setPaint(gp);
        g.fillRoundRect(8, 24, 48, 12, 4, 4);

        // Arcane Backboard
        g.setColor(new Color(65, 42, 26));
        g.fillRect(12, 10, 40, 16);
        g.setColor(new Color(40, 25, 15));
        g.drawRect(12, 10, 40, 16);

        // Crystals on bench (Cyan and Purple)
        Polygon c1 = new Polygon(new int[]{20, 24, 22}, new int[]{24, 24, 12}, 3);
        g.setColor(new Color(0, 230, 255));
        g.fill(c1);

        Polygon c2 = new Polygon(new int[]{38, 44, 41}, new int[]{24, 24, 10}, 3);
        g.setColor(new Color(180, 70, 245));
        g.fill(c2);

        // Blueprint in center
        g.setColor(new Color(230, 220, 190));
        g.fillRect(26, 14, 12, 10);
        g.setColor(new Color(100, 140, 180));
        g.drawRect(26, 14, 12, 10);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void generateBergbauwerkbankIcon(File file) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Industrial Forged Legs
        g.setColor(new Color(50, 52, 58));
        g.fillRect(12, 36, 10, 20);
        g.fillRect(42, 36, 10, 20);

        // Tabletop with Heavy Copper Bracing
        GradientPaint gp = new GradientPaint(8, 24, new Color(110, 75, 45), 56, 36, new Color(60, 40, 25));
        g.setPaint(gp);
        g.fillRoundRect(8, 24, 48, 12, 3, 3);

        g.setColor(new Color(195, 110, 55));
        g.fillRect(8, 24, 8, 12);
        g.fillRect(48, 24, 8, 12);

        // Mining Hammer resting on bench
        g.setColor(new Color(130, 80, 45));
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(20, 20, 36, 12);

        g.setColor(new Color(180, 190, 205));
        g.fillRect(34, 8, 10, 8);
        g.setColor(new Color(40, 42, 48));
        g.drawRect(34, 8, 10, 8);

        g.dispose();
        ImageIO.write(img, "png", file);
    }
}
