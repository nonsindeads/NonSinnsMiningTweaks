import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class PaletteVariants {
    private PaletteVariants() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            throw new IllegalArgumentException("usage: PaletteVariants <input.png> <output.png> <hex-rgb> <strength>");
        }
        BufferedImage source = ImageIO.read(new File(args[0]));
        BufferedImage output = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Color tint = Color.decode(args[2]);
        double strength = Double.parseDouble(args[3]);
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);
                int alpha = (argb >>> 24) & 0xff;
                if (alpha == 0) {
                    output.setRGB(x, y, argb);
                    continue;
                }
                Color original = new Color(argb, true);
                float[] hsb = Color.RGBtoHSB(original.getRed(), original.getGreen(), original.getBlue(), null);
                if (hsb[1] < 0.28f && hsb[2] > 0.07f) {
                    double light = Math.max(0.18, hsb[2]);
                    int tr = clamp((int) Math.round(tint.getRed() * light * 1.25));
                    int tg = clamp((int) Math.round(tint.getGreen() * light * 1.25));
                    int tb = clamp((int) Math.round(tint.getBlue() * light * 1.25));
                    int red = mix(original.getRed(), tr, strength);
                    int green = mix(original.getGreen(), tg, strength);
                    int blue = mix(original.getBlue(), tb, strength);
                    output.setRGB(x, y, new Color(red, green, blue, alpha).getRGB());
                } else {
                    output.setRGB(x, y, argb);
                }
            }
        }
        ImageIO.write(output, "png", new File(args[1]));
    }

    private static int mix(int from, int to, double strength) {
        return clamp((int) Math.round(from * (1.0 - strength) + to * strength));
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
