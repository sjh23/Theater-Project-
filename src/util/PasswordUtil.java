package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 비밀번호 암호화 유틸리티 클래스
 * SHA-256 해시 함수를 사용하여 비밀번호를 암호화합니다.
 */
public class PasswordUtil {
    
    private static final String ALGORITHM = "SHA-256";
    private static SecureRandom random = new SecureRandom();
    
    /**
     * 비밀번호를 SHA-256 해시로 암호화합니다.
     * 
     * @param password 평문 비밀번호
     * @return 해시된 비밀번호 (16진수 문자열)
     * @throws RuntimeException 암호화 실패 시
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 null이거나 비어있을 수 없습니다.");
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hashBytes = md.digest(password.getBytes());
            
            // 16진수 문자열로 변환
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }
    
    /**
     * 비밀번호를 검증합니다.
     * 입력된 평문 비밀번호를 해시하여 저장된 해시값과 비교합니다.
     * 
     * @param inputPassword 사용자가 입력한 평문 비밀번호
     * @param storedHash 저장된 해시된 비밀번호
     * @return 일치하면 true, 아니면 false
     */
    public static boolean verifyPassword(String inputPassword, String storedHash) {
        if (inputPassword == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        
        // 빈 비밀번호는 검증 실패
        if (inputPassword.isEmpty()) {
            return false;
        }
        
        try {
            String inputHash = hashPassword(inputPassword);
            return inputHash.equals(storedHash);
        } catch (IllegalArgumentException e) {
            // 비밀번호가 유효하지 않으면 검증 실패
            return false;
        }
    }
    
    /**
     * 솔트(Salt)를 사용한 비밀번호 해싱 (선택적 기능)
     * 더 강력한 보안을 위해 솔트를 추가할 수 있습니다.
     * 
     * @param password 평문 비밀번호
     * @param salt 솔트 값
     * @return 해시된 비밀번호
     */
    public static String hashPasswordWithSalt(String password, String salt) {
        return hashPassword(password + salt);
    }
    
    /**
     * 랜덤 솔트를 생성합니다.
     * 
     * @return 16자리 랜덤 솔트 문자열
     */
    public static String generateSalt() {
        byte[] saltBytes = new byte[8];
        random.nextBytes(saltBytes);
        
        StringBuilder sb = new StringBuilder();
        for (byte b : saltBytes) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }
}

