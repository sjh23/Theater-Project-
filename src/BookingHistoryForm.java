/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author User
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import dao.BookingDAO;
import dao.BookingDAO.BookingHistoryInfo;
import model.User;
import util.DesignConstants;
import java.sql.SQLException;

public class BookingHistoryForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(BookingHistoryForm.class.getName());
    
    private User currentUser = null;
    private BookingDAO bookingDAO = new BookingDAO();
    private SwingWorker<List<BookingHistoryInfo>, Void> currentWorker = null;

    /**
     * Creates new form BookingHistoryForm
     */
    public BookingHistoryForm() {
        initComponents();
        
        // 다크 테마 적용
        applyDarkTheme();
        
        // 컴포넌트 초기화 및 이벤트 핸들러 설정
        initializeComponents();
    }
    
    /**
     * 로그인된 사용자 정보를 설정하고 예매 내역을 로드합니다.
     */
    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            loadBookingHistory();
        }
    }
    
    /**
     * 컴포넌트 초기화 및 이벤트 핸들러 설정
     */
    private void initializeComponents() {
        // 테이블 초기화
        initializeTable();
        
        // 새로고침 버튼 이벤트
        if (btnRefresh != null) {
            btnRefresh.addActionListener(e -> {
                if (currentUser != null) {
                    loadBookingHistory();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "로그인이 필요합니다.",
                        "로그인 필요",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        
        // 메인으로 버튼 이벤트
        if (btnBackToMain != null) {
            btnBackToMain.addActionListener(e -> goBackToMain());
        }
    }
    
    /**
     * 테이블 초기화
     */
    private void initializeTable() {
        String[] columnNames = {
            "영화", "상영시간", "상영관", "좌석", "가격", "상태", "예매일시"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        if (tblBookingHistory != null) {
            tblBookingHistory.setModel(model);
            tblBookingHistory.setRowHeight(40);
            tblBookingHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            // 컬럼 너비 설정
            if (tblBookingHistory.getColumnModel().getColumnCount() >= 7) {
                tblBookingHistory.getColumnModel().getColumn(0).setPreferredWidth(200); // 영화
                tblBookingHistory.getColumnModel().getColumn(1).setPreferredWidth(180); // 상영시간
                tblBookingHistory.getColumnModel().getColumn(2).setPreferredWidth(80);  // 상영관
                tblBookingHistory.getColumnModel().getColumn(3).setPreferredWidth(80);  // 좌석
                tblBookingHistory.getColumnModel().getColumn(4).setPreferredWidth(100); // 가격
                tblBookingHistory.getColumnModel().getColumn(5).setPreferredWidth(80);  // 상태
                tblBookingHistory.getColumnModel().getColumn(6).setPreferredWidth(180); // 예매일시
            }
        }
    }
    
    /**
     * 예매 내역 로드
     */
    private void loadBookingHistory() {
        if (currentUser == null) {
            logger.warning("사용자 정보가 없어 예매 내역을 로드할 수 없습니다.");
            return;
        }
        
        // 이전 작업이 있으면 취소
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
            logger.info("이전 로딩 작업 취소됨");
        }
        
        // 테이블 먼저 비우기
        DefaultTableModel model = (DefaultTableModel) tblBookingHistory.getModel();
        model.setRowCount(0);
        model.fireTableDataChanged();
        
        // 로딩 표시
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // SwingWorker로 백그라운드에서 데이터 로드
        currentWorker = new SwingWorker<List<BookingHistoryInfo>, Void>() {
            @Override
            protected List<BookingHistoryInfo> doInBackground() throws Exception {
                try {
                    logger.info("예매 내역 로드 시작 - UserID: " + currentUser.getUserId());
                    List<BookingHistoryInfo> historyList = bookingDAO.getBookingHistoryByUserId(currentUser.getUserId());
                    logger.info("예매 내역 로드 완료 - 개수: " + historyList.size());
                    return historyList;
                } catch (SQLException e) {
                    logger.severe("예매 내역 로드 중 SQL 오류: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                } catch (Exception e) {
                    logger.severe("예매 내역 로드 중 오류: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            }
            
            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                
                try {
                    if (isCancelled()) {
                        logger.info("예매 내역 로드 작업 취소됨");
                        return;
                    }
                    
                    List<BookingHistoryInfo> historyList = get();
                    DefaultTableModel model = (DefaultTableModel) tblBookingHistory.getModel();
                    model.setRowCount(0); // 기존 데이터 제거
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    
                    if (historyList == null || historyList.isEmpty()) {
                        // 예매 내역이 없는 경우
                        model.addRow(new Object[]{
                            "예매 내역이 없습니다.", "", "", "", "", "", ""
                        });
                    } else {
                        // 예매 내역이 있는 경우
                        for (BookingHistoryInfo info : historyList) {
                            String movieTitle = info.movieTitle != null ? info.movieTitle : "";
                            String showtime = "";
                            if (info.startTime != null && info.endTime != null) {
                                showtime = timeFormat.format(info.startTime) + " ~ " + timeFormat.format(info.endTime);
                            }
                            String screenName = info.screenName != null ? info.screenName : "";
                            String seat = (info.seatRow != null ? info.seatRow : "") + 
                                         (info.seatCol != null ? info.seatCol : "");
                            String price = info.totalPrice != null ? 
                                          String.format("%,d원", info.totalPrice.intValue()) : "0원";
                            String status = info.status != null ? info.status : "";
                            String bookingTime = info.bookingTime != null ? 
                                                dateFormat.format(info.bookingTime) : "";
                            
                            model.addRow(new Object[]{
                                movieTitle, showtime, screenName, seat, price, status, bookingTime
                            });
                        }
                    }
                    
                    model.fireTableDataChanged();
                    tblBookingHistory.revalidate();
                    tblBookingHistory.repaint();
                    
                    logger.info("예매 내역 테이블 업데이트 완료");
                    
                } catch (Exception e) {
                    logger.severe("예매 내역 표시 중 오류: " + e.getMessage());
                    e.printStackTrace();
                    
                    JOptionPane.showMessageDialog(BookingHistoryForm.this,
                        "예매 내역을 불러오는 중 오류가 발생했습니다: " + e.getMessage(),
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
                    
                    // 오류 발생 시에도 테이블에 오류 메시지 표시
                    DefaultTableModel model = (DefaultTableModel) tblBookingHistory.getModel();
                    model.setRowCount(0);
                    model.addRow(new Object[]{
                        "데이터를 불러오는 중 오류가 발생했습니다.", "", "", "", "", "", ""
                    });
                    model.fireTableDataChanged();
                }
            }
        };
        
        currentWorker.execute();
    }
    
    /**
     * 메인 화면으로 돌아가기
     */
    private void goBackToMain() {
        // 현재 열려있는 MainFrame 찾기
        MainFrame mainFrame = null;
        for (java.awt.Window window : java.awt.Window.getWindows()) {
            if (window instanceof MainFrame && window.isVisible()) {
                mainFrame = (MainFrame) window;
                break;
            }
        }
        
        if (mainFrame == null) {
            // MainFrame이 없으면 새로 생성
            mainFrame = new MainFrame();
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        }
        
        dispose();
    }
    
    /**
     * 참고 디자인 기반 디자인 요소를 적용합니다.
     * (색상 제외 - 폰트, 레이아웃, 버튼 스타일만 적용)
     */
    private void applyDarkTheme() {
        // 폰트는 .form에서 설정됨
        // 여기서는 추가 스타일 적용만 수행
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        btnBackToMain = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        scrollPaneHistory = new javax.swing.JScrollPane();
        tblBookingHistory = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("예매 내역");
        setPreferredSize(new java.awt.Dimension(1200, 700));
        setResizable(false);

        pnlHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        pnlHeader.setPreferredSize(new java.awt.Dimension(1200, 80));
        pnlHeader.setLayout(new java.awt.BorderLayout());

        btnBackToMain.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnBackToMain.setText("메인으로");
        btnBackToMain.setBorderPainted(false);
        btnBackToMain.setFocusPainted(false);
        btnBackToMain.setPreferredSize(new java.awt.Dimension(100, 35));
        pnlHeader.add(btnBackToMain, java.awt.BorderLayout.LINE_START);

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 1, 24)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("예매 내역");
        pnlHeader.add(lblTitle, java.awt.BorderLayout.CENTER);

        btnRefresh.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnRefresh.setText("새로고침");
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setPreferredSize(new java.awt.Dimension(100, 35));
        pnlHeader.add(btnRefresh, java.awt.BorderLayout.LINE_END);

        getContentPane().add(pnlHeader, java.awt.BorderLayout.PAGE_START);

        tblBookingHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "영화", "상영시간", "상영관", "좌석", "가격", "상태", "예매일시"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBookingHistory.setRowHeight(35);
        tblBookingHistory.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        scrollPaneHistory.setViewportView(tblBookingHistory);

        getContentPane().add(scrollPaneHistory, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBackToMain;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JScrollPane scrollPaneHistory;
    private javax.swing.JTable tblBookingHistory;
    // End of variables declaration//GEN-END:variables
}
