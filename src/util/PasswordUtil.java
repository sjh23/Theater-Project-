package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtil {
    
    private static final String ALGORITHM = "SHA-256";
    private static SecureRandom random = new SecureRandom();

    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 null이거나 비어있을 수 없습니다.");
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hashBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }

    public static boolean verifyPassword(String inputPassword, String storedHash) {
        if (inputPassword == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }

        if (inputPassword.isEmpty()) {
            return false;
        }
        
        try {
            String inputHash = hashPassword(inputPassword);
            return inputHash.equals(storedHash);
        } catch (IllegalArgumentException e) {

            return false;
        }
    }

    public static String hashPasswordWithSalt(String password, String salt) {
        return hashPassword(password + salt);
    }

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

