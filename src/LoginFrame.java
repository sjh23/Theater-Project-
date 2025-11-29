/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import javax.swing.*;
import dao.UserDAO;
import model.User;
import util.CaptchaUtil;
import util.DesignConstants;
import java.awt.*;
import java.sql.SQLException;

/**
 *
 * @author User
 */
public class LoginFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginFrame.class.getName());
    
    // 캡챠 관련
    private String currentCaptchaText;
    private UserDAO userDAO;
    
    // 로그인 성공 시 전달할 사용자 정보 (MainFrame 등에서 사용)
    private User loggedInUser;
    
    // 로그인 성공 시 업데이트할 MainFrame 인스턴스
    private MainFrame mainFrame = null;

    /**
     * Creates new form LoginFrame
     */
    public LoginFrame() {
        this(null);
    }
    
    /**
     * Creates new form LoginFrame with MainFrame reference
     * @param mainFrame 로그인 성공 시 업데이트할 MainFrame 인스턴스
     */
    public LoginFrame(MainFrame mainFrame) {
        initComponents();
        
        // 다크 테마 적용
        applyDarkTheme();
        
        // 레이아웃 개선
        improveLayout();
        
        userDAO = new UserDAO();
        initializeCaptcha();
        setupEventHandlers();
        
        // MainFrame 인스턴스 저장
        this.mainFrame = mainFrame;
    }
    
    /**
     * 참고 디자인 기반 디자인 요소를 적용합니다.
     * (색상 제외 - 폰트, 레이아웃, 버튼 스타일만 적용)
     */
    private void applyDarkTheme() {
        // 레이블 폰트만 적용 (색상 제외)
        if (lblTitle != null) {
            lblTitle.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_TITLE));
        }
        
        if (lblUserId != null) {
            lblUserId.setFont(DesignConstants.getDefaultFont());
        }
        
        if (lblPassword != null) {
            lblPassword.setFont(DesignConstants.getDefaultFont());
        }
        
        // 입력 필드 폰트만 적용 (색상 제외)
        if (txtUserID != null) {
            txtUserID.setFont(DesignConstants.getDefaultFont());
        }
        
        if (txtPassword != null) {
            txtPassword.setFont(DesignConstants.getDefaultFont());
        }
        
        if (txtCaptchaInput != null) {
            txtCaptchaInput.setFont(DesignConstants.getDefaultFont());
        }
        
        // 버튼 폰트 및 스타일만 적용 (색상 제외)
        if (btnLogin != null) {
            btnLogin.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
            btnLogin.setBorderPainted(false);
            btnLogin.setFocusPainted(false);
        }
        
        if (btnJoin != null) {
            btnJoin.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
            btnJoin.setBorderPainted(false);
            btnJoin.setFocusPainted(false);
        }
        
        if (btnCaptchaRefresh != null) {
            btnCaptchaRefresh.setFont(DesignConstants.getDefaultFont());
            btnCaptchaRefresh.setBorderPainted(false);
            btnCaptchaRefresh.setFocusPainted(false);
        }
        
        // 체크박스 폰트만 적용 (색상 제외)
        if (chkRememberLogin != null) {
            chkRememberLogin.setFont(DesignConstants.getDefaultFont());
        }
    }
    
    /**
     * 레이아웃을 깔끔하게 개선합니다.
     * 이미지에서 본 것처럼 중앙 정렬된 깔끔한 디자인으로 만듭니다.
     */
    private void improveLayout() {
        // 창 크기 및 중앙 배치
        setSize(450, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        
        // 제목 중앙 정렬
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        
        // 레이블 왼쪽 정렬
        lblUserId.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        
        // 입력 필드 크기 통일 (더 넓게)
        txtUserID.setPreferredSize(new java.awt.Dimension(280, 30));
        txtPassword.setPreferredSize(new java.awt.Dimension(280, 30));
        txtCaptchaInput.setPreferredSize(new java.awt.Dimension(100, 30));
        
        // 패널 여백 추가 (더 넓은 여백)
        pnlLoginContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 50, 40, 50));
        
        // 메인으로 돌아가기 버튼 추가
        addBackToMainButton();
        
        // 전체를 중앙 정렬하기 위한 레이아웃 (initComponents 이후에 적용)
        // 기존 레이아웃을 유지하면서 중앙 정렬만 추가
        java.awt.Component parent = getContentPane();
        if (parent instanceof java.awt.Container) {
            java.awt.LayoutManager currentLayout = ((java.awt.Container) parent).getLayout();
            if (currentLayout != null) {
                // 기존 레이아웃을 유지하면서 중앙 정렬 패널로 감싸기
                javax.swing.JPanel centerPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
                centerPanel.add(pnlLoginContent);
                getContentPane().removeAll();
                getContentPane().setLayout(new java.awt.BorderLayout());
                getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);
            }
        }
        
        pack();
    }
    
    /**
     * 캡챠를 초기화합니다.
     */
    private void initializeCaptcha() {
        currentCaptchaText = CaptchaUtil.generateCaptchaText();
        lblCaptchaImage.setIcon(CaptchaUtil.generateCaptchaImageIcon(currentCaptchaText));
        txtCaptchaInput.setText(""); // 입력 필드 초기화
    }
    
    /**
     * 이벤트 핸들러를 설정합니다.
     */
    private void setupEventHandlers() {
        // 캡챠 새로고침 버튼
        btnCaptchaRefresh.addActionListener(e -> initializeCaptcha());
        
        // 로그인 버튼
        btnLogin.addActionListener(e -> performLogin());
        
        // 회원가입 버튼
        btnJoin.addActionListener(e -> openJoinForm());
        
        // Enter 키로 로그인
        txtPassword.addActionListener(e -> performLogin());
        txtCaptchaInput.addActionListener(e -> performLogin());
        
        // 캡챠 입력 필드 자동 대문자 변환
        setupCaptchaUpperCaseConversion();
    }
    
    /**
     * 캡챠 입력 필드에 자동 대문자 변환 기능을 추가합니다.
     */
    private void setupCaptchaUpperCaseConversion() {
        txtCaptchaInput.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                convertToUpperCase();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                // 삭제 시에는 변환 불필요
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                convertToUpperCase();
            }
            
            private void convertToUpperCase() {
                String text = txtCaptchaInput.getText();
                String upperText = text.toUpperCase();
                
                // 대문자가 아닌 문자가 있으면 대문자로 변환
                if (!text.equals(upperText)) {
                    // DocumentListener를 일시적으로 제거하여 무한 루프 방지
                    txtCaptchaInput.getDocument().removeDocumentListener(this);
                    txtCaptchaInput.setText(upperText);
                    // 커서를 끝으로 이동
                    txtCaptchaInput.setCaretPosition(upperText.length());
                    // DocumentListener 다시 추가
                    txtCaptchaInput.getDocument().addDocumentListener(this);
                }
            }
        });
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
        
        // 버튼 패널에 추가 (기존 버튼들 앞에 추가)
        if (pnlButtons != null) {
            pnlButtons.add(btnBackToMain, 0); // 첫 번째 위치에 추가
        }
    }
    
    /**
     * 메인 화면으로 돌아갑니다.
     */
    private void goBackToMain() {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setLocationRelativeTo(this);
        mainFrame.setVisible(true);
        this.dispose(); // LoginFrame 닫기
    }
    
    /**
     * 로그인을 수행합니다.
     */
    private void performLogin() {
        // 입력값 검증
        String username = txtUserID.getText().trim();
        String password = new String(txtPassword.getPassword());
        String captchaInput = txtCaptchaInput.getText().trim();
        
        // 빈 값 체크
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            txtUserID.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "비밀번호를 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }
        
        if (captchaInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "캡챠를 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            txtCaptchaInput.requestFocus();
            return;
        }
        
        // 로그인 처리
        try {
            loggedInUser = userDAO.loginWithCaptcha(username, password, captchaInput, currentCaptchaText);
            
            if (loggedInUser != null) {
                // 로그인 성공
                JOptionPane.showMessageDialog(this, 
                    "로그인 성공! 환영합니다, " + loggedInUser.getName() + "님.", 
                    "로그인 성공", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // 기존 MainFrame이 있으면 업데이트, 없으면 새로 생성
                if (mainFrame != null) {
                    // 기존 MainFrame 업데이트
                    mainFrame.setLoggedInUser(loggedInUser);
                    this.dispose(); // LoginFrame 닫기
                } else {
                    // MainFrame으로 전환 (새로 생성)
                    openMainFrame();
                    this.dispose(); // LoginFrame 닫기
                }
            } else {
                // 로그인 실패
                JOptionPane.showMessageDialog(this, 
                    "로그인 실패했습니다.\n아이디, 비밀번호 또는 캡챠를 확인하세요.", 
                    "로그인 실패", 
                    JOptionPane.ERROR_MESSAGE);
                
                // 캡챠 새로고침
                initializeCaptcha();
                txtPassword.setText(""); // 비밀번호 필드 초기화
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "로그인 중 데이터베이스 오류 발생", e);
            JOptionPane.showMessageDialog(this, 
                "데이터베이스 연결 오류가 발생했습니다.\n관리자에게 문의하세요.", 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "로그인 중 예기치 않은 오류 발생", e);
            JOptionPane.showMessageDialog(this, 
                "로그인 처리 중 오류가 발생했습니다.\n다시 시도해주세요.", 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 회원가입 화면을 엽니다.
     */
    private void openJoinForm() {
        JoinForm joinForm = new JoinForm();
        joinForm.setLocationRelativeTo(this);
        joinForm.setVisible(true);
        this.dispose(); // LoginFrame 닫기
    }
    
    /**
     * 메인 화면을 엽니다.
     */
    private void openMainFrame() {
        MainFrame mainFrame = new MainFrame();
        // 로그인된 사용자 정보 전달
        if (loggedInUser != null) {
            mainFrame.setLoggedInUser(loggedInUser);
        }
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    /**
     * 로그인된 사용자 정보를 반환합니다.
     * @return 로그인된 User 객체, 로그인하지 않은 경우 null
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlLoginContent = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        lblUserId = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        txtUserID = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        lblPassword = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        txtPassword = new javax.swing.JPasswordField();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        chkRememberLogin = new javax.swing.JCheckBox();
        pnlCaptchaArea = new javax.swing.JPanel();
        lblCaptchaImage = new javax.swing.JLabel();
        txtCaptchaInput = new javax.swing.JTextField();
        btnCaptchaRefresh = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        pnlButtons = new javax.swing.JPanel();
        btnLogin = new javax.swing.JButton();
        btnJoin = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new java.awt.Dimension(400, 450));

        pnlLoginContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 50, 40, 50));
        pnlLoginContent.setLayout(new javax.swing.BoxLayout(pnlLoginContent, javax.swing.BoxLayout.Y_AXIS));

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 1, 28)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("로그인");
        lblTitle.setAlignmentX(0.5F);
        lblTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlLoginContent.add(lblTitle);
        pnlLoginContent.add(filler3);

        lblUserId.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblUserId.setText("아이디");
        lblUserId.setAlignmentX(0.5F);
        lblUserId.setPreferredSize(new java.awt.Dimension(280, 30));
        pnlLoginContent.add(lblUserId);
        pnlLoginContent.add(filler2);

        txtUserID.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtUserID.setPreferredSize(new java.awt.Dimension(250, 30));
        pnlLoginContent.add(txtUserID);
        pnlLoginContent.add(filler1);

        lblPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPassword.setText("비밀번호");
        lblPassword.setAlignmentX(0.5F);
        lblPassword.setPreferredSize(new java.awt.Dimension(280, 30));
        pnlLoginContent.add(lblPassword);
        pnlLoginContent.add(filler4);

        txtPassword.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtPassword.setPreferredSize(new java.awt.Dimension(250, 30));
        pnlLoginContent.add(txtPassword);
        pnlLoginContent.add(filler5);

        chkRememberLogin.setText("로그인 상태 유지");
        chkRememberLogin.setAlignmentX(0.5F);
        pnlLoginContent.add(chkRememberLogin);

        lblCaptchaImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlCaptchaArea.add(lblCaptchaImage);

        txtCaptchaInput.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlCaptchaArea.add(txtCaptchaInput);

        btnCaptchaRefresh.setText("새로고침");
        pnlCaptchaArea.add(btnCaptchaRefresh);

        pnlLoginContent.add(pnlCaptchaArea);
        pnlLoginContent.add(filler6);

        btnLogin.setText("로그인");
        pnlButtons.add(btnLogin);

        btnJoin.setText("회원가입");
        pnlButtons.add(btnJoin);

        pnlLoginContent.add(pnlButtons);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlLoginContent, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlLoginContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

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
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCaptchaRefresh;
    private javax.swing.JButton btnJoin;
    private javax.swing.JButton btnLogin;
    private javax.swing.JCheckBox chkRememberLogin;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.JLabel lblCaptchaImage;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserId;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlCaptchaArea;
    private javax.swing.JPanel pnlLoginContent;
    private javax.swing.JTextField txtCaptchaInput;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUserID;
    // End of variables declaration//GEN-END:variables
}
