package dao;

import model.Screen;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SEAT 및 SCREEN 테이블에 대한 데이터 접근 객체
 * ShowtimeID 기반 상영관 구조 조회 기능 제공
 */
public class SeatDAO {
    
    /**
     * ShowtimeID를 기반으로 해당 상영관의 행(Rows)과 열(Cols) 정보를 조회합니다.
     * SCHEDULE 테이블을 통해 Screen_ID를 찾고, SCREEN 테이블에서 구조 정보를 가져옵니다.
     * 
     * @param showtimeId 상영 시간표 ID (Schedule_ID)
     * @return Screen 객체 (Rows, Cols 정보 포함), 없으면 null
     * @throws SQLException 데이터베이스 오류 시
     */
    public Screen getScreenStructureByShowtimeId(Integer showtimeId) throws SQLException {
        // SCHEDULE 테이블에서 Screen_ID를 조회하고, SCREEN 테이블에서 구조 정보를 가져옴
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
    
    /**
     * ShowtimeID를 기반으로 상영관의 행(Rows) 수를 반환합니다.
     * 
     * @param showtimeId 상영 시간표 ID
     * @return 행 수, 없으면 0
     * @throws SQLException 데이터베이스 오류 시
     */
    public int getRowsByShowtimeId(Integer showtimeId) throws SQLException {
        Screen screen = getScreenStructureByShowtimeId(showtimeId);
        return (screen != null && screen.getRows() != null) ? screen.getRows() : 0;
    }
    
    /**
     * ShowtimeID를 기반으로 상영관의 열(Cols) 수를 반환합니다.
     * 
     * @param showtimeId 상영 시간표 ID
     * @return 열 수, 없으면 0
     * @throws SQLException 데이터베이스 오류 시
     */
    public int getColsByShowtimeId(Integer showtimeId) throws SQLException {
        Screen screen = getScreenStructureByShowtimeId(showtimeId);
        return (screen != null && screen.getCols() != null) ? screen.getCols() : 0;
    }
    
    /**
     * Screen_ID를 기반으로 상영관 구조를 조회합니다.
     * 
     * @param screenId 상영관 ID
     * @return Screen 객체
     * @throws SQLException 데이터베이스 오류 시
     */
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

