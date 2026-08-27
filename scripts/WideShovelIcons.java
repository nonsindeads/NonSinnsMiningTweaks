import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class WideShovelIcons {
    private WideShovelIcons() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: WideShovelIcons <input.png> <output.png>");
        }
        BufferedImage source = ImageIO.read(new File(args[0]));
        BufferedImage output = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = output.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.drawImage(source, 0, 0, null);

        // A compact 3x3 badge distinguishes area shovels without replacing Hytale's tier artwork.
        graphics.setColor(new Color(12, 18, 24, 210));
        graphics.fillRoundRect(43, 43, 17, 17, 3, 3);
        graphics.setColor(new Color(90, 226, 255, 255));
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                graphics.fillRect(46 + column * 4, 46 + row * 4, 2, 2);
            }
        }
        graphics.dispose();
        ImageIO.write(output, "png", new File(args[1]));
    }
}
