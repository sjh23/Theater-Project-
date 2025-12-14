package dao;

import model.Booking;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

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

    public List<String> getReservedSeats(Integer scheduleId) throws SQLException {
        List<String> reservedSeats = new ArrayList<>();
        String sql = "SELECT Seat_Row, Seat_Col FROM BOOKING WHERE Schedule_ID = ? AND Status = 'CONFIRMED'";
        
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

    public boolean isSeatReserved(Integer scheduleId, String seatRow, String seatCol) throws SQLException {
        String sql = "SELECT COUNT(*) FROM BOOKING WHERE Schedule_ID = ? AND Seat_Row = ? AND Seat_Col = ? AND Status = 'CONFIRMED'";
        
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

    public boolean insertMultipleBookings(List<Booking> bookings) throws SQLException {
        if (bookings == null || bookings.isEmpty()) {
            return false;
        }

        Integer scheduleId = bookings.get(0).getScheduleId();
        for (Booking booking : bookings) {
            if (!scheduleId.equals(booking.getScheduleId())) {
                throw new IllegalArgumentException("모든 예약은 같은 상영 시간표(Schedule_ID)를 가져야 합니다.");
            }
        }
        
        String insertSql = "INSERT INTO BOOKING (User_ID, Schedule_ID, Seat_Row, Seat_Col, Booking_Time, Total_Price, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);
            
            try {

                String checkSql = "SELECT COUNT(*) FROM BOOKING WHERE Schedule_ID = ? AND Seat_Row = ? AND Seat_Col = ? AND Status = 'CONFIRMED'";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    for (Booking booking : bookings) {
                        checkStmt.setInt(1, booking.getScheduleId());
                        checkStmt.setString(2, booking.getSeatRow());
                        checkStmt.setString(3, booking.getSeatCol());
                        
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {

                                conn.rollback();
                                throw new SQLException("좌석 " + booking.getSeatRow() + booking.getSeatCol() + "는 이미 예약되었습니다.");
                            }
                        }
                    }
                }

                try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    for (int i = 0; i < bookings.size(); i++) {
                        Booking booking = bookings.get(i);

                        if (booking.getUserId() == null || booking.getScheduleId() == null ||
                            booking.getSeatRow() == null || booking.getSeatCol() == null ||
                            booking.getStatus() == null) {
                            conn.rollback();
                            throw new SQLException("예약 정보에 필수 필드가 누락되었습니다.");
                        }
                        
                        pstmt.setInt(1, booking.getUserId());
                        pstmt.setInt(2, booking.getScheduleId());
                        pstmt.setString(3, booking.getSeatRow());
                        pstmt.setString(4, booking.getSeatCol());
                        pstmt.setTimestamp(5, booking.getBookingTime());
                        pstmt.setBigDecimal(6, booking.getTotalPrice());
                        pstmt.setString(7, booking.getStatus());

                        int affectedRows = pstmt.executeUpdate();
                        
                        if (affectedRows <= 0) {
                            conn.rollback();
                            throw new SQLException("예약 삽입 실패: 좌석 " + booking.getSeatRow() + booking.getSeatCol());
                        }

                        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                booking.setBookingId(generatedKeys.getInt(1));
                            }
                        }
                    }

                    conn.commit();
                    return true;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean reserveSeats(Integer userId, Integer showtimeId, String[] seatRows, String[] seatCols, java.math.BigDecimal pricePerSeat) throws SQLException {
        if (seatRows == null || seatCols == null || seatRows.length != seatCols.length) {
            throw new IllegalArgumentException("좌석 행과 열 배열의 길이가 일치해야 합니다.");
        }
        
        List<Booking> bookings = new ArrayList<>();
        java.sql.Timestamp bookingTime = new java.sql.Timestamp(System.currentTimeMillis());
        
        for (int i = 0; i < seatRows.length; i++) {

            if (seatRows[i] == null || seatCols[i] == null) {
                throw new IllegalArgumentException("좌석 행 또는 열 정보가 null일 수 없습니다.");
            }
            
            Booking booking = new Booking();
            booking.setUserId(userId);
            booking.setScheduleId(showtimeId);
            booking.setSeatRow(seatRows[i]);
            booking.setSeatCol(seatCols[i]);
            booking.setBookingTime(bookingTime);
            booking.setTotalPrice(pricePerSeat);
            booking.setStatus("CONFIRMED");
            bookings.add(booking);
        }
        
        return insertMultipleBookings(bookings);
    }

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

    public boolean updateBookingStatus(Integer bookingId, String status) throws SQLException {
        String sql = "UPDATE BOOKING SET Status = ? WHERE Booking_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, bookingId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteBooking(Integer bookingId) throws SQLException {
        String sql = "DELETE FROM BOOKING WHERE Booking_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookingId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public static class BookingHistoryInfo {
        public Integer bookingId;
        public Integer scheduleId;
        public String movieTitle;
        public java.sql.Timestamp startTime;
        public java.sql.Timestamp endTime;
        public String screenName;
        public String seatRow;
        public String seatCol;
        public java.math.BigDecimal totalPrice;
        public String status;
        public java.sql.Timestamp bookingTime;
    }

    public List<BookingHistoryInfo> getBookingHistoryByUserId(Integer userId) throws SQLException {
        List<BookingHistoryInfo> historyList = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    b.Booking_ID, " +
                     "    b.Schedule_ID, " +
                     "    b.Seat_Row, " +
                     "    b.Seat_Col, " +
                     "    b.Total_Price, " +
                     "    b.Status, " +
                     "    b.Booking_Time, " +
                     "    m.title AS Movie_Title, " +
                     "    sch.Start_Time, " +
                     "    sch.End_Time, " +
                     "    scr.Name AS Screen_Name " +
                     "FROM BOOKING b " +
                     "INNER JOIN SCHEDULE sch ON b.Schedule_ID = sch.Schedule_ID " +
                     "INNER JOIN MOVIE m ON sch.Movie_ID = m.movie_id " +
                     "INNER JOIN SCREEN scr ON sch.Screen_ID = scr.Screen_ID " +
                     "WHERE b.User_ID = ? " +
                     "ORDER BY b.Booking_Time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BookingHistoryInfo info = new BookingHistoryInfo();
                    info.bookingId = rs.getInt("Booking_ID");
                    info.scheduleId = rs.getInt("Schedule_ID");
                    info.movieTitle = rs.getString("Movie_Title");
                    info.startTime = rs.getTimestamp("Start_Time");
                    info.endTime = rs.getTimestamp("End_Time");
                    info.screenName = rs.getString("Screen_Name");
                    info.seatRow = rs.getString("Seat_Row");
                    info.seatCol = rs.getString("Seat_Col");
                    info.totalPrice = rs.getBigDecimal("Total_Price");
                    info.status = rs.getString("Status");
                    info.bookingTime = rs.getTimestamp("Booking_Time");
                    
                    historyList.add(info);
                }
            }
        }
        
        return historyList;
    }

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

