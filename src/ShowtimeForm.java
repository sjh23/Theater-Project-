import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import dao.ScheduleDAO;
import dao.ScreenDAO;
import dao.ScheduleDAO.ShowtimeInfo;
import model.Schedule;
import model.Screen;
import model.User;
import util.DesignConstants;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;

public class ShowtimeForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ShowtimeForm.class.getName());
    
    private String movieTitle = "";
    private Integer movieId = null;
    private LocalDate startDate = LocalDate.now();
    private LocalDate selectedDate = LocalDate.now();
    private List<JButton> dateButtons = new ArrayList<>();
    private boolean componentsInitialized = false;
    private User currentUser = null;
    
    private ScheduleDAO scheduleDAO = new ScheduleDAO();
    private ScreenDAO screenDAO = new ScreenDAO();

    public ShowtimeForm() {
        initComponents();

        applyDarkTheme();
        
        initializeComponents();
    }

    private void applyDarkTheme() {

        if (lblSelectedMovie != null) {
            lblSelectedMovie.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_LARGE));
        }

        if (btnPrev != null) {
            btnPrev.setFont(DesignConstants.getDefaultFont());
            btnPrev.setBorderPainted(false);
            btnPrev.setFocusPainted(false);
        }
        
        if (btnNext != null) {
            btnNext.setFont(DesignConstants.getDefaultFont());
            btnNext.setBorderPainted(false);
            btnNext.setFocusPainted(false);
        }

        if (tblShowtimes != null) {
            tblShowtimes.setFont(DesignConstants.getDefaultFont());
        }

    }
    
    public void setMovieTitle(String title) {
        this.movieTitle = title;

        if (lblSelectedMovie != null && title != null && !title.trim().isEmpty()) {
            lblSelectedMovie.setText(title);
        }
    }
    
    public void setMovieId(Integer movieId) {
        this.movieId = movieId;

        if (componentsInitialized && movieId != null) {
            selectFirstAvailableDate();
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
    }
    
    private void initializeComponents() {

        dateButtons.add(btnDate1);
        dateButtons.add(btnDate2);
        dateButtons.add(btnDate3);
        dateButtons.add(btnDate4);
        dateButtons.add(btnDate5);
        dateButtons.add(btnDate6);
        dateButtons.add(btnDate7);

        for (int i = 0; i < dateButtons.size(); i++) {
            final int index = i;
            JButton btn = dateButtons.get(i);
            btn.addActionListener(e -> selectDate(startDate.plusDays(index)));
        }

        btnPrev.addActionListener(e -> {
            startDate = startDate.minusDays(7);
            updateDateButtons();
        });
        
        btnNext.addActionListener(e -> {
            startDate = startDate.plusDays(7);
            updateDateButtons();
        });

        initializeShowtimeTable();

        updateDateButtons();

        updateStepDisplay();

        addBackToMainButton();

        componentsInitialized = true;

        if (movieId != null) {
            selectFirstAvailableDate();
        }
    }

    private void updateDateButtons() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd (E)");
        for (int i = 0; i < dateButtons.size(); i++) {
            LocalDate date = startDate.plusDays(i);
            JButton btn = dateButtons.get(i);
            btn.setText(date.format(formatter));

        }

    }

    private void selectDate(LocalDate date) {
        selectedDate = date;
        updateDateButtons();
        updateShowtimeTable();
    }
    
    private void initializeShowtimeTable() {
        String[] columnNames = {"상영시간", "상영관", "잔여석", "등급", "Schedule_ID"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblShowtimes.setModel(model);
        tblShowtimes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblShowtimes.setRowHeight(30);
        tblShowtimes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tblShowtimes.getColumnModel().getColumn(4).setMinWidth(0);
        tblShowtimes.getColumnModel().getColumn(4).setMaxWidth(0);
        tblShowtimes.getColumnModel().getColumn(4).setWidth(0);

        if (tblShowtimes.getColumnModel().getColumnCount() >= 4) {
            tblShowtimes.getColumnModel().getColumn(0).setPreferredWidth(120);
            tblShowtimes.getColumnModel().getColumn(1).setPreferredWidth(100);
            tblShowtimes.getColumnModel().getColumn(2).setPreferredWidth(100);
            tblShowtimes.getColumnModel().getColumn(3).setPreferredWidth(120);
        }

        tblShowtimes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = tblShowtimes.getSelectedRow();
                    if (selectedRow >= 0) {
                        String showtime = (String) tblShowtimes.getValueAt(selectedRow, 0);
                        String theater = (String) tblShowtimes.getValueAt(selectedRow, 1);

                        Integer scheduleId = (Integer) tblShowtimes.getValueAt(selectedRow, 4);

                        if ("상영 시간표 없음".equals(showtime)) {
                            JOptionPane.showMessageDialog(ShowtimeForm.this, 
                                "선택한 날짜에 상영 시간표가 없습니다.\n다른 날짜를 선택해주세요.", 
                                "안내", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }

                        logger.info("좌석 선택 화면 이동 요청 - ScheduleID: " + scheduleId + ", Showtime: " + showtime + ", Theater: " + theater);
                        
                        if (scheduleId == null) {
                            logger.warning("ScheduleID가 null입니다 - Showtime: " + showtime + ", Theater: " + theater);
                            JOptionPane.showMessageDialog(ShowtimeForm.this, 
                                "상영 시간표 정보가 올바르지 않습니다.\n다시 선택해주세요.", 
                                "오류", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (currentUser == null || currentUser.getUserId() == null) {
                            int loginResult = JOptionPane.showConfirmDialog(ShowtimeForm.this,
                                "예매를 하려면 로그인이 필요합니다.\n로그인 화면으로 이동하시겠습니까?",
                                "로그인 필요",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                            
                            if (loginResult == JOptionPane.YES_OPTION) {

                                java.awt.Window[] windows = java.awt.Window.getWindows();
                                MainFrame mainFrame = null;
                                for (java.awt.Window window : windows) {
                                    if (window instanceof MainFrame) {
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
                                LoginFrame loginFrame = new LoginFrame(mainFrame);
                                loginFrame.setLocationRelativeTo(mainFrame);
                                loginFrame.setVisible(true);
                            }
                            return;
                        }
                        
                        int confirm = JOptionPane.showConfirmDialog(
                            ShowtimeForm.this,
                            "상영시간: " + showtime + "\n상영관: " + theater + "\n\n좌석 선택 화면으로 이동하시겠습니까?",
                            "좌석 선택",
                            JOptionPane.YES_NO_OPTION
                        );
                        
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                logger.info("SeatSelectionForm 생성 시작 - ScheduleID: " + scheduleId);
                                SeatSelectionForm seatForm = new SeatSelectionForm();
                                seatForm.setShowtimeId(scheduleId);

                                seatForm.setUser(currentUser);
                                seatForm.setLocationRelativeTo(ShowtimeForm.this);
                                seatForm.setVisible(true);
                                logger.info("SeatSelectionForm 표시 완료");
                                dispose();
                            } catch (Exception e) {
                                logger.severe("좌석 선택 화면 열기 실패: " + e.getMessage());
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(ShowtimeForm.this, 
                                    "좌석 선택 화면을 열 수 없습니다: " + e.getMessage() + "\n\n자세한 내용은 콘솔을 확인하세요.", 
                                    "오류", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        });
        
        updateShowtimeTable();
    }

    private void updateShowtimeTable() {
        DefaultTableModel model = (DefaultTableModel) tblShowtimes.getModel();
        model.setRowCount(0);
        
        if (movieId == null) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<List<ShowtimeInfo>, Void> worker = new SwingWorker<List<ShowtimeInfo>, Void>() {
            @Override
            protected List<ShowtimeInfo> doInBackground() throws Exception {

                Date sqlDate = Date.valueOf(selectedDate);

                logger.info("상영시간표 조회 시작 - MovieID: " + movieId + ", Date: " + sqlDate);

                List<ShowtimeInfo> result = scheduleDAO.getShowtimeInfoByMovieAndDate(movieId, sqlDate);
                
                logger.info("상영시간표 조회 완료 - 결과 개수: " + (result != null ? result.size() : 0));

                if (result == null || result.isEmpty()) {
                    logger.warning("상영시간표 조회 결과가 비어있습니다.");
                    logger.warning("  - MovieID: " + movieId);
                    logger.warning("  - Date: " + sqlDate);
                    logger.warning("  - DB에서 해당 날짜의 상영시간표 데이터가 있는지 확인하세요.");

                    try {
                        List<Schedule> allSchedules = scheduleDAO.getSchedulesByMovieId(movieId);
                        logger.info("  - 해당 영화의 전체 상영시간표 개수: " + (allSchedules != null ? allSchedules.size() : 0));
                        if (allSchedules != null && !allSchedules.isEmpty()) {
                            logger.info("  - 전체 상영시간표 날짜들:");
                            for (Schedule s : allSchedules) {
                                if (s.getStartTime() != null) {
                                    logger.info("    * " + s.getStartTime().toString());
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warning("  - 전체 상영시간표 조회 실패: " + e.getMessage());
                    }
                }
                
                return result;
            }
            
            @Override
            protected void done() {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                
                try {
                    List<ShowtimeInfo> showtimes = get();
                    
                    if (showtimes == null || showtimes.isEmpty()) {

                        logger.warning("상영 시간표 데이터 없음 - MovieID: " + movieId + ", Date: " + selectedDate);
                        model.addRow(new Object[]{
                            "상영 시간표 없음",
                            "-",
                            "-",
                            "-",
                            null
                        });

                        model.fireTableDataChanged();
                        tblShowtimes.revalidate();
                        tblShowtimes.repaint();
                        return;
                    }
                    
                    logger.info("상영 시간표 데이터 로드 완료 - " + showtimes.size() + "개");

                    for (ShowtimeInfo info : showtimes) {
                        if (info == null || info.scheduleId == null) {
                            continue;
                        }

                        String timeStr = "미정";
                        if (info.startTime != null) {
                            try {
                                timeStr = info.startTime.toLocalDateTime().toLocalTime().format(
                                    java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                            } catch (Exception e) {

                            }
                        }

                        String screenName = (info.screenName != null) ? info.screenName : "미정";

                        int remainingSeats = (info.remainingSeats != null) ? info.remainingSeats : 0;
                        String remainingSeatsStr = remainingSeats + "석";

                        String rating = (info.movieRating != null && !info.movieRating.trim().isEmpty()) 
                                       ? info.movieRating 
                                       : "일반";

                        model.addRow(new Object[]{
                            timeStr,
                            screenName,
                            remainingSeatsStr,
                            rating,
                            info.scheduleId
                        });
                    }

                    model.fireTableDataChanged();
                    tblShowtimes.revalidate();
                    tblShowtimes.repaint();
                    
                } catch (Exception e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    JOptionPane.showMessageDialog(ShowtimeForm.this, 
                        "상영시간표를 불러오는 중 오류가 발생했습니다:\n" + e.getMessage() + "\n\n" +
                        "자세한 내용은 콘솔을 확인하세요.", 
                        "오류", JOptionPane.ERROR_MESSAGE);
                    logger.severe("상영시간표 로드 오류: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    private void updateStepDisplay() {

        lblStep2.setFont(lblStep2.getFont().deriveFont(Font.BOLD));
    }

    private void selectFirstAvailableDate() {
        if (movieId == null) {
            return;
        }

        SwingWorker<LocalDate, Void> worker = new SwingWorker<LocalDate, Void>() {
            @Override
            protected LocalDate doInBackground() throws Exception {
                try {

                    List<Schedule> schedules = scheduleDAO.getSchedulesByMovieId(movieId);
                    
                    if (schedules == null || schedules.isEmpty()) {
                        logger.info("예매 가능한 상영시간표가 없습니다 - MovieID: " + movieId);
                        return null;
                    }
                    
                    LocalDate today = LocalDate.now();
                    LocalDate firstAvailableDate = null;

                    for (Schedule schedule : schedules) {
                        if (schedule.getStartTime() != null) {
                            LocalDate scheduleDate = schedule.getStartTime().toLocalDateTime().toLocalDate();

                            if (!scheduleDate.isBefore(today)) {

                                if (firstAvailableDate == null || scheduleDate.isBefore(firstAvailableDate)) {
                                    firstAvailableDate = scheduleDate;
                                }
                            }
                        }
                    }
                    
                    logger.info("예매 가능한 가장 가까운 날짜: " + firstAvailableDate + " - MovieID: " + movieId);
                    return firstAvailableDate;
                    
                } catch (SQLException e) {
                    logger.warning("예매 가능한 날짜 조회 중 오류: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            
            @Override
            protected void done() {
                try {
                    LocalDate availableDate = get();
                    
                    if (availableDate != null) {

                        adjustDateRange(availableDate);

                        selectDate(availableDate);
                        logger.info("자동 날짜 선택 완료: " + availableDate);
                    } else {

                        logger.info("예매 가능한 날짜가 없어 오늘 날짜를 사용합니다.");
                    }
                } catch (Exception e) {
                    logger.warning("자동 날짜 선택 중 오류: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }

    private void adjustDateRange(LocalDate targetDate) {

        if (targetDate.isBefore(startDate) || targetDate.isAfter(startDate.plusDays(6))) {

            startDate = targetDate;
            updateDateButtons();
        }
    }

    private void addBackToMainButton() {
        javax.swing.JButton btnBackToMain = new javax.swing.JButton("메인으로");
        btnBackToMain.setFont(DesignConstants.getDefaultFont());
        btnBackToMain.setBorderPainted(false);
        btnBackToMain.setFocusPainted(false);
        btnBackToMain.addActionListener(e -> goBackToMain());

        if (pnlFooter != null) {
            pnlFooter.add(btnBackToMain, 0);
        }
    }

    private void goBackToMain() {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setLocationRelativeTo(this);
        mainFrame.setVisible(true);
        this.dispose();
    }

    @SuppressWarnings("unchecked")

    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        pnlFooter = new javax.swing.JPanel();
        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        pnlHeader = new javax.swing.JPanel();
        lblSelectedMovie = new javax.swing.JLabel();
        pnlSteps = new javax.swing.JPanel();
        lblStep1 = new javax.swing.JLabel();
        lblStep2 = new javax.swing.JLabel();
        lblStep3 = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        pnlContent = new javax.swing.JPanel();
        pnlDateList = new javax.swing.JPanel();
        lblDateHeader = new javax.swing.JLabel();
        btnDate1 = new javax.swing.JButton();
        btnDate2 = new javax.swing.JButton();
        btnDate3 = new javax.swing.JButton();
        btnDate4 = new javax.swing.JButton();
        btnDate5 = new javax.swing.JButton();
        btnDate6 = new javax.swing.JButton();
        btnDate7 = new javax.swing.JButton();
        pnlShowtimeList = new javax.swing.JPanel();
        lblShowtimeHeader = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblShowtimes = new javax.swing.JTable();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnPrev.setText("이전");
        pnlFooter.add(btnPrev);

        btnNext.setText("다음");
        pnlFooter.add(btnNext);

        getContentPane().add(pnlFooter, java.awt.BorderLayout.SOUTH);

        pnlHeader.setLayout(new java.awt.BorderLayout());

        lblSelectedMovie.setText("영화명");
        pnlHeader.add(lblSelectedMovie, java.awt.BorderLayout.LINE_START);

        lblStep1.setText("영화");
        pnlSteps.add(lblStep1);

        lblStep2.setText("날짜");
        pnlSteps.add(lblStep2);

        lblStep3.setText("인원수/좌석");
        pnlSteps.add(lblStep3);

        pnlHeader.add(pnlSteps, java.awt.BorderLayout.CENTER);

        lblTitle.setText("영화 예매 시스템");
        pnlHeader.add(lblTitle, java.awt.BorderLayout.EAST);

        getContentPane().add(pnlHeader, java.awt.BorderLayout.NORTH);

        pnlContent.setLayout(new java.awt.BorderLayout());

        pnlDateList.setLayout(new javax.swing.BoxLayout(pnlDateList, javax.swing.BoxLayout.Y_AXIS));

        lblDateHeader.setText("날짜");
        pnlDateList.add(lblDateHeader);

        btnDate1.setText("jButton1");
        pnlDateList.add(btnDate1);

        btnDate2.setText("jButton1");
        pnlDateList.add(btnDate2);

        btnDate3.setText("jButton1");
        pnlDateList.add(btnDate3);

        btnDate4.setText("jButton1");
        pnlDateList.add(btnDate4);

        btnDate5.setText("jButton1");
        pnlDateList.add(btnDate5);

        btnDate6.setText("jButton1");
        pnlDateList.add(btnDate6);

        btnDate7.setText("jButton1");
        pnlDateList.add(btnDate7);

        pnlContent.add(pnlDateList, java.awt.BorderLayout.WEST);

        pnlShowtimeList.setLayout(new java.awt.BorderLayout());

        lblShowtimeHeader.setText("상영관 / 시간");
        pnlShowtimeList.add(lblShowtimeHeader, java.awt.BorderLayout.NORTH);

        tblShowtimes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblShowtimes);

        pnlShowtimeList.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pnlContent.add(pnlShowtimeList, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlContent, java.awt.BorderLayout.CENTER);

        pack();
    }

    public static void main(String args[]) {

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

        java.awt.EventQueue.invokeLater(() -> new ShowtimeForm().setVisible(true));
    }

    private javax.swing.JButton btnDate1;
    private javax.swing.JButton btnDate2;
    private javax.swing.JButton btnDate3;
    private javax.swing.JButton btnDate4;
    private javax.swing.JButton btnDate5;
    private javax.swing.JButton btnDate6;
    private javax.swing.JButton btnDate7;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDateHeader;
    private javax.swing.JLabel lblSelectedMovie;
    private javax.swing.JLabel lblShowtimeHeader;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlDateList;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlShowtimeList;
    private javax.swing.JPanel pnlSteps;
    private javax.swing.JTable tblShowtimes;

}
