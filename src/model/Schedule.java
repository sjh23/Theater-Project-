package model;

import java.sql.Timestamp;

/**
 * SCHEDULE 테이블에 해당하는 모델 클래스
 */
public class Schedule {
    private Integer scheduleId;
    private Integer movieId;
    private Integer screenId;
    private Timestamp startTime;
    private Timestamp endTime;
    private java.math.BigDecimal price;
    
    public Schedule() {
    }
    
    public Schedule(Integer scheduleId, Integer movieId, Integer screenId, 
                    Timestamp startTime, Timestamp endTime, java.math.BigDecimal price) {
        this.scheduleId = scheduleId;
        this.movieId = movieId;
        this.screenId = screenId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }
    
    // Getters and Setters
    public Integer getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public Integer getMovieId() {
        return movieId;
    }
    
    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }
    
    public Integer getScreenId() {
        return screenId;
    }
    
    public void setScreenId(Integer screenId) {
        this.screenId = screenId;
    }
    
    public Timestamp getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }
    
    public Timestamp getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
    
    public java.math.BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(java.math.BigDecimal price) {
        this.price = price;
    }
    
    @Override
    public String toString() {
        return "Schedule{" +
                "scheduleId=" + scheduleId +
                ", movieId=" + movieId +
                ", screenId=" + screenId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", price=" + price +
                '}';
    }
}

