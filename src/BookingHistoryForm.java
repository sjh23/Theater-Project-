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

    public BookingHistoryForm() {
        initComponents();

        applyDarkTheme();

        initializeComponents();
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            loadBookingHistory();
        }
    }

    private void initializeComponents() {

        initializeTable();

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

        if (btnBackToMain != null) {
            btnBackToMain.addActionListener(e -> goBackToMain());
        }
    }

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

            if (tblBookingHistory.getColumnModel().getColumnCount() >= 7) {
                tblBookingHistory.getColumnModel().getColumn(0).setPreferredWidth(200);
                tblBookingHistory.getColumnModel().getColumn(1).setPreferredWidth(180);
                tblBookingHistory.getColumnModel().getColumn(2).setPreferredWidth(80);
                tblBookingHistory.getColumnModel().getColumn(3).setPreferredWidth(80);
                tblBookingHistory.getColumnModel().getColumn(4).setPreferredWidth(100);
                tblBookingHistory.getColumnModel().getColumn(5).setPreferredWidth(80);
                tblBookingHistory.getColumnModel().getColumn(6).setPreferredWidth(180);
            }
        }
    }

    private void loadBookingHistory() {
        if (currentUser == null) {
            logger.warning("사용자 정보가 없어 예매 내역을 로드할 수 없습니다.");
            return;
        }

        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
            logger.info("이전 로딩 작업 취소됨");
        }

        DefaultTableModel model = (DefaultTableModel) tblBookingHistory.getModel();
        model.setRowCount(0);
        model.fireTableDataChanged();

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

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
                    model.setRowCount(0);
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    
                    if (historyList == null || historyList.isEmpty()) {

                        model.addRow(new Object[]{
                            "예매 내역이 없습니다.", "", "", "", "", "", ""
                        });
                    } else {

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

    private void goBackToMain() {

        MainFrame mainFrame = null;
        for (java.awt.Window window : java.awt.Window.getWindows()) {
            if (window instanceof MainFrame && window.isVisible()) {
                mainFrame = (MainFrame) window;
                break;
            }
        }
        
        if (mainFrame == null) {

            mainFrame = new MainFrame();
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        }
        
        dispose();
    }

    private void applyDarkTheme() {

    }

    @SuppressWarnings("unchecked")

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

        btnBackToMain.setFont(new java.awt.Font("맑은 고딕", 0, 14));
        btnBackToMain.setText("메인으로");
        btnBackToMain.setBorderPainted(false);
        btnBackToMain.setFocusPainted(false);
        btnBackToMain.setPreferredSize(new java.awt.Dimension(100, 35));
        pnlHeader.add(btnBackToMain, java.awt.BorderLayout.LINE_START);

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 1, 24));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("예매 내역");
        pnlHeader.add(lblTitle, java.awt.BorderLayout.CENTER);

        btnRefresh.setFont(new java.awt.Font("맑은 고딕", 0, 14));
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
    }

    private javax.swing.JButton btnBackToMain;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JScrollPane scrollPaneHistory;
    private javax.swing.JTable tblBookingHistory;

}
