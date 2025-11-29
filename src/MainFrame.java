/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author User
 */
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import dao.MovieDAO;
import model.Movie;
import model.User;
import util.DesignConstants;
import java.sql.SQLException;

public class MainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());
    
    // 영화 데이터
    private List<Movie> movies = new ArrayList<>();
    private MovieDAO movieDAO = new MovieDAO();
    private int currentCarouselIndex = 0;
    private javax.swing.Timer carouselTimer;
    private static final int CAROUSEL_INTERVAL = 5000; // 5초 (밀리초)
    
    // 로그인된 사용자 정보
    private User loggedInUser = null;
    private javax.swing.JLabel lblWelcome = null; // 환영 메시지 레이블 (동적 생성)
    private javax.swing.JButton btnBookingHistory = null; // 예매 내역 버튼 (동적 생성)

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        
        // 다크 테마 적용 (참고 디자인 기반)
        applyDarkTheme();
        
        initializeComponents();
        
        // 환영 메시지 레이블 동적 생성 (initializeComponents() 후에 호출해야 pnlHeaderButtons가 초기화됨)
        createWelcomeLabel();
    }
    
    /**
     * 로그인된 사용자 정보를 설정하고 UI를 업데이트합니다.
     */
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        updateUIForLogin();
    }
    
    /**
     * 환영 메시지 레이블과 예매 내역 버튼을 생성합니다.
     */
    private void createWelcomeLabel() {
        lblWelcome = new javax.swing.JLabel();
        lblWelcome.setFont(DesignConstants.getDefaultFont());
        lblWelcome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWelcome.setVisible(false);
        // 헤더에 환영 메시지 추가 (타이틀과 버튼 사이)
        if (pnlHeader != null) {
            pnlHeader.add(lblWelcome, java.awt.BorderLayout.CENTER);
        }
        
        // 예매 내역 버튼 생성
        btnBookingHistory = new javax.swing.JButton();
        btnBookingHistory.setText("예매 내역");
        btnBookingHistory.setFont(DesignConstants.getDefaultFont());
        btnBookingHistory.setBorderPainted(false);
        btnBookingHistory.setFocusPainted(false);
        btnBookingHistory.setVisible(false); // 기본적으로 숨김
        
        // 예매 내역 버튼 이벤트 (BookingHistoryForm이 생성되면 주석 해제)
        btnBookingHistory.addActionListener(e -> {
            if (loggedInUser == null) {
                JOptionPane.showMessageDialog(this,
                    "로그인이 필요합니다.",
                    "로그인 필요",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            try {
                BookingHistoryForm historyForm = new BookingHistoryForm();
                historyForm.setUser(loggedInUser);
                historyForm.setLocationRelativeTo(this);
                historyForm.setVisible(true);
            } catch (Exception ex) {
                logger.severe("예매 내역 화면 열기 실패: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "예매 내역 화면을 열 수 없습니다: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // 예매 내역 버튼을 헤더 버튼 패널에 추가 (btnJoin 앞에)
        if (pnlHeaderButtons != null && btnJoin != null) {
            // FlowLayout에서는 컴포넌트 순서를 유지하기 위해
            // 기존 버튼들을 제거하고 순서대로 다시 추가
            java.awt.Component[] components = pnlHeaderButtons.getComponents();
            pnlHeaderButtons.removeAll();
            
            // 예매 내역 버튼을 첫 번째로 추가
            pnlHeaderButtons.add(btnBookingHistory);
            
            // 기존 컴포넌트들을 다시 추가
            for (java.awt.Component comp : components) {
                pnlHeaderButtons.add(comp);
            }
            
            pnlHeaderButtons.revalidate();
            pnlHeaderButtons.repaint();
        }
    }
    
    /**
     * 로그인 상태에 따라 UI를 업데이트합니다.
     */
    private void updateUIForLogin() {
        if (loggedInUser != null) {
            // 로그인 상태
            btnLogin.setText("로그아웃");
            btnJoin.setVisible(false);
            
            // 예매 내역 버튼 표시
            if (btnBookingHistory != null) {
                btnBookingHistory.setVisible(true);
            }
            
            // 환영 메시지 표시
            if (lblWelcome != null) {
                String userName = (loggedInUser.getName() != null && !loggedInUser.getName().trim().isEmpty()) 
                                 ? loggedInUser.getName() 
                                 : loggedInUser.getUsername();
                lblWelcome.setText("환영합니다 " + userName + "님");
                lblWelcome.setVisible(true);
            }
        } else {
            // 로그아웃 상태
            btnLogin.setText("로그인");
            btnJoin.setVisible(true);
            
            // 예매 내역 버튼 숨김
            if (btnBookingHistory != null) {
                btnBookingHistory.setVisible(false);
            }
            
            // 환영 메시지 숨김
            if (lblWelcome != null) {
                lblWelcome.setVisible(false);
            }
        }
    }
    
    /**
     * 참고 디자인 기반 디자인 요소를 적용합니다.
     * (색상 제외 - 폰트, 레이아웃, 버튼 스타일만 적용)
     */
    private void applyDarkTheme() {
        // 레이블 폰트만 적용 (색상 제외)
        if (lblTitle != null) {
            lblTitle.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_LARGE));
        }
        
        if (lblMovieTitle != null) {
            lblMovieTitle.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_BIG_TITLE));
        }
        
        if (lblSearchLabel != null) {
            lblSearchLabel.setFont(DesignConstants.getDefaultFont());
        }
        
        // 입력 필드 폰트만 적용 (색상 제외)
        if (txtGlobalSearch != null) {
            txtGlobalSearch.setFont(DesignConstants.getDefaultFont());
        }
        
        // 버튼 폰트 및 스타일만 적용 (색상 제외)
        if (btnLogin != null) {
            btnLogin.setFont(DesignConstants.getDefaultFont());
            btnLogin.setBorderPainted(false);
            btnLogin.setFocusPainted(false);
        }
        
        if (btnJoin != null) {
            btnJoin.setFont(DesignConstants.getDefaultFont());
            btnJoin.setBorderPainted(false);
            btnJoin.setFocusPainted(false);
        }
        
        if (btnReservation != null) {
            btnReservation.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
            btnReservation.setBorderPainted(false);
            btnReservation.setFocusPainted(false);
        }
        
        if (btnReserveNow != null) {
            btnReserveNow.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
            btnReserveNow.setBorderPainted(false);
            btnReserveNow.setFocusPainted(false);
        }
        
        if (btnSearch != null) {
            btnSearch.setFont(DesignConstants.getDefaultFont());
            btnSearch.setBorderPainted(false);
            btnSearch.setFocusPainted(false);
        }
        
        if (btnShowtimesList != null) {
            btnShowtimesList.setFont(DesignConstants.getDefaultFont());
            btnShowtimesList.setBorderPainted(false);
            btnShowtimesList.setFocusPainted(false);
        }
        
        if (btnCarouselPrev != null) {
            btnCarouselPrev.setFont(DesignConstants.getDefaultFont());
            btnCarouselPrev.setBorderPainted(false);
            btnCarouselPrev.setFocusPainted(false);
        }
        
        if (btnCarouselNext != null) {
            btnCarouselNext.setFont(DesignConstants.getDefaultFont());
            btnCarouselNext.setBorderPainted(false);
            btnCarouselNext.setFocusPainted(false);
        }
    }
    
    private void initializeComponents() {
        // 헤더의 "예매" 버튼 숨기기 (이미지 아래의 "예매하기" 버튼만 사용)
        if (btnReservation != null) {
            btnReservation.setVisible(false);
        }
        
        // 검색 필드 플레이스홀더 처리
        txtGlobalSearch.setForeground(Color.GRAY);
        txtGlobalSearch.setText("영화 제목을 입력하세요");
        txtGlobalSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtGlobalSearch.getText().equals("영화 제목을 입력하세요")) {
                    txtGlobalSearch.setText("");
                    txtGlobalSearch.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtGlobalSearch.getText().isEmpty()) {
                    txtGlobalSearch.setForeground(Color.GRAY);
                    txtGlobalSearch.setText("영화 제목을 입력하세요");
                }
            }
        });
        
        // 검색 버튼 이벤트
        btnSearch.addActionListener(e -> performSearch());
        
        // Enter 키로 검색
        txtGlobalSearch.addActionListener(e -> performSearch());
        
        // 회원가입 버튼 이벤트
        btnJoin.addActionListener(e -> btnJoinActionPerformed(e));
        
        // 상영 시간표 버튼 이벤트
        btnShowtimesList.addActionListener(e -> {
            try {
                DailyScheduleForm dailyScheduleForm = new DailyScheduleForm();
                // 로그인된 사용자 정보 전달
                if (loggedInUser != null) {
                    dailyScheduleForm.setUser(loggedInUser);
                }
                dailyScheduleForm.setLocationRelativeTo(this);
                dailyScheduleForm.setVisible(true);
            } catch (Exception ex) {
                logger.severe("상영 시간표 화면 열기 실패: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "상영 시간표 화면을 열 수 없습니다: " + ex.getMessage(), 
                    "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // 캐러셀 이전 버튼 이벤트
        btnCarouselPrev.addActionListener(e -> {
            if (movies != null && movies.size() > 0) {
                currentCarouselIndex = (currentCarouselIndex - 1 + movies.size()) % movies.size();
                updateCarousel();
            }
        });
        
        // 캐러셀 다음 버튼 이벤트
        btnCarouselNext.addActionListener(e -> {
            if (movies != null && movies.size() > 0) {
                currentCarouselIndex = (currentCarouselIndex + 1) % movies.size();
                updateCarousel();
            }
        });
        
        // 캐러셀 예매 버튼
        btnReserveNow.addActionListener(e -> {
            // 로그인 확인
            if (loggedInUser == null) {
                int result = JOptionPane.showConfirmDialog(this,
                    "예매를 하려면 로그인이 필요합니다.\n로그인 화면으로 이동하시겠습니까?",
                    "로그인 필요",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (result == JOptionPane.YES_OPTION) {
                    LoginFrame loginFrame = new LoginFrame(this);
                    loginFrame.setLocationRelativeTo(this);
                    loginFrame.setVisible(true);
                }
                return;
            }
            
            if (currentCarouselIndex < movies.size()) {
                Movie selectedMovie = movies.get(currentCarouselIndex);
                try {
                    ShowtimeForm showtimeForm = new ShowtimeForm();
                    showtimeForm.setMovieId(selectedMovie.getMovieId());
                    showtimeForm.setMovieTitle(selectedMovie.getTitle());
                    // 로그인된 사용자 정보 전달
                    showtimeForm.setUser(loggedInUser);
                    showtimeForm.setVisible(true);
                    this.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "화면 전환 중 오류가 발생했습니다: " + ex.getMessage(), 
                        "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // 영화 목록 로드
        loadMovies();
        
        // 캐러셀 타이머 설정 (5초마다 자동 전환)
        carouselTimer = new javax.swing.Timer(CAROUSEL_INTERVAL, e -> {
            if (movies != null && movies.size() > 0) {
                currentCarouselIndex = (currentCarouselIndex + 1) % movies.size();
                updateCarousel();
            }
        });
        carouselTimer.start();
        
        // 초기 화면 설정
        updateCarousel();
    }
    
    /**
     * MovieDAO를 호출하여 영화 목록을 로드합니다.
     */
    private void loadMovies() {
        try {
            System.out.println("[영화 로드 시작] MovieDAO.getAllMovies() 호출");
            movies = movieDAO.getAllMovies();
            System.out.println("[영화 로드 완료] 영화 개수: " + movies.size());
            
            if (movies.isEmpty()) {
                // DB에 영화가 없는 경우
                System.out.println("[경고] DB에 영화 데이터가 없습니다. MOVIE 테이블에 데이터를 추가해주세요.");
                JOptionPane.showMessageDialog(this, 
                    "데이터베이스에서 영화를 불러올 수 없습니다.\n\n" +
                    "원인: MOVIE 테이블에 데이터가 없습니다.\n\n" +
                    "해결 방법:\n" +
                    "1. SQL Server Management Studio에서 Theater 데이터베이스 열기\n" +
                    "2. MOVIE 테이블에 영화 데이터 추가\n\n" +
                    "또는 테스트용 영화 데이터 추가 스크립트를 실행해주세요.", 
                    "알림", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            System.err.println("[오류] 영화 목록 로드 실패:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "영화 목록을 불러오는 중 오류가 발생했습니다:\n" + e.getMessage() + 
                "\n\n자세한 내용은 콘솔을 확인하세요.", 
                "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateCarousel() {
        if (movies == null || movies.isEmpty()) {
            if (lblMovieTitle != null) lblMovieTitle.setText("영화 정보 없음");
            if (lblPosterImage != null) {
                lblPosterImage.setIcon(null);
                lblPosterImage.setText("포스터 이미지\n(없음)");
            }
            return;
        }
        
        // 현재 캐러셀 인덱스 범위 체크
        if (currentCarouselIndex < 0 || currentCarouselIndex >= movies.size()) {
            currentCarouselIndex = 0; // 범위를 벗어나면 첫 번째로 리셋
        }
        
        // 현재 캐러셀 인덱스에 해당하는 영화 표시
        Movie movie = movies.get(currentCarouselIndex);
        if (movie != null) {
            if (lblMovieTitle != null) {
                lblMovieTitle.setText(movie.getTitle() != null ? movie.getTitle() : "제목 없음");
            }
            
            if (lblPosterImage != null) {
                // 이미지 로드 (영화 ID 기반)
                loadPosterImage(movie);
            }
        }
    }
    
    /**
     * 영화 포스터 이미지를 로드하여 표시합니다.
     * 이미지 파일명: poster_{movieId}.jpg (예: poster_1.jpg)
     * 파일 위치: 프로젝트 루트의 images/ 폴더
     * 
     * @param movie 영화 정보
     */
    private void loadPosterImage(Movie movie) {
        if (movie == null || movie.getMovieId() == null) {
            lblPosterImage.setIcon(null);
            lblPosterImage.setText("포스터 이미지\n(영화 정보 없음)");
            return;
        }
        
        try {
            // 이미지 파일명: poster_{movieId}.{확장자}
            String baseFileName = "poster_" + movie.getMovieId();
            String[] extensions = {"jpg", "jpeg", "png", "gif", "webp"};
            
            ImageIcon imageIcon = null;
            java.awt.image.BufferedImage loadedBufferedImage = null; // ImageIO로 로드한 원본 이미지 저장
            String foundExtension = null;
            
            // 프로젝트 루트 기준 images 폴더 경로
            java.io.File projectDir = new java.io.File(System.getProperty("user.dir"));
            java.io.File imagesDir = new java.io.File(projectDir, "images");
            
            // 디버깅: images 폴더 존재 여부 확인
            if (!imagesDir.exists() || !imagesDir.isDirectory()) {
                logger.warning("[" + baseFileName + "] images 폴더를 찾을 수 없습니다: " + imagesDir.getAbsolutePath());
            } else {
                // 디버깅: images 폴더 내 파일 목록 확인
                java.io.File[] allFiles = imagesDir.listFiles();
                if (allFiles != null) {
                    java.util.List<String> matchingFiles = new java.util.ArrayList<>();
                    for (java.io.File f : allFiles) {
                        if (f.getName().startsWith(baseFileName + ".")) {
                            matchingFiles.add(f.getName());
                        }
                    }
                    if (!matchingFiles.isEmpty()) {
                        logger.info("[" + baseFileName + "] images 폴더에서 발견된 파일: " + matchingFiles);
                    } else {
                        logger.warning("[" + baseFileName + "] images 폴더에서 해당 파일을 찾을 수 없습니다. 폴더 내용: " + 
                                      java.util.Arrays.toString(allFiles).substring(0, Math.min(200, java.util.Arrays.toString(allFiles).length())));
                    }
                }
            }
            
            // 각 확장자를 시도하면서 이미지 파일 찾기
            for (String ext : extensions) {
                java.io.File imageFile = new java.io.File(imagesDir, baseFileName + "." + ext);
                String absolutePath = imageFile.getAbsolutePath();
                
                // 파일 존재 여부 확인
                if (!imageFile.exists()) {
                    logger.info("[" + baseFileName + "] 파일 없음: " + absolutePath);
                    continue; // 파일이 없으면 다음 확장자로
                }
                
                logger.info("[" + baseFileName + "] 파일 발견: " + absolutePath + " (확장자: " + ext + ")");
                
                if (!imageFile.isFile() || !imageFile.canRead()) {
                    logger.warning("[" + baseFileName + "] 이미지 파일을 읽을 수 없습니다: " + absolutePath);
                    continue;
                }
                
                try {
                    // webp 파일은 Java 기본 ImageIO에서 지원하지 않음
                    if ("webp".equalsIgnoreCase(ext)) {
                        logger.warning("[" + baseFileName + "] .webp 형식은 Java에서 기본 지원되지 않습니다. " +
                                      "이미지를 .jpg 또는 .png로 변환해주세요: " + absolutePath);
                        continue; // webp는 건너뛰고 다른 형식 찾기
                    }
                    
                    // ImageIO를 사용하여 더 안정적으로 이미지 로드
                    java.awt.image.BufferedImage bufferedImage = null;
                    try {
                        bufferedImage = javax.imageio.ImageIO.read(imageFile);
                    } catch (Exception ioEx) {
                        logger.warning("[" + baseFileName + "] ImageIO로 이미지 읽기 실패: " + absolutePath + " - " + ioEx.getMessage());
                        // ImageIO 실패 시 ImageIcon으로 재시도
                        bufferedImage = null;
                    }
                    
                    // ImageIO로 로드 실패 시 ImageIcon으로 재시도
                    if (bufferedImage == null || bufferedImage.getWidth() <= 0 || bufferedImage.getHeight() <= 0) {
                        logger.info("[" + baseFileName + "] ImageIO 실패, ImageIcon으로 재시도: " + absolutePath);
                        imageIcon = new ImageIcon(absolutePath);
                        Image image = imageIcon.getImage();
                        
                        if (image != null) {
                            // MediaTracker로 이미지 로드 완료 대기
                            JPanel tempPanel = new JPanel();
                            MediaTracker tracker = new MediaTracker(tempPanel);
                            tracker.addImage(image, 0);
                            try {
                                tracker.waitForID(0, 3000); // 최대 3초 대기
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                            
                            if (tracker.isErrorID(0)) {
                                logger.warning("[" + baseFileName + "] MediaTracker 오류: " + absolutePath);
                                imageIcon = null;
                                continue;
                            }
                            
                            // 크기 확인
                            int width = imageIcon.getIconWidth();
                            int height = imageIcon.getIconHeight();
                            
                            if (width <= 0 || height <= 0) {
                                logger.warning("[" + baseFileName + "] 이미지 크기가 0: " + absolutePath + 
                                              " (크기: " + width + "x" + height + ")");
                                imageIcon = null;
                                continue;
                            }
                            
                            foundExtension = ext;
                            logger.info("[" + baseFileName + "] ImageIcon으로 로드 성공: " + absolutePath + 
                                       " (크기: " + width + "x" + height + ")");
                            break;
                        } else {
                            logger.warning("[" + baseFileName + "] 이미지 객체가 null: " + absolutePath);
                            imageIcon = null;
                            continue;
                        }
                    } else {
                        // ImageIO로 성공적으로 로드됨
                        int width = bufferedImage.getWidth();
                        int height = bufferedImage.getHeight();
                        loadedBufferedImage = bufferedImage; // 원본 이미지 저장
                        imageIcon = new ImageIcon(bufferedImage);
                        foundExtension = ext;
                        logger.info("[" + baseFileName + "] ImageIO로 로드 성공: " + absolutePath + 
                                   " (크기: " + width + "x" + height + ")");
                        break;
                    }
                } catch (Exception ex) {
                    logger.warning("[" + baseFileName + "] 이미지 로드 중 예외: " + imageFile.getName() + " - " + ex.getMessage());
                    ex.printStackTrace(); // 스택 트레이스 출력
                    imageIcon = null;
                    continue;
                }
            }
            
            // 이미지를 찾지 못한 경우, 리소스 경로로도 시도
            if (imageIcon == null || imageIcon.getIconWidth() <= 0) {
                for (String ext : extensions) {
                    String resourcePath = "/images/" + baseFileName + "." + ext;
                    java.net.URL imageUrl = getClass().getResource(resourcePath);
                    
                    if (imageUrl != null) {
                        try {
                            imageIcon = new ImageIcon(imageUrl);
                            
                            // 이미지가 제대로 로드되었는지 확인
                            if (imageIcon.getIconWidth() > 0 && imageIcon.getIconHeight() > 0) {
                                foundExtension = ext;
                                logger.info("리소스 경로에서 이미지 로드: " + resourcePath);
                                break;
                            }
                        } catch (Exception ex) {
                            logger.warning("리소스 이미지 로드 중 예외: " + resourcePath + " - " + ex.getMessage());
                            imageIcon = null;
                            continue;
                        }
                    }
                }
            }
            
            if (imageIcon != null && imageIcon.getIconWidth() > 0) {
                // 이미지 로드 성공
                try {
                    // 레이블 크기에 맞춰 이미지 크기 조정 (비율 유지)
                    int labelWidth = lblPosterImage.getWidth() > 0 ? lblPosterImage.getWidth() : 1000;
                    int labelHeight = lblPosterImage.getHeight() > 0 ? lblPosterImage.getHeight() : 500;
                    
                    // 원본 이미지 비율 계산
                    int originalWidth = imageIcon.getIconWidth();
                    int originalHeight = imageIcon.getIconHeight();
                    
                    // 비율을 유지하면서 크기 조정
                    double widthRatio = (double) labelWidth / originalWidth;
                    double heightRatio = (double) labelHeight / originalHeight;
                    double ratio = Math.min(widthRatio, heightRatio);
                    
                    int scaledWidth = (int) (originalWidth * ratio);
                    int scaledHeight = (int) (originalHeight * ratio);
                    
                    // BufferedImage를 사용하여 더 안정적으로 스케일링
                    java.awt.image.BufferedImage originalBuffered = null;
                    
                    // ImageIO로 로드한 BufferedImage가 있으면 사용, 없으면 ImageIcon에서 가져오기
                    if (loadedBufferedImage != null) {
                        originalBuffered = loadedBufferedImage;
                    } else {
                        Image originalImage = imageIcon.getImage();
                        
                        // Image를 BufferedImage로 변환
                        if (originalImage instanceof java.awt.image.BufferedImage) {
                            originalBuffered = (java.awt.image.BufferedImage) originalImage;
                        } else {
                            // ImageIcon의 Image가 BufferedImage가 아닌 경우 변환
                            originalBuffered = new java.awt.image.BufferedImage(
                                originalWidth, originalHeight, 
                                java.awt.image.BufferedImage.TYPE_INT_RGB
                            );
                            Graphics2D g = originalBuffered.createGraphics();
                            g.drawImage(originalImage, 0, 0, null);
                            g.dispose();
                        }
                    }
                    
                    // 고품질 스케일링
                    java.awt.image.BufferedImage scaledBuffered = new java.awt.image.BufferedImage(
                        scaledWidth, scaledHeight, 
                        java.awt.image.BufferedImage.TYPE_INT_RGB
                    );
                    Graphics2D g2 = scaledBuffered.createGraphics();
                    g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, 
                                       java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, 
                                       java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                    g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                                       java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.drawImage(originalBuffered, 0, 0, scaledWidth, scaledHeight, null);
                    g2.dispose();
                    
                    // ImageIcon 생성 및 설정
                    ImageIcon scaledIcon = new ImageIcon(scaledBuffered);
                    
                    // 직접 설정 (이미 EDT에서 실행 중)
                    lblPosterImage.setIcon(scaledIcon);
                    lblPosterImage.setText(""); // 텍스트 제거
                    
                    // UI 업데이트 강제
                    lblPosterImage.revalidate();
                    lblPosterImage.repaint();
                    
                    // 부모 컨테이너도 업데이트
                    java.awt.Container parent = lblPosterImage.getParent();
                    while (parent != null) {
                        parent.revalidate();
                        parent.repaint();
                        parent = parent.getParent();
                    }
                    
                    String imagePathUsed = "images/poster_" + movie.getMovieId() + "." + (foundExtension != null ? foundExtension : "jpg");
                    logger.info("포스터 이미지 로드 성공: " + imagePathUsed + " (크기: " + scaledWidth + "x" + scaledHeight + ")");
                } catch (Exception e) {
                    logger.warning("이미지 스케일링 중 오류: " + e.getMessage());
                    e.printStackTrace();
                    // 스케일링 실패 시 원본 이미지 사용
                    lblPosterImage.setIcon(imageIcon);
                    lblPosterImage.setText("");
                    lblPosterImage.revalidate();
                    lblPosterImage.repaint();
                }
            } else {
                // 이미지 파일이 없는 경우
                lblPosterImage.setIcon(null);
                String movieTitle = movie.getTitle() != null ? movie.getTitle() : "제목 없음";
                
                // webp 파일이 있는지 확인
                java.io.File webpFile = new java.io.File(imagesDir, baseFileName + ".webp");
                if (webpFile.exists()) {
                    lblPosterImage.setText("포스터 이미지\n(" + movieTitle + ")\n\n⚠️ .webp 형식은 지원되지 않습니다.\n.jpg 또는 .png로 변환해주세요.");
                    logger.warning("[" + baseFileName + "] .webp 파일이 있지만 로드할 수 없습니다. " +
                                  "이미지를 .jpg 또는 .png로 변환해주세요: " + webpFile.getAbsolutePath());
                } else {
                    lblPosterImage.setText("포스터 이미지\n(" + movieTitle + ")");
                    String imagePathAttempted = "images/poster_" + movie.getMovieId();
                    logger.info("[" + baseFileName + "] 포스터 이미지 파일 없음: " + imagePathAttempted + 
                               " (확장자: jpg/jpeg/png/gif 필요)");
                }
            }
        } catch (Exception e) {
            // 이미지 로드 실패 시 텍스트로 대체
            logger.warning("포스터 이미지 로드 실패: " + e.getMessage());
            lblPosterImage.setIcon(null);
            String movieTitle = movie.getTitle() != null ? movie.getTitle() : "제목 없음";
            lblPosterImage.setText("포스터 이미지\n(" + movieTitle + ")");
        }
    }
    
    private void performSearch() {
        String searchText = txtGlobalSearch.getText().trim();
        if (searchText.isEmpty() || searchText.equals("영화 제목을 입력하세요")) {
            JOptionPane.showMessageDialog(this, "검색어를 입력해주세요.", "검색 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            List<Movie> results = movieDAO.searchMoviesByTitle(searchText);
            
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.", "검색 결과", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // 검색 결과 다이얼로그 표시 (예매하기 버튼 포함)
                showSearchResultsDialog(results);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "검색 중 오류가 발생했습니다: " + e.getMessage(), 
                "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * 검색 결과를 다이얼로그로 표시하고 각 영화에 예매하기 버튼을 제공합니다.
     */
    private void showSearchResultsDialog(List<Movie> results) {
        JDialog searchDialog = new JDialog(this, "검색 결과 (" + results.size() + "개)", true);
        searchDialog.setSize(600, 500);
        searchDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 검색 결과 목록 패널
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        
        // 스크롤 가능하게 만들기
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("검색 결과"));
        scrollPane.setPreferredSize(new Dimension(550, 350));
        
        // 각 검색 결과 항목 추가
                for (Movie movie : results) {
            if (movie == null) continue;
            
            JPanel moviePanel = new JPanel(new BorderLayout(10, 5));
            moviePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            moviePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            
            // 영화 정보 패널 (왼쪽)
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel titleLabel = new JLabel(movie.getTitle() != null ? movie.getTitle() : "제목 없음");
            titleLabel.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
            
            String genreText = movie.getGenre() != null ? movie.getGenre() : "미정";
            String ratingText = movie.getRating() != null ? movie.getRating() : "";
            String directorText = movie.getDirector() != null ? "감독: " + movie.getDirector() : "";
            
            JLabel detailLabel = new JLabel(genreText + " | " + ratingText + 
                (directorText.isEmpty() ? "" : " | " + directorText));
            detailLabel.setFont(DesignConstants.getDefaultFont());
            detailLabel.setForeground(Color.GRAY);
            
            infoPanel.add(titleLabel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(detailLabel);
            
            // 예매하기 버튼 (오른쪽)
            JButton reserveButton = new JButton("예매하기");
            reserveButton.setFont(DesignConstants.getDefaultFont());
            reserveButton.setPreferredSize(new Dimension(100, 40));
            reserveButton.setBorderPainted(false);
            reserveButton.setFocusPainted(false);
            
            // 예매하기 버튼 클릭 이벤트
            reserveButton.addActionListener(e -> {
                searchDialog.dispose();
                // 해당 영화를 캐러셀에 표시하고 예매 진행
                selectMovieForReservation(movie);
            });
            
            moviePanel.add(infoPanel, BorderLayout.CENTER);
            moviePanel.add(reserveButton, BorderLayout.EAST);
            
            resultsPanel.add(moviePanel);
            resultsPanel.add(Box.createVerticalStrut(5));
        }
        
        // 닫기 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("닫기");
        closeButton.setFont(DesignConstants.getDefaultFont());
        closeButton.setPreferredSize(new Dimension(80, 35));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> searchDialog.dispose());
        buttonPanel.add(closeButton);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        searchDialog.add(mainPanel);
        searchDialog.setVisible(true);
    }
    
    /**
     * 선택한 영화를 캐러셀에 표시하고 예매 화면으로 이동합니다.
     */
    private void selectMovieForReservation(Movie selectedMovie) {
        if (selectedMovie == null || selectedMovie.getMovieId() == null) {
            JOptionPane.showMessageDialog(this, "영화 정보가 올바르지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 로그인 확인
        if (loggedInUser == null) {
            int result = JOptionPane.showConfirmDialog(this,
                "예매를 하려면 로그인이 필요합니다.\n로그인 화면으로 이동하시겠습니까?",
                "로그인 필요",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                LoginFrame loginFrame = new LoginFrame(this);
                loginFrame.setLocationRelativeTo(this);
                loginFrame.setVisible(true);
            }
            return;
        }
        
        // 해당 영화를 캐러셀에 표시
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            if (movie != null && movie.getMovieId() != null && 
                movie.getMovieId().equals(selectedMovie.getMovieId())) {
                currentCarouselIndex = i;
        updateCarousel();
                break;
            }
        }
        
        // 예매 화면으로 이동
        try {
            ShowtimeForm showtimeForm = new ShowtimeForm();
            showtimeForm.setMovieId(selectedMovie.getMovieId());
            showtimeForm.setMovieTitle(selectedMovie.getTitle() != null ? selectedMovie.getTitle() : "");
            // 로그인된 사용자 정보 전달 (로그인 확인 후이므로 반드시 있음)
            showtimeForm.setUser(loggedInUser);
            showtimeForm.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "화면 전환 중 오류가 발생했습니다: " + e.getMessage(), 
                "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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

        pnlTopContainer = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        pnlHeaderButtons = new javax.swing.JPanel();
        btnLogin = new javax.swing.JButton();
        btnJoin = new javax.swing.JButton();
        btnReservation = new javax.swing.JButton();
        pnlSearch = new javax.swing.JPanel();
        lblSearchLabel = new javax.swing.JLabel();
        txtGlobalSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnShowtimesList = new javax.swing.JButton();
        pnlCarouselContent = new javax.swing.JPanel();
        btnCarouselPrev = new javax.swing.JButton();
        lblPosterImage = new javax.swing.JLabel();
        btnCarouselNext = new javax.swing.JButton();
        pnlReservationArea = new javax.swing.JPanel();
        lblMovieTitle = new javax.swing.JLabel();
        btnReserveNow = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(81, 74, 74));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        setSize(new java.awt.Dimension(1200, 800));

        pnlTopContainer.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlTopContainer.setLayout(new javax.swing.BoxLayout(pnlTopContainer, javax.swing.BoxLayout.Y_AXIS));

        pnlHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 20, 20));
        pnlHeader.setPreferredSize(new java.awt.Dimension(1200, 80));
        pnlHeader.setLayout(new java.awt.BorderLayout());

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 1, 24)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTitle.setText("영화 예매 시스템");
        pnlHeader.add(lblTitle, java.awt.BorderLayout.LINE_START);

        pnlHeaderButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 10, 5));

        btnLogin.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnLogin.setText("로그인");
        btnLogin.setPreferredSize(new java.awt.Dimension(100, 35));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        pnlHeaderButtons.add(btnLogin);

        btnJoin.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnJoin.setText("회원가입");
        btnJoin.setPreferredSize(new java.awt.Dimension(100, 35));
        pnlHeaderButtons.add(btnJoin);

        btnReservation.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnReservation.setText("예매");
        btnReservation.setPreferredSize(new java.awt.Dimension(100, 35));
        btnReservation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReservationActionPerformed(evt);
            }
        });
        pnlHeaderButtons.add(btnReservation);

        pnlHeader.add(pnlHeaderButtons, java.awt.BorderLayout.LINE_END);

        pnlTopContainer.add(pnlHeader);

        pnlSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 20, 20));
        pnlSearch.setPreferredSize(new java.awt.Dimension(1200, 60));
        pnlSearch.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        lblSearchLabel.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblSearchLabel.setText("영화검색");
        pnlSearch.add(lblSearchLabel);

        txtGlobalSearch.setText("영화 제목을 입력하세요");
        txtGlobalSearch.setPreferredSize(new java.awt.Dimension(300, 30));
        pnlSearch.add(txtGlobalSearch);

        btnSearch.setText("검색");
        btnSearch.setToolTipText("");
        btnSearch.setPreferredSize(new java.awt.Dimension(80, 30));
        pnlSearch.add(btnSearch);

        btnShowtimesList.setText("상영 시간표");
        btnShowtimesList.setPreferredSize(new java.awt.Dimension(120, 30));
        pnlSearch.add(btnShowtimesList);

        pnlTopContainer.add(pnlSearch);

        getContentPane().add(pnlTopContainer, java.awt.BorderLayout.NORTH);

        pnlCarouselContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlCarouselContent.setPreferredSize(new java.awt.Dimension(1000, 500));
        pnlCarouselContent.setLayout(new java.awt.BorderLayout());

        btnCarouselPrev.setText("◀");
        btnCarouselPrev.setPreferredSize(new java.awt.Dimension(50, 500));
        pnlCarouselContent.add(btnCarouselPrev, java.awt.BorderLayout.LINE_START);

        lblPosterImage.setFont(new java.awt.Font("맑은 고딕", 0, 16)); // NOI18N
        lblPosterImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPosterImage.setText("포스터 이미지");
        lblPosterImage.setPreferredSize(new java.awt.Dimension(1000, 500));
        pnlCarouselContent.add(lblPosterImage, java.awt.BorderLayout.CENTER);

        btnCarouselNext.setText("▶");
        btnCarouselNext.setPreferredSize(new java.awt.Dimension(50, 500));
        pnlCarouselContent.add(btnCarouselNext, java.awt.BorderLayout.LINE_END);

        pnlReservationArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 10));
        pnlReservationArea.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));

        lblMovieTitle.setFont(new java.awt.Font("맑은 고딕", 1, 16)); // NOI18N
        lblMovieTitle.setText("영화 제목");
        lblMovieTitle.setToolTipText("");
        lblMovieTitle.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblMovieTitle.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        pnlReservationArea.add(lblMovieTitle);

        btnReserveNow.setFont(new java.awt.Font("맑은 고딕", 1, 18)); // NOI18N
        btnReserveNow.setText("예매하기");
        btnReserveNow.setToolTipText("");
        btnReserveNow.setPreferredSize(new java.awt.Dimension(200, 50));
        btnReserveNow.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlReservationArea.add(btnReserveNow);

        pnlCarouselContent.add(pnlReservationArea, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlCarouselContent, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        if (loggedInUser != null) {
            // 로그아웃
            int confirm = JOptionPane.showConfirmDialog(this, 
                "로그아웃 하시겠습니까?", 
                "로그아웃", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                loggedInUser = null;
                updateUIForLogin();
            }
        } else {
            // 로그인
            LoginFrame loginFrame = new LoginFrame(this); // 자기 자신(this)을 전달
        loginFrame.setLocationRelativeTo(this);
        loginFrame.setVisible(true);
            // 로그인 성공 시 기존 MainFrame 화면이 업데이트됨
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnReservationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReservationActionPerformed
        // 현재 캐러셀에 표시된 영화로 예매 진행
        if (movies == null || movies.isEmpty() || currentCarouselIndex < 0 || currentCarouselIndex >= movies.size()) {
            JOptionPane.showMessageDialog(this, "예매할 영화를 선택해 주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Movie selectedMovie = movies.get(currentCarouselIndex);
            if (selectedMovie == null || selectedMovie.getMovieId() == null) {
                JOptionPane.showMessageDialog(this, "영화 정보가 올바르지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ShowtimeForm showtimeForm = new ShowtimeForm();
            showtimeForm.setMovieId(selectedMovie.getMovieId());
            showtimeForm.setMovieTitle(selectedMovie.getTitle() != null ? selectedMovie.getTitle() : "");
            // 로그인된 사용자 정보 전달
            if (loggedInUser != null) {
                showtimeForm.setUser(loggedInUser);
            }
            showtimeForm.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "화면 전환 중 시스템 오류가 발생했습니다: " + e.getMessage(),
                "시스템 오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnReservationActionPerformed
    
    private void btnJoinActionPerformed(java.awt.event.ActionEvent evt) {
        // JoinForm 열기
        JoinForm joinForm = new JoinForm();
        joinForm.setLocationRelativeTo(this);
        joinForm.setVisible(true);
        this.dispose(); // MainFrame 닫기
    }

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
        java.awt.EventQueue.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setLocationRelativeTo(null); // 화면 중앙에 배치
                frame.setVisible(true);
                frame.setSize(800, 700); // 명시적으로 크기 설정
                frame.setMinimumSize(new java.awt.Dimension(600, 500));
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "애플리케이션을 시작하는 중 오류가 발생했습니다:\n" + e.getMessage(), 
                    "오류", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCarouselNext;
    private javax.swing.JButton btnCarouselPrev;
    private javax.swing.JButton btnJoin;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnReservation;
    private javax.swing.JButton btnReserveNow;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnShowtimesList;
    private javax.swing.JLabel lblMovieTitle;
    private javax.swing.JLabel lblPosterImage;
    private javax.swing.JLabel lblSearchLabel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlCarouselContent;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlHeaderButtons;
    private javax.swing.JPanel pnlReservationArea;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPanel pnlTopContainer;
    private javax.swing.JTextField txtGlobalSearch;
    // End of variables declaration//GEN-END:variables
}
