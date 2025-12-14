import javax.swing.*;
import dao.UserDAO;
import model.User;
import util.CaptchaUtil;
import util.DesignConstants;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginFrame.class.getName());

    private String currentCaptchaText;
    private UserDAO userDAO;

    private User loggedInUser;

    private MainFrame mainFrame = null;

    public LoginFrame() {
        this(null);
    }

    public LoginFrame(MainFrame mainFrame) {
        initComponents();

        applyDarkTheme();

        improveLayout();
        
        userDAO = new UserDAO();
        initializeCaptcha();
        setupEventHandlers();

        this.mainFrame = mainFrame;
    }

    private void applyDarkTheme() {

        if (lblTitle != null) {
            lblTitle.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_TITLE));
        }
        
        if (lblUserId != null) {
            lblUserId.setFont(DesignConstants.getDefaultFont());
        }
        
        if (lblPassword != null) {
            lblPassword.setFont(DesignConstants.getDefaultFont());
        }

        if (txtUserID != null) {
            txtUserID.setFont(DesignConstants.getDefaultFont());
        }
        
        if (txtPassword != null) {
            txtPassword.setFont(DesignConstants.getDefaultFont());
        }
        
        if (txtCaptchaInput != null) {
            txtCaptchaInput.setFont(DesignConstants.getDefaultFont());
        }

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

        if (chkRememberLogin != null) {
            chkRememberLogin.setFont(DesignConstants.getDefaultFont());
        }
    }

    private void improveLayout() {

        setSize(450, 500);
        setResizable(false);
        setLocationRelativeTo(null);

        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        lblUserId.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        txtUserID.setPreferredSize(new java.awt.Dimension(280, 30));
        txtPassword.setPreferredSize(new java.awt.Dimension(280, 30));
        txtCaptchaInput.setPreferredSize(new java.awt.Dimension(100, 30));

        pnlLoginContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 50, 40, 50));

        addBackToMainButton();

        java.awt.Component parent = getContentPane();
        if (parent instanceof java.awt.Container) {
            java.awt.LayoutManager currentLayout = ((java.awt.Container) parent).getLayout();
            if (currentLayout != null) {

                javax.swing.JPanel centerPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
                centerPanel.add(pnlLoginContent);
                getContentPane().removeAll();
                getContentPane().setLayout(new java.awt.BorderLayout());
                getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);
            }
        }
        
        pack();
    }

    private void initializeCaptcha() {
        currentCaptchaText = CaptchaUtil.generateCaptchaText();
        lblCaptchaImage.setIcon(CaptchaUtil.generateCaptchaImageIcon(currentCaptchaText));
        txtCaptchaInput.setText("");
    }

    private void setupEventHandlers() {

        btnCaptchaRefresh.addActionListener(e -> initializeCaptcha());

        btnLogin.addActionListener(e -> performLogin());

        btnJoin.addActionListener(e -> openJoinForm());

        txtPassword.addActionListener(e -> performLogin());
        txtCaptchaInput.addActionListener(e -> performLogin());

        setupCaptchaUpperCaseConversion();
    }

    private void setupCaptchaUpperCaseConversion() {
        txtCaptchaInput.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                convertToUpperCase();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {

            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                convertToUpperCase();
            }
            
            private void convertToUpperCase() {
                String text = txtCaptchaInput.getText();
                String upperText = text.toUpperCase();

                if (!text.equals(upperText)) {

                    txtCaptchaInput.getDocument().removeDocumentListener(this);
                    txtCaptchaInput.setText(upperText);

                    txtCaptchaInput.setCaretPosition(upperText.length());

                    txtCaptchaInput.getDocument().addDocumentListener(this);
                }
            }
        });
    }

    private void addBackToMainButton() {
        javax.swing.JButton btnBackToMain = new javax.swing.JButton("메인으로");
        btnBackToMain.setFont(DesignConstants.getDefaultFont());
        btnBackToMain.setBorderPainted(false);
        btnBackToMain.setFocusPainted(false);
        btnBackToMain.addActionListener(e -> goBackToMain());

        if (pnlButtons != null) {
            pnlButtons.add(btnBackToMain, 0);
        }
    }

    private void goBackToMain() {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setLocationRelativeTo(this);
        mainFrame.setVisible(true);
        this.dispose();
    }

    private void performLogin() {

        String username = txtUserID.getText().trim();
        String password = new String(txtPassword.getPassword());
        String captchaInput = txtCaptchaInput.getText().trim();

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

        try {
            loggedInUser = userDAO.loginWithCaptcha(username, password, captchaInput, currentCaptchaText);
            
            if (loggedInUser != null) {

                JOptionPane.showMessageDialog(this, 
                    "로그인 성공! 환영합니다, " + loggedInUser.getName() + "님.", 
                    "로그인 성공", 
                    JOptionPane.INFORMATION_MESSAGE);

                if (mainFrame != null) {

                    mainFrame.setLoggedInUser(loggedInUser);
                    this.dispose();
                } else {

                    openMainFrame();
                    this.dispose();
                }
            } else {

                JOptionPane.showMessageDialog(this, 
                    "로그인 실패했습니다.\n아이디, 비밀번호 또는 캡챠를 확인하세요.", 
                    "로그인 실패", 
                    JOptionPane.ERROR_MESSAGE);

                initializeCaptcha();
                txtPassword.setText("");
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

    private void openJoinForm() {
        JoinForm joinForm = new JoinForm();
        joinForm.setLocationRelativeTo(this);
        joinForm.setVisible(true);
        this.dispose();
    }

    private void openMainFrame() {
        MainFrame mainFrame = new MainFrame();

        if (loggedInUser != null) {
            mainFrame.setLoggedInUser(loggedInUser);
        }
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    @SuppressWarnings("unchecked")

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

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 1, 28));
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

        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

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

}
