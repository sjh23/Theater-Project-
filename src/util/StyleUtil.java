package util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class StyleUtil {

    public static void applyPanelStyle(JPanel panel) {
        panel.setBackground(DesignConstants.BG_COLOR);
        panel.setOpaque(true);
    }

    public static void applyContentPanelStyle(JPanel panel) {
        panel.setBackground(DesignConstants.CONTENT_AREA_COLOR);
        panel.setOpaque(true);
    }

    public static void applyLabelStyle(JLabel label, boolean isTitle) {
        label.setForeground(DesignConstants.FONT_COLOR);
        if (isTitle) {
            label.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_TITLE));
        } else {
            label.setFont(DesignConstants.getDefaultFont());
        }
    }

    public static void applyTextFieldStyle(JTextField textField) {
        textField.setBackground(DesignConstants.CONTENT_AREA_COLOR);
        textField.setForeground(DesignConstants.FONT_COLOR);
        textField.setCaretColor(DesignConstants.FONT_COLOR);
        textField.setSelectionColor(DesignConstants.POINT_COLOR);

        Border lineBorder = new LineBorder(DesignConstants.BORDER_COLOR, DesignConstants.BORDER_WIDTH);
        Border emptyBorder = new EmptyBorder(DesignConstants.PADDING_SMALL, 15, DesignConstants.PADDING_SMALL, 15);
        textField.setBorder(new CompoundBorder(lineBorder, emptyBorder));
        
        textField.setFont(DesignConstants.getDefaultFont());
    }

    public static void applyPasswordFieldStyle(JPasswordField passwordField) {
        passwordField.setBackground(DesignConstants.CONTENT_AREA_COLOR);
        passwordField.setForeground(DesignConstants.FONT_COLOR);
        passwordField.setCaretColor(DesignConstants.FONT_COLOR);
        passwordField.setSelectionColor(DesignConstants.POINT_COLOR);

        Border lineBorder = new LineBorder(DesignConstants.BORDER_COLOR, DesignConstants.BORDER_WIDTH);
        Border emptyBorder = new EmptyBorder(DesignConstants.PADDING_SMALL, 15, DesignConstants.PADDING_SMALL, 15);
        passwordField.setBorder(new CompoundBorder(lineBorder, emptyBorder));
        
        passwordField.setFont(DesignConstants.getDefaultFont());
    }

    public static void applyButtonStyle(JButton button, boolean isPrimary) {
        if (isPrimary) {

            button.setBackground(DesignConstants.POINT_COLOR);
            button.setForeground(Color.WHITE);
        } else {

            button.setBackground(DesignConstants.CONTENT_AREA_COLOR);
            button.setForeground(DesignConstants.FONT_COLOR);
        }
        
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));

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

    public static void applyTableStyle(JTable table) {
        table.setBackground(DesignConstants.CONTENT_AREA_COLOR);
        table.setForeground(DesignConstants.FONT_COLOR);
        table.setGridColor(DesignConstants.BORDER_COLOR);
        table.setSelectionBackground(DesignConstants.POINT_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(DesignConstants.getDefaultFont());
        table.setRowHeight(35);

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setBackground(DesignConstants.TITLE_COLOR);
            header.setForeground(DesignConstants.FONT_COLOR);
            header.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));
        }
    }

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

    public static void applyScreenLabelStyle(JLabel label) {
        label.setBackground(DesignConstants.TITLE_COLOR);
        label.setForeground(DesignConstants.FONT_COLOR);
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(DesignConstants.getBoldFont(DesignConstants.FONT_SIZE_NORMAL));

        label.setBorder(new EmptyBorder(
            DesignConstants.PADDING_SMALL,
            DesignConstants.PADDING_NORMAL,
            DesignConstants.PADDING_SMALL,
            DesignConstants.PADDING_NORMAL
        ));
    }

    public static void applyFrameStyle(JFrame frame) {
        frame.getContentPane().setBackground(DesignConstants.BG_COLOR);

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {

        }
    }

    public enum SeatStatus {
        AVAILABLE,
        SELECTED,
        RESERVED
    }
}

