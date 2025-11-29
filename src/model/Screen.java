package model;

/**
 * SCREEN 테이블에 해당하는 모델 클래스
 */
public class Screen {
    private Integer screenId;
    private String name;
    private Integer totalSeats;
    private Integer rows;
    private Integer cols;
    
    public Screen() {
    }
    
    public Screen(Integer screenId, String name, Integer totalSeats, Integer rows, Integer cols) {
        this.screenId = screenId;
        this.name = name;
        this.totalSeats = totalSeats;
        this.rows = rows;
        this.cols = cols;
    }
    
    // Getters and Setters
    public Integer getScreenId() {
        return screenId;
    }
    
    public void setScreenId(Integer screenId) {
        this.screenId = screenId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getTotalSeats() {
        return totalSeats;
    }
    
    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }
    
    public Integer getRows() {
        return rows;
    }
    
    public void setRows(Integer rows) {
        this.rows = rows;
    }
    
    public Integer getCols() {
        return cols;
    }
    
    public void setCols(Integer cols) {
        this.cols = cols;
    }
    
    @Override
    public String toString() {
        return "Screen{" +
                "screenId=" + screenId +
                ", name='" + name + '\'' +
                ", totalSeats=" + totalSeats +
                ", rows=" + rows +
                ", cols=" + cols +
                '}';
    }
}




