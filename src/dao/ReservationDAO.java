package dao;

import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RESERVATION 테이블에 대한 데이터 접근 객체
 * ShowtimeID 기반 좌석 현황 조회 기능 제공
 */
public class ReservationDAO {
    
    /**
     * ShowtimeID를 기반으로 예매된 좌석 코드 리스트를 반환합니다.
     * 좌석 코드 형식: "A1", "B2" 등 (Seat_Row + Seat_Col)
     * 
     * @param showtimeId 상영 시간표 ID (Schedule_ID)
     * @return 예매된 좌석 코드 리스트
     * @throws SQLException 데이터베이스 오류 시
     */
    public List<String> getReservedSeatCodesByShowtimeId(Integer showtimeId) throws SQLException {
        List<String> reservedSeatCodes = new ArrayList<>();
        
        // BOOKING 테이블에서 예약된 좌석 조회
        // Status가 'CONFIRMED'인 경우만 예약 완료로 간주 (CANCELLED는 제외)
        String sql = "SELECT Seat_Row, Seat_Col FROM BOOKING WHERE Schedule_ID = ? AND Status = 'CONFIRMED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, showtimeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String row = rs.getString("Seat_Row");
                    String col = rs.getString("Seat_Col");
                    // null 체크
                    if (row != null && col != null) {
                        // 좌석 코드 형식: "A1", "B2" 등
                        reservedSeatCodes.add(row + col);
                    }
                }
            }
        }
        
        return reservedSeatCodes;
    }
    
    /**
     * ShowtimeID를 기반으로 예매된 좌석의 행과 열 정보를 반환합니다.
     * 
     * @param showtimeId 상영 시간표 ID (Schedule_ID)
     * @return 예매된 좌석 정보 리스트 (각 요소는 [행, 열] 배열)
     * @throws SQLException 데이터베이스 오류 시
     */
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
                    // null 체크
                    if (row != null && col != null) {
                        reservedSeats.add(new String[]{row, col});
                    }
                }
            }
        }
        
        return reservedSeats;
    }
    
    /**
     * 특정 ShowtimeID의 특정 좌석이 예약되어 있는지 확인합니다.
     * 
     * @param showtimeId 상영 시간표 ID
     * @param seatRow 좌석 행 (예: "A", "B")
     * @param seatCol 좌석 열 (예: "1", "2")
     * @return 예약되어 있으면 true, 아니면 false
     * @throws SQLException 데이터베이스 오류 시
     */
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

