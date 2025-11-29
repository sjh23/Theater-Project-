/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import dao.ScheduleDAO;
import dao.ScheduleDAO.DailyScheduleInfo;
import model.User;
import java.sql.Date;

/**
 *
 * @author User
 */
public class DailyScheduleForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DailyScheduleForm.class.getName());
    
    private ScheduleDAO scheduleDAO = new ScheduleDAO();
    private LocalDate startDate = LocalDate.now();
    private LocalDate selectedDate = LocalDate.now();
    private List<JButton> dateButtons = new ArrayList<>();
    private SwingWorker<List<DailyScheduleInfo>, Void> currentWorker = null;  // 현재 실행 중인 작업 추적
    private User currentUser = null; // 로그인된 사용자 정보

    /**
     * Creates new form DailyScheduleForm
     */
    public DailyScheduleForm() {
        initComponents();
        
        initializeComponents();
    }
    
    /**
     * 컴포넌트 초기화 및 이벤트 핸들러 설정
     */
    private void initializeComponents() {
        // 날짜 버튼 리스트 초기화
        dateButtons.add(btnDate1);
        dateButtons.add(btnDate2);
        dateButtons.add(btnDate3);
        dateButtons.add(btnDate4);
        dateButtons.add(btnDate5);
        dateButtons.add(btnDate6);
        dateButtons.add(btnDate7);
        
        // 날짜 버튼에 이벤트 추가
        for (int i = 0; i < dateButtons.size(); i++) {
            final int index = i;
            JButton btn = dateButtons.get(i);
            btn.addActionListener(e -> selectDate(startDate.plusDays(index)));
        }
        
        // 이전/다음 버튼 이벤트
        btnPrevDate.addActionListener(e -> {
            startDate = startDate.minusDays(7);
            updateDateButtons();
        });
        
        btnNextDate.addActionListener(e -> {
            startDate = startDate.plusDays(7);
            updateDateButtons();
        });
        
        // 테이블 초기화
        initializeTable();
        
        // 테이블 더블클릭 이벤트 추가
        addTableDoubleClickEvent();
        
        // 날짜 버튼 업데이트
        updateDateButtons();
        
        // 초기 데이터 로드
        loadDailySchedule();
    }
    
    /**
     * 로그인된 사용자 정보를 설정합니다.
     */
    public void setUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * 테이블 초기화
     */
    private void initializeTable() {
        String[] columnNames = {"영화 제목", "상영시간", "등급", "상영관", "Schedule_ID"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblSchedule.setModel(model);
        tblSchedule.setRowHeight(40);
        tblSchedule.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Schedule_ID 컬럼 숨기기
        if (tblSchedule.getColumnModel().getColumnCount() > 4) {
            tblSchedule.getColumnModel().getColumn(4).setMinWidth(0);
            tblSchedule.getColumnModel().getColumn(4).setMaxWidth(0);
            tblSchedule.getColumnModel().getColumn(4).setWidth(0);
        }
        
        // 컬럼 너비 설정
        tblSchedule.getColumnModel().getColumn(0).setPreferredWidth(300); // 영화 제목
        tblSchedule.getColumnModel().getColumn(1).setPreferredWidth(150); // 상영시간
        tblSchedule.getColumnModel().getColumn(2).setPreferredWidth(100); // 등급
        tblSchedule.getColumnModel().getColumn(3).setPreferredWidth(150); // 상영관
    }
    
    /**
     * 테이블 더블클릭 이벤트 추가 (예매 화면으로 이동)
     */
    private void addTableDoubleClickEvent() {
        tblSchedule.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = tblSchedule.getSelectedRow();
                    
                    if (selectedRow < 0) {
                        return;
                    }
                    
                    // "선택한 날짜에 상영 시간표가 없습니다." 같은 메시지 행은 무시
                    DefaultTableModel model = (DefaultTableModel) tblSchedule.getModel();
                    Object scheduleIdObj = model.getValueAt(selectedRow, 4); // Schedule_ID 컬럼
                    
                    if (scheduleIdObj == null) {
                        logger.info("유효하지 않은 행 선택 (Schedule_ID 없음)");
                        return;
                    }
                    
                    Integer scheduleId = null;
                    if (scheduleIdObj instanceof Integer) {
                        scheduleId = (Integer) scheduleIdObj;
                    } else if (scheduleIdObj instanceof String) {
                        try {
                            scheduleId = Integer.parseInt((String) scheduleIdObj);
                        } catch (NumberFormatException ex) {
                            logger.warning("Schedule_ID 파싱 실패: " + scheduleIdObj);
                            return;
                        }
                    }
                    
                    if (scheduleId == null || scheduleId <= 0) {
                        logger.warning("유효하지 않은 Schedule_ID: " + scheduleIdObj);
                        return;
                    }
                    
                    // 로그인 확인
                    if (currentUser == null) {
                        int result = JOptionPane.showConfirmDialog(
                            DailyScheduleForm.this,
                            "예매를 하려면 로그인이 필요합니다.\n로그인 화면으로 이동하시겠습니까?",
                            "로그인 필요",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                        );
                        
                        if (result == JOptionPane.YES_OPTION) {
                            // MainFrame 찾기
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
                            
                            // 로그인 화면 열기
                            dispose();
                            LoginFrame loginFrame = new LoginFrame(mainFrame);
                            loginFrame.setLocationRelativeTo(mainFrame);
                            loginFrame.setVisible(true);
                        }
                        return;
                    }
                    
                    // 예매 확인
                    String movieTitle = (String) model.getValueAt(selectedRow, 0);
                    String showtime = (String) model.getValueAt(selectedRow, 1);
                    String screenName = (String) model.getValueAt(selectedRow, 3);
                    
                    int confirm = JOptionPane.showConfirmDialog(
                        DailyScheduleForm.this,
                        "영화: " + movieTitle + "\n" +
                        "상영시간: " + showtime + "\n" +
                        "상영관: " + screenName + "\n\n" +
                        "좌석 선택 화면으로 이동하시겠습니까?",
                        "예매 진행",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            logger.info("SeatSelectionForm 생성 시작 - ScheduleID: " + scheduleId);
                            SeatSelectionForm seatForm = new SeatSelectionForm();
                            seatForm.setShowtimeId(scheduleId);
                            seatForm.setUser(currentUser);
                            seatForm.setLocationRelativeTo(DailyScheduleForm.this);
                            seatForm.setVisible(true);
                            logger.info("SeatSelectionForm 표시 완료");
                            dispose();
                        } catch (Exception ex) {
                            logger.severe("좌석 선택 화면 열기 실패: " + ex.getMessage());
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(
                                DailyScheduleForm.this,
                                "좌석 선택 화면을 열 수 없습니다: " + ex.getMessage() + "\n\n자세한 내용은 콘솔을 확인하세요.",
                                "오류",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }
            }
        });
    }
    
    /**
     * 날짜 버튼 텍스트 업데이트
     */
    private void updateDateButtons() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd (E)");
        for (int i = 0; i < dateButtons.size(); i++) {
            LocalDate date = startDate.plusDays(i);
            JButton btn = dateButtons.get(i);
            btn.setText(date.format(formatter));
        }
    }
    
    /**
     * 날짜 선택 및 테이블 업데이트
     */
    private void selectDate(LocalDate date) {
        selectedDate = date;
        updateDateButtons();
        loadDailySchedule();
    }
    
    /**
     * 일일 상영시간표 로드
     */
    private void loadDailySchedule() {
        // 이전 작업이 있으면 취소
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
            logger.info("이전 로딩 작업 취소됨");
        }
        
        // 테이블 먼저 비우기
        DefaultTableModel model = (DefaultTableModel) tblSchedule.getModel();
        model.setRowCount(0);
        model.fireTableDataChanged();
        
        // 로딩 표시
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // SwingWorker로 백그라운드에서 데이터 로드
        currentWorker = new SwingWorker<List<DailyScheduleInfo>, Void>() {
            @Override
            protected List<DailyScheduleInfo> doInBackground() throws Exception {
                Date sqlDate = Date.valueOf(selectedDate);
                logger.info("일일 상영시간표 조회 시작 - Date: " + sqlDate);
                
                List<DailyScheduleInfo> result = scheduleDAO.getDailyScheduleInfoByDate(sqlDate);
                
                logger.info("일일 상영시간표 조회 완료 - 결과 개수: " + (result != null ? result.size() : 0));
                
                return result;
            }
            
            @Override
            protected void done() {
                // 작업이 취소되었으면 테이블 업데이트 하지 않음
                if (isCancelled()) {
                    logger.info("로딩 작업이 취소되었습니다.");
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    return;
                }
                
                // done()은 EDT에서 실행되므로 직접 UI 업데이트 가능
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                
                DefaultTableModel tableModel = (DefaultTableModel) tblSchedule.getModel();
                
                try {
                    List<DailyScheduleInfo> schedules = get();
                    
                    // 테이블 비우기
                    tableModel.setRowCount(0);
                    
                    if (schedules == null || schedules.isEmpty()) {
                        logger.warning("일일 상영시간표 데이터 없음 - Date: " + selectedDate);
                        // 데이터가 없을 때 메시지 표시
                        tableModel.addRow(new Object[]{
                            "선택한 날짜에 상영 시간표가 없습니다.",
                            "-",
                            "-",
                            "-",
                            null  // Schedule_ID 없음
                        });
                        // 테이블 새로고침
                        tableModel.fireTableDataChanged();
                        tblSchedule.revalidate();
                        tblSchedule.repaint();
                        return;
                    }
                    
                    // 테이블에 데이터 추가
                    for (DailyScheduleInfo info : schedules) {
                        if (info == null || info.scheduleId == null) {
                            continue;
                        }
                        
                        // 영화 제목
                        String movieTitle = (info.movieTitle != null) ? info.movieTitle : "제목 없음";
                        
                        // 상영시간 포맷팅
                        String timeStr = "미정";
                        if (info.startTime != null) {
                            try {
                                timeStr = info.startTime.toLocalDateTime().toLocalTime().format(
                                    java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                            } catch (Exception e) {
                                logger.warning("상영시간 포맷팅 실패: " + e.getMessage());
                            }
                        }
                        
                        // 등급
                        String rating = (info.movieRating != null && !info.movieRating.trim().isEmpty()) 
                                       ? info.movieRating 
                                       : "일반";
                        
                        // 상영관
                        String screenName = (info.screenName != null) ? info.screenName : "미정";
                        
                        // 테이블에 추가
                        tableModel.addRow(new Object[]{
                            movieTitle,
                            timeStr,
                            rating,
                            screenName,
                            info.scheduleId  // 숨겨진 컬럼
                        });
                    }
                    
                    logger.info("일일 상영시간표 데이터 로드 완료 - " + schedules.size() + "개");
                    
                    // 테이블 새로고침 강제
                    tableModel.fireTableDataChanged();
                    tblSchedule.revalidate();
                    tblSchedule.repaint();
                    
                } catch (java.util.concurrent.ExecutionException e) {
                    Throwable cause = e.getCause();
                    logger.severe("일일 상영시간표 로드 오류: " + (cause != null ? cause.getMessage() : e.getMessage()));
                    if (cause != null) {
                        cause.printStackTrace();
                    }
                    
                    tableModel.setRowCount(0);
                    tableModel.addRow(new Object[]{
                        "상영시간표를 불러오는 중 오류가 발생했습니다.",
                        "-",
                        "-",
                        "-",
                        null
                    });
                    
                    JOptionPane.showMessageDialog(DailyScheduleForm.this, 
                        "상영시간표를 불러오는 중 오류가 발생했습니다:\n" + 
                        (cause != null ? cause.getMessage() : e.getMessage()), 
                        "오류", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    logger.severe("일일 상영시간표 로드 오류: " + e.getMessage());
                    e.printStackTrace();
                    
                    tableModel.setRowCount(0);
                    tableModel.addRow(new Object[]{
                        "상영시간표를 불러오는 중 오류가 발생했습니다.",
                        "-",
                        "-",
                        "-",
                        null
                    });
                    
                    JOptionPane.showMessageDialog(DailyScheduleForm.this, 
                        "상영시간표를 불러오는 중 오류가 발생했습니다:\n" + e.getMessage(), 
                        "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        currentWorker.execute();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTopContainer = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblNavigation = new javax.swing.JLabel();
        scrollPaneSchedule = new javax.swing.JScrollPane();
        tblSchedule = new javax.swing.JTable();
        pnlDateSelection = new javax.swing.JPanel();
        btnPrevDate = new javax.swing.JButton();
        btnDate1 = new javax.swing.JButton();
        btnDate2 = new javax.swing.JButton();
        btnDate3 = new javax.swing.JButton();
        btnDate4 = new javax.swing.JButton();
        btnDate5 = new javax.swing.JButton();
        btnDate6 = new javax.swing.JButton();
        btnDate7 = new javax.swing.JButton();
        btnNextDate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("일일 상영시간표");
        setResizable(false);
        setSize(new java.awt.Dimension(100, 100));

        pnlTopContainer.setLayout(new javax.swing.BoxLayout(pnlTopContainer, javax.swing.BoxLayout.Y_AXIS));

        pnlHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlHeader.setLayout(new java.awt.BorderLayout());

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 1, 24)); // NOI18N
        lblTitle.setText("일일 상영표");
        pnlHeader.add(lblTitle, java.awt.BorderLayout.LINE_START);

        lblNavigation.setText("홈 > 영화 > 상영시간표 > 일일상영시간표");
        pnlHeader.add(lblNavigation, java.awt.BorderLayout.LINE_END);

        tblSchedule.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "영화 제목", "상영시간", "등급", "상영관", "상영 시간표"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSchedule.setRowHeight(40);
        tblSchedule.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        scrollPaneSchedule.setViewportView(tblSchedule);

        pnlHeader.add(scrollPaneSchedule, java.awt.BorderLayout.PAGE_END);

        pnlTopContainer.add(pnlHeader);

        btnPrevDate.setText("이전");
        btnPrevDate.setPreferredSize(new java.awt.Dimension(60, 30));
        pnlDateSelection.add(btnPrevDate);

        btnDate1.setText("11.29(토)");
        btnDate1.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlDateSelection.add(btnDate1);

        btnDate2.setText("11.30(일)");
        btnDate2.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlDateSelection.add(btnDate2);

        btnDate3.setText("12.01(월)");
        btnDate3.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlDateSelection.add(btnDate3);

        btnDate4.setText("12.02(화)");
        btnDate4.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlDateSelection.add(btnDate4);

        btnDate5.setText("12.03(수)");
        btnDate5.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlDateSelection.add(btnDate5);

        btnDate6.setText("12.04(목)");
        btnDate6.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlDateSelection.add(btnDate6);

        btnDate7.setText("11.05(금)");
        btnDate7.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlDateSelection.add(btnDate7);

        btnNextDate.setText("다음");
        btnNextDate.setPreferredSize(new java.awt.Dimension(60, 30));
        pnlDateSelection.add(btnNextDate);

        pnlTopContainer.add(pnlDateSelection);

        getContentPane().add(pnlTopContainer, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new DailyScheduleForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDate1;
    private javax.swing.JButton btnDate2;
    private javax.swing.JButton btnDate3;
    private javax.swing.JButton btnDate4;
    private javax.swing.JButton btnDate5;
    private javax.swing.JButton btnDate6;
    private javax.swing.JButton btnDate7;
    private javax.swing.JButton btnNextDate;
    private javax.swing.JButton btnPrevDate;
    private javax.swing.JLabel lblNavigation;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlDateSelection;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTopContainer;
    private javax.swing.JScrollPane scrollPaneSchedule;
    private javax.swing.JTable tblSchedule;
    // End of variables declaration//GEN-END:variables
}
