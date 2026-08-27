import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class GradeIconBadges {
    private GradeIconBadges() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) throw new IllegalArgumentException("base.png output.png grade(1-3)");
        BufferedImage source = ImageIO.read(new File(args[0]));
        int grade = Integer.parseInt(args[2]);
        BufferedImage output = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = output.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.drawImage(source, 0, 0, 64, 64, null);

        // Small forged studs communicate quality without a neon panel or sci-fi machinery.
        Color accent = grade == 1 ? new Color(166, 119, 72) : grade == 2
            ? new Color(176, 188, 187) : new Color(210, 166, 77);
        for (int i = 0; i < grade; i++) {
            int x = 47 + i * 5;
            graphics.setColor(new Color(45, 35, 26, 225));
            graphics.fillRect(x - 1, 51, 5, 7);
            graphics.setColor(accent);
            graphics.fillRect(x, 50, 3, 7);
            graphics.setColor(new Color(235, 220, 184, 220));
            graphics.fillRect(x, 50, 2, 1);
        }
        graphics.dispose();
        ImageIO.write(output, "png", new File(args[1]));
    }
}
