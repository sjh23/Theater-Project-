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
import java.util.regex.Pattern;

/**
 *
 * @author User
 */
public class JoinForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JoinForm.class.getName());
    
    // 캡챠 관련
    private String currentCaptchaText;
    private UserDAO userDAO;
    
    // 이메일 형식 검증용 패턴
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Creates new form JoinForm
     */
    public JoinForm() {
        initComponents();
        
        // 참고 디자인 기반 디자인 요소 적용 (색상 제외)
        applyDarkTheme();
        
        // 레이아웃 개선 (로그인 화면과 동일한 방식)
        improveLayout();
        
        userDAO = new UserDAO();
        initializeCaptcha();
        setupEventHandlers();
    }
    
    /**
     * 참고 디자인 기반 디자인 요소를 적용합니다.
     * (색상 제외 - 레이아웃, 간격, 폰트, 버튼 스타일만 적용)
     */
    private void applyDarkTheme() {
        // 레이블 폰트 스타일만 적용 (색상 제외)
        if (lblTitle != null) {
            lblTitle.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_TITLE));
        }
        
        if (lblUserId != null) {
            lblUserId.setFont(DesignConstants.getDefaultFont());
        }
        
        if (lblPassword != null) {
            lblPassword.setFont(DesignConstants.getDefaultFont());
        }
        
        if (lblConfirmPassword != null) {
            lblConfirmPassword.setFont(DesignConstants.getDefaultFont());
        }
        
        if (lblFullName != null) {
            lblFullName.setFont(DesignConstants.getDefaultFont());
        }
        
        if (lblEmail != null) {
            lblEmail.setFont(DesignConstants.getDefaultFont());
        }
        
        // 입력 필드 폰트만 적용 (색상 제외)
        if (txtUserID != null) {
            txtUserID.setFont(DesignConstants.getDefaultFont());
        }
        
        if (txtPassword != null) {
            txtPassword.setFont(DesignConstants.getDefaultFont());
        }
        
        if (txtConfirmPassword != null) {
            txtConfirmPassword.setFont(DesignConstants.getDefaultFont());
        }
        
        if (txtFullName != null) {
            txtFullName.setFont(DesignConstants.getDefaultFont());
        }
        
        if (txtEmail != null) {
            txtEmail.setFont(DesignConstants.getDefaultFont());
        }
        
        if (txtCaptchaInput != null) {
            txtCaptchaInput.setFont(DesignConstants.getDefaultFont());
        }
        
        // 버튼 폰트 및 스타일만 적용 (색상 제외)
        if (btnSubmit != null) {
            btnSubmit.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
            btnSubmit.setBorderPainted(false);
            btnSubmit.setFocusPainted(false);
        }
        
        if (btnBack != null) {
            btnBack.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
            btnBack.setBorderPainted(false);
            btnBack.setFocusPainted(false);
        }
        
        if (btnCaptchaRefresh != null) {
            btnCaptchaRefresh.setFont(DesignConstants.getDefaultFont());
            btnCaptchaRefresh.setBorderPainted(false);
            btnCaptchaRefresh.setFocusPainted(false);
        }
    }
    
    /**
     * 레이아웃을 깔끔하게 개선합니다.
     * 로그인 화면과 동일한 방식으로 레이아웃을 정리합니다.
     */
    private void improveLayout() {
        // 창 크기 및 중앙 배치
        setSize(450, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        
        // 제목 중앙 정렬
        if (lblTitle != null) {
            lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }
        
        // 레이블 왼쪽 정렬
        if (lblUserId != null) {
            lblUserId.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }
        if (lblPassword != null) {
            lblPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }
        if (lblConfirmPassword != null) {
            lblConfirmPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }
        if (lblFullName != null) {
            lblFullName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }
        if (lblEmail != null) {
            lblEmail.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }
        
        // 입력 필드 크기 통일 (로그인 화면과 동일)
        if (txtUserID != null) {
            txtUserID.setPreferredSize(new java.awt.Dimension(280, 30));
        }
        if (txtPassword != null) {
            txtPassword.setPreferredSize(new java.awt.Dimension(280, 30));
        }
        if (txtConfirmPassword != null) {
            txtConfirmPassword.setPreferredSize(new java.awt.Dimension(280, 30));
        }
        if (txtFullName != null) {
            txtFullName.setPreferredSize(new java.awt.Dimension(280, 30));
        }
        if (txtEmail != null) {
            txtEmail.setPreferredSize(new java.awt.Dimension(280, 30));
        }
        if (txtCaptchaInput != null) {
            txtCaptchaInput.setPreferredSize(new java.awt.Dimension(100, 30));
        }
        
        // 패널 여백 추가 (로그인 화면과 동일)
        if (pnlJoinContent != null) {
            pnlJoinContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 50, 40, 50));
        }
        
        // 전체를 중앙 정렬하기 위한 레이아웃 (로그인 화면과 동일)
        java.awt.Component parent = getContentPane();
        if (parent instanceof java.awt.Container) {
            java.awt.LayoutManager currentLayout = ((java.awt.Container) parent).getLayout();
            if (currentLayout != null) {
                // 기존 레이아웃을 유지하면서 중앙 정렬 패널로 감싸기
                javax.swing.JPanel centerPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
                centerPanel.add(pnlJoinContent);
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
        
        // 회원가입 버튼
        btnSubmit.addActionListener(e -> performJoin());
        
        // 로그인으로 돌아가기 버튼
        btnBack.addActionListener(e -> goBackToLogin());
        
        // Enter 키로 회원가입 (비밀번호 확인 필드 또는 캡챠 필드에서)
        txtConfirmPassword.addActionListener(e -> performJoin());
        txtCaptchaInput.addActionListener(e -> performJoin());
        
        // 메인으로 돌아가기 버튼 추가
        addBackToMainButton();
        
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
        
        // 버튼 패널에 추가
        if (pnlButtons != null) {
            pnlButtons.add(btnBackToMain);
        }
    }
    
    /**
     * 메인 화면으로 돌아갑니다.
     */
    private void goBackToMain() {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setLocationRelativeTo(this);
        mainFrame.setVisible(true);
        this.dispose(); // JoinForm 닫기
    }
    
    /**
     * 회원가입을 수행합니다.
     */
    private void performJoin() {
        // 입력값 가져오기
        String username = txtUserID.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String name = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String captchaInput = txtCaptchaInput.getText().trim();
        
        // 입력값 검증
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
        
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "비밀번호는 최소 4자 이상이어야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            txtConfirmPassword.requestFocus();
            txtConfirmPassword.setText("");
            return;
        }
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름을 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            txtFullName.requestFocus();
            return;
        }
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "이메일을 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        
        // 이메일 형식 검증
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "올바른 이메일 형식을 입력하세요.\n예: user@example.com", "입력 오류", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        
        if (captchaInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "캡챠를 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            txtCaptchaInput.requestFocus();
            return;
        }
        
        // 회원가입 처리
        try {
            // 중복 아이디 확인
            User existingUser = userDAO.getUserByUsername(username);
            if (existingUser != null) {
                JOptionPane.showMessageDialog(this, 
                    "이미 사용 중인 아이디입니다.\n다른 아이디를 선택해주세요.", 
                    "회원가입 실패", 
                    JOptionPane.WARNING_MESSAGE);
                txtUserID.requestFocus();
                txtUserID.selectAll();
                return;
            }
            
            // User 객체 생성
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password); // UserDAO에서 자동으로 해시 처리됨
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setRole("Customer"); // 기본 역할
            
            // 회원가입 처리
            boolean success = userDAO.insertUserWithCaptcha(newUser, captchaInput, currentCaptchaText);
            
            if (success) {
                // 회원가입 성공
                JOptionPane.showMessageDialog(this, 
                    "회원가입이 완료되었습니다!\n로그인 화면으로 이동합니다.", 
                    "회원가입 성공", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // 로그인 화면으로 돌아가기
                goBackToLogin();
            } else {
                // 회원가입 실패 (캡챠 오류 등)
                JOptionPane.showMessageDialog(this, 
                    "회원가입 실패했습니다.\n캡챠를 확인하거나 다시 시도해주세요.", 
                    "회원가입 실패", 
                    JOptionPane.ERROR_MESSAGE);
                
                // 캡챠 새로고침
                initializeCaptcha();
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "회원가입 중 데이터베이스 오류 발생", e);
            JOptionPane.showMessageDialog(this, 
                "데이터베이스 연결 오류가 발생했습니다.\n관리자에게 문의하세요.", 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "회원가입 중 예기치 않은 오류 발생", e);
            JOptionPane.showMessageDialog(this, 
                "회원가입 처리 중 오류가 발생했습니다.\n다시 시도해주세요.", 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 로그인 화면으로 돌아갑니다.
     */
    private void goBackToLogin() {
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setLocationRelativeTo(this);
        loginFrame.setVisible(true);
        this.dispose(); // JoinForm 닫기
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlJoinContent = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        lblUserId = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        txtUserID = new javax.swing.JTextField();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        lblPassword = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        txtPassword = new javax.swing.JPasswordField();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        lblConfirmPassword = new javax.swing.JLabel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        txtConfirmPassword = new javax.swing.JPasswordField();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        lblFullName = new javax.swing.JLabel();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        txtFullName = new javax.swing.JTextField();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        lblEmail = new javax.swing.JLabel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        txtEmail = new javax.swing.JTextField();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        pnlCaptchaArea = new javax.swing.JPanel();
        lblCaptchaImage = new javax.swing.JLabel();
        txtCaptchaInput = new javax.swing.JTextField();
        btnCaptchaRefresh = new javax.swing.JButton();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        pnlButtons = new javax.swing.JPanel();
        btnSubmit = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlJoinContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 50, 40, 50));
        pnlJoinContent.setLayout(new javax.swing.BoxLayout(pnlJoinContent, javax.swing.BoxLayout.Y_AXIS));

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 1, 28)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("회원가입");
        lblTitle.setAlignmentX(0.5F);
        pnlJoinContent.add(lblTitle);
        pnlJoinContent.add(filler1);

        lblUserId.setText("아이디");
        lblUserId.setAlignmentX(0.5F);
        pnlJoinContent.add(lblUserId);
        pnlJoinContent.add(filler2);
        pnlJoinContent.add(txtUserID);
        pnlJoinContent.add(filler3);

        lblPassword.setText("비밀번호");
        lblPassword.setAlignmentX(0.5F);
        pnlJoinContent.add(lblPassword);
        pnlJoinContent.add(filler4);
        pnlJoinContent.add(txtPassword);
        pnlJoinContent.add(filler5);

        lblConfirmPassword.setText("비밀번호 확인");
        lblConfirmPassword.setAlignmentX(0.5F);
        pnlJoinContent.add(lblConfirmPassword);
        pnlJoinContent.add(filler6);
        pnlJoinContent.add(txtConfirmPassword);
        pnlJoinContent.add(filler7);

        lblFullName.setText("이름");
        lblFullName.setAlignmentX(0.5F);
        pnlJoinContent.add(lblFullName);
        pnlJoinContent.add(filler8);
        pnlJoinContent.add(txtFullName);
        pnlJoinContent.add(filler9);

        lblEmail.setText("이메일");
        lblEmail.setAlignmentX(0.5F);
        pnlJoinContent.add(lblEmail);
        pnlJoinContent.add(filler10);
        pnlJoinContent.add(txtEmail);
        pnlJoinContent.add(filler11);

        lblCaptchaImage.setAlignmentX(0.5F);
        lblCaptchaImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlCaptchaArea.add(lblCaptchaImage);
        pnlCaptchaArea.add(txtCaptchaInput);

        btnCaptchaRefresh.setText("새로고침");
        btnCaptchaRefresh.setAlignmentX(0.5F);
        pnlCaptchaArea.add(btnCaptchaRefresh);

        pnlJoinContent.add(pnlCaptchaArea);
        pnlJoinContent.add(filler12);

        btnSubmit.setText("회원가입");
        btnSubmit.setAlignmentX(0.5F);
        pnlButtons.add(btnSubmit);

        btnBack.setText("로그인으로 돌아가기");
        btnBack.setAlignmentX(0.5F);
        pnlButtons.add(btnBack);

        pnlJoinContent.add(pnlButtons);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addComponent(pnlJoinContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlJoinContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 48, Short.MAX_VALUE))
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
        java.awt.EventQueue.invokeLater(() -> new JoinForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCaptchaRefresh;
    private javax.swing.JButton btnSubmit;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel lblCaptchaImage;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserId;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlCaptchaArea;
    private javax.swing.JPanel pnlJoinContent;
    private javax.swing.JTextField txtCaptchaInput;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUserID;
    // End of variables declaration//GEN-END:variables
}
