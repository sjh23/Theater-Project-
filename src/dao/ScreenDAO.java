package dao;

import model.Screen;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScreenDAO {

    public List<Screen> getAllScreens() throws SQLException {
        List<Screen> screens = new ArrayList<>();
        String sql = "SELECT * FROM SCREEN";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                screens.add(mapResultSetToScreen(rs));
            }
        }
        
        return screens;
    }

    public Screen getScreenById(Integer screenId) throws SQLException {
        String sql = "SELECT * FROM SCREEN WHERE Screen_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, screenId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToScreen(rs);
                }
            }
        }
        
        return null;
    }

    public boolean insertScreen(Screen screen) throws SQLException {
        String sql = "INSERT INTO SCREEN (Name, Total_Seats, Rows, Cols) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, screen.getName());
            pstmt.setInt(2, screen.getTotalSeats());
            pstmt.setInt(3, screen.getRows());
            pstmt.setInt(4, screen.getCols());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        screen.setScreenId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        
        return false;
    }

    public boolean updateScreen(Screen screen) throws SQLException {
        String sql = "UPDATE SCREEN SET Name = ?, Total_Seats = ?, Rows = ?, Cols = ? WHERE Screen_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, screen.getName());
            pstmt.setInt(2, screen.getTotalSeats());
            pstmt.setInt(3, screen.getRows());
            pstmt.setInt(4, screen.getCols());
            pstmt.setInt(5, screen.getScreenId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteScreen(Integer screenId) throws SQLException {
        String sql = "DELETE FROM SCREEN WHERE Screen_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, screenId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    private Screen mapResultSetToScreen(ResultSet rs) throws SQLException {
        Screen screen = new Screen();
        screen.setScreenId(rs.getInt("Screen_ID"));
        screen.setName(rs.getString("Name"));
        screen.setTotalSeats(rs.getInt("Total_Seats"));
        screen.setRows(rs.getInt("Rows"));
        screen.setCols(rs.getInt("Cols"));
        return screen;
    }
}

