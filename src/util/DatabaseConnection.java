package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_SERVER = "localhost\\SQLEXPRESS";
    private static final String DB_PORT = "1433";
    private static final String DB_NAME = "Theater";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "inha1958";
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private static final String DB_PATH = "C:\\Users\\User\\OneDrive\\바탕 화면\\Theater.accdb";
    private static final String ACCESS_DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";

    private static final String DB_TYPE = "MSSQL";

    public static Connection getConnection() throws SQLException {
        if ("MSSQL".equals(DB_TYPE)) {
            return getMSSQLConnection();
        } else {
            return getAccessConnection();
        }
    }

    private static Connection getMSSQLConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MS-SQL JDBC 드라이버를 찾을 수 없습니다. mssql-jdbc 라이브러리를 확인하세요.", e);
        }

        String[] connectionStrings = {
            String.format("jdbc:sqlserver://%s;databaseName=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=true",
                DB_SERVER, DB_NAME, DB_USER, DB_PASSWORD),
            String.format("jdbc:sqlserver://%s:%s;databaseName=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=true",
                DB_SERVER, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD),
            String.format("jdbc:sqlserver://localhost;databaseName=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=true",
                DB_NAME, DB_USER, DB_PASSWORD),
            String.format("jdbc:sqlserver://localhost:%s;databaseName=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=true",
                DB_PORT, DB_NAME, DB_USER, DB_PASSWORD),
            String.format("jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=true",
                DB_NAME, DB_USER, DB_PASSWORD)
        };
        
        SQLException lastException = null;
        for (String connectionString : connectionStrings) {
            try {

                String logString = connectionString.replace(DB_PASSWORD, "****");
                System.out.println("[DB 연결 시도] " + logString);
                
                Connection conn = DriverManager.getConnection(connectionString);
                System.out.println("[DB 연결 성공!] " + logString);
                return conn;
            } catch (SQLException e) {
                lastException = e;

            }
        }

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

    private static Connection getAccessConnection() throws SQLException {
        try {
            Class.forName(ACCESS_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("UCanAccess 드라이버를 찾을 수 없습니다. 라이브러리를 확인하세요.", e);
        }

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

