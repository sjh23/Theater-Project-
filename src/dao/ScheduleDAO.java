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
        // MS-SQL에서는 CAST를 사용하여 날짜 부분만 비교
        String sql = "SELECT * FROM SCHEDULE WHERE CAST(Start_Time AS DATE) = CAST(? AS DATE) ORDER BY Start_Time";
        
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
        // MS-SQL 날짜 비교 - CONVERT를 사용하여 더 정확한 날짜 비교
        // 날짜 형식 변환 후 비교로 시간 부분 무시
        String sql = "SELECT * FROM SCHEDULE " +
                     "WHERE Movie_ID = ? " +
                     "AND CONVERT(DATE, Start_Time) = CONVERT(DATE, ?) " +
                     "ORDER BY Start_Time";
        
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
     * 영화 ID와 날짜로 스케줄을 최적화된 JOIN 쿼리로 조회합니다.
     * 상영관 정보, 잔여석 수, 영화 등급을 한 번에 조회합니다.
     * 
     * @param movieId 영화 ID
     * @param date 날짜
     * @return ShowtimeInfo 리스트 (스케줄 정보 + 상영관 + 잔여석 + 등급)
     */
    public List<ShowtimeInfo> getShowtimeInfoByMovieAndDate(Integer movieId, Date date) throws SQLException {
        List<ShowtimeInfo> showtimes = new ArrayList<>();
        
        // JOIN 쿼리로 한 번에 모든 정보 가져오기
        String sql = "SELECT " +
                     "    sch.Schedule_ID, " +
                     "    sch.Start_Time, " +
                     "    sch.End_Time, " +
                     "    sch.Price, " +
                     "    scr.Screen_ID, " +
                     "    scr.Name AS Screen_Name, " +
                     "    scr.Total_Seats, " +
                     "    scr.Rows AS Screen_Rows, " +
                     "    scr.Cols AS Screen_Cols, " +
                     "    m.rating AS Movie_Rating, " +
                     "    ISNULL(COUNT(DISTINCT b.Booking_ID), 0) AS Reserved_Seat_Count " +
                     "FROM SCHEDULE sch " +
                     "INNER JOIN SCREEN scr ON sch.Screen_ID = scr.Screen_ID " +
                     "INNER JOIN MOVIE m ON sch.Movie_ID = m.movie_id " +
                     "LEFT JOIN BOOKING b ON sch.Schedule_ID = b.Schedule_ID " +
                     "    AND (b.Status = 'CONFIRMED' OR b.Status = 'PAID') " +
                     "WHERE sch.Movie_ID = ? " +
                     "    AND CONVERT(DATE, sch.Start_Time) = CONVERT(DATE, ?) " +
                     "GROUP BY " +
                     "    sch.Schedule_ID, sch.Start_Time, sch.End_Time, sch.Price, " +
                     "    scr.Screen_ID, scr.Name, scr.Total_Seats, scr.Rows, scr.Cols, " +
                     "    m.rating " +
                     "ORDER BY sch.Start_Time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, movieId);
            pstmt.setDate(2, date);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ShowtimeInfo info = new ShowtimeInfo();
                    info.scheduleId = rs.getInt("Schedule_ID");
                    info.startTime = rs.getTimestamp("Start_Time");
                    info.endTime = rs.getTimestamp("End_Time");
                    info.price = rs.getBigDecimal("Price");
                    info.screenId = rs.getInt("Screen_ID");
                    info.screenName = rs.getString("Screen_Name");
                    info.totalSeats = rs.getInt("Total_Seats");
                    info.rows = rs.getInt("Screen_Rows");
                    info.cols = rs.getInt("Screen_Cols");
                    info.movieRating = rs.getString("Movie_Rating");
                    info.reservedSeatCount = rs.getInt("Reserved_Seat_Count");
                    info.remainingSeats = info.totalSeats - info.reservedSeatCount;
                    
                    showtimes.add(info);
                }
            }
        }
        
        return showtimes;
    }
    
    /**
     * ShowtimeForm에서 사용할 최적화된 정보 클래스
     */
    public static class ShowtimeInfo {
        public Integer scheduleId;
        public java.sql.Timestamp startTime;
        public java.sql.Timestamp endTime;
        public java.math.BigDecimal price;
        public Integer screenId;
        public String screenName;
        public Integer totalSeats;
        public Integer rows;
        public Integer cols;
        public String movieRating;
        public Integer reservedSeatCount;
        public Integer remainingSeats;
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
     * 일일 상영시간표용 정보 클래스
     * 모든 영화의 상영시간표를 날짜별로 조회할 때 사용
     */
    public static class DailyScheduleInfo {
        public Integer scheduleId;
        public String movieTitle;
        public java.sql.Timestamp startTime;
        public String movieRating;
        public String screenName;
        public Integer screenId;
    }
    
    /**
     * 특정 날짜의 모든 영화 상영시간표를 조회합니다 (일일 상영시간표용).
     * 영화 제목, 상영시간, 등급, 상영관 정보를 포함합니다.
     * 
     * @param date 날짜
     * @return DailyScheduleInfo 리스트
     */
    public List<DailyScheduleInfo> getDailyScheduleInfoByDate(Date date) throws SQLException {
        List<DailyScheduleInfo> schedules = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    sch.Schedule_ID, " +
                     "    m.title AS Movie_Title, " +
                     "    sch.Start_Time, " +
                     "    m.rating AS Movie_Rating, " +
                     "    scr.Name AS Screen_Name, " +
                     "    scr.Screen_ID " +
                     "FROM SCHEDULE sch " +
                     "INNER JOIN MOVIE m ON sch.Movie_ID = m.movie_id " +
                     "INNER JOIN SCREEN scr ON sch.Screen_ID = scr.Screen_ID " +
                     "WHERE CONVERT(DATE, sch.Start_Time) = CONVERT(DATE, ?) " +
                     "ORDER BY sch.Start_Time, m.title";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, date);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DailyScheduleInfo info = new DailyScheduleInfo();
                    info.scheduleId = rs.getInt("Schedule_ID");
                    info.movieTitle = rs.getString("Movie_Title");
                    info.startTime = rs.getTimestamp("Start_Time");
                    info.movieRating = rs.getString("Movie_Rating");
                    info.screenName = rs.getString("Screen_Name");
                    info.screenId = rs.getInt("Screen_ID");
                    
                    schedules.add(info);
                }
            }
        }
        
        return schedules;
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

