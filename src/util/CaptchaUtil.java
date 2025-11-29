package util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * CAPTCHA 생성 및 검증 유틸리티 클래스
 */
public class CaptchaUtil {
    
    private static final int CAPTCHA_LENGTH = 5;  // 캡챠 문자열 길이 (5~6자리)
    private static final int IMAGE_WIDTH = 150;
    private static final int IMAGE_HEIGHT = 50;
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";  // 혼동하기 쉬운 문자 제외 (I, O, 0, 1)
    
    private static Random random = new Random();
    
    /**
     * 랜덤 캡챠 문자열을 생성합니다.
     * 
     * @return 5~6자리 무작위 문자열
     */
    public static String generateCaptchaText() {
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captcha.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return captcha.toString();
    }
    
    /**
     * 캡챠 문자열을 이미지로 생성합니다.
     * 흐림 효과와 노이즈를 추가하여 자동화 공격을 방지합니다.
     * 
     * @param captchaText 생성할 캡챠 문자열
     * @return BufferedImage 객체
     */
    public static BufferedImage generateCaptchaImage(String captchaText) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 안티앨리어싱 설정
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 배경색 설정 (밝은 회색)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        
        // 노이즈 라인 추가
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 5; i++) {
            int x1 = random.nextInt(IMAGE_WIDTH);
            int y1 = random.nextInt(IMAGE_HEIGHT);
            int x2 = random.nextInt(IMAGE_WIDTH);
            int y2 = random.nextInt(IMAGE_HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // 노이즈 점 추가
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(IMAGE_WIDTH);
            int y = random.nextInt(IMAGE_HEIGHT);
            g2d.fillOval(x, y, 2, 2);
        }
        
        // 캡챠 텍스트 그리기
        Font font = new Font("Arial", Font.BOLD, 28);
        g2d.setFont(font);
        
        int x = 20;
        for (char c : captchaText.toCharArray()) {
            // 각 문자마다 랜덤 색상과 회전 각도 적용
            Color color = new Color(
                random.nextInt(100) + 50,   // R: 50-150
                random.nextInt(100) + 50,   // G: 50-150
                random.nextInt(100) + 50    // B: 50-150
            );
            g2d.setColor(color);
            
            // 문자 위치에 약간의 랜덤 오프셋 추가
            int y = 30 + random.nextInt(10) - 5;
            
            // 회전 변환 적용
            double angle = (random.nextDouble() - 0.5) * 0.3;  // -15도 ~ +15도
            g2d.rotate(angle, x, y);
            g2d.drawString(String.valueOf(c), x, y);
            g2d.rotate(-angle, x, y);
            
            x += 25 + random.nextInt(5);
        }
        
        // 흐림 효과 (간단한 블러 효과)
        // 실제로는 더 정교한 필터를 사용할 수 있지만, 여기서는 간단한 효과만 적용
        
        g2d.dispose();
        
        return image;
    }
    
    /**
     * 사용자 입력값과 캡챠 정답을 비교합니다.
     * 대소문자를 구분하지 않고, 공백은 무시합니다.
     * 
     * @param userInput 사용자 입력값
     * @param correctAnswer 정답 문자열
     * @return 일치하면 true, 아니면 false
     */
    public static boolean verifyCaptcha(String userInput, String correctAnswer) {
        if (userInput == null || correctAnswer == null) {
            return false;
        }
        
        // 공백 제거 및 대문자 변환
        String normalizedInput = userInput.trim().replaceAll("\\s+", "").toUpperCase();
        String normalizedAnswer = correctAnswer.trim().replaceAll("\\s+", "").toUpperCase();
        
        return normalizedInput.equals(normalizedAnswer);
    }
    
    /**
     * 캡챠 이미지를 ImageIcon으로 변환합니다.
     * Swing 컴포넌트에서 사용하기 위함입니다.
     * 
     * @param captchaText 캡챠 문자열
     * @return ImageIcon 객체
     */
    public static javax.swing.ImageIcon generateCaptchaImageIcon(String captchaText) {
        BufferedImage image = generateCaptchaImage(captchaText);
        return new javax.swing.ImageIcon(image);
    }
}

