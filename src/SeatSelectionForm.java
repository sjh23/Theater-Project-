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

    private enum SeatStatus {
        AVAILABLE,
        SELECTED,
        RESERVED
    }
    
    private Map<JButton, SeatStatus> seatStatusMap = new HashMap<>();
    private Map<JButton, String> seatNameMap = new HashMap<>();
    private Map<JButton, String[]> seatPositionMap = new HashMap<>();
    private List<JButton> selectedSeatButtons = new ArrayList<>();
    private List<JButton> allSeatButtons = new ArrayList<>();
    
    private Integer showtimeId = null;
    private int selectedPeopleCount = 0;
    private User currentUser = null;
    private SeatDAO seatDAO = new SeatDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private ScheduleDAO scheduleDAO = new ScheduleDAO();
    private MovieDAO movieDAO = new MovieDAO();

    private int rows = 10;
    private int cols = 15;

    public SeatSelectionForm() {
        initComponents();

        applyDarkTheme();

        initializePeopleButtons();

    }

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

    private void selectPeopleCount(int count, JButton[] allButtons) {
        selectedPeopleCount = count;

        for (JButton btn : allButtons) {
            if (btn != null) {

                btn.setFont(btn.getFont().deriveFont(Font.PLAIN));
            }
        }

        if (allButtons[count - 1] != null) {
            allButtons[count - 1].setFont(allButtons[count - 1].getFont().deriveFont(Font.BOLD));
        }

        if (selectedSeatButtons.size() > count) {

            int excess = selectedSeatButtons.size() - count;
            List<JButton> buttonsToRemove = new ArrayList<>();

            for (int i = selectedSeatButtons.size() - 1; i >= count; i--) {
                JButton btn = selectedSeatButtons.get(i);
                buttonsToRemove.add(btn);
            }

            for (JButton btn : buttonsToRemove) {
                seatStatusMap.put(btn, SeatStatus.AVAILABLE);
                btn.setBackground(null);
                btn.setOpaque(false);
                selectedSeatButtons.remove(btn);
            }

            updateSelectedSeatsDisplay();

            JOptionPane.showMessageDialog(this, 
                "인원수(" + count + "명)에 맞춰 초과된 좌석 " + excess + "석이 자동으로 해제되었습니다.", 
                "좌석 자동 해제", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void applyDarkTheme() {

        if (lblScreen != null) {
            lblScreen.setHorizontalAlignment(SwingConstants.CENTER);
            lblScreen.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
        }

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

    }

    public void setShowtimeId(Integer showtimeId) {
        this.showtimeId = showtimeId;
        initializeSeats();
    }

    public void setUser(User user) {
        this.currentUser = user;
    }
    
    private void initializeSeats() {
        if (showtimeId == null) {
            JOptionPane.showMessageDialog(this, "ShowtimeID가 설정되지 않았습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {

            Screen screen = seatDAO.getScreenStructureByShowtimeId(showtimeId);
            if (screen == null) {
                JOptionPane.showMessageDialog(this, "상영관 정보를 불러올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            rows = (screen.getRows() != null) ? screen.getRows() : 10;
            cols = (screen.getCols() != null) ? screen.getCols() : 15;

            if (rows <= 0 || cols <= 0) {
                JOptionPane.showMessageDialog(this, "상영관 좌석 정보가 올바르지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<String[]> reservedSeats = reservationDAO.getReservedSeatsByShowtimeId(showtimeId);
            Map<String, Boolean> reservedMap = new HashMap<>();
            for (String[] seat : reservedSeats) {
                if (seat != null && seat.length >= 2 && seat[0] != null && seat[1] != null) {
                    try {

                        String row = seat[0];
                        String col = seat[1];

                        int colNum = Integer.parseInt(col);
                        String formattedCol = String.format("%02d", colNum);
                        reservedMap.put(row + formattedCol, true);
                    } catch (NumberFormatException e) {

                        logger.warning("좌석 열 번호 파싱 실패: " + seat[1] + ", 원본 형식 사용");
                        reservedMap.put(seat[0] + seat[1], true);
                    }
                }
            }

            if (jPanel1 != null) {
            jPanel1.removeAll();
            }
            allSeatButtons.clear();
            seatStatusMap.clear();
            seatNameMap.clear();
            seatPositionMap.clear();
            selectedSeatButtons.clear();

            if (jPanel1 != null) {

            jPanel1.setLayout(new GridLayout(rows, cols, 3, 3));

            int seatWidth = 45;
            int seatHeight = 35;

            for (int row = 0; row < rows; row++) {
                char rowChar = (char) ('A' + row);

                for (int col = 1; col <= cols; col++) {
                    String seatRow = String.valueOf(rowChar);
                    String seatCol = String.format("%02d", col);
                    String seatName = seatRow + seatCol;
                    
                    JButton seatButton = new JButton();
                    seatButton.setPreferredSize(new Dimension(seatWidth, seatHeight));
                    seatButton.setMinimumSize(new Dimension(seatWidth, seatHeight));
                    seatButton.setMaximumSize(new Dimension(seatWidth, seatHeight));

                    seatButton.setText(seatName);

                    if (reservedMap.containsKey(seatName)) {
                        seatStatusMap.put(seatButton, SeatStatus.RESERVED);
                        seatButton.setEnabled(false);
                        seatButton.setText("");
                        seatButton.setBorderPainted(false);
                    } else {
                        seatStatusMap.put(seatButton, SeatStatus.AVAILABLE);
                        seatButton.setBorderPainted(true);
                    }

                    seatButton.setFont(new Font(DesignConstants.getAvailableFont(), Font.PLAIN, 9));

                    seatNameMap.put(seatButton, seatName);
                    seatPositionMap.put(seatButton, new String[]{seatRow, seatCol});
                    allSeatButtons.add(seatButton);

                    seatButton.addActionListener(e -> handleSeatClick(seatButton));

                    jPanel1.add(seatButton);
                }
            }

            jPanel1.revalidate();
            jPanel1.repaint();
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "좌석 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage(), 
                "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        if (btnBack != null) {
            btnBack.addActionListener(e -> {
                ShowtimeForm showtimeForm = new ShowtimeForm();
                showtimeForm.setVisible(true);
                dispose();
            });
        }

        if (btnSeatSelection != null) {
            btnSeatSelection.addActionListener(e -> {

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

            seatStatusMap.put(seatButton, SeatStatus.AVAILABLE);
            seatButton.setBackground(null);
            seatButton.setOpaque(false);
            selectedSeatButtons.remove(seatButton);
        } else {

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

            seatStatusMap.put(seatButton, SeatStatus.SELECTED);
            seatButton.setBackground(new Color(255, 0, 0));
            seatButton.setOpaque(true);
            seatButton.setBorderPainted(true);
            selectedSeatButtons.add(seatButton);
        }

        updateSelectedSeatsDisplay();
    }

    private void updateSelectedSeatsDisplay() {

    }

    private void processBooking() {

        if (currentUser == null || currentUser.getUserId() == null) {
            int result = JOptionPane.showConfirmDialog(this,
                "예매를 하려면 로그인이 필요합니다.\n로그인 화면으로 이동하시겠습니까?",
                "로그인 필요",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {

                try {

                    java.awt.Window[] windows = java.awt.Window.getWindows();
                    MainFrame mainFrame = null;
                    for (java.awt.Window window : windows) {
                        if (window instanceof MainFrame) {
                            mainFrame = (MainFrame) window;
                            break;
                        }
                    }
                    
                    if (mainFrame != null) {
                        dispose();
                        LoginFrame loginFrame = new LoginFrame(mainFrame);
                        loginFrame.setLocationRelativeTo(null);
                        loginFrame.setVisible(true);
                    } else {

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

            Movie movie = null;
            if (schedule.getMovieId() != null) {
                movie = movieDAO.getMovieById(schedule.getMovieId());
            }

            String[] seatRows = new String[selectedSeatButtons.size()];
            String[] seatCols = new String[selectedSeatButtons.size()];
            StringBuilder seatList = new StringBuilder();
            
            for (int i = 0; i < selectedSeatButtons.size(); i++) {
                JButton btn = selectedSeatButtons.get(i);
                String[] position = seatPositionMap.get(btn);
                if (position != null && position.length >= 2) {
                    seatRows[i] = position[0];

                    String colStr = position[1];
                    try {
                        int colNum = Integer.parseInt(colStr);
                        seatCols[i] = String.valueOf(colNum);
                    } catch (NumberFormatException e) {
                        seatCols[i] = colStr;
                    }
                    
                    if (seatList.length() > 0) seatList.append(", ");
                    seatList.append(seatNameMap.get(btn));
                }
            }

            BigDecimal pricePerSeat = schedule.getPrice() != null ? schedule.getPrice() : BigDecimal.ZERO;
            BigDecimal totalPrice = pricePerSeat.multiply(BigDecimal.valueOf(selectedSeatButtons.size()));

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

                    dispose();

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

    @SuppressWarnings("unchecked")

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

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 1, 18));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("영화예매시스템");
        pnlHeader.add(lblTitle);

        pnlSteps.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 5));

        lblStep1.setFont(new java.awt.Font("맑은 고딕", 0, 14));
        lblStep1.setText("극장");
        pnlSteps.add(lblStep1);

        lblStep2.setFont(new java.awt.Font("맑은 고딕", 0, 14));
        lblStep2.setText("날짜/상영관");
        pnlSteps.add(lblStep2);

        lblStep3.setFont(new java.awt.Font("맑은 고딕", 1, 14));
        lblStep3.setText("인원수/좌석");
        pnlSteps.add(lblStep3);

        pnlHeader.add(pnlSteps);

        getContentPane().add(pnlHeader, java.awt.BorderLayout.CENTER);

        pnlMainContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlMainContent.setLayout(new java.awt.BorderLayout());

        lblScreen.setFont(new java.awt.Font("맑은 고딕", 1, 16));
        lblScreen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblScreen.setText("Screen");
        lblScreen.setPreferredSize(new java.awt.Dimension(800, 40));
        pnlMainContent.add(lblScreen, java.awt.BorderLayout.PAGE_START);

        jPanel1.setLayout(new java.awt.GridLayout(1, 1, 5, 5));
        pnlMainContent.add(jPanel1, java.awt.BorderLayout.CENTER);

        pnlPeopleSelection.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlPeopleSelection.setLayout(new javax.swing.BoxLayout(pnlPeopleSelection, javax.swing.BoxLayout.Y_AXIS));

        lblPeopleSelection.setFont(new java.awt.Font("맑은 고딕", 0, 14));
        lblPeopleSelection.setText("인원선택 최대 8명까지 선택가능");
        lblPeopleSelection.setAlignmentX(0.5F);
        pnlPeopleSelection.add(lblPeopleSelection);

        pnlPeopleButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        btnPeople1.setFont(new java.awt.Font("맑은 고딕", 1, 16));
        btnPeople1.setText("1");
        btnPeople1.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople1);

        btnPeople2.setFont(new java.awt.Font("맑은 고딕", 1, 16));
        btnPeople2.setText("2");
        btnPeople2.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople2);

        btnPeople3.setFont(new java.awt.Font("맑은 고딕", 1, 16));
        btnPeople3.setText("3");
        btnPeople3.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople3);

        btnPeople4.setFont(new java.awt.Font("맑은 고딕", 1, 16));
        btnPeople4.setText("4");
        btnPeople4.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople4);

        btnPeople5.setFont(new java.awt.Font("맑은 고딕", 1, 16));
        btnPeople5.setText("5");
        btnPeople5.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople5);

        btnPeople6.setFont(new java.awt.Font("맑은 고딕", 1, 16));
        btnPeople6.setText("6");
        btnPeople6.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople6);

        btnPeople7.setFont(new java.awt.Font("맑은 고딕", 1, 16));
        btnPeople7.setText("7");
        btnPeople7.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople7);

        btnPeople8.setFont(new java.awt.Font("맑은 고딕", 1, 16));
        btnPeople8.setText("8");
        btnPeople8.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlPeopleButtons.add(btnPeople8);

        pnlPeopleSelection.add(pnlPeopleButtons);

        pnlMainContent.add(pnlPeopleSelection, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(pnlMainContent, java.awt.BorderLayout.CENTER);

        pnlFooter.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));

        btnBack.setFont(new java.awt.Font("맑은 고딕", 0, 16));
        btnBack.setText("이전");
        btnBack.setPreferredSize(new java.awt.Dimension(150, 50));
        pnlFooter.add(btnBack);

        btnSeatSelection.setFont(new java.awt.Font("맑은 고딕", 1, 16));
        btnSeatSelection.setText("예매하기");
        btnSeatSelection.setPreferredSize(new java.awt.Dimension(150, 50));
        pnlFooter.add(btnSeatSelection);

        getContentPane().add(pnlFooter, java.awt.BorderLayout.SOUTH);

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

        java.awt.EventQueue.invokeLater(() -> new SeatSelectionForm().setVisible(true));
    }

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

}
