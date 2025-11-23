package dao;

import model.User;
import util.DatabaseConnection;
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
        String sql = "SELECT * FROM USER";
        
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
        String sql = "SELECT * FROM USER WHERE User_ID = ?";
        
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
        String sql = "SELECT * FROM USER WHERE Username = ?";
        
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
     */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM USER WHERE Username = ? AND Password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * 새 사용자를 추가합니다.
     */
    public boolean insertUser(User user) throws SQLException {
        String sql = "INSERT INTO USER (Username, Password, Name, Email, Role) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
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
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 사용자 정보를 업데이트합니다.
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE USER SET Username = ?, Password = ?, Name = ?, Email = ?, Role = ? WHERE User_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
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
        String sql = "DELETE FROM USER WHERE User_ID = ?";
        
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

