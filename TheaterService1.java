import java.sql.*;

public class TheaterService {
    // 1. 보안 취약점: 하드코딩된 DB 비밀번호 (CodeRabbit이 지적할 포인트)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/theater";;
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "12345678"; // 취약한 비밀번호

    public void bookSeat(String movieTitle, String seatNumber, String userId) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement stmt = conn.createStatement();

            // 2. 보안 취약점: SQL Injection 위험 (CodeRabbit이 강하게 지적할 포인트)
            // PreparedStatement를 쓰지 않고 문자열을 직접 더해서 쿼리를 만듦
            String query = "INSERT INTO bookings (movie, seat, user) VALUES ('" 
                            + movieTitle + "', '" + seatNumber + "', '" + userId + "')";
            
            stmt.executeUpdate(query);
            System.out.println("예매 성공!");

        } catch (Exception e) {
            // 3. 나쁜 습관: 예외 처리 미비 (그냥 출력만 하고 넘어감)
            e.printStackTrace();
        }
    }

    // 4. 비효율적 로직: 매번 전체 리스트를 순회하여 확인 (성능 최적화 지적 포인트)
    public boolean isSeatAvailable(String[] reservedSeats, String targetSeat) {
        for (int i = 0; i < reservedSeats.length; i++) {
            if (reservedSeats[i].equals(targetSeat)) {
                return false;
            }
        }
        return true;
    }
}
