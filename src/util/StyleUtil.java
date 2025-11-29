package util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * 참고 디자인(cinema-system) 스타일을 Java Swing 컴포넌트에 적용하는 유틸리티 클래스
 */
public class StyleUtil {
    
    /**
     * 기본 패널 스타일을 적용합니다.
     * 배경색: BG_COLOR (#131313)
     */
    public static void applyPanelStyle(JPanel panel) {
        panel.setBackground(DesignConstants.BG_COLOR);
        panel.setOpaque(true);
    }
    
    /**
     * 콘텐츠 영역 패널 스타일을 적용합니다.
     * 배경색: CONTENT_AREA_COLOR (#242424)
     */
    public static void applyContentPanelStyle(JPanel panel) {
        panel.setBackground(DesignConstants.CONTENT_AREA_COLOR);
        panel.setOpaque(true);
    }
    
    /**
     * 레이블 스타일을 적용합니다.
     */
    public static void applyLabelStyle(JLabel label, boolean isTitle) {
        label.setForeground(DesignConstants.FONT_COLOR);
        if (isTitle) {
            label.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_TITLE));
        } else {
            label.setFont(DesignConstants.getDefaultFont());
        }
    }
    
    /**
     * 텍스트 필드 스타일을 적용합니다.
     */
    public static void applyTextFieldStyle(JTextField textField) {
        textField.setBackground(DesignConstants.CONTENT_AREA_COLOR);
        textField.setForeground(DesignConstants.FONT_COLOR);
        textField.setCaretColor(DesignConstants.FONT_COLOR);
        textField.setSelectionColor(DesignConstants.POINT_COLOR);
        
        // 테두리 설정
        Border lineBorder = new LineBorder(DesignConstants.BORDER_COLOR, DesignConstants.BORDER_WIDTH);
        Border emptyBorder = new EmptyBorder(DesignConstants.PADDING_SMALL, 15, DesignConstants.PADDING_SMALL, 15);
        textField.setBorder(new CompoundBorder(lineBorder, emptyBorder));
        
        textField.setFont(DesignConstants.getDefaultFont());
    }
    
    /**
     * 비밀번호 필드 스타일을 적용합니다.
     */
    public static void applyPasswordFieldStyle(JPasswordField passwordField) {
        passwordField.setBackground(DesignConstants.CONTENT_AREA_COLOR);
        passwordField.setForeground(DesignConstants.FONT_COLOR);
        passwordField.setCaretColor(DesignConstants.FONT_COLOR);
        passwordField.setSelectionColor(DesignConstants.POINT_COLOR);
        
        // 테두리 설정
        Border lineBorder = new LineBorder(DesignConstants.BORDER_COLOR, DesignConstants.BORDER_WIDTH);
        Border emptyBorder = new EmptyBorder(DesignConstants.PADDING_SMALL, 15, DesignConstants.PADDING_SMALL, 15);
        passwordField.setBorder(new CompoundBorder(lineBorder, emptyBorder));
        
        passwordField.setFont(DesignConstants.getDefaultFont());
    }
    
    /**
     * 기본 버튼 스타일을 적용합니다 (회색 배경).
     */
    public static void applyButtonStyle(JButton button, boolean isPrimary) {
        if (isPrimary) {
            // 주요 버튼 (빨간색)
            button.setBackground(DesignConstants.POINT_COLOR);
            button.setForeground(Color.WHITE);
        } else {
            // 보조 버튼 (회색)
            button.setBackground(DesignConstants.CONTENT_AREA_COLOR);
            button.setForeground(DesignConstants.FONT_COLOR);
        }
        
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
        
        // 호버 효과를 위한 마우스 리스너 추가
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isPrimary && button.isEnabled()) {
                    button.setBackground(DesignConstants.POINT_COLOR_HOVER);
                } else if (button.isEnabled()) {
                    button.setBackground(DesignConstants.BORDER_COLOR);
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(DesignConstants.POINT_COLOR);
                } else {
                    button.setBackground(DesignConstants.CONTENT_AREA_COLOR);
                }
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (isPrimary && button.isEnabled()) {
                    button.setBackground(DesignConstants.POINT_COLOR_ACTIVE);
                }
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(DesignConstants.POINT_COLOR);
                }
            }
        });
    }
    
    /**
     * 테이블 스타일을 적용합니다.
     */
    public static void applyTableStyle(JTable table) {
        table.setBackground(DesignConstants.CONTENT_AREA_COLOR);
        table.setForeground(DesignConstants.FONT_COLOR);
        table.setGridColor(DesignConstants.BORDER_COLOR);
        table.setSelectionBackground(DesignConstants.POINT_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(DesignConstants.getDefaultFont());
        table.setRowHeight(35);
        
        // 헤더 스타일
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setBackground(DesignConstants.TITLE_COLOR);
            header.setForeground(DesignConstants.FONT_COLOR);
            header.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
        }
    }
    
    /**
     * 좌석 버튼 스타일을 적용합니다.
     */
    public static void applySeatButtonStyle(JButton button, SeatStatus status) {
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(DesignConstants.getDefaultFont());
        
        switch (status) {
            case AVAILABLE:
                button.setBackground(DesignConstants.SEAT_AVAILABLE);
                button.setForeground(Color.WHITE);
                button.setEnabled(true);
                break;
            case SELECTED:
                button.setBackground(DesignConstants.SEAT_SELECTED);
                button.setForeground(Color.WHITE);
                button.setEnabled(true);
                break;
            case RESERVED:
                button.setBackground(DesignConstants.SEAT_RESERVED);
                button.setForeground(Color.WHITE);
                button.setEnabled(false);
                break;
        }
    }
    
    /**
     * 날짜 버튼 스타일을 적용합니다.
     */
    public static void applyDateButtonStyle(JButton button, boolean isSelected) {
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
        
        if (isSelected) {
            button.setBackground(DesignConstants.POINT_COLOR);
            button.setForeground(Color.WHITE);
            button.setBorder(new LineBorder(DesignConstants.POINT_COLOR, DesignConstants.BORDER_WIDTH));
        } else {
            button.setBackground(DesignConstants.CONTENT_AREA_COLOR);
            button.setForeground(DesignConstants.FONT_COLOR);
            button.setBorder(new LineBorder(DesignConstants.BORDER_COLOR, DesignConstants.BORDER_WIDTH));
        }
    }
    
    /**
     * Screen 레이블 스타일을 적용합니다.
     */
    public static void applyScreenLabelStyle(JLabel label) {
        label.setBackground(DesignConstants.TITLE_COLOR);
        label.setForeground(DesignConstants.FONT_COLOR);
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
        
        // 패딩 추가
        label.setBorder(new EmptyBorder(
            DesignConstants.PADDING_SMALL,
            DesignConstants.PADDING_NORMAL,
            DesignConstants.PADDING_SMALL,
            DesignConstants.PADDING_NORMAL
        ));
    }
    
    /**
     * 프레임 기본 스타일을 적용합니다.
     */
    public static void applyFrameStyle(JFrame frame) {
        frame.getContentPane().setBackground(DesignConstants.BG_COLOR);
        // Look and Feel 설정 (시스템 테마 사용 또는 Nimbus)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // 기본 Look and Feel 사용
        }
    }
    
    /**
     * 좌석 상태 열거형
     */
    public enum SeatStatus {
        AVAILABLE,  // 사용 가능
        SELECTED,   // 선택됨
        RESERVED    // 예약됨
    }
}

