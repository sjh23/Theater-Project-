package util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptchaUtil {
    
    private static final int CAPTCHA_LENGTH = 5;
    private static final int IMAGE_WIDTH = 150;
    private static final int IMAGE_HEIGHT = 50;
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    
    private static Random random = new Random();

    public static String generateCaptchaText() {
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captcha.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return captcha.toString();
    }

    public static BufferedImage generateCaptchaImage(String captchaText) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 5; i++) {
            int x1 = random.nextInt(IMAGE_WIDTH);
            int y1 = random.nextInt(IMAGE_HEIGHT);
            int x2 = random.nextInt(IMAGE_WIDTH);
            int y2 = random.nextInt(IMAGE_HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }

        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(IMAGE_WIDTH);
            int y = random.nextInt(IMAGE_HEIGHT);
            g2d.fillOval(x, y, 2, 2);
        }

        Font font = new Font("Arial", Font.BOLD, 28);
        g2d.setFont(font);
        
        int x = 20;
        for (char c : captchaText.toCharArray()) {

            Color color = new Color(
                random.nextInt(100) + 50,
                random.nextInt(100) + 50,
                random.nextInt(100) + 50
            );
            g2d.setColor(color);

            int y = 30 + random.nextInt(10) - 5;

            double angle = (random.nextDouble() - 0.5) * 0.3;
            g2d.rotate(angle, x, y);
            g2d.drawString(String.valueOf(c), x, y);
            g2d.rotate(-angle, x, y);
            
            x += 25 + random.nextInt(5);
        }

        g2d.dispose();
        
        return image;
    }

    public static boolean verifyCaptcha(String userInput, String correctAnswer) {
        if (userInput == null || correctAnswer == null) {
            return false;
        }

        String normalizedInput = userInput.trim().replaceAll("\\s+", "").toUpperCase();
        String normalizedAnswer = correctAnswer.trim().replaceAll("\\s+", "").toUpperCase();
        
        return normalizedInput.equals(normalizedAnswer);
    }

    public static javax.swing.ImageIcon generateCaptchaImageIcon(String captchaText) {
        BufferedImage image = generateCaptchaImage(captchaText);
        return new javax.swing.ImageIcon(image);
    }
}

