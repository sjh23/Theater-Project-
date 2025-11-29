package dao;

import model.User;
import util.DatabaseConnection;
import util.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * USER 테이블에 대한 데이터 접근 객체
 */
public class UserDAO {
    
    /**
     * 모든 사용자를 조회합니다.
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM [USER]";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        
        return users;
    }
    
    /**
     * 사용자 ID로 사용자를 조회합니다.
     */
    public User getUserById(Integer userId) throws SQLException {
        String sql = "SELECT * FROM [USER] WHERE User_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * 사용자명으로 사용자를 조회합니다.
     */
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM [USER] WHERE Username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * 사용자명과 비밀번호로 로그인을 확인합니다.
     * 비밀번호는 해시값으로 저장되어 있으므로 해시 비교를 수행합니다.
     */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM [USER] WHERE Username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    String storedHash = user.getPassword();
                    
                    // 저장된 해시가 null이면 로그인 실패
                    if (storedHash == null || storedHash.isEmpty()) {
                        return null;
                    }
                    
                    // 입력된 비밀번호를 해시하여 저장된 해시와 비교
                    if (PasswordUtil.verifyPassword(password, storedHash)) {
                        return user;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * 사용자명, 비밀번호, 캡챠를 검증하여 로그인을 확인합니다.
     * 
     * @param username 사용자명
     * @param password 비밀번호
     * @param captchaInput 사용자가 입력한 캡챠 값
     * @param correctCaptcha 서버에 저장된 정답 캡챠 값
     * @return 로그인 성공 시 User 객체, 실패 시 null
     * @throws SQLException 데이터베이스 오류 시
     */
    public User loginWithCaptcha(String username, String password, String captchaInput, String correctCaptcha) throws SQLException {
        // 캡챠 검증
        if (!util.CaptchaUtil.verifyCaptcha(captchaInput, correctCaptcha)) {
            return null;  // 캡챠 검증 실패
        }
        
        // 로그인 처리
        return login(username, password);
    }
    
    /**
     * 새 사용자를 추가합니다.
     * 비밀번호는 자동으로 해시 처리됩니다.
     */
    public boolean insertUser(User user) throws SQLException {
        String sql = "INSERT INTO [USER] (Username, Password, Name, Email, Role) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // 비밀번호 해시 처리
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword);  // 해시된 비밀번호 저장
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getRole());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                // User 객체의 비밀번호도 해시값으로 업데이트 (보안상 원본 비밀번호는 저장하지 않음)
                user.setPassword(hashedPassword);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 새 사용자를 추가합니다 (캡챠 검증 포함).
     * 
     * @param user 사용자 정보
     * @param captchaInput 사용자가 입력한 캡챠 값
     * @param correctCaptcha 서버에 저장된 정답 캡챠 값
     * @return 성공 시 true, 실패 시 false
     * @throws SQLException 데이터베이스 오류 시
     */
    public boolean insertUserWithCaptcha(User user, String captchaInput, String correctCaptcha) throws SQLException {
        // 캡챠 검증
        if (!util.CaptchaUtil.verifyCaptcha(captchaInput, correctCaptcha)) {
            return false;  // 캡챠 검증 실패
        }
        
        // 사용자 추가
        return insertUser(user);
    }
    
    /**
     * 사용자 정보를 업데이트합니다.
     * 비밀번호가 변경된 경우 자동으로 해시 처리됩니다.
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE [USER] SET Username = ?, Password = ?, Name = ?, Email = ?, Role = ? WHERE User_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // 비밀번호가 이미 해시된 형식인지 확인 (64자리 16진수 문자열)
            // 단, 빈 문자열이거나 null인 경우는 그대로 유지 (업데이트하지 않음)
            String password = user.getPassword();
            if (password != null && !password.isEmpty() && password.length() != 64) {
                // 해시되지 않은 비밀번호인 경우 해시 처리
                try {
                    password = PasswordUtil.hashPassword(password);
                } catch (IllegalArgumentException e) {
                    // 빈 비밀번호인 경우 원본 유지
                    password = user.getPassword();
                }
            }
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, password);
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getRole());
            pstmt.setInt(6, user.getUserId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * 사용자를 삭제합니다.
     */
    public boolean deleteUser(Integer userId) throws SQLException {
        String sql = "DELETE FROM [USER] WHERE User_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * ResultSet을 User 객체로 변환합니다.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("User_ID"));
        user.setUsername(rs.getString("Username"));
        user.setPassword(rs.getString("Password"));
        user.setName(rs.getString("Name"));
        user.setEmail(rs.getString("Email"));
        user.setRole(rs.getString("Role"));
        return user;
    }
}

