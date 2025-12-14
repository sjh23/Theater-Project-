package util;

import java.awt.Color;
import java.awt.Font;

public class DesignConstants {

    public static final Color BG_COLOR = new Color(19, 19, 19);

    public static final Color CONTENT_AREA_COLOR = new Color(36, 36, 36);

    public static final Color SCREEN_COLOR = new Color(22, 22, 22);

    public static final Color BORDER_COLOR = new Color(64, 64, 64);

    public static final Color FONT_COLOR = new Color(222, 222, 222);

    public static final Color POINT_COLOR = new Color(255, 0, 0);

    public static final Color TITLE_COLOR = new Color(45, 45, 45);

    public static final Color SEAT_AVAILABLE = new Color(76, 175, 80);

    public static final Color SEAT_SELECTED = new Color(33, 150, 243);

    public static final Color SEAT_RESERVED = POINT_COLOR;

    public static final Color POINT_COLOR_HOVER = new Color(204, 0, 0);

    public static final Color POINT_COLOR_ACTIVE = new Color(153, 0, 0);

    public static final Color OVERLAY_BG = new Color(19, 19, 19, 200);

    public static final Color BORDER_COLOR_TRANSPARENT = new Color(64, 64, 64, 128);

    public static final Color GRADIENT_START = new Color(19, 19, 19, 200);

    public static final Color GRADIENT_END = new Color(19, 19, 19, 0);

    public static final String DEFAULT_FONT_NAME = "나눔스퀘어 네오";

    public static final String[] FONT_FALLBACK = {
        "나눔스퀘어 네오",
        "맑은 고딕",
        Font.SANS_SERIF
    };

    public static final int FONT_SIZE_SMALL = 12;

    public static final int FONT_SIZE_NORMAL = 14;

    public static final int FONT_SIZE_LARGE = 18;

    public static final int FONT_SIZE_TITLE = 24;

    public static final int FONT_SIZE_BIG_TITLE = 30;

    public static final int PADDING_SMALL = 10;

    public static final int PADDING_NORMAL = 20;

    public static final int PADDING_LARGE = 40;

    public static final int PADDING_XLARGE = 50;

    public static final int BORDER_WIDTH = 1;

    public static final int BORDER_WIDTH_THICK = 2;

    public static final int CORNER_RADIUS_SMALL = 5;

    public static final int CORNER_RADIUS_NORMAL = 10;

    public static final int CORNER_RADIUS_LARGE = 15;

    public static String getAvailableFont() {
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        
        for (String fontName : FONT_FALLBACK) {
            for (String available : availableFonts) {
                if (available.contains(fontName) || fontName.equals(available)) {
                    return available;
                }
            }
        }
        
        return Font.SANS_SERIF;
    }

    public static java.awt.Font createFont(int style, int size) {
        return new java.awt.Font(getAvailableFont(), style, size);
    }

    public static java.awt.Font getDefaultFont() {
        return createFont(java.awt.Font.PLAIN, FONT_SIZE_NORMAL);
    }

    public static java.awt.Font getBoldFont(int size) {
        return createFont(java.awt.Font.BOLD, size);
    }
}

