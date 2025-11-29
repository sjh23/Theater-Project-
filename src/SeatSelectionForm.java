/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author User
 */
import javax.swing.*;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dao.SeatDAO;
import dao.ReservationDAO;
import dao.BookingDAO;
import dao.ScheduleDAO;
import dao.MovieDAO;
import model.Screen;
import model.User;
import model.Schedule;
import model.Movie;
import util.DesignConstants;
import java.sql.SQLException;
import java.math.BigDecimal;

public class SeatSelectionForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SeatSelectionForm.class.getName());
    
    // 좌석 상태 관리
    private enum SeatStatus {
        AVAILABLE,      // 사용 가능
        SELECTED,       // 선택됨
        RESERVED        // 예약됨
    }
    
    private Map<JButton, SeatStatus> seatStatusMap = new HashMap<>();
    private Map<JButton, String> seatNameMap = new HashMap<>();
    private Map<JButton, String[]> seatPositionMap = new HashMap<>(); // 버튼 -> [행, 열] 매핑
    private List<JButton> selectedSeatButtons = new ArrayList<>(); // 여러 좌석 선택 가능
    private List<JButton> allSeatButtons = new ArrayList<>();
    
    private Integer showtimeId = null;
    private int selectedPeopleCount = 0; // 선택된 인원수
    private User currentUser = null; // 로그인된 사용자 정보
    private SeatDAO seatDAO = new SeatDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private ScheduleDAO scheduleDAO = new ScheduleDAO();
    private MovieDAO movieDAO = new MovieDAO();
    
    // 좌석 배치 (동적으로 설정됨)
    private int rows = 10;
    private int cols = 15;

    /**
     * Creates new form SeatSelectionForm
     */
    public SeatSelectionForm() {
        initComponents();
        
        // 다크 테마 적용 (참고 디자인 기반)
        applyDarkTheme();
        
        // 인원수 버튼 이벤트 초기화
        initializePeopleButtons();
        
        // ShowtimeID가 설정되면 동적으로 좌석 생성
        // initializeSeats()는 setShowtimeId() 후에 호출됨
    }
    
    /**
     * 인원수 선택 버튼 이벤트를 초기화합니다.
     */
    private void initializePeopleButtons() {
        JButton[] peopleButtons = {btnPeople1, btnPeople2, btnPeople3, btnPeople4, 
                                   btnPeople5, btnPeople6, btnPeople7, btnPeople8};
        
        for (int i = 0; i < peopleButtons.length; i++) {
            final int peopleCount = i + 1;
            JButton btn = peopleButtons[i];
            if (btn != null) {
                btn.addActionListener(e -> selectPeopleCount(peopleCount, peopleButtons));
            }
        }
    }
    
    /**
     * 인원수를 선택합니다.
     */
    private void selectPeopleCount(int count, JButton[] allButtons) {
        selectedPeopleCount = count;
        
        // 모든 버튼 초기화
        for (JButton btn : allButtons) {
            if (btn != null) {
                // 버튼 스타일 초기화 (Java 코드에서 색상 설정 제거)
                btn.setFont(btn.getFont().deriveFont(Font.PLAIN));
            }
        }
        
        // 선택된 버튼 강조
        if (allButtons[count - 1] != null) {
            allButtons[count - 1].setFont(allButtons[count - 1].getFont().deriveFont(Font.BOLD));
        }
        
        // 선택된 좌석 개수와 인원수 일치 확인
        if (selectedSeatButtons.size() > count) {
            // 선택된 좌석이 인원수보다 많으면 초과분 자동 해제
            int excess = selectedSeatButtons.size() - count;
            List<JButton> buttonsToRemove = new ArrayList<>();
            
            // 초과분 좌석 선택 해제 (나중에 선택된 것부터 해제)
            for (int i = selectedSeatButtons.size() - 1; i >= count; i--) {
                JButton btn = selectedSeatButtons.get(i);
                buttonsToRemove.add(btn);
            }
            
            // 선택 해제
            for (JButton btn : buttonsToRemove) {
                seatStatusMap.put(btn, SeatStatus.AVAILABLE);
                btn.setBackground(null);
                btn.setOpaque(false);
                selectedSeatButtons.remove(btn);
            }
            
            // 선택된 좌석 정보 업데이트
            updateSelectedSeatsDisplay();
            
            // 안내 메시지
            JOptionPane.showMessageDialog(this, 
                "인원수(" + count + "명)에 맞춰 초과된 좌석 " + excess + "석이 자동으로 해제되었습니다.", 
                "좌석 자동 해제", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * 참고 디자인 기반 디자인 요소를 적용합니다.
     * (색상 제외 - 폰트, 레이아웃, 버튼 스타일만 적용)
     */
    private void applyDarkTheme() {
        // Screen 레이블 폰트 및 정렬만 적용 (색상 제외)
        if (lblScreen != null) {
            lblScreen.setHorizontalAlignment(SwingConstants.CENTER);
            lblScreen.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
        }
        
        // 버튼 폰트 및 스타일만 적용 (색상 제외)
        if (btnSeatSelection != null) {
            btnSeatSelection.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
            btnSeatSelection.setBorderPainted(false);
            btnSeatSelection.setFocusPainted(false);
        }
        
        if (btnBack != null) {
            btnBack.setFont(DesignConstants.getDefaultFont());
            btnBack.setBorderPainted(false);
            btnBack.setFocusPainted(false);
        }
        
        // 좌석 버튼 스타일은 initializeSeats()에서 동적으로 처리됨
    }
    
    /**
     * ShowtimeID를 설정하고 좌석을 초기화합니다.
     */
    public void setShowtimeId(Integer showtimeId) {
        this.showtimeId = showtimeId;
        initializeSeats();
    }
    
    /**
     * 로그인된 사용자 정보를 설정합니다.
     */
    public void setUser(User user) {
        this.currentUser = user;
    }
    
    private void initializeSeats() {
        if (showtimeId == null) {
            JOptionPane.showMessageDialog(this, "ShowtimeID가 설정되지 않았습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // ShowtimeID를 이용해 상영관 구조 조회
            Screen screen = seatDAO.getScreenStructureByShowtimeId(showtimeId);
            if (screen == null) {
                JOptionPane.showMessageDialog(this, "상영관 정보를 불러올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            rows = (screen.getRows() != null) ? screen.getRows() : 10;
            cols = (screen.getCols() != null) ? screen.getCols() : 15;
            
            // 유효성 검사
            if (rows <= 0 || cols <= 0) {
                JOptionPane.showMessageDialog(this, "상영관 좌석 정보가 올바르지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 예매 현황 조회
            List<String[]> reservedSeats = reservationDAO.getReservedSeatsByShowtimeId(showtimeId);
            Map<String, Boolean> reservedMap = new HashMap<>();
            for (String[] seat : reservedSeats) {
                if (seat != null && seat.length >= 2 && seat[0] != null && seat[1] != null) {
                    try {
                        // DB 형식: "A", "1" 또는 "A", "01" -> 표시 형식: "A01"로 변환하여 저장
                        String row = seat[0];
                        String col = seat[1];
                        // 열 번호를 정수로 파싱 후 2자리 형식으로 변환 (1 -> "01", 10 -> "10")
                        int colNum = Integer.parseInt(col);
                        String formattedCol = String.format("%02d", colNum);
                        reservedMap.put(row + formattedCol, true); // "A01", "B02" 형식으로 저장
                    } catch (NumberFormatException e) {
                        // 열 번호 파싱 실패 시 원본 형식 그대로 사용
                        logger.warning("좌석 열 번호 파싱 실패: " + seat[1] + ", 원본 형식 사용");
                        reservedMap.put(seat[0] + seat[1], true);
                    }
                }
            }
            
            // 기존 패널의 모든 컴포넌트 제거 (jPanel1에 좌석 그리드가 배치됨)
            if (jPanel1 != null) {
            jPanel1.removeAll();
            }
            allSeatButtons.clear();
            seatStatusMap.clear();
            seatNameMap.clear();
            seatPositionMap.clear();
            selectedSeatButtons.clear();
            
            // jPanel1에 GridLayout으로 좌석 배치 (모든 좌석 번호 유지: 01부터 nn까지, 통로 없음)
            if (jPanel1 != null) {
            // GridLayout으로 좌석 배치 (통로 없이 모든 좌석 연속 배치)
            jPanel1.setLayout(new GridLayout(rows, cols, 3, 3));
            
            // 좌석 크기 설정
            int seatWidth = 45;
            int seatHeight = 35;
            
            // 좌석 버튼 동적 생성 (모든 좌석 번호 생성: 01부터 nn까지)
            for (int row = 0; row < rows; row++) {
                char rowChar = (char) ('A' + row);
                
                // 각 열의 좌석 버튼 생성 (1부터 cols까지)
                for (int col = 1; col <= cols; col++) {
                    String seatRow = String.valueOf(rowChar);
                    String seatCol = String.format("%02d", col); // 01, 02, 03... nn 형식
                    String seatName = seatRow + seatCol;
                    
                    JButton seatButton = new JButton();
                    seatButton.setPreferredSize(new Dimension(seatWidth, seatHeight));
                    seatButton.setMinimumSize(new Dimension(seatWidth, seatHeight));
                    seatButton.setMaximumSize(new Dimension(seatWidth, seatHeight));
                    
                    // 일반 좌석 (통로 없이 모든 좌석 생성)
                    seatButton.setText(seatName);
                    
                    // 예약된 좌석인지 확인
                    if (reservedMap.containsKey(seatName)) {
                        seatStatusMap.put(seatButton, SeatStatus.RESERVED);
                        seatButton.setEnabled(false);
                        seatButton.setText(""); // 예약된 좌석은 텍스트 숨김
                        seatButton.setBorderPainted(false);
                    } else {
                        seatStatusMap.put(seatButton, SeatStatus.AVAILABLE);
                        seatButton.setBorderPainted(true);
                    }
                    
                    // 좌석 버튼 폰트 설정
                    seatButton.setFont(new Font(DesignConstants.getAvailableFont(), Font.PLAIN, 9));
                    
                    // 매핑 저장
                    seatNameMap.put(seatButton, seatName);
                    seatPositionMap.put(seatButton, new String[]{seatRow, seatCol});
                    allSeatButtons.add(seatButton);
                    
                    // 클릭 이벤트 추가
                    seatButton.addActionListener(e -> handleSeatClick(seatButton));
                    
                    // jPanel1에 좌석 버튼 추가
                    jPanel1.add(seatButton);
                }
            }
            
            // 패널 갱신
            jPanel1.revalidate();
            jPanel1.repaint();
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "좌석 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage(), 
                "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        
        // 이전 버튼 이벤트
        if (btnBack != null) {
            btnBack.addActionListener(e -> {
                ShowtimeForm showtimeForm = new ShowtimeForm();
                showtimeForm.setVisible(true);
                dispose();
            });
        }
        
        // 예매하기 버튼 이벤트
        if (btnSeatSelection != null) {
            btnSeatSelection.addActionListener(e -> {
                // 선택된 좌석과 인원수 확인
                if (selectedSeatButtons.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "좌석을 선택해주세요.", 
                        "선택 오류", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (selectedPeopleCount <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "인원수를 선택해주세요.", 
                        "선택 오류", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (selectedSeatButtons.size() != selectedPeopleCount) {
                    JOptionPane.showMessageDialog(this, 
                        "선택한 좌석 수(" + selectedSeatButtons.size() + "석)와 인원수(" + selectedPeopleCount + "명)가 일치하지 않습니다.\n" +
                        "좌석 또는 인원수를 다시 선택해주세요.", 
                        "인원수 불일치", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 예매 처리 로직
                processBooking();
            });
        }
    }
    
    private void handleSeatClick(JButton seatButton) {
        SeatStatus status = seatStatusMap.get(seatButton);
        
        if (status == SeatStatus.RESERVED) {
            JOptionPane.showMessageDialog(this, 
                "이미 예약된 좌석입니다.", 
                "예약 불가", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (status == SeatStatus.SELECTED) {
            // 선택 해제
            seatStatusMap.put(seatButton, SeatStatus.AVAILABLE);
            seatButton.setBackground(null); // 기본 색상으로 복원
            seatButton.setOpaque(false);
            selectedSeatButtons.remove(seatButton);
        } else {
            // 새 좌석 선택
            // 인원수 제한 체크
            if (selectedPeopleCount > 0) {
                if (selectedSeatButtons.size() >= selectedPeopleCount) {
                    JOptionPane.showMessageDialog(this, 
                        "인원수(" + selectedPeopleCount + "명)만큼만 좌석을 선택할 수 있습니다.\n" +
                        "기존 좌석을 해제한 후 다시 선택해주세요.", 
                        "좌석 선택 제한", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            // 좌석 선택
            seatStatusMap.put(seatButton, SeatStatus.SELECTED);
            seatButton.setBackground(new Color(255, 0, 0)); // 빨간색으로 표시
            seatButton.setOpaque(true);
            seatButton.setBorderPainted(true);
            selectedSeatButtons.add(seatButton);
        }
        
        // 선택된 좌석 정보 업데이트
        updateSelectedSeatsDisplay();
    }
    
    /**
     * 선택된 좌석 정보를 표시합니다.
     */
    private void updateSelectedSeatsDisplay() {
        // TODO: lblSelectedSeats와 lblTotalPrice가 있다면 업데이트
        // 현재는 .form에 해당 라벨이 없으므로 주석 처리
        /*
        if (lblSelectedSeats != null) {
            StringBuilder seats = new StringBuilder();
            for (JButton btn : selectedSeatButtons) {
                if (seats.length() > 0) seats.append(", ");
                seats.append(seatNameMap.get(btn));
            }
            lblSelectedSeats.setText("선택된 좌석: " + seats.toString());
        }
        
        if (lblTotalPrice != null) {
            // TODO: 좌석당 가격을 가져와서 계산
            int pricePerSeat = 12000; // 임시 가격
            int totalPrice = selectedSeatButtons.size() * pricePerSeat;
            lblTotalPrice.setText("총 가격: " + totalPrice + "원");
        }
        */
    }

    /**
     * 예매 처리 로직
     */
    private void processBooking() {
        // 1. 로그인 확인
        if (currentUser == null || currentUser.getUserId() == null) {
            int result = JOptionPane.showConfirmDialog(this,
                "예매를 하려면 로그인이 필요합니다.\n로그인 화면으로 이동하시겠습니까?",
                "로그인 필요",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                // 로그인 화면 열기
                try {
                    // MainFrame 찾기
                    java.awt.Window[] windows = java.awt.Window.getWindows();
                    MainFrame mainFrame = null;
                    for (java.awt.Window window : windows) {
                        if (window instanceof MainFrame) {
                            mainFrame = (MainFrame) window;
                            break;
                        }
                    }
                    
                    if (mainFrame != null) {
                        dispose(); // 현재 화면 닫기
                        LoginFrame loginFrame = new LoginFrame(mainFrame);
                        loginFrame.setLocationRelativeTo(null);
                        loginFrame.setVisible(true);
                    } else {
                        // MainFrame이 없으면 새로 생성
                        dispose();
                        MainFrame newMainFrame = new MainFrame();
                        newMainFrame.setLocationRelativeTo(null);
                        newMainFrame.setVisible(true);
                        
                        LoginFrame loginFrame = new LoginFrame(newMainFrame);
                        loginFrame.setLocationRelativeTo(null);
                        loginFrame.setVisible(true);
                    }
                } catch (Exception e) {
                    logger.warning("로그인 화면 열기 실패: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "로그인 화면을 열 수 없습니다: " + e.getMessage(),
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        }
        
        // 2. Schedule 정보 조회
        if (showtimeId == null) {
            JOptionPane.showMessageDialog(this,
                "상영 시간표 정보가 올바르지 않습니다.",
                "오류",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Schedule schedule = scheduleDAO.getScheduleById(showtimeId);
            if (schedule == null) {
                JOptionPane.showMessageDialog(this,
                    "상영 시간표 정보를 찾을 수 없습니다.",
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 3. 영화 정보 조회
            Movie movie = null;
            if (schedule.getMovieId() != null) {
                movie = movieDAO.getMovieById(schedule.getMovieId());
            }
            
            // 4. 선택된 좌석 정보 수집
            String[] seatRows = new String[selectedSeatButtons.size()];
            String[] seatCols = new String[selectedSeatButtons.size()];
            StringBuilder seatList = new StringBuilder();
            
            for (int i = 0; i < selectedSeatButtons.size(); i++) {
                JButton btn = selectedSeatButtons.get(i);
                String[] position = seatPositionMap.get(btn);
                if (position != null && position.length >= 2) {
                    seatRows[i] = position[0]; // 행 (예: "A")
                    // 열 번호를 DB 형식으로 변환 ("01" -> "1")
                    String colStr = position[1];
                    try {
                        int colNum = Integer.parseInt(colStr);
                        seatCols[i] = String.valueOf(colNum); // "1", "2" 형식
                    } catch (NumberFormatException e) {
                        seatCols[i] = colStr; // 파싱 실패 시 원본 사용
                    }
                    
                    if (seatList.length() > 0) seatList.append(", ");
                    seatList.append(seatNameMap.get(btn));
                }
            }
            
            // 5. 가격 계산
            BigDecimal pricePerSeat = schedule.getPrice() != null ? schedule.getPrice() : BigDecimal.ZERO;
            BigDecimal totalPrice = pricePerSeat.multiply(BigDecimal.valueOf(selectedSeatButtons.size()));
            
            // 6. 예매 정보 확인 다이얼로그
            String movieTitle = (movie != null && movie.getTitle() != null) ? movie.getTitle() : "영화 정보 없음";
            String scheduleTime = schedule.getStartTime() != null 
                ? schedule.getStartTime().toString().substring(0, 16).replace("T", " ")
                : "시간 정보 없음";
            
            String confirmMessage = "예매 정보를 확인해주세요.\n\n" +
                                   "영화: " + movieTitle + "\n" +
                                   "상영 시간: " + scheduleTime + "\n" +
                                   "좌석: " + seatList.toString() + "\n" +
                                   "인원수: " + selectedPeopleCount + "명\n" +
                                   "좌석당 가격: " + String.format("%,d원", pricePerSeat.intValue()) + "\n" +
                                   "총 가격: " + String.format("%,d원", totalPrice.intValue()) + "\n\n" +
                                   "예매를 확정하시겠습니까?";
            
            int confirm = JOptionPane.showConfirmDialog(this,
                confirmMessage,
                "예매 확인",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // 7. 예매 저장
                boolean success = bookingDAO.reserveSeats(
                    currentUser.getUserId(),
                    showtimeId,
                    seatRows,
                    seatCols,
                    pricePerSeat
                );
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "예매가 완료되었습니다.\n\n" +
                        "영화: " + movieTitle + "\n" +
                        "좌석: " + seatList.toString() + "\n" +
                        "총 가격: " + String.format("%,d원", totalPrice.intValue()),
                        "예매 완료",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // 화면 닫고 MainFrame으로 이동
                    dispose();
                    
                    // MainFrame 찾기 또는 생성
                    java.awt.Window[] windows = java.awt.Window.getWindows();
                    MainFrame mainFrame = null;
                    for (java.awt.Window window : windows) {
                        if (window instanceof MainFrame) {
                            mainFrame = (MainFrame) window;
                            break;
                        }
                    }
                    
                    if (mainFrame != null) {
                        mainFrame.setVisible(true);
                    } else {
                        MainFrame newMainFrame = new MainFrame();
                        newMainFrame.setLoggedInUser(currentUser);
                        newMainFrame.setLocationRelativeTo(null);
                        newMainFrame.setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "예매 저장 중 오류가 발생했습니다.\n다시 시도해주세요.",
                        "예매 실패",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (SQLException e) {
            logger.severe("예매 처리 중 DB 오류: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "예매 처리 중 오류가 발생했습니다:\n" + e.getMessage() + "\n\n자세한 내용은 콘솔을 확인하세요.",
                "오류",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.severe("예매 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "예매 처리 중 예상치 못한 오류가 발생했습니다:\n" + e.getMessage(),
                "오류",
                JOptionPane.ERROR_MESSAGE);
        }
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
        lblTitle = new javax.swing.JLabel();
        pnlSteps = new javax.swing.JPanel();
        lblStep1 = new javax.swing.JLabel();
        lblStep2 = new javax.swing.JLabel();
        lblStep3 = new javax.swing.JLabel();
        pnlMainContent = new javax.swing.JPanel();
        lblScreen = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        pnlPeopleSelection = new javax.swing.JPanel();
        lblPeopleSelection = new javax.swing.JLabel();
        pnlPeopleButtons = new javax.swing.JPanel();
        btnPeople1 = new javax.swing.JButton();
        btnPeople2 = new javax.swing.JButton();
        btnPeople3 = new javax.swing.JButton();
        btnPeople4 = new javax.swing.JButton();
        btnPeople5 = new javax.swing.JButton();
        btnPeople6 = new javax.swing.JButton();
        btnPeople7 = new javax.swing.JButton();
        btnPeople8 = new javax.swing.JButton();
        pnlFooter = new javax.swing.JPanel();
        btnBack = new javax.swing.JButton();
        btnSeatSelection = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("영화예매시스템");
        setPreferredSize(new java.awt.Dimension(1200, 800));
        setResizable(false);

        pnlHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlHeader.setLayout(new javax.swing.BoxLayout(pnlHeader, javax.swing.BoxLayout.Y_AXIS));

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 1, 18)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("영화예매시스템");
        pnlHeader.add(lblTitle);

        pnlSteps.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 5));

        lblStep1.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblStep1.setText("극장");
        pnlSteps.add(lblStep1);

        lblStep2.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblStep2.setText("날짜/상영관");
        pnlSteps.add(lblStep2);

        lblStep3.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        lblStep3.setText("인원수/좌석");
        pnlSteps.add(lblStep3);

        pnlHeader.add(pnlSteps);

        getContentPane().add(pnlHeader, java.awt.BorderLayout.CENTER);

        pnlMainContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlMainContent.setLayout(new java.awt.BorderLayout());

        lblScreen.setFont(new java.awt.Font("맑은 고딕", 1, 16)); // NOI18N
        lblScreen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblScreen.setText("Screen");
        lblScreen.setPreferredSize(new java.awt.Dimension(800, 40));
        pnlMainContent.add(lblScreen, java.awt.BorderLayout.PAGE_START);

        jPanel1.setLayout(new java.awt.GridLayout(1, 1, 5, 5));
        pnlMainContent.add(jPanel1, java.awt.BorderLayout.CENTER);

        pnlPeopleSelection.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlPeopleSelection.setLayout(new javax.swing.BoxLayout(pnlPeopleSelection, javax.swing.BoxLayout.Y_AXIS));

        lblPeopleSelection.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblPeopleSelection.setText("인원선택 최대 8명까지 선택가능");
        lblPeopleSelection.setAlignmentX(0.5F);
        pnlPeopleSelection.add(lblPeopleSelection);

        pnlPeopleButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        btnPeople1.setFont(new java.awt.Font("맑은 고딕", 1, 16)); // NOI18N
        btnPeople1.setText("1");
        btnPeople1.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople1);

        btnPeople2.setFont(new java.awt.Font("맑은 고딕", 1, 16)); // NOI18N
        btnPeople2.setText("2");
        btnPeople2.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople2);

        btnPeople3.setFont(new java.awt.Font("맑은 고딕", 1, 16)); // NOI18N
        btnPeople3.setText("3");
        btnPeople3.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople3);

        btnPeople4.setFont(new java.awt.Font("맑은 고딕", 1, 16)); // NOI18N
        btnPeople4.setText("4");
        btnPeople4.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople4);

        btnPeople5.setFont(new java.awt.Font("맑은 고딕", 1, 16)); // NOI18N
        btnPeople5.setText("5");
        btnPeople5.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople5);

        btnPeople6.setFont(new java.awt.Font("맑은 고딕", 1, 16)); // NOI18N
        btnPeople6.setText("6");
        btnPeople6.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople6);

        btnPeople7.setFont(new java.awt.Font("맑은 고딕", 1, 16)); // NOI18N
        btnPeople7.setText("7");
        btnPeople7.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople7);

        btnPeople8.setFont(new java.awt.Font("맑은 고딕", 1, 16)); // NOI18N
        btnPeople8.setText("8");
        btnPeople8.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople8);

        pnlPeopleSelection.add(pnlPeopleButtons);

        pnlMainContent.add(pnlPeopleSelection, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(pnlMainContent, java.awt.BorderLayout.CENTER);

        pnlFooter.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));

        btnBack.setFont(new java.awt.Font("맑은 고딕", 0, 16)); // NOI18N
        btnBack.setText("이전");
        btnBack.setPreferredSize(new java.awt.Dimension(150, 50));
        pnlFooter.add(btnBack);

        btnSeatSelection.setFont(new java.awt.Font("맑은 고딕", 1, 16)); // NOI18N
        btnSeatSelection.setText("예매하기");
        btnSeatSelection.setPreferredSize(new java.awt.Dimension(150, 50));
        pnlFooter.add(btnSeatSelection);

        getContentPane().add(pnlFooter, java.awt.BorderLayout.SOUTH);

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
        java.awt.EventQueue.invokeLater(() -> new SeatSelectionForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnPeople1;
    private javax.swing.JButton btnPeople2;
    private javax.swing.JButton btnPeople3;
    private javax.swing.JButton btnPeople4;
    private javax.swing.JButton btnPeople5;
    private javax.swing.JButton btnPeople6;
    private javax.swing.JButton btnPeople7;
    private javax.swing.JButton btnPeople8;
    private javax.swing.JButton btnSeatSelection;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblPeopleSelection;
    private javax.swing.JLabel lblScreen;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlMainContent;
    private javax.swing.JPanel pnlPeopleButtons;
    private javax.swing.JPanel pnlPeopleSelection;
    private javax.swing.JPanel pnlSteps;
    // End of variables declaration//GEN-END:variables
}
