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

    private List<Movie> movies = new ArrayList<>();
    private MovieDAO movieDAO = new MovieDAO();
    private int currentCarouselIndex = 0;
    private javax.swing.Timer carouselTimer;
    private static final int CAROUSEL_INTERVAL = 5000;

    private User loggedInUser = null;
    private javax.swing.JLabel lblWelcome = null;
    private javax.swing.JButton btnBookingHistory = null;

    public MainFrame() {
        initComponents();

        applyDarkTheme();
        
        initializeComponents();

        createWelcomeLabel();
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        updateUIForLogin();
    }

    private void createWelcomeLabel() {
        lblWelcome = new javax.swing.JLabel();
        lblWelcome.setFont(DesignConstants.getDefaultFont());
        lblWelcome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWelcome.setVisible(false);

        if (pnlHeader != null) {
            pnlHeader.add(lblWelcome, java.awt.BorderLayout.CENTER);
        }

        btnBookingHistory = new javax.swing.JButton();
        btnBookingHistory.setText("예매 내역");
        btnBookingHistory.setFont(DesignConstants.getDefaultFont());
        btnBookingHistory.setBorderPainted(false);
        btnBookingHistory.setFocusPainted(false);
        btnBookingHistory.setVisible(false);

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

        if (pnlHeaderButtons != null && btnJoin != null) {

            java.awt.Component[] components = pnlHeaderButtons.getComponents();
            pnlHeaderButtons.removeAll();

            pnlHeaderButtons.add(btnBookingHistory);

            for (java.awt.Component comp : components) {
                pnlHeaderButtons.add(comp);
            }
            
            pnlHeaderButtons.revalidate();
            pnlHeaderButtons.repaint();
        }
    }

    private void updateUIForLogin() {
        if (loggedInUser != null) {

            btnLogin.setText("로그아웃");
            btnJoin.setVisible(false);

            if (btnBookingHistory != null) {
                btnBookingHistory.setVisible(true);
            }

            if (lblWelcome != null) {
                String userName = (loggedInUser.getName() != null && !loggedInUser.getName().trim().isEmpty()) 
                                 ? loggedInUser.getName() 
                                 : loggedInUser.getUsername();
                lblWelcome.setText("환영합니다 " + userName + "님");
                lblWelcome.setVisible(true);
            }
        } else {

            btnLogin.setText("로그인");
            btnJoin.setVisible(true);

            if (btnBookingHistory != null) {
                btnBookingHistory.setVisible(false);
            }

            if (lblWelcome != null) {
                lblWelcome.setVisible(false);
            }
        }
    }

    private void applyDarkTheme() {

        if (lblTitle != null) {
            lblTitle.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_LARGE));
        }
        
        if (lblMovieTitle != null) {
            lblMovieTitle.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_BIG_TITLE));
        }
        
        if (lblSearchLabel != null) {
            lblSearchLabel.setFont(DesignConstants.getDefaultFont());
        }

        if (txtGlobalSearch != null) {
            txtGlobalSearch.setFont(DesignConstants.getDefaultFont());
        }

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

        if (btnReservation != null) {
            btnReservation.setVisible(false);
        }

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

        btnSearch.addActionListener(e -> performSearch());

        txtGlobalSearch.addActionListener(e -> performSearch());

        btnJoin.addActionListener(e -> btnJoinActionPerformed(e));

        btnShowtimesList.addActionListener(e -> {
            try {
                DailyScheduleForm dailyScheduleForm = new DailyScheduleForm();

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

        btnCarouselPrev.addActionListener(e -> {
            if (movies != null && movies.size() > 0) {
                currentCarouselIndex = (currentCarouselIndex - 1 + movies.size()) % movies.size();
                updateCarousel();
            }
        });

        btnCarouselNext.addActionListener(e -> {
            if (movies != null && movies.size() > 0) {
                currentCarouselIndex = (currentCarouselIndex + 1) % movies.size();
                updateCarousel();
            }
        });

        btnReserveNow.addActionListener(e -> {

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

        loadMovies();

        carouselTimer = new javax.swing.Timer(CAROUSEL_INTERVAL, e -> {
            if (movies != null && movies.size() > 0) {
                currentCarouselIndex = (currentCarouselIndex + 1) % movies.size();
                updateCarousel();
            }
        });
        carouselTimer.start();

        updateCarousel();
    }

    private void loadMovies() {
        try {
            System.out.println("[영화 로드 시작] MovieDAO.getAllMovies() 호출");
            movies = movieDAO.getAllMovies();
            System.out.println("[영화 로드 완료] 영화 개수: " + movies.size());
            
            if (movies.isEmpty()) {

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

        if (currentCarouselIndex < 0 || currentCarouselIndex >= movies.size()) {
            currentCarouselIndex = 0;
        }

        Movie movie = movies.get(currentCarouselIndex);
        if (movie != null) {
            if (lblMovieTitle != null) {
                lblMovieTitle.setText(movie.getTitle() != null ? movie.getTitle() : "제목 없음");
            }
            
            if (lblPosterImage != null) {

                loadPosterImage(movie);
            }
        }
    }

    private void loadPosterImage(Movie movie) {
        if (movie == null || movie.getMovieId() == null) {
            lblPosterImage.setIcon(null);
            lblPosterImage.setText("포스터 이미지\n(영화 정보 없음)");
            return;
        }
        
        try {

            String baseFileName = "poster_" + movie.getMovieId();
            String[] extensions = {"jpg", "jpeg", "png", "gif", "webp"};
            
            ImageIcon imageIcon = null;
            java.awt.image.BufferedImage loadedBufferedImage = null;
            String foundExtension = null;

            java.io.File projectDir = new java.io.File(System.getProperty("user.dir"));
            java.io.File imagesDir = new java.io.File(projectDir, "images");

            if (!imagesDir.exists() || !imagesDir.isDirectory()) {
                logger.warning("[" + baseFileName + "] images 폴더를 찾을 수 없습니다: " + imagesDir.getAbsolutePath());
            } else {

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

            for (String ext : extensions) {
                java.io.File imageFile = new java.io.File(imagesDir, baseFileName + "." + ext);
                String absolutePath = imageFile.getAbsolutePath();

                if (!imageFile.exists()) {
                    logger.info("[" + baseFileName + "] 파일 없음: " + absolutePath);
                    continue;
                }
                
                logger.info("[" + baseFileName + "] 파일 발견: " + absolutePath + " (확장자: " + ext + ")");
                
                if (!imageFile.isFile() || !imageFile.canRead()) {
                    logger.warning("[" + baseFileName + "] 이미지 파일을 읽을 수 없습니다: " + absolutePath);
                    continue;
                }
                
                try {

                    if ("webp".equalsIgnoreCase(ext)) {
                        logger.warning("[" + baseFileName + "] .webp 형식은 Java에서 기본 지원되지 않습니다. " +
                                      "이미지를 .jpg 또는 .png로 변환해주세요: " + absolutePath);
                        continue;
                    }

                    java.awt.image.BufferedImage bufferedImage = null;
                    try {
                        bufferedImage = javax.imageio.ImageIO.read(imageFile);
                    } catch (Exception ioEx) {
                        logger.warning("[" + baseFileName + "] ImageIO로 이미지 읽기 실패: " + absolutePath + " - " + ioEx.getMessage());

                        bufferedImage = null;
                    }

                    if (bufferedImage == null || bufferedImage.getWidth() <= 0 || bufferedImage.getHeight() <= 0) {
                        logger.info("[" + baseFileName + "] ImageIO 실패, ImageIcon으로 재시도: " + absolutePath);
                        imageIcon = new ImageIcon(absolutePath);
                        Image image = imageIcon.getImage();
                        
                        if (image != null) {

                            JPanel tempPanel = new JPanel();
                            MediaTracker tracker = new MediaTracker(tempPanel);
                            tracker.addImage(image, 0);
                            try {
                                tracker.waitForID(0, 3000);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                            
                            if (tracker.isErrorID(0)) {
                                logger.warning("[" + baseFileName + "] MediaTracker 오류: " + absolutePath);
                                imageIcon = null;
                                continue;
                            }

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

                        int width = bufferedImage.getWidth();
                        int height = bufferedImage.getHeight();
                        loadedBufferedImage = bufferedImage;
                        imageIcon = new ImageIcon(bufferedImage);
                        foundExtension = ext;
                        logger.info("[" + baseFileName + "] ImageIO로 로드 성공: " + absolutePath + 
                                   " (크기: " + width + "x" + height + ")");
                        break;
                    }
                } catch (Exception ex) {
                    logger.warning("[" + baseFileName + "] 이미지 로드 중 예외: " + imageFile.getName() + " - " + ex.getMessage());
                    ex.printStackTrace();
                    imageIcon = null;
                    continue;
                }
            }

            if (imageIcon == null || imageIcon.getIconWidth() <= 0) {
                for (String ext : extensions) {
                    String resourcePath = "/images/" + baseFileName + "." + ext;
                    java.net.URL imageUrl = getClass().getResource(resourcePath);
                    
                    if (imageUrl != null) {
                        try {
                            imageIcon = new ImageIcon(imageUrl);

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

                try {

                    int labelWidth = lblPosterImage.getWidth() > 0 ? lblPosterImage.getWidth() : 1000;
                    int labelHeight = lblPosterImage.getHeight() > 0 ? lblPosterImage.getHeight() : 500;

                    int originalWidth = imageIcon.getIconWidth();
                    int originalHeight = imageIcon.getIconHeight();

                    double widthRatio = (double) labelWidth / originalWidth;
                    double heightRatio = (double) labelHeight / originalHeight;
                    double ratio = Math.min(widthRatio, heightRatio);
                    
                    int scaledWidth = (int) (originalWidth * ratio);
                    int scaledHeight = (int) (originalHeight * ratio);

                    java.awt.image.BufferedImage originalBuffered = null;

                    if (loadedBufferedImage != null) {
                        originalBuffered = loadedBufferedImage;
                    } else {
                        Image originalImage = imageIcon.getImage();

                        if (originalImage instanceof java.awt.image.BufferedImage) {
                            originalBuffered = (java.awt.image.BufferedImage) originalImage;
                        } else {

                            originalBuffered = new java.awt.image.BufferedImage(
                                originalWidth, originalHeight, 
                                java.awt.image.BufferedImage.TYPE_INT_RGB
                            );
                            Graphics2D g = originalBuffered.createGraphics();
                            g.drawImage(originalImage, 0, 0, null);
                            g.dispose();
                        }
                    }

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

                    ImageIcon scaledIcon = new ImageIcon(scaledBuffered);

                    lblPosterImage.setIcon(scaledIcon);
                    lblPosterImage.setText("");

                    lblPosterImage.revalidate();
                    lblPosterImage.repaint();

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

                    lblPosterImage.setIcon(imageIcon);
                    lblPosterImage.setText("");
                    lblPosterImage.revalidate();
                    lblPosterImage.repaint();
                }
            } else {

                lblPosterImage.setIcon(null);
                String movieTitle = movie.getTitle() != null ? movie.getTitle() : "제목 없음";

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

                showSearchResultsDialog(results);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "검색 중 오류가 발생했습니다: " + e.getMessage(), 
                "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showSearchResultsDialog(List<Movie> results) {
        JDialog searchDialog = new JDialog(this, "검색 결과 (" + results.size() + "개)", true);
        searchDialog.setSize(600, 500);
        searchDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("검색 결과"));
        scrollPane.setPreferredSize(new Dimension(550, 350));

                for (Movie movie : results) {
            if (movie == null) continue;
            
            JPanel moviePanel = new JPanel(new BorderLayout(10, 5));
            moviePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            moviePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

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

            JButton reserveButton = new JButton("예매하기");
            reserveButton.setFont(DesignConstants.getDefaultFont());
            reserveButton.setPreferredSize(new Dimension(100, 40));
            reserveButton.setBorderPainted(false);
            reserveButton.setFocusPainted(false);

            reserveButton.addActionListener(e -> {
                searchDialog.dispose();

                selectMovieForReservation(movie);
            });
            
            moviePanel.add(infoPanel, BorderLayout.CENTER);
            moviePanel.add(reserveButton, BorderLayout.EAST);
            
            resultsPanel.add(moviePanel);
            resultsPanel.add(Box.createVerticalStrut(5));
        }

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

    private void selectMovieForReservation(Movie selectedMovie) {
        if (selectedMovie == null || selectedMovie.getMovieId() == null) {
            JOptionPane.showMessageDialog(this, "영화 정보가 올바르지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

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

        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            if (movie != null && movie.getMovieId() != null && 
                movie.getMovieId().equals(selectedMovie.getMovieId())) {
                currentCarouselIndex = i;
        updateCarousel();
                break;
            }
        }

        try {
            ShowtimeForm showtimeForm = new ShowtimeForm();
            showtimeForm.setMovieId(selectedMovie.getMovieId());
            showtimeForm.setMovieTitle(selectedMovie.getTitle() != null ? selectedMovie.getTitle() : "");

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

    @SuppressWarnings("unchecked")

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

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 1, 24));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTitle.setText("영화 예매 시스템");
        pnlHeader.add(lblTitle, java.awt.BorderLayout.LINE_START);

        pnlHeaderButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 10, 5));

        btnLogin.setFont(new java.awt.Font("맑은 고딕", 0, 14));
        btnLogin.setText("로그인");
        btnLogin.setPreferredSize(new java.awt.Dimension(100, 35));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        pnlHeaderButtons.add(btnLogin);

        btnJoin.setFont(new java.awt.Font("맑은 고딕", 0, 14));
        btnJoin.setText("회원가입");
        btnJoin.setPreferredSize(new java.awt.Dimension(100, 35));
        pnlHeaderButtons.add(btnJoin);

        btnReservation.setFont(new java.awt.Font("맑은 고딕", 0, 14));
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

        lblSearchLabel.setFont(new java.awt.Font("맑은 고딕", 0, 14));
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

        lblPosterImage.setFont(new java.awt.Font("맑은 고딕", 0, 16));
        lblPosterImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPosterImage.setText("포스터 이미지");
        lblPosterImage.setPreferredSize(new java.awt.Dimension(1000, 500));
        pnlCarouselContent.add(lblPosterImage, java.awt.BorderLayout.CENTER);

        btnCarouselNext.setText("▶");
        btnCarouselNext.setPreferredSize(new java.awt.Dimension(50, 500));
        pnlCarouselContent.add(btnCarouselNext, java.awt.BorderLayout.LINE_END);

        pnlReservationArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 10));
        pnlReservationArea.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));

        lblMovieTitle.setFont(new java.awt.Font("맑은 고딕", 1, 16));
        lblMovieTitle.setText("영화 제목");
        lblMovieTitle.setToolTipText("");
        lblMovieTitle.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblMovieTitle.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        pnlReservationArea.add(lblMovieTitle);

        btnReserveNow.setFont(new java.awt.Font("맑은 고딕", 1, 18));
        btnReserveNow.setText("예매하기");
        btnReserveNow.setToolTipText("");
        btnReserveNow.setPreferredSize(new java.awt.Dimension(200, 50));
        btnReserveNow.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlReservationArea.add(btnReserveNow);

        pnlCarouselContent.add(pnlReservationArea, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlCarouselContent, java.awt.BorderLayout.CENTER);

        pack();
    }

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        if (loggedInUser != null) {

            int confirm = JOptionPane.showConfirmDialog(this, 
                "로그아웃 하시겠습니까?", 
                "로그아웃", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                loggedInUser = null;
                updateUIForLogin();
            }
        } else {

            LoginFrame loginFrame = new LoginFrame(this);
        loginFrame.setLocationRelativeTo(this);
        loginFrame.setVisible(true);

        }
    }

    private void btnReservationActionPerformed(java.awt.event.ActionEvent evt) {

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
    }
    
    private void btnJoinActionPerformed(java.awt.event.ActionEvent evt) {

        JoinForm joinForm = new JoinForm();
        joinForm.setLocationRelativeTo(this);
        joinForm.setVisible(true);
        this.dispose();
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

        java.awt.EventQueue.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setSize(800, 700);
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

}
