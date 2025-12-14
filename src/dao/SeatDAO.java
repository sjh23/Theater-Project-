package dao;

import model.Screen;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    public Screen getScreenStructureByShowtimeId(Integer showtimeId) throws SQLException {

        String sql = "SELECT s.Screen_ID, s.Name, s.Total_Seats, s.Rows, s.Cols " +
                     "FROM SCREEN s " +
                     "INNER JOIN SCHEDULE sch ON s.Screen_ID = sch.Screen_ID " +
                     "WHERE sch.Schedule_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, showtimeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Screen screen = new Screen();
                    screen.setScreenId(rs.getInt("Screen_ID"));
                    screen.setName(rs.getString("Name"));
                    screen.setTotalSeats(rs.getInt("Total_Seats"));
                    screen.setRows(rs.getInt("Rows"));
                    screen.setCols(rs.getInt("Cols"));
                    return screen;
                }
            }
        }
        
        return null;
    }

    public int getRowsByShowtimeId(Integer showtimeId) throws SQLException {
        Screen screen = getScreenStructureByShowtimeId(showtimeId);
        return (screen != null && screen.getRows() != null) ? screen.getRows() : 0;
    }

    public int getColsByShowtimeId(Integer showtimeId) throws SQLException {
        Screen screen = getScreenStructureByShowtimeId(showtimeId);
        return (screen != null && screen.getCols() != null) ? screen.getCols() : 0;
    }

    public Screen getScreenStructureByScreenId(Integer screenId) throws SQLException {
        String sql = "SELECT Screen_ID, Name, Total_Seats, Rows, Cols FROM SCREEN WHERE Screen_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, screenId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Screen screen = new Screen();
                    screen.setScreenId(rs.getInt("Screen_ID"));
                    screen.setName(rs.getString("Name"));
                    screen.setTotalSeats(rs.getInt("Total_Seats"));
                    screen.setRows(rs.getInt("Rows"));
                    screen.setCols(rs.getInt("Cols"));
                    return screen;
                }
            }
        }
        
        return null;
    }
}

