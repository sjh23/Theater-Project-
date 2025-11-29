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
    
    /**
     * 특정 좌석이 예약되어 있는지 확인합니다.
     */
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
     * 트랜잭션을 사용하여 안전하게 처리하고, 동시 예매 상황을 방지합니다.
     * 
     * @param bookings 예약할 좌석 목록
     * @return 성공 시 true, 실패 시 false
     * @throws SQLException 데이터베이스 오류 시
     */
    public boolean insertMultipleBookings(List<Booking> bookings) throws SQLException {
        if (bookings == null || bookings.isEmpty()) {
            return false;
        }
        
        // 모든 예약이 같은 Schedule_ID를 가지는지 확인
        Integer scheduleId = bookings.get(0).getScheduleId();
        for (Booking booking : bookings) {
            if (!scheduleId.equals(booking.getScheduleId())) {
                throw new IllegalArgumentException("모든 예약은 같은 상영 시간표(Schedule_ID)를 가져야 합니다.");
            }
        }
        
        String insertSql = "INSERT INTO BOOKING (User_ID, Schedule_ID, Seat_Row, Seat_Col, Booking_Time, Total_Price, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 트랜잭션 시작
            conn.setAutoCommit(false);
            
            try {
                // 먼저 모든 좌석이 예약 가능한지 확인 (동시 예매 방지)
                // Status가 'CONFIRMED'인 경우만 예약된 것으로 간주
                String checkSql = "SELECT COUNT(*) FROM BOOKING WHERE Schedule_ID = ? AND Seat_Row = ? AND Seat_Col = ? AND Status = 'CONFIRMED'";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    for (Booking booking : bookings) {
                        checkStmt.setInt(1, booking.getScheduleId());
                        checkStmt.setString(2, booking.getSeatRow());
                        checkStmt.setString(3, booking.getSeatCol());
                        
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                // 이미 예약된 좌석이 있음
                                conn.rollback();
                                throw new SQLException("좌석 " + booking.getSeatRow() + booking.getSeatCol() + "는 이미 예약되었습니다.");
                            }
                        }
                    }
                }
                
                // 모든 좌석이 예약 가능하므로 예약 처리
                // SQL Server에서 배치 실행 후 getGeneratedKeys()가 제대로 작동하지 않으므로
                // 각 예약을 개별적으로 삽입하되, 트랜잭션 내에서 처리
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    for (int i = 0; i < bookings.size(); i++) {
                        Booking booking = bookings.get(i);
                        
                        // null 체크
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
                        
                        // 각 예약을 개별적으로 실행
                        int affectedRows = pstmt.executeUpdate();
                        
                        if (affectedRows <= 0) {
                            conn.rollback();
                            throw new SQLException("예약 삽입 실패: 좌석 " + booking.getSeatRow() + booking.getSeatCol());
                        }
                        
                        // 생성된 키 가져오기
                        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                booking.setBookingId(generatedKeys.getInt(1));
                            }
                        }
                    }
                    
                    // 트랜잭션 커밋
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
    
    /**
     * ShowtimeID와 좌석 정보를 이용하여 예매를 처리합니다.
     * 동시 예매를 방지하기 위해 트랜잭션과 좌석 확인 로직을 포함합니다.
     * 
     * @param userId 사용자 ID
     * @param showtimeId 상영 시간표 ID (Schedule_ID)
     * @param seatRows 좌석 행 배열 (예: ["A", "A", "B"])
     * @param seatCols 좌석 열 배열 (예: ["1", "2", "1"])
     * @param pricePerSeat 좌석당 가격
     * @return 성공 시 true, 실패 시 false
     * @throws SQLException 데이터베이스 오류 시
     */
    public boolean reserveSeats(Integer userId, Integer showtimeId, String[] seatRows, String[] seatCols, java.math.BigDecimal pricePerSeat) throws SQLException {
        if (seatRows == null || seatCols == null || seatRows.length != seatCols.length) {
            throw new IllegalArgumentException("좌석 행과 열 배열의 길이가 일치해야 합니다.");
        }
        
        List<Booking> bookings = new ArrayList<>();
        java.sql.Timestamp bookingTime = new java.sql.Timestamp(System.currentTimeMillis());
        
        for (int i = 0; i < seatRows.length; i++) {
            // null 체크
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
            booking.setStatus("CONFIRMED");  // 예약 완료 상태
            bookings.add(booking);
        }
        
        return insertMultipleBookings(bookings);
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
     * 예매 내역 정보를 담는 내부 클래스
     */
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
    
    /**
     * 사용자 ID로 예매 내역을 조회합니다. (영화명, 상영시간, 상영관 정보 포함)
     * 
     * @param userId 사용자 ID
     * @return 예매 내역 정보 리스트 (최신순)
     * @throws SQLException 데이터베이스 오류 시
     */
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

