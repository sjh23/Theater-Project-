package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 데이터베이스 연결을 관리하는 유틸리티 클래스
 * MS-SQL Server 연결을 지원합니다.
 */
public class DatabaseConnection {
    
    // MS-SQL Server 연결 정보
    private static final String DB_SERVER = "localhost\\SQLEXPRESS";
    private static final String DB_PORT = "1433";         // 포트 번호
    private static final String DB_NAME = "Theater";      // 데이터베이스 이름
    private static final String DB_USER = "sa";           // 사용자명
    private static final String DB_PASSWORD = "inha1958";  // 설치 시 설정한 비밀번호
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    
    // Access DB 연결 정보 (레거시 지원)
    private static final String DB_PATH = "C:\\Users\\User\\OneDrive\\바탕 화면\\Theater.accdb";
    private static final String ACCESS_DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
    
    // 사용할 데이터베이스 타입 (MSSQL 또는 ACCESS)
    private static final String DB_TYPE = "MSSQL";  // "MSSQL" 또는 "ACCESS"
    
    /**
     * 데이터베이스 연결을 반환합니다.
     * @return Connection 객체
     * @throws SQLException 연결 실패 시
     */
    public static Connection getConnection() throws SQLException {
        if ("MSSQL".equals(DB_TYPE)) {
            return getMSSQLConnection();
        } else {
            return getAccessConnection();
        }
    }
    
    /**
     * MS-SQL Server 연결을 반환합니다.
     * 여러 연결 방법을 시도하여 가장 적합한 방법으로 연결합니다.
     */
    private static Connection getMSSQLConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MS-SQL JDBC 드라이버를 찾을 수 없습니다. mssql-jdbc 라이브러리를 확인하세요.", e);
        }
        
        // 여러 연결 문자열 시도 (가능한 모든 형식)
        String[] connectionStrings = {
            // 1. Named Instance (포트 없음)
            String.format("jdbc:sqlserver://%s;databaseName=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=true",
                DB_SERVER, DB_NAME, DB_USER, DB_PASSWORD),
            // 2. 포트 포함
            String.format("jdbc:sqlserver://%s:%s;databaseName=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=true",
                DB_SERVER, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD),
            // 3. localhost 시도
            String.format("jdbc:sqlserver://localhost;databaseName=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=true",
                DB_NAME, DB_USER, DB_PASSWORD),
            // 4. localhost 포트 포함
            String.format("jdbc:sqlserver://localhost:%s;databaseName=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=true",
                DB_PORT, DB_NAME, DB_USER, DB_PASSWORD),
            // 5. localhost Named Instance
            String.format("jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=true",
                DB_NAME, DB_USER, DB_PASSWORD)
        };
        
        SQLException lastException = null;
        for (String connectionString : connectionStrings) {
            try {
                // 비밀번호를 숨기고 로그 출력
                String logString = connectionString.replace(DB_PASSWORD, "****");
                System.out.println("[DB 연결 시도] " + logString);
                
                Connection conn = DriverManager.getConnection(connectionString);
                System.out.println("[DB 연결 성공!] " + logString);
                return conn;
            } catch (SQLException e) {
                lastException = e;
                // 연결 실패 로그는 생략 (너무 많이 출력될 수 있음)
            }
        }
        
        // 모든 연결 시도 실패
        throw new SQLException(
            "MS-SQL 데이터베이스 연결에 실패했습니다. " +
            "다음 사항을 확인하세요:\n" +
            "1. SQL Server가 실행 중인지 확인\n" +
            "2. 서버 이름이 올바른지 확인 (현재: " + DB_SERVER + ")\n" +
            "3. 방화벽 설정 확인\n" +
            "4. 로컬 SQL Server 설치 여부 확인\n" +
            "마지막 오류: " + (lastException != null ? lastException.getMessage() : "알 수 없는 오류"),
            lastException
        );
    }
    
    /**
     * Access DB 연결을 반환합니다 (레거시 지원).
     */
    private static Connection getAccessConnection() throws SQLException {
        try {
            Class.forName(ACCESS_DRIVER);
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

