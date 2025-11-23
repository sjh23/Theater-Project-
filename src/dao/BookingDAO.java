package dao;

import model.Booking;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BOOKING 테이블에 대한 데이터 접근 객체
 */
public class BookingDAO {
    
    /**
     * 모든 예약을 조회합니다.
     */
    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM BOOKING";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        }
        
        return bookings;
    }
    
    /**
     * 예약 ID로 예약을 조회합니다.
     */
    public Booking getBookingById(Integer bookingId) throws SQLException {
        String sql = "SELECT * FROM BOOKING WHERE Booking_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * 사용자 ID로 예약을 조회합니다.
     */
    public List<Booking> getBookingsByUserId(Integer userId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM BOOKING WHERE User_ID = ? ORDER BY Booking_Time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        
        return bookings;
    }
    
    /**
     * 스케줄 ID로 예약을 조회합니다.
     */
    public List<Booking> getBookingsByScheduleId(Integer scheduleId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM BOOKING WHERE Schedule_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, scheduleId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        
        return bookings;
    }
    
    /**
     * 특정 스케줄의 예약된 좌석을 조회합니다.
     */
    public List<String> getReservedSeats(Integer scheduleId) throws SQLException {
        List<String> reservedSeats = new ArrayList<>();
        String sql = "SELECT Seat_Row, Seat_Col FROM BOOKING WHERE Schedule_ID = ? AND Status = '예약완료'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, scheduleId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String row = rs.getString("Seat_Row");
                    String col = rs.getString("Seat_Col");
                    reservedSeats.add(row + col);
                }
            }
        }
        
        return reservedSeats;
    }
    
    /**
     * 특정 좌석이 예약되어 있는지 확인합니다.
     */
    public boolean isSeatReserved(Integer scheduleId, String seatRow, String seatCol) throws SQLException {
        String sql = "SELECT COUNT(*) FROM BOOKING WHERE Schedule_ID = ? AND Seat_Row = ? AND Seat_Col = ? AND Status = '예약완료'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, scheduleId);
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
    
    /**
     * 새 예약을 추가합니다.
     */
    public boolean insertBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO BOOKING (User_ID, Schedule_ID, Seat_Row, Seat_Col, Booking_Time, Total_Price, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, booking.getUserId());
            pstmt.setInt(2, booking.getScheduleId());
            pstmt.setString(3, booking.getSeatRow());
            pstmt.setString(4, booking.getSeatCol());
            pstmt.setTimestamp(5, booking.getBookingTime());
            pstmt.setBigDecimal(6, booking.getTotalPrice());
            pstmt.setString(7, booking.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setBookingId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 여러 좌석을 한 번에 예약합니다.
     */
    public boolean insertMultipleBookings(List<Booking> bookings) throws SQLException {
        String sql = "INSERT INTO BOOKING (User_ID, Schedule_ID, Seat_Row, Seat_Col, Booking_Time, Total_Price, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (Booking booking : bookings) {
                    pstmt.setInt(1, booking.getUserId());
                    pstmt.setInt(2, booking.getScheduleId());
                    pstmt.setString(3, booking.getSeatRow());
                    pstmt.setString(4, booking.getSeatCol());
                    pstmt.setTimestamp(5, booking.getBookingTime());
                    pstmt.setBigDecimal(6, booking.getTotalPrice());
                    pstmt.setString(7, booking.getStatus());
                    pstmt.addBatch();
                }
                
                int[] results = pstmt.executeBatch();
                conn.commit();
                
                // 모든 삽입이 성공했는지 확인
                for (int result : results) {
                    if (result <= 0) {
                        return false;
                    }
                }
                
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * 예약 정보를 업데이트합니다.
     */
    public boolean updateBooking(Booking booking) throws SQLException {
        String sql = "UPDATE BOOKING SET User_ID = ?, Schedule_ID = ?, Seat_Row = ?, Seat_Col = ?, Booking_Time = ?, Total_Price = ?, Status = ? WHERE Booking_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, booking.getUserId());
            pstmt.setInt(2, booking.getScheduleId());
            pstmt.setString(3, booking.getSeatRow());
            pstmt.setString(4, booking.getSeatCol());
            pstmt.setTimestamp(5, booking.getBookingTime());
            pstmt.setBigDecimal(6, booking.getTotalPrice());
            pstmt.setString(7, booking.getStatus());
            pstmt.setInt(8, booking.getBookingId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * 예약 상태를 업데이트합니다.
     */
    public boolean updateBookingStatus(Integer bookingId, String status) throws SQLException {
        String sql = "UPDATE BOOKING SET Status = ? WHERE Booking_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, bookingId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * 예약을 삭제합니다.
     */
    public boolean deleteBooking(Integer bookingId) throws SQLException {
        String sql = "DELETE FROM BOOKING WHERE Booking_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookingId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * ResultSet을 Booking 객체로 변환합니다.
     */
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("Booking_ID"));
        booking.setUserId(rs.getInt("User_ID"));
        booking.setScheduleId(rs.getInt("Schedule_ID"));
        booking.setSeatRow(rs.getString("Seat_Row"));
        booking.setSeatCol(rs.getString("Seat_Col"));
        booking.setBookingTime(rs.getTimestamp("Booking_Time"));
        booking.setTotalPrice(rs.getBigDecimal("Total_Price"));
        booking.setStatus(rs.getString("Status"));
        return booking;
    }
}

