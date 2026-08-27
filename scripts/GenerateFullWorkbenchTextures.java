package dev.nonsinn.miningtweaks.scripts;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class GenerateFullWorkbenchTextures {

    public static void main(String[] args) throws Exception {
        String baseBlocks = "Common/Blocks/NonSinn/MiningTweaks";
        new File(baseBlocks).mkdirs();

        // 1. Render Modifikatorwerkbank (Resonanzschmiede) in-world texture
        renderModifikatorwerkbankTexture(new File(baseBlocks, "Modifikatorwerkbank_Texture.png"));

        // 2. Render Bergbauwerkbank in-world texture
        renderBergbauwerkbankTexture(new File(baseBlocks, "Bergbauwerkbank_Texture.png"));

        // 3. Render Montagebank in-world texture
        renderMontagebankTexture(new File(baseBlocks, "Montagebank_Texture.png"));

        System.out.println("In-world workbench textures generation complete.");
    }

    private static void renderModifikatorwerkbankTexture(File file) throws Exception {
        BufferedImage img = new BufferedImage(160, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Base Tabletop (0, 0, 64, 32): Dark Arcane Mahogany
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 64; x++) {
                int shade = 35 + ((x * 3 + y * 7) % 8);
                g.setColor(new Color(shade + 20, shade + 8, shade + 4));
                g.fillRect(x, y, 1, 1);
            }
        }

        // Metal Tabletop Corner Brackets
        g.setColor(new Color(190, 150, 70)); // Brass
        g.fillRect(0, 0, 8, 4);
        g.fillRect(0, 0, 4, 8);
        g.fillRect(56, 0, 8, 4);
        g.fillRect(60, 0, 4, 8);
        g.fillRect(0, 28, 8, 4);
        g.fillRect(0, 24, 4, 8);
        g.fillRect(56, 28, 8, 4);
        g.fillRect(60, 24, 4, 8);

        // Glowing Cyan & Purple Arcane Rune Inlay on Tabletop
        g.setColor(new Color(0, 210, 245, 230)); // Cyan Conduits
        g.drawLine(10, 16, 24, 16);
        g.drawLine(40, 16, 54, 16);
        g.drawLine(32, 4, 32, 10);
        g.drawLine(32, 22, 32, 28);

        // Central Resonance Core Ring on Tabletop
        g.setColor(new Color(180, 70, 250, 240)); // Amethyst Ring
        g.drawOval(25, 9, 14, 14);
        g.setColor(new Color(0, 240, 255, 255)); // Bright Core Center
        g.fillOval(29, 13, 6, 6);
        g.setColor(Color.WHITE);
        g.fillOval(31, 15, 2, 2);

        // Lower Front Panel (0, 32, 64, 26)
        for (int y = 32; y < 58; y++) {
            for (int x = 0; x < 64; x++) {
                int shade = 30 + ((x * 5 + y * 3) % 6);
                g.setColor(new Color(shade + 15, shade + 6, shade + 3));
                g.fillRect(x, y, 1, 1);
            }
        }
        // Drawer Handle & Brass Lock
        g.setColor(new Color(190, 150, 70));
        g.fillRect(28, 42, 8, 3);
        g.setColor(new Color(0, 220, 240));
        g.fillRect(31, 46, 2, 3);

        // Front Rim (0, 58, 64, 6)
        g.setColor(new Color(60, 40, 25));
        g.fillRect(0, 58, 64, 6);
        g.setColor(new Color(0, 210, 245));
        g.fillRect(8, 60, 48, 2);

        // Blueprint Sheet (80, 0, 32, 30)
        g.setColor(new Color(30, 65, 115)); // Arcane Blueprint Blue
        g.fillRect(80, 0, 32, 30);
        g.setColor(new Color(75, 135, 210));
        g.drawRect(80, 0, 31, 29);
        // Blueprint White/Cyan Lines
        g.setColor(new Color(180, 230, 255, 220));
        g.drawOval(88, 6, 16, 16);
        g.drawLine(84, 14, 108, 14);
        g.drawLine(96, 2, 96, 26);

        // Crystals & Vise on Right (112, 0, 48, 64)
        for (int y = 0; y < 64; y++) {
            for (int x = 112; x < 160; x++) {
                int shade = 45 + ((x + y * 2) % 10);
                g.setColor(new Color(shade, shade + 4, shade + 8));
                g.fillRect(x, y, 1, 1);
            }
        }
        // Glowing Crystal Shards on Bench Shelf
        g.setColor(new Color(0, 230, 255));
        g.fillPolygon(new int[]{120, 126, 123}, new int[]{25, 25, 8}, 3);
        g.setColor(new Color(190, 75, 255));
        g.fillPolygon(new int[]{130, 136, 133}, new int[]{25, 25, 6}, 3);

        // Legs / Shelves in remaining sections
        for (int y = 0; y < 64; y++) {
            for (int x = 64; x < 80; x++) {
                int shade = 35 + ((x * 2 + y * 4) % 6);
                g.setColor(new Color(shade + 18, shade + 8, shade + 4));
                g.fillRect(x, y, 1, 1);
            }
        }

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void renderBergbauwerkbankTexture(File file) throws Exception {
        BufferedImage img = new BufferedImage(160, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Base Tabletop (0, 0, 64, 32): Heavy Forged Dark Iron Plate
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 64; x++) {
                int shade = 55 + ((x * 4 + y * 6) % 8);
                g.setColor(new Color(shade, shade + 3, shade + 8));
                g.fillRect(x, y, 1, 1);
            }
        }

        // Heavy Riveted Copper Corner Plates
        g.setColor(new Color(195, 110, 55)); // Copper
        g.fillRect(0, 0, 12, 6);
        g.fillRect(0, 0, 6, 12);
        g.fillRect(52, 0, 12, 6);
        g.fillRect(58, 0, 6, 12);
        g.fillRect(0, 26, 12, 6);
        g.fillRect(0, 20, 6, 12);
        g.fillRect(52, 26, 12, 6);
        g.fillRect(58, 20, 6, 12);

        // Rivet Dots
        g.setColor(new Color(245, 190, 130));
        g.fillRect(3, 3, 2, 2);
        g.fillRect(59, 3, 2, 2);
        g.fillRect(3, 27, 2, 2);
        g.fillRect(59, 27, 2, 2);

        // 3x3 Grid Etching in Center of Bench Top
        g.setColor(new Color(30, 32, 38));
        g.fillRect(23, 7, 18, 18);
        g.setColor(new Color(195, 110, 55));
        g.drawRect(23, 7, 18, 18);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                g.setColor(new Color(0, 210, 240, 220)); // Glowing 3x3 matrix
                g.fillRect(25 + c * 5, 9 + r * 5, 4, 4);
                g.setColor(Color.WHITE);
                g.fillRect(26 + c * 5, 10 + r * 5, 2, 2);
            }
        }

        // Lower Front Panel (0, 32, 64, 26): Heavy Oak with Steel Straps
        for (int y = 32; y < 58; y++) {
            for (int x = 0; x < 64; x++) {
                int shade = 40 + ((x * 3 + y * 5) % 8);
                g.setColor(new Color(shade + 30, shade + 15, shade + 5));
                g.fillRect(x, y, 1, 1);
            }
        }
        // Horizontal Steel Reinforce Straps
        g.setColor(new Color(50, 55, 62));
        g.fillRect(0, 36, 64, 4);
        g.fillRect(0, 48, 64, 4);
        g.setColor(new Color(195, 110, 55));
        g.fillRect(28, 41, 8, 4); // Copper latch

        // Front Rim (0, 58, 64, 6)
        g.setColor(new Color(195, 110, 55));
        g.fillRect(0, 58, 64, 6);
        g.setColor(new Color(245, 190, 130));
        g.fillRect(2, 60, 60, 2);

        // Blueprint / Plan on Shelf (80, 0, 32, 30)
        g.setColor(new Color(220, 205, 175)); // Parchment Blueprint
        g.fillRect(80, 0, 32, 30);
        g.setColor(new Color(140, 95, 55));
        g.drawRect(80, 0, 31, 29);
        // Hammer Sketch on Blueprint
        g.setColor(new Color(90, 60, 35));
        g.drawLine(86, 22, 102, 8);
        g.fillRect(98, 6, 8, 6);

        // Heavy Tools on Right (112, 0, 48, 64)
        for (int y = 0; y < 64; y++) {
            for (int x = 112; x < 160; x++) {
                int shade = 40 + ((x + y) % 8);
                g.setColor(new Color(shade + 10, shade + 10, shade + 12));
                g.fillRect(x, y, 1, 1);
            }
        }
        // Steel Saw & Chisel
        g.setColor(new Color(180, 190, 205));
        g.fillRect(122, 10, 4, 30);
        g.fillRect(134, 15, 12, 6);

        // Wood texture for legs (64..80)
        for (int y = 0; y < 64; y++) {
            for (int x = 64; x < 80; x++) {
                int shade = 40 + ((x * 2 + y * 3) % 6);
                g.setColor(new Color(shade + 25, shade + 12, shade + 5));
                g.fillRect(x, y, 1, 1);
            }
        }

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void renderMontagebankTexture(File file) throws Exception {
        BufferedImage img = new BufferedImage(160, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Dark Polished Slate / Steel Base
        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 160; x++) {
                int shade = 42 + ((x * 3 + y * 4) % 8);
                g.setColor(new Color(shade, shade + 4, shade + 8));
                g.fillRect(x, y, 1, 1);
            }
        }

        // Tabletop Region (0, 0, 64, 48)
        GradientPaint gp = new GradientPaint(0, 0, new Color(55, 60, 70), 64, 48, new Color(30, 34, 40));
        g.setPaint(gp);
        g.fillRect(0, 0, 64, 48);

        // Golden Brass Edge Trim
        g.setColor(new Color(220, 175, 60));
        g.drawRect(2, 2, 60, 44);
        g.fillRect(0, 0, 6, 6);
        g.fillRect(58, 0, 6, 6);
        g.fillRect(0, 42, 6, 6);
        g.fillRect(58, 42, 6, 6);

        // Glowing Cyan Mounting Socket in Table Center (32, 24)
        g.setColor(new Color(0, 220, 255, 230));
        g.drawOval(20, 12, 24, 24);
        g.setColor(new Color(0, 240, 255, 200));
        g.fillOval(26, 18, 12, 12);
        g.setColor(Color.WHITE);
        g.fillOval(29, 21, 6, 6);

        // 4 Radial Resonance Conduits
        g.setColor(new Color(0, 220, 255, 200));
        g.drawLine(8, 24, 20, 24);
        g.drawLine(44, 24, 56, 24);
        g.drawLine(32, 4, 32, 12);
        g.drawLine(32, 36, 32, 44);

        // Anvil & Vise Area (80, 0, 80, 64)
        g.setColor(new Color(80, 88, 98));
        g.fillRect(85, 10, 40, 24);
        g.setColor(new Color(130, 140, 155));
        g.fillRect(95, 5, 20, 10);
        g.setColor(new Color(220, 175, 60));
        g.fillRect(90, 30, 30, 4);

        // Lower Drawers and Leather Tool Rolls (0, 64, 160, 64)
        for (int y = 64; y < 128; y++) {
            for (int x = 0; x < 160; x++) {
                int shade = 35 + ((x * 2 + y * 3) % 6);
                g.setColor(new Color(shade + 20, shade + 10, shade + 5)); // Rich dark leather/wood
                g.fillRect(x, y, 1, 1);
            }
        }
        // Brass Handles
        g.setColor(new Color(220, 175, 60));
        g.fillRect(25, 80, 14, 4);
        g.fillRect(75, 80, 14, 4);
        g.fillRect(125, 80, 14, 4);

        g.dispose();
        ImageIO.write(img, "png", file);
    }
}
