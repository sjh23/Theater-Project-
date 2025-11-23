package model;

import java.sql.Timestamp;

/**
 * BOOKING 테이블에 해당하는 모델 클래스
 */
public class Booking {
    private Integer bookingId;
    private Integer userId;
    private Integer scheduleId;
    private String seatRow;
    private String seatCol;
    private Timestamp bookingTime;
    private java.math.BigDecimal totalPrice;
    private String status;
    
    public Booking() {
    }
    
    public Booking(Integer bookingId, Integer userId, Integer scheduleId, 
                   String seatRow, String seatCol, Timestamp bookingTime, 
                   java.math.BigDecimal totalPrice, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.seatRow = seatRow;
        this.seatCol = seatCol;
        this.bookingTime = bookingTime;
        this.totalPrice = totalPrice;
        this.status = status;
    }
    
    // Getters and Setters
    public Integer getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public String getSeatRow() {
        return seatRow;
    }
    
    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }
    
    public String getSeatCol() {
        return seatCol;
    }
    
    public void setSeatCol(String seatCol) {
        this.seatCol = seatCol;
    }
    
    public Timestamp getBookingTime() {
        return bookingTime;
    }
    
    public void setBookingTime(Timestamp bookingTime) {
        this.bookingTime = bookingTime;
    }
    
    public java.math.BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(java.math.BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", scheduleId=" + scheduleId +
                ", seatRow='" + seatRow + '\'' +
                ", seatCol='" + seatCol + '\'' +
                ", bookingTime=" + bookingTime +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                '}';
    }
}

