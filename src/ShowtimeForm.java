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

public class ShowtimeForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ShowtimeForm.class.getName());
    
    private String movieTitle = "";
    private LocalDate startDate = LocalDate.now();
    private LocalDate selectedDate = LocalDate.now();
    private List<JButton> dateButtons = new ArrayList<>();
    
    // 더미 상영시간 데이터
    private String[][] dummyShowtimes = {
        {"10:00", "1관", "120석", "일반"},
        {"13:30", "2관", "150석", "일반"},
        {"16:00", "3관", "100석", "일반"},
        {"19:00", "1관", "120석", "일반"},
        {"21:30", "2관", "150석", "일반"}
    };

    /**
     * Creates new form ShowtimeForm
     */
    public ShowtimeForm() {
        initComponents();
        initializeComponents();
    }
    
    public void setMovieTitle(String title) {
        this.movieTitle = title;
        if (lblSelectedMovie != null) {
            lblSelectedMovie.setText("영화: " + title);
        }
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
    }
    
    private void updateDateButtons() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd (E)");
        for (int i = 0; i < dateButtons.size(); i++) {
            LocalDate date = startDate.plusDays(i);
            JButton btn = dateButtons.get(i);
            btn.setText(date.format(formatter));
            
            if (date.equals(selectedDate)) {
                btn.setBackground(new Color(200, 220, 255));
                btn.setForeground(Color.BLUE);
            } else {
                btn.setBackground(null);
                btn.setForeground(Color.BLACK);
            }
        }
        
        // 선택된 날짜의 상영시간표 업데이트
        updateShowtimeTable();
    }
    
    private void selectDate(LocalDate date) {
        selectedDate = date;
        updateDateButtons();
    }
    
    private void initializeShowtimeTable() {
        String[] columnNames = {"상영시간", "상영관", "잔여석", "등급"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblShowtimes.setModel(model);
        tblShowtimes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblShowtimes.setRowHeight(30);
        
        // 테이블 클릭 이벤트 - 좌석 선택 화면으로 이동
        tblShowtimes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = tblShowtimes.getSelectedRow();
                    if (selectedRow >= 0) {
                        String showtime = (String) tblShowtimes.getValueAt(selectedRow, 0);
                        String theater = (String) tblShowtimes.getValueAt(selectedRow, 1);
                        
                        int confirm = JOptionPane.showConfirmDialog(
                            ShowtimeForm.this,
                            "상영시간: " + showtime + "\n상영관: " + theater + "\n\n좌석 선택 화면으로 이동하시겠습니까?",
                            "좌석 선택",
                            JOptionPane.YES_NO_OPTION
                        );
                        
                        if (confirm == JOptionPane.YES_OPTION) {
                            SeatSelectionForm seatForm = new SeatSelectionForm();
                            seatForm.setVisible(true);
                            dispose();
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
        
        for (String[] showtime : dummyShowtimes) {
            model.addRow(showtime);
        }
    }
    
    private void updateStepDisplay() {
        // 현재 단계 하이라이트 (2단계: 날짜 선택)
        lblStep2.setForeground(Color.BLUE);
        lblStep2.setFont(lblStep2.getFont().deriveFont(Font.BOLD));
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

        lblShowtimeHeader.setText("시간");
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
