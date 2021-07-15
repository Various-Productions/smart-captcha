package me.lucacw.smartcaptcha.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
public final class ImageUtils {

    protected static Font font = new Font("Verdana", Font.ITALIC
            | Font.BOLD, 35);

    public static BufferedImage createImage(String code, int width, int height) {
        char[] chars = code.toCharArray();

        BufferedImage bi = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        AlphaComposite ac3;
        Color color;
        int len = chars.length;
        g.setColor(Color.decode("#2b3e51"));
        g.fillRect(0, 0, width, height);
        for (int i = 0; i < 15; i++) {
            color = color(150, 250);
            g.setColor(color);
            g.drawOval(num(width), num(height), 5 + num(10),
                    5 + num(10));
        }
        g.setFont(font);
        int h = height - ((height - font.getSize()) >> 1), w = width
                / len, size = w - font.getSize() + 1;
        for (int i = 0; i < len; i++) {
            //
            ac3 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    0.7f);
            g.setComposite(ac3);

            color = new Color(20 + num(110), 30 + num(110),
                    30 + num(110));
            g.setColor(color);
            g.drawString(chars[i] + "", (width - (len - i) * w) + size,
                    h - 4);
        }

        return bi;
    }

    protected static Color color(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + num(bc - fc);
        int g = fc + num(bc - fc);
        int b = fc + num(bc - fc);
        return new Color(r, g, b);
    }

    public static int num(int num) {
        return (new Random()).nextInt(num);
    }

}
