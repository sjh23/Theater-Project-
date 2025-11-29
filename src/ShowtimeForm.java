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
    private boolean componentsInitialized = false; // 컴포넌트 초기화 여부
    private User currentUser = null; // 로그인된 사용자 정보
    
    private ScheduleDAO scheduleDAO = new ScheduleDAO();
    private ScreenDAO screenDAO = new ScreenDAO();

    /**
     * Creates new form ShowtimeForm
     */
    public ShowtimeForm() {
        initComponents();
        
        // 다크 테마 적용 (참고 디자인 기반)
        applyDarkTheme();
        
        initializeComponents();
    }
    
    /**
     * 참고 디자인 기반 디자인 요소를 적용합니다.
     * (색상 제외 - 폰트, 레이아웃, 버튼 스타일만 적용)
     */
    private void applyDarkTheme() {
        // 레이블 폰트만 적용 (색상 제외)
        if (lblSelectedMovie != null) {
            lblSelectedMovie.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_LARGE));
        }
        
        // 버튼 폰트 및 스타일만 적용 (색상 제외)
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
        
        // 테이블 폰트만 적용 (색상 제외)
        if (tblShowtimes != null) {
            tblShowtimes.setFont(DesignConstants.getDefaultFont());
        }
        
        // 날짜 버튼 스타일은 initializeComponents()에서 동적으로 처리
    }
    
    public void setMovieTitle(String title) {
        this.movieTitle = title;
        // 영화명 레이블 업데이트
        if (lblSelectedMovie != null && title != null && !title.trim().isEmpty()) {
            lblSelectedMovie.setText(title);
        }
    }
    
    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
        // 컴포넌트가 초기화된 후에만 자동 날짜 선택
        if (componentsInitialized && movieId != null) {
            selectFirstAvailableDate();
        }
    }
    
    /**
     * 로그인된 사용자 정보를 설정합니다.
     */
    public void setUser(User user) {
        this.currentUser = user;
    }
    
    private void initializeComponents() {
        // 날짜 버튼 초기화
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
        btnPrev.addActionListener(e -> {
            startDate = startDate.minusDays(7);
            updateDateButtons();
        });
        
        btnNext.addActionListener(e -> {
            startDate = startDate.plusDays(7);
            updateDateButtons();
        });
        
        // 상영시간표 테이블 초기화
        initializeShowtimeTable();
        
        // 날짜 버튼 업데이트
        updateDateButtons();
        
        // 단계 표시 업데이트
        updateStepDisplay();
        
        // 메인으로 돌아가기 버튼 추가
        addBackToMainButton();
        
        // 컴포넌트 초기화 완료
        componentsInitialized = true;
        
        // movieId가 이미 설정되어 있으면 예매 가능한 첫 번째 날짜 자동 선택
        if (movieId != null) {
            selectFirstAvailableDate();
        }
    }
    
    /**
     * 날짜 버튼 텍스트만 업데이트 (테이블 업데이트 없음 - 최적화)
     */
    private void updateDateButtons() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd (E)");
        for (int i = 0; i < dateButtons.size(); i++) {
            LocalDate date = startDate.plusDays(i);
            JButton btn = dateButtons.get(i);
            btn.setText(date.format(formatter));
            
            // 색상 설정 제거 - 날짜 선택 상태는 텍스트만으로 표시
            // 선택된 날짜는 나중에 .form 파일에서 스타일로 처리 가능
        }
        // 최적화: 날짜 버튼 텍스트만 업데이트할 때는 테이블 업데이트하지 않음
    }
    
    /**
     * 날짜 선택 및 상영시간표 업데이트
     */
    private void selectDate(LocalDate date) {
        selectedDate = date;
        updateDateButtons(); // 날짜 버튼 텍스트만 업데이트
        updateShowtimeTable(); // 선택된 날짜의 상영시간표만 업데이트
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
        
        // Schedule_ID 컬럼 숨기기
        tblShowtimes.getColumnModel().getColumn(4).setMinWidth(0);
        tblShowtimes.getColumnModel().getColumn(4).setMaxWidth(0);
        tblShowtimes.getColumnModel().getColumn(4).setWidth(0);
        
        // 컬럼 너비 설정 (초기화 후에도 설정됨)
        if (tblShowtimes.getColumnModel().getColumnCount() >= 4) {
            tblShowtimes.getColumnModel().getColumn(0).setPreferredWidth(120); // 상영시간
            tblShowtimes.getColumnModel().getColumn(1).setPreferredWidth(100); // 상영관
            tblShowtimes.getColumnModel().getColumn(2).setPreferredWidth(100); // 잔여석
            tblShowtimes.getColumnModel().getColumn(3).setPreferredWidth(120); // 등급
        }
        
        // 테이블 클릭 이벤트 - 좌석 선택 화면으로 이동
        tblShowtimes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = tblShowtimes.getSelectedRow();
                    if (selectedRow >= 0) {
                        String showtime = (String) tblShowtimes.getValueAt(selectedRow, 0);
                        String theater = (String) tblShowtimes.getValueAt(selectedRow, 1);
                        
                        // Schedule_ID 추출 (마지막 컬럼)
                        Integer scheduleId = (Integer) tblShowtimes.getValueAt(selectedRow, 4);
                        
                        // "상영 시간표 없음" 행은 클릭 불가 처리
                        if ("상영 시간표 없음".equals(showtime)) {
                            JOptionPane.showMessageDialog(ShowtimeForm.this, 
                                "선택한 날짜에 상영 시간표가 없습니다.\n다른 날짜를 선택해주세요.", 
                                "안내", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        
                        // 디버깅 로그
                        logger.info("좌석 선택 화면 이동 요청 - ScheduleID: " + scheduleId + ", Showtime: " + showtime + ", Theater: " + theater);
                        
                        if (scheduleId == null) {
                            logger.warning("ScheduleID가 null입니다 - Showtime: " + showtime + ", Theater: " + theater);
                            JOptionPane.showMessageDialog(ShowtimeForm.this, 
                                "상영 시간표 정보가 올바르지 않습니다.\n다시 선택해주세요.", 
                                "오류", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // 로그인 확인 - 예매하려면 로그인 필수
                        if (currentUser == null || currentUser.getUserId() == null) {
                            int loginResult = JOptionPane.showConfirmDialog(ShowtimeForm.this,
                                "예매를 하려면 로그인이 필요합니다.\n로그인 화면으로 이동하시겠습니까?",
                                "로그인 필요",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                            
                            if (loginResult == JOptionPane.YES_OPTION) {
                                // MainFrame 찾기
                                java.awt.Window[] windows = java.awt.Window.getWindows();
                                MainFrame mainFrame = null;
                                for (java.awt.Window window : windows) {
                                    if (window instanceof MainFrame) {
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
                                
                                // 로그인 화면 열기
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
                                // 로그인된 사용자 정보 전달 (로그인 확인 후이므로 반드시 있음)
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
    
    /**
     * 상영시간표 테이블을 업데이트합니다.
     * 최적화된 JOIN 쿼리를 사용하여 한 번에 모든 정보를 가져옵니다.
     */
    private void updateShowtimeTable() {
        DefaultTableModel model = (DefaultTableModel) tblShowtimes.getModel();
        model.setRowCount(0);
        
        if (movieId == null) {
            return;
        }
        
        // 로딩 표시
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // SwingWorker로 백그라운드에서 데이터 로드 (UI 블로킹 방지)
        SwingWorker<List<ShowtimeInfo>, Void> worker = new SwingWorker<List<ShowtimeInfo>, Void>() {
            @Override
            protected List<ShowtimeInfo> doInBackground() throws Exception {
                // 선택된 날짜를 SQL Date로 변환
                Date sqlDate = Date.valueOf(selectedDate);
                
                // 디버깅 로그
                logger.info("상영시간표 조회 시작 - MovieID: " + movieId + ", Date: " + sqlDate);
                
                // 최적화된 JOIN 쿼리로 한 번에 모든 정보 조회
                List<ShowtimeInfo> result = scheduleDAO.getShowtimeInfoByMovieAndDate(movieId, sqlDate);
                
                logger.info("상영시간표 조회 완료 - 결과 개수: " + (result != null ? result.size() : 0));
                
                // 결과가 비어있으면 상세 로그 출력
                if (result == null || result.isEmpty()) {
                    logger.warning("상영시간표 조회 결과가 비어있습니다.");
                    logger.warning("  - MovieID: " + movieId);
                    logger.warning("  - Date: " + sqlDate);
                    logger.warning("  - DB에서 해당 날짜의 상영시간표 데이터가 있는지 확인하세요.");
                    
                    // 대안: 날짜 없이 영화 ID로만 조회해서 데이터가 있는지 확인
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
                        // 데이터가 없으면 로그 출력 및 테이블에 안내 메시지 표시
                        logger.warning("상영 시간표 데이터 없음 - MovieID: " + movieId + ", Date: " + selectedDate);
                        model.addRow(new Object[]{
                            "상영 시간표 없음",
                            "-",
                            "-",
                            "-",
                            null
                        });
                        // 테이블 새로고침
                        model.fireTableDataChanged();
                        tblShowtimes.revalidate();
                        tblShowtimes.repaint();
                        return;
                    }
                    
                    logger.info("상영 시간표 데이터 로드 완료 - " + showtimes.size() + "개");
                    
                    // 테이블에 데이터 추가
                    for (ShowtimeInfo info : showtimes) {
                        if (info == null || info.scheduleId == null) {
                            continue;
                        }
                        
                        // 시간 포맷팅
                        String timeStr = "미정";
                        if (info.startTime != null) {
                            try {
                                timeStr = info.startTime.toLocalDateTime().toLocalTime().format(
                                    java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                            } catch (Exception e) {
                                // 포맷팅 실패 시 기본값 유지
                            }
                        }
                        
                        // 상영관 이름
                        String screenName = (info.screenName != null) ? info.screenName : "미정";
                        
                        // 잔여석
                        int remainingSeats = (info.remainingSeats != null) ? info.remainingSeats : 0;
                        String remainingSeatsStr = remainingSeats + "석";
                        
                        // 등급
                        String rating = (info.movieRating != null && !info.movieRating.trim().isEmpty()) 
                                       ? info.movieRating 
                                       : "일반";
                        
                        // 테이블에 추가
                        model.addRow(new Object[]{
                            timeStr,
                            screenName,
                            remainingSeatsStr,
                            rating,
                            info.scheduleId  // 숨겨진 컬럼으로 Schedule_ID 저장
                        });
                    }
                    
                    // 테이블 새로고침 강제
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
        // 현재 단계 하이라이트 (2단계: 날짜 선택)
        // 색상 설정 제거 - 폰트만 적용
        lblStep2.setFont(lblStep2.getFont().deriveFont(Font.BOLD));
    }
    
    /**
     * 영화에 대한 예매 가능한 가장 가까운 날짜를 찾아서 자동 선택합니다.
     * 오늘 날짜부터 최대 30일까지 확인합니다.
     */
    private void selectFirstAvailableDate() {
        if (movieId == null) {
            return;
        }
        
        // 백그라운드에서 예매 가능한 날짜 찾기
        SwingWorker<LocalDate, Void> worker = new SwingWorker<LocalDate, Void>() {
            @Override
            protected LocalDate doInBackground() throws Exception {
                try {
                    // 영화의 모든 스케줄 조회
                    List<Schedule> schedules = scheduleDAO.getSchedulesByMovieId(movieId);
                    
                    if (schedules == null || schedules.isEmpty()) {
                        logger.info("예매 가능한 상영시간표가 없습니다 - MovieID: " + movieId);
                        return null;
                    }
                    
                    LocalDate today = LocalDate.now();
                    LocalDate firstAvailableDate = null;
                    
                    // 모든 스케줄에서 오늘 이후의 가장 가까운 날짜 찾기
                    for (Schedule schedule : schedules) {
                        if (schedule.getStartTime() != null) {
                            LocalDate scheduleDate = schedule.getStartTime().toLocalDateTime().toLocalDate();
                            
                            // 오늘 이후의 날짜만 고려
                            if (!scheduleDate.isBefore(today)) {
                                // 가장 가까운 날짜 선택
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
                        // 해당 날짜로 날짜 선택 버튼 범위 조정
                        adjustDateRange(availableDate);
                        // 날짜 선택
                        selectDate(availableDate);
                        logger.info("자동 날짜 선택 완료: " + availableDate);
                    } else {
                        // 예매 가능한 날짜가 없으면 오늘 날짜 그대로 사용
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
    
    /**
     * 선택된 날짜가 표시되도록 날짜 범위를 조정합니다.
     */
    private void adjustDateRange(LocalDate targetDate) {
        // 선택된 날짜가 현재 표시 범위에 없으면 조정
        if (targetDate.isBefore(startDate) || targetDate.isAfter(startDate.plusDays(6))) {
            // 선택된 날짜를 포함하도록 startDate 조정
            // 선택된 날짜가 첫 번째 버튼 위치가 되도록
            startDate = targetDate;
            updateDateButtons();
        }
    }
    
    /**
     * 메인으로 돌아가기 버튼을 추가합니다.
     */
    private void addBackToMainButton() {
        javax.swing.JButton btnBackToMain = new javax.swing.JButton("메인으로");
        btnBackToMain.setFont(DesignConstants.getDefaultFont());
        btnBackToMain.setBorderPainted(false);
        btnBackToMain.setFocusPainted(false);
        btnBackToMain.addActionListener(e -> goBackToMain());
        
        // footer 패널에 추가 (이전/다음 버튼 앞에 추가)
        if (pnlFooter != null) {
            pnlFooter.add(btnBackToMain, 0); // 첫 번째 위치에 추가
        }
    }
    
    /**
     * 메인 화면으로 돌아갑니다.
     */
    private void goBackToMain() {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setLocationRelativeTo(this);
        mainFrame.setVisible(true);
        this.dispose(); // ShowtimeForm 닫기
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        java.awt.EventQueue.invokeLater(() -> new ShowtimeForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables
}
