package dao;

import model.User;
import util.DatabaseConnection;
import util.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

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

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM [USER] WHERE Username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    String storedHash = user.getPassword();

                    if (storedHash == null || storedHash.isEmpty()) {
                        return null;
                    }

                    if (PasswordUtil.verifyPassword(password, storedHash)) {
                        return user;
                    }
                }
            }
        }
        
        return null;
    }

    public User loginWithCaptcha(String username, String password, String captchaInput, String correctCaptcha) throws SQLException {

        if (!util.CaptchaUtil.verifyCaptcha(captchaInput, correctCaptcha)) {
            return null;
        }

        return login(username, password);
    }

    public boolean insertUser(User user) throws SQLException {
        String sql = "INSERT INTO [USER] (Username, Password, Name, Email, Role) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword);
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

                user.setPassword(hashedPassword);
                return true;
            }
        }
        
        return false;
    }

    public boolean insertUserWithCaptcha(User user, String captchaInput, String correctCaptcha) throws SQLException {

        if (!util.CaptchaUtil.verifyCaptcha(captchaInput, correctCaptcha)) {
            return false;
        }

        return insertUser(user);
    }

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE [USER] SET Username = ?, Password = ?, Name = ?, Email = ?, Role = ? WHERE User_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String password = user.getPassword();
            if (password != null && !password.isEmpty() && password.length() != 64) {

                try {
                    password = PasswordUtil.hashPassword(password);
                } catch (IllegalArgumentException e) {

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

    public boolean deleteUser(Integer userId) throws SQLException {
        String sql = "DELETE FROM [USER] WHERE User_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

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

