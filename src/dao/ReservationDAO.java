package dao;

import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public List<String> getReservedSeatCodesByShowtimeId(Integer showtimeId) throws SQLException {
        List<String> reservedSeatCodes = new ArrayList<>();

        String sql = "SELECT Seat_Row, Seat_Col FROM BOOKING WHERE Schedule_ID = ? AND Status = 'CONFIRMED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, showtimeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String row = rs.getString("Seat_Row");
                    String col = rs.getString("Seat_Col");

                    if (row != null && col != null) {

                        reservedSeatCodes.add(row + col);
                    }
                }
            }
        }
        
        return reservedSeatCodes;
    }

    public List<String[]> getReservedSeatsByShowtimeId(Integer showtimeId) throws SQLException {
        List<String[]> reservedSeats = new ArrayList<>();
        
        String sql = "SELECT Seat_Row, Seat_Col FROM BOOKING WHERE Schedule_ID = ? AND Status = 'CONFIRMED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, showtimeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String row = rs.getString("Seat_Row");
                    String col = rs.getString("Seat_Col");

                    if (row != null && col != null) {
                        reservedSeats.add(new String[]{row, col});
                    }
                }
            }
        }
        
        return reservedSeats;
    }

    public boolean isSeatReserved(Integer showtimeId, String seatRow, String seatCol) throws SQLException {
        String sql = "SELECT COUNT(*) FROM BOOKING WHERE Schedule_ID = ? AND Seat_Row = ? AND Seat_Col = ? AND Status = 'CONFIRMED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, showtimeId);
            pstmt.setString(2, seatRow);
            pstmt.setString(3, seatCol);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
}

