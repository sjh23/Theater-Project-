package dao;

import model.Schedule;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SCHEDULE 테이블에 대한 데이터 접근 객체
 */
public class ScheduleDAO {
    
    /**
     * 모든 스케줄을 조회합니다.
     */
    public List<Schedule> getAllSchedules() throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM SCHEDULE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
        }
        
        return schedules;
    }
    
    /**
     * 스케줄 ID로 스케줄을 조회합니다.
     */
    public Schedule getScheduleById(Integer scheduleId) throws SQLException {
        String sql = "SELECT * FROM SCHEDULE WHERE Schedule_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, scheduleId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSchedule(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * 영화 ID로 스케줄을 조회합니다.
     */
    public List<Schedule> getSchedulesByMovieId(Integer movieId) throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM SCHEDULE WHERE Movie_ID = ? ORDER BY Start_Time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, movieId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        }
        
        return schedules;
    }
    
    /**
     * 상영관 ID로 스케줄을 조회합니다.
     */
    public List<Schedule> getSchedulesByScreenId(Integer screenId) throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM SCHEDULE WHERE Screen_ID = ? ORDER BY Start_Time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, screenId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        }
        
        return schedules;
    }
    
    /**
     * 특정 날짜의 스케줄을 조회합니다.
     */
    public List<Schedule> getSchedulesByDate(Date date) throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM SCHEDULE WHERE DATE(Start_Time) = ? ORDER BY Start_Time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, date);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        }
        
        return schedules;
    }
    
    /**
     * 영화 ID와 날짜로 스케줄을 조회합니다.
     */
    public List<Schedule> getSchedulesByMovieAndDate(Integer movieId, Date date) throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM SCHEDULE WHERE Movie_ID = ? AND DATE(Start_Time) = ? ORDER BY Start_Time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, movieId);
            pstmt.setDate(2, date);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        }
        
        return schedules;
    }
    
    /**
     * 새 스케줄을 추가합니다.
     */
    public boolean insertSchedule(Schedule schedule) throws SQLException {
        String sql = "INSERT INTO SCHEDULE (Movie_ID, Screen_ID, Start_Time, End_Time, Price) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, schedule.getMovieId());
            pstmt.setInt(2, schedule.getScreenId());
            pstmt.setTimestamp(3, schedule.getStartTime());
            pstmt.setTimestamp(4, schedule.getEndTime());
            pstmt.setBigDecimal(5, schedule.getPrice());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        schedule.setScheduleId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 스케줄 정보를 업데이트합니다.
     */
    public boolean updateSchedule(Schedule schedule) throws SQLException {
        String sql = "UPDATE SCHEDULE SET Movie_ID = ?, Screen_ID = ?, Start_Time = ?, End_Time = ?, Price = ? WHERE Schedule_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, schedule.getMovieId());
            pstmt.setInt(2, schedule.getScreenId());
            pstmt.setTimestamp(3, schedule.getStartTime());
            pstmt.setTimestamp(4, schedule.getEndTime());
            pstmt.setBigDecimal(5, schedule.getPrice());
            pstmt.setInt(6, schedule.getScheduleId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * 스케줄을 삭제합니다.
     */
    public boolean deleteSchedule(Integer scheduleId) throws SQLException {
        String sql = "DELETE FROM SCHEDULE WHERE Schedule_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, scheduleId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * ResultSet을 Schedule 객체로 변환합니다.
     */
    private Schedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(rs.getInt("Schedule_ID"));
        schedule.setMovieId(rs.getInt("Movie_ID"));
        schedule.setScreenId(rs.getInt("Screen_ID"));
        schedule.setStartTime(rs.getTimestamp("Start_Time"));
        schedule.setEndTime(rs.getTimestamp("End_Time"));
        schedule.setPrice(rs.getBigDecimal("Price"));
        return schedule;
    }
}

