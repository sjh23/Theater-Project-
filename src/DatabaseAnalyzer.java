import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Access DB의 테이블 구조를 분석하는 유틸리티 클래스
 */
public class DatabaseAnalyzer {
    
    private static final String DB_PATH = "C:\\Users\\User\\OneDrive\\바탕 화면\\Theater.accdb";
    
    public static void main(String[] args) {
        analyzeDatabase();
    }
    
    public static void analyzeDatabase() {
        Connection conn = null;
        try {
            // UCanAccess JDBC 드라이버 로드 시도
            try {
                Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            } catch (ClassNotFoundException e) {
                System.err.println("==========================================");
                System.err.println("UCanAccess 라이브러리가 없습니다!");
                System.err.println("==========================================");
                System.err.println("\n다음 단계를 따라주세요:");
                System.err.println("1. UCanAccess 다운로드:");
                System.err.println("   https://sourceforge.net/projects/ucanaccess/");
                System.err.println("\n2. 다음 JAR 파일들을 다운로드:");
                System.err.println("   - ucanaccess-5.x.x.jar");
                System.err.println("   - commons-lang3-3.x.x.jar");
                System.err.println("   - commons-logging-1.x.x.jar");
                System.err.println("   - hsqldb-2.x.x.jar");
                System.err.println("   - jackcess-3.x.x.jar");
                System.err.println("\n3. NetBeans에서 프로젝트 우클릭 → Properties → Libraries");
                System.err.println("   → Add JAR/Folder로 위 JAR 파일들 추가");
                System.err.println("\n또는 Maven을 사용하는 경우:");
                System.err.println("   <dependency>");
                System.err.println("       <groupId>net.ucanaccess</groupId>");
                System.err.println("       <artifactId>ucanaccess</artifactId>");
                System.err.println("       <version>5.0.1</version>");
                System.err.println("   </dependency>");
                System.err.println("==========================================");
                return;
            }
            
            // UCanAccess JDBC 드라이버 사용
            // 여러 연결 문자열 옵션 시도
            String[] connectionStrings = {
                "jdbc:ucanaccess://" + DB_PATH + ";memory=false;ignoreCase=true",
                "jdbc:ucanaccess://" + DB_PATH + ";memory=false",
                "jdbc:ucanaccess://" + DB_PATH + ";ignoreCase=true",
                "jdbc:ucanaccess://" + DB_PATH
            };
            
            conn = null;
            SQLException lastException = null;
            
            for (String url : connectionStrings) {
                try {
                    System.out.println("연결 시도: " + url);
                    conn = DriverManager.getConnection(url);
                    System.out.println("연결 성공!\n");
                    break;
                } catch (SQLException e) {
                    lastException = e;
                    System.err.println("연결 실패: " + e.getMessage());
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException ignored) {}
                        conn = null;
                    }
                }
            }
            
            if (conn == null) {
                throw lastException != null ? lastException : new SQLException("모든 연결 시도 실패");
            }
            
            DatabaseMetaData metaData = conn.getMetaData();
            
            System.out.println("=== 데이터베이스 정보 ===");
            System.out.println("데이터베이스 제품명: " + metaData.getDatabaseProductName());
            System.out.println("드라이버명: " + metaData.getDriverName());
            System.out.println("\n");
            
            // 모든 테이블 목록 가져오기
            System.out.println("=== 테이블 목록 ===");
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            List<String> tableNames = new ArrayList<>();
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName);
                System.out.println("- " + tableName);
            }
            tables.close();
            System.out.println("\n");
            
            // 각 테이블의 컬럼 정보 출력
            for (String tableName : tableNames) {
                System.out.println("=== 테이블: " + tableName + " ===");
                ResultSet columns = metaData.getColumns(null, null, tableName, null);
                
                System.out.println("컬럼명\t\t데이터타입\t\tNULL 허용\t기본값");
                System.out.println("------------------------------------------------------------");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String dataType = columns.getString("TYPE_NAME");
                    int nullable = columns.getInt("NULLABLE");
                    String defaultValue = columns.getString("COLUMN_DEF");
                    
                    System.out.printf("%-20s %-15s %-10s %s%n", 
                        columnName, 
                        dataType, 
                        nullable == 1 ? "YES" : "NO",
                        defaultValue != null ? defaultValue : "");
                }
                columns.close();
                System.out.println("\n");
            }
            
            // 외래키 관계 정보
            System.out.println("=== 외래키 관계 ===");
            for (String tableName : tableNames) {
                ResultSet foreignKeys = metaData.getImportedKeys(null, null, tableName);
                boolean hasFK = false;
                while (foreignKeys.next()) {
                    if (!hasFK) {
                        System.out.println("테이블: " + tableName);
                        hasFK = true;
                    }
                    String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
                    String pkTableName = foreignKeys.getString("PKTABLE_NAME");
                    String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
                    System.out.println("  " + fkColumnName + " -> " + pkTableName + "." + pkColumnName);
                }
                foreignKeys.close();
                if (hasFK) {
                    System.out.println();
                }
            }
            
        } catch (SQLException e) {
            System.err.println("==========================================");
            System.err.println("데이터베이스 연결 오류 발생");
            System.err.println("==========================================");
            System.err.println("오류 메시지: " + e.getMessage());
            System.err.println("\n");
            
            // Functions 관련 오류인 경우 특별 안내
            if (e.getMessage() != null && e.getMessage().contains("Functions")) {
                System.err.println("이 오류는 UCanAccess와 HSQLDB 버전 호환성 문제일 수 있습니다.");
                System.err.println("\n해결 방법:");
                System.err.println("1. HSQLDB 버전 확인:");
                System.err.println("   - 현재 사용 중인 hsqldb JAR 파일의 버전을 확인하세요");
                System.err.println("   - UCanAccess 5.0.1은 hsqldb 2.7.1과 호환됩니다");
                System.err.println("\n2. JAR 파일 순서 확인:");
                System.err.println("   - NetBeans에서 Libraries 탭에서 JAR 순서 확인");
                System.err.println("   - ucanaccess-5.0.1.jar가 먼저 로드되도록 해야 합니다");
                System.err.println("\n3. 대안:");
                System.err.println("   - UCanAccess 4.0.4 버전 사용 시도 (더 안정적)");
                System.err.println("   - 또는 모든 JAR 파일을 삭제하고 다시 추가");
            }
            
            System.err.println("\n전체 오류 스택:");
            e.printStackTrace();
            System.err.println("==========================================");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

