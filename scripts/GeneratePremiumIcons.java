package dev.nonsinn.miningtweaks.scripts;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class GeneratePremiumIcons {

    public static void main(String[] args) throws Exception {
        String baseDir = args.length > 0 ? args[0] : "Common/Icons/ItemsGenerated";
        new File(baseDir).mkdirs();

        // 1. Generate Blanks (Copper, Iron, Thorium)
        generateBlank(new File(baseDir, "NonSinn_Resonanzrohling_Copper.png"), new Color(195, 110, 60), new Color(130, 65, 30), new Color(0, 220, 240));
        generateBlank(new File(baseDir, "NonSinn_Resonanzrohling.png"), new Color(195, 110, 60), new Color(130, 65, 30), new Color(0, 220, 240));
        generateBlank(new File(baseDir, "NonSinn_Resonanzrohling_Iron.png"), new Color(200, 210, 220), new Color(100, 110, 125), new Color(0, 230, 255));
        generateBlank(new File(baseDir, "NonSinn_Resonanzrohling_Thorium.png"), new Color(150, 90, 190), new Color(75, 35, 110), new Color(0, 255, 220));

        // 2. Generate Splitters
        generateSplitter(new File(baseDir, "NonSinn_Resonanzsplitter.png"));

        // 3. Generate Modifier Runestones
        // Tempo (Cyan)
        generateModifier(new File(baseDir, "NonSinn_Mod_Tempo_Base.png"), "TEMPO", 0, new Color(0, 210, 240), new Color(180, 250, 255));
        generateModifier(new File(baseDir, "NonSinn_Mod_Tempo_Standard.png"), "TEMPO", 1, new Color(0, 210, 240), new Color(180, 250, 255));
        generateModifier(new File(baseDir, "NonSinn_Mod_Tempo_Praezise.png"), "TEMPO", 2, new Color(0, 210, 240), new Color(180, 250, 255));
        generateModifier(new File(baseDir, "NonSinn_Mod_Tempo_Meisterlich.png"), "TEMPO", 3, new Color(0, 210, 240), new Color(180, 250, 255));

        // Haltbarkeit (Purple)
        generateModifier(new File(baseDir, "NonSinn_Mod_Haltbarkeit_Base.png"), "HALTBARKEIT", 0, new Color(175, 75, 230), new Color(235, 180, 255));
        generateModifier(new File(baseDir, "NonSinn_Mod_Haltbarkeit_Standard.png"), "HALTBARKEIT", 1, new Color(175, 75, 230), new Color(235, 180, 255));
        generateModifier(new File(baseDir, "NonSinn_Mod_Haltbarkeit_Praezise.png"), "HALTBARKEIT", 2, new Color(175, 75, 230), new Color(235, 180, 255));
        generateModifier(new File(baseDir, "NonSinn_Mod_Haltbarkeit_Meisterlich.png"), "HALTBARKEIT", 3, new Color(175, 75, 230), new Color(235, 180, 255));

        // Stabilitaet (Golden White)
        generateModifier(new File(baseDir, "NonSinn_Mod_Stabilitaet_Base.png"), "STABILITAET", 0, new Color(235, 195, 80), new Color(255, 245, 200));
        generateModifier(new File(baseDir, "NonSinn_Mod_Stabilitaet_Standard.png"), "STABILITAET", 1, new Color(235, 195, 80), new Color(255, 245, 200));
        generateModifier(new File(baseDir, "NonSinn_Mod_Stabilitaet_Praezise.png"), "STABILITAET", 2, new Color(235, 195, 80), new Color(255, 245, 200));
        generateModifier(new File(baseDir, "NonSinn_Mod_Stabilitaet_Meisterlich.png"), "STABILITAET", 3, new Color(235, 195, 80), new Color(255, 245, 200));

        // 4. Generate Clean 3x3 Hammers
        generateHammer(new File(baseDir, "NonSinn_Bergbauhammer_Copper.png"), new Color(195, 110, 60), new Color(120, 60, 25));
        generateHammer(new File(baseDir, "NonSinn_Bergbauhammer_Iron.png"), new Color(205, 215, 225), new Color(105, 115, 128));
        generateHammer(new File(baseDir, "NonSinn_Bergbauhammer_Thorium.png"), new Color(155, 95, 195), new Color(80, 40, 115));
        generateHammer(new File(baseDir, "NonSinn_Bergbauhammer_Cobalt.png"), new Color(65, 140, 240), new Color(25, 65, 150));

        // 5. Generate Clean 3x3 Shovels
        generateShovel(new File(baseDir, "NonSinn_Flaechenschaufel_Copper.png"), new Color(195, 110, 60), new Color(120, 60, 25));
        generateShovel(new File(baseDir, "NonSinn_Flaechenschaufel_Iron.png"), new Color(205, 215, 225), new Color(105, 115, 128));
        generateShovel(new File(baseDir, "NonSinn_Flaechenschaufel_Thorium.png"), new Color(155, 95, 195), new Color(80, 40, 115));
        generateShovel(new File(baseDir, "NonSinn_Flaechenschaufel_Cobalt.png"), new Color(65, 140, 240), new Color(25, 65, 150));

        System.out.println("Premium icon generation complete.");
    }

    private static void generateBlank(File file, Color primary, Color dark, Color coreGlow) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Outer dark metal rim
        g.setColor(new Color(38, 42, 48));
        g.fillOval(8, 8, 48, 48);

        // Tier Metal Ring
        GradientPaint gp = new GradientPaint(10, 10, primary, 54, 54, dark);
        g.setPaint(gp);
        g.fillOval(11, 11, 42, 42);

        // Inset bevel
        g.setColor(new Color(25, 28, 32));
        g.fillOval(17, 17, 30, 30);

        // Glowing center core matrix
        RadialGradientPaint rgp = new RadialGradientPaint(32, 32, 14,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{Color.WHITE, coreGlow, new Color(coreGlow.getRed(), coreGlow.getGreen(), coreGlow.getBlue(), 0)});
        g.setPaint(rgp);
        g.fillOval(18, 18, 28, 28);

        // Four metal clasps
        g.setColor(primary);
        g.fillRect(30, 7, 4, 8);
        g.fillRect(30, 49, 4, 8);
        g.fillRect(7, 30, 8, 4);
        g.fillRect(49, 30, 8, 4);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void generateSplitter(File file) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Central Crystal 1
        Polygon p1 = new Polygon(new int[]{32, 42, 38, 26}, new int[]{8, 26, 52, 36}, 4);
        GradientPaint gp1 = new GradientPaint(26, 8, new Color(240, 210, 255), 42, 52, new Color(130, 40, 210));
        g.setPaint(gp1);
        g.fill(p1);

        // Crystal 2 (Left)
        Polygon p2 = new Polygon(new int[]{22, 28, 16, 12}, new int[]{22, 38, 48, 30}, 4);
        GradientPaint gp2 = new GradientPaint(12, 22, new Color(210, 180, 255), 28, 48, new Color(100, 30, 180));
        g.setPaint(gp2);
        g.fill(p2);

        // Crystal 3 (Right)
        Polygon p3 = new Polygon(new int[]{42, 52, 46, 36}, new int[]{26, 36, 50, 42}, 4);
        GradientPaint gp3 = new GradientPaint(36, 26, new Color(210, 180, 255), 52, 50, new Color(90, 20, 170));
        g.setPaint(gp3);
        g.fill(p3);

        // Golden Binding Clasp
        g.setColor(new Color(230, 180, 70));
        g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(18, 32, 28, 12, 190, 170);

        // Sparkle / Highlight
        g.setColor(Color.WHITE);
        g.fillOval(31, 14, 4, 6);
        g.fillOval(20, 28, 3, 4);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void generateModifier(File file, String type, int tier, Color color, Color highlight) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Outer Dark Slate Base Tablet
        Polygon hex = createHexagon(32, 32, 26);
        g.setColor(new Color(32, 35, 42));
        g.fill(hex);
        g.setColor(new Color(55, 60, 72));
        g.setStroke(new BasicStroke(2.0f));
        g.draw(hex);

        // Inner Metal Frame
        Polygon innerHex = createHexagon(32, 32, 21);
        Color frameColor = tier == 3 ? new Color(230, 185, 80) : (tier == 2 ? new Color(200, 215, 225) : new Color(130, 138, 150));
        g.setColor(frameColor);
        g.setStroke(new BasicStroke(2.2f));
        g.draw(innerHex);

        // Glowing Rune/Emblem in Center
        if ("TEMPO".equals(type)) {
            // Wing / Speed Bolt
            Path2D bolt = new Path2D.Double();
            bolt.moveTo(34, 15);
            bolt.lineTo(24, 32);
            bolt.lineTo(31, 32);
            bolt.lineTo(28, 49);
            bolt.lineTo(40, 29);
            bolt.lineTo(33, 29);
            bolt.closePath();
            g.setColor(color);
            g.fill(bolt);
            g.setColor(highlight);
            g.setStroke(new BasicStroke(1.2f));
            g.draw(bolt);
        } else if ("HALTBARKEIT".equals(type)) {
            // Heavy Shield Core
            Polygon shield = new Polygon(
                new int[]{32, 43, 41, 32, 23, 21},
                new int[]{16, 20, 36, 48, 36, 20}, 6);
            g.setColor(color);
            g.fill(shield);
            g.setColor(highlight);
            g.setStroke(new BasicStroke(1.5f));
            g.draw(shield);
            // Inset Cross
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(30, 23, 4, 16);
            g.fillRect(25, 28, 14, 4);
        } else {
            // Stabilitaet: 3x3 Lattice / Diamond Matrix
            for (int r = -1; r <= 1; r++) {
                for (int c = -1; c <= 1; c++) {
                    int cx = 32 + c * 7;
                    int cy = 32 + r * 7;
                    g.setColor(color);
                    g.fillRect(cx - 2, cy - 2, 5, 5);
                    g.setColor(highlight);
                    g.drawRect(cx - 2, cy - 2, 5, 5);
                }
            }
        }

        // Tier Star / Facet Badges on Bottom
        if (tier > 0) {
            int startX = 32 - (tier - 1) * 5;
            for (int i = 0; i < tier; i++) {
                int px = startX + i * 10;
                int py = 54;
                g.setColor(new Color(20, 22, 26, 220));
                g.fillOval(px - 4, py - 4, 8, 8);
                g.setColor(tier == 3 ? new Color(255, 215, 60) : (tier == 2 ? new Color(220, 235, 245) : new Color(205, 125, 70)));
                g.fillOval(px - 3, py - 3, 6, 6);
                g.setColor(Color.WHITE);
                g.fillOval(px - 1, py - 2, 2, 2);
            }
        }

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void generateHammer(File file, Color headColor, Color headDark) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Wooden Handle (Diagonal)
        g.setColor(new Color(110, 65, 35));
        g.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(16, 48, 42, 22);

        // Leather Grip Banding
        g.setColor(new Color(160, 110, 65));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(18, 46, 22, 42);
        g.drawLine(24, 40, 28, 36);

        // Heavy Hammer Head
        AffineTransform old = g.getTransform();
        g.translate(42, 20);
        g.rotate(Math.toRadians(45));

        // Head Body
        GradientPaint gp = new GradientPaint(-14, -10, headColor, 14, 10, headDark);
        g.setPaint(gp);
        g.fillRoundRect(-14, -10, 28, 20, 4, 4);

        // Head Border
        g.setColor(new Color(30, 32, 36));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(-14, -10, 28, 20, 4, 4);

        // Center Reinforce Ring
        g.setColor(new Color(230, 185, 75));
        g.fillRect(-4, -11, 8, 22);

        // 3x3 Engraved Rune Grid on Striking Face
        g.setColor(new Color(0, 220, 250, 220));
        g.fillRect(8, -4, 3, 3);
        g.fillRect(8, 1, 3, 3);
        g.fillRect(-11, -4, 3, 3);
        g.fillRect(-11, 1, 3, 3);

        g.setTransform(old);
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void generateShovel(File file, Color metalColor, Color metalDark) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Wooden Handle (Diagonal)
        g.setColor(new Color(110, 65, 35));
        g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(15, 49, 44, 20);

        // Handle Grip Top
        g.setColor(new Color(160, 110, 65));
        g.drawOval(11, 45, 7, 7);

        // Wide Spade Blade
        AffineTransform old = g.getTransform();
        g.translate(44, 20);
        g.rotate(Math.toRadians(45));

        // Broad curved spade polygon
        Polygon spade = new Polygon(
            new int[]{-12, 12, 14, 0, -14},
            new int[]{-8, -8, 14, 22, 14}, 5);

        GradientPaint gp = new GradientPaint(-12, -8, metalColor, 12, 22, metalDark);
        g.setPaint(gp);
        g.fill(spade);

        // Blade Border
        g.setColor(new Color(30, 32, 36));
        g.setStroke(new BasicStroke(1.6f));
        g.draw(spade);

        // Socket Collar
        g.setColor(new Color(230, 185, 75));
        g.fillRect(-5, -11, 10, 6);

        // Central Resonance Line / Rune
        g.setColor(new Color(0, 220, 250, 220));
        g.fillRect(-1, -4, 2, 16);

        g.setTransform(old);
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static Polygon createHexagon(int cx, int cy, int r) {
        int[] x = new int[6];
        int[] y = new int[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            x[i] = (int) Math.round(cx + r * Math.cos(angle));
            y[i] = (int) Math.round(cy + r * Math.sin(angle));
        }
        return new Polygon(x, y, 6);
    }
}
