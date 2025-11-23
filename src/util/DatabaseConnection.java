package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 데이터베이스 연결을 관리하는 유틸리티 클래스
 */
public class DatabaseConnection {
    
    private static final String DB_PATH = "C:\\Users\\User\\OneDrive\\바탕 화면\\Theater.accdb";
    private static final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
    
    /**
     * 데이터베이스 연결을 반환합니다.
     * @return Connection 객체
     * @throws SQLException 연결 실패 시
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("UCanAccess 드라이버를 찾을 수 없습니다. 라이브러리를 확인하세요.", e);
        }
        
        // 여러 연결 문자열 옵션 시도
        String[] connectionStrings = {
            "jdbc:ucanaccess://" + DB_PATH + ";memory=false;ignoreCase=true",
            "jdbc:ucanaccess://" + DB_PATH + ";memory=false",
            "jdbc:ucanaccess://" + DB_PATH + ";ignoreCase=true",
            "jdbc:ucanaccess://" + DB_PATH
        };
        
        SQLException lastException = null;
        for (String url : connectionStrings) {
            try {
                return DriverManager.getConnection(url);
            } catch (SQLException e) {
                lastException = e;
                // 다음 옵션 시도
            }
        }
        
        throw lastException != null ? lastException : new SQLException("데이터베이스 연결에 실패했습니다.");
    }
    
    /**
     * 연결을 안전하게 닫습니다.
     * @param conn 닫을 Connection 객체
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("연결 종료 중 오류 발생: " + e.getMessage());
            }
        }
    }
}

