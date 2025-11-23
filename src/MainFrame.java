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

public class MainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());
    
    // 페이지네이션 관련
    private int currentPage = 1;
    private final int itemsPerPage = 5;
    private List<JLabel> paginationLabels = new ArrayList<>();
    
    // 메뉴 탭 상태
    private String currentFilter = "ALL"; // ALL, NOW_SHOWING, UPCOMING
    
    // 더미 영화 데이터 (DB 없이 테스트용)
    private String[][] dummyMovies = {
        {"어벤져스", "액션", "2024-01-01"},
        {"겨울왕국", "애니메이션", "2024-01-15"},
        {"인터스텔라", "SF", "2024-02-01"},
        {"기생충", "드라마", "2024-02-10"},
        {"토르", "액션", "2024-03-01"},
        {"토이스토리", "애니메이션", "2024-03-15"},
        {"다크나이트", "액션", "2024-04-01"},
        {"인셉션", "SF", "2024-04-10"},
        {"매트릭스", "SF", "2024-05-01"},
        {"타이타닉", "로맨스", "2024-05-15"}
    };
    
    private int currentCarouselIndex = 0;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        initializeComponents();
    }
    
    private void initializeComponents() {
        // 페이지네이션 라벨 초기화
        paginationLabels.add(jLabel1);
        paginationLabels.add(jLabel2);
        paginationLabels.add(jLabel3);
        paginationLabels.add(jLabel4);
        paginationLabels.add(jLabel5);
        paginationLabels.add(jLabel6);
        paginationLabels.add(jLabel7);
        paginationLabels.add(jLabel8);
        paginationLabels.add(jLabel9);
        paginationLabels.add(jLabel10);
        paginationLabels.add(jLabel11);
        
        // 페이지네이션 라벨에 클릭 이벤트 추가
        for (int i = 0; i < paginationLabels.size(); i++) {
            final int pageNum = i + 1;
            JLabel label = paginationLabels.get(i);
            label.setText(String.valueOf(pageNum));
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    goToPage(pageNum);
                }
            });
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
        
        // 메뉴 탭 버튼 이벤트
        btnNowShowing.addActionListener(e -> {
            currentFilter = "NOW_SHOWING";
            updateMovieDisplay();
            highlightTabButton(btnNowShowing);
        });
        
        btnUpcoming.addActionListener(e -> {
            currentFilter = "UPCOMING";
            updateMovieDisplay();
            highlightTabButton(btnUpcoming);
        });
        
        btnShowtimesList.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "상영 시간표 기능은 준비 중입니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // 캐러셀 예매 버튼
        btnReserveNow.addActionListener(e -> {
            if (lblMovieTitle.getText() != null && !lblMovieTitle.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "영화: " + lblMovieTitle.getText() + "\n예매 기능은 준비 중입니다.", 
                    "예매", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // 초기 화면 설정
        updateMovieDisplay();
        updateCarousel();
    }
    
    private void goToPage(int page) {
        currentPage = page;
        updatePagination();
        updateCarousel();
    }
    
    private void updatePagination() {
        for (int i = 0; i < paginationLabels.size(); i++) {
            JLabel label = paginationLabels.get(i);
            if (i + 1 == currentPage) {
                label.setForeground(Color.BLUE);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            } else {
                label.setForeground(Color.BLACK);
                label.setFont(label.getFont().deriveFont(Font.PLAIN));
            }
        }
    }
    
    private void updateCarousel() {
        int startIndex = (currentPage - 1) * itemsPerPage;
        if (startIndex < dummyMovies.length) {
            String[] movie = dummyMovies[startIndex];
            lblMovieTitle.setText(movie[0]);
            txtMovieDesc.setText("장르: " + movie[1] + " | 개봉일: " + movie[2]);
            lblPosterImage.setText("포스터 이미지\n(" + movie[0] + ")");
        }
    }
    
    private void performSearch() {
        String searchText = txtGlobalSearch.getText().trim();
        if (searchText.isEmpty() || searchText.equals("영화 제목을 입력하세요")) {
            JOptionPane.showMessageDialog(this, "검색어를 입력해주세요.", "검색 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<String> results = new ArrayList<>();
        for (String[] movie : dummyMovies) {
            if (movie[0].contains(searchText)) {
                results.add(movie[0] + " - " + movie[1]);
            }
        }
        
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.", "검색 결과", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String message = "검색 결과 (" + results.size() + "개):\n\n";
            for (String result : results) {
                message += "• " + result + "\n";
            }
            JOptionPane.showMessageDialog(this, message, "검색 결과", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void updateMovieDisplay() {
        // 필터에 따라 영화 표시 업데이트
        updateCarousel();
        updatePagination();
    }
    
    private void highlightTabButton(JButton activeButton) {
        btnNowShowing.setBackground(null);
        btnUpcoming.setBackground(null);
        btnShowtimesList.setBackground(null);
        
        if (activeButton != null) {
            activeButton.setBackground(new Color(200, 220, 255));
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

        lblTitle = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        btnJoin = new javax.swing.JButton();
        btnReservation = new javax.swing.JButton();
        pnlTopContainer = new javax.swing.JPanel();
        pnlCarouselContent = new javax.swing.JPanel();
        pnlImageArea = new javax.swing.JPanel();
        lblPosterImage = new javax.swing.JLabel();
        pnlTextInfo = new javax.swing.JPanel();
        lblMovieTitle = new javax.swing.JLabel();
        txtMovieDesc = new javax.swing.JTextField();
        btnReserveNow = new javax.swing.JButton();
        pnlPagination = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        pnlMenuTabs = new javax.swing.JPanel();
        btnNowShowing = new javax.swing.JButton();
        btnShowtimesList = new javax.swing.JButton();
        btnUpcoming = new javax.swing.JButton();
        lblSearchLabel = new javax.swing.JLabel();
        txtGlobalSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(81, 74, 74));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 0, 18)); // NOI18N
        lblTitle.setText("영화 예매 시스템");

        btnLogin.setText("로그인");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        btnJoin.setText("회원가입");
        btnJoin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJoinActionPerformed(evt);
            }
        });

        btnReservation.setText("예매");
        btnReservation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReservationActionPerformed(evt);
            }
        });

        pnlTopContainer.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlTopContainer.setLayout(new javax.swing.BoxLayout(pnlTopContainer, javax.swing.BoxLayout.Y_AXIS));

        pnlCarouselContent.setLayout(new java.awt.BorderLayout());

        pnlImageArea.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, java.awt.Color.darkGray, null, null));
        pnlImageArea.setLayout(new java.awt.BorderLayout());

        lblPosterImage.setText("이미지");
        pnlImageArea.add(lblPosterImage, java.awt.BorderLayout.CENTER);

        pnlCarouselContent.add(pnlImageArea, java.awt.BorderLayout.WEST);

        pnlTextInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlTextInfo.setLayout(new javax.swing.BoxLayout(pnlTextInfo, javax.swing.BoxLayout.LINE_AXIS));

        lblMovieTitle.setToolTipText("");
        lblMovieTitle.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        pnlTextInfo.add(lblMovieTitle);

        txtMovieDesc.setToolTipText("");
        txtMovieDesc.setEditable(false); // 읽기 전용으로 설정
        txtMovieDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovieDescActionPerformed(evt);
            }
        });
        pnlTextInfo.add(txtMovieDesc);

        btnReserveNow.setText("예매");
        btnReserveNow.setToolTipText("");
        btnReserveNow.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlTextInfo.add(btnReserveNow);

        pnlCarouselContent.add(pnlTextInfo, java.awt.BorderLayout.CENTER);

        pnlTopContainer.add(pnlCarouselContent);

        jLabel1.setText("jLabel1");
        pnlPagination.add(jLabel1);

        jLabel2.setText("jLabel1");
        pnlPagination.add(jLabel2);

        jLabel3.setText("jLabel1");
        pnlPagination.add(jLabel3);

        jLabel4.setText("jLabel1");
        pnlPagination.add(jLabel4);

        jLabel5.setText("jLabel1");
        pnlPagination.add(jLabel5);

        jLabel6.setText("jLabel1");
        pnlPagination.add(jLabel6);

        jLabel8.setText("jLabel1");
        pnlPagination.add(jLabel8);

        jLabel9.setText("jLabel1");
        pnlPagination.add(jLabel9);

        jLabel10.setText("jLabel1");
        pnlPagination.add(jLabel10);

        jLabel11.setText("jLabel1");
        pnlPagination.add(jLabel11);

        jLabel7.setText("jLabel1");
        pnlPagination.add(jLabel7);

        pnlTopContainer.add(pnlPagination);

        pnlMenuTabs.setToolTipText("");
        pnlMenuTabs.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        btnNowShowing.setText("현재 상영작");
        pnlMenuTabs.add(btnNowShowing);

        btnShowtimesList.setText("상영 시간표");
        pnlMenuTabs.add(btnShowtimesList);

        btnUpcoming.setText("개봉 예정작");
        pnlMenuTabs.add(btnUpcoming);

        lblSearchLabel.setText("영화검색");
        pnlMenuTabs.add(lblSearchLabel);

        txtGlobalSearch.setText("입력란");
        pnlMenuTabs.add(txtGlobalSearch);

        btnSearch.setText("검색");
        btnSearch.setToolTipText("");
        pnlMenuTabs.add(btnSearch);

        pnlTopContainer.add(pnlMenuTabs);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblTitle)
                                .addGap(137, 137, 137)
                                .addComponent(btnLogin)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnJoin))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnReservation)
                                .addGap(8, 8, 8))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(pnlTopContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 714, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogin)
                    .addComponent(btnJoin)
                    .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addComponent(pnlTopContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(btnReservation)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        // 간단한 로그인 다이얼로그 (DB 없이 테스트용)
        String username = JOptionPane.showInputDialog(this, "사용자명을 입력하세요:", "로그인", JOptionPane.QUESTION_MESSAGE);
        if (username != null && !username.trim().isEmpty()) {
            btnLogin.setText("로그아웃");
            JOptionPane.showMessageDialog(this, "로그인되었습니다: " + username, "로그인 완료", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnReservationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReservationActionPerformed
        // 현재 캐러셀에 표시된 영화로 예매 진행
        String movieTitle = lblMovieTitle.getText();
        if (movieTitle == null || movieTitle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "예매할 영화를 선택해 주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ShowtimeForm showtimeForm = new ShowtimeForm();
            showtimeForm.setMovieTitle(movieTitle);
            showtimeForm.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "화면 전환 중 시스템 오류가 발생했습니다.", "시스템 오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnReservationActionPerformed

    private void txtMovieDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovieDescActionPerformed
        // 영화 설명 필드는 읽기 전용으로 처리
    }//GEN-LAST:event_txtMovieDescActionPerformed
    
    private void btnJoinActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this, "회원가입 기능은 준비 중입니다.", "회원가입", JOptionPane.INFORMATION_MESSAGE);
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
    private javax.swing.JButton btnJoin;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnNowShowing;
    private javax.swing.JButton btnReservation;
    private javax.swing.JButton btnReserveNow;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnShowtimesList;
    private javax.swing.JButton btnUpcoming;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblMovieTitle;
    private javax.swing.JLabel lblPosterImage;
    private javax.swing.JLabel lblSearchLabel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlCarouselContent;
    private javax.swing.JPanel pnlImageArea;
    private javax.swing.JPanel pnlMenuTabs;
    private javax.swing.JPanel pnlPagination;
    private javax.swing.JPanel pnlTextInfo;
    private javax.swing.JPanel pnlTopContainer;
    private javax.swing.JTextField txtGlobalSearch;
    private javax.swing.JTextField txtMovieDesc;
    // End of variables declaration//GEN-END:variables
}
