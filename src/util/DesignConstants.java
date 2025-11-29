package util;

import java.awt.Color;
import java.awt.Font;

/**
 * 참고 디자인(cinema-system)의 색상 테마를 정의한 상수 클래스
 * 다크 테마 기반의 모던한 디자인 시스템
 */
public class DesignConstants {
    
    // 배경색
    /** 메인 배경색 (어두운 검정) #131313 */
    public static final Color BG_COLOR = new Color(19, 19, 19);
    
    /** 콘텐츠 영역 배경색 #242424 */
    public static final Color CONTENT_AREA_COLOR = new Color(36, 36, 36);
    
    /** 스크린/전체 영역 배경색 #161616 */
    public static final Color SCREEN_COLOR = new Color(22, 22, 22);
    
    // 테두리
    /** 테두리 색상 #404040 */
    public static final Color BORDER_COLOR = new Color(64, 64, 64);
    
    // 텍스트
    /** 기본 텍스트 색상 (밝은 회색) #DEDEDE */
    public static final Color FONT_COLOR = new Color(222, 222, 222);
    
    // 강조색
    /** 포인트 컬러 (빨간색) #FF0000 */
    public static final Color POINT_COLOR = new Color(255, 0, 0);
    
    // 타이틀/헤더
    /** 타이틀 배경색 #2D2D2D */
    public static final Color TITLE_COLOR = new Color(45, 45, 45);
    
    // 좌석 색상
    /** 사용 가능한 좌석 색상 (초록색 계열) */
    public static final Color SEAT_AVAILABLE = new Color(76, 175, 80);
    
    /** 선택된 좌석 색상 (파란색) */
    public static final Color SEAT_SELECTED = new Color(33, 150, 243);
    
    /** 예약된 좌석 색상 (빨간색) */
    public static final Color SEAT_RESERVED = POINT_COLOR;
    
    // 버튼 호버 효과를 위한 색상
    /** 포인트 컬러 호버 (약간 어두운 빨간색) */
    public static final Color POINT_COLOR_HOVER = new Color(204, 0, 0);
    
    /** 포인트 컬러 활성화 (더 어두운 빨간색) */
    public static final Color POINT_COLOR_ACTIVE = new Color(153, 0, 0);
    
    // 투명도가 있는 색상
    /** 반투명 배경 (오버레이용) */
    public static final Color OVERLAY_BG = new Color(19, 19, 19, 200);
    
    /** 반투명 테두리 */
    public static final Color BORDER_COLOR_TRANSPARENT = new Color(64, 64, 64, 128);
    
    // 그라데이션용 색상
    /** 그라데이션 시작 색상 */
    public static final Color GRADIENT_START = new Color(19, 19, 19, 200);
    
    /** 그라데이션 끝 색상 */
    public static final Color GRADIENT_END = new Color(19, 19, 19, 0);
    
    // 폰트 설정
    /** 기본 폰트 이름 */
    public static final String DEFAULT_FONT_NAME = "나눔스퀘어 네오";
    
    /** 폰트 대체 옵션 (나눔스퀘어 네오가 없을 경우) */
    public static final String[] FONT_FALLBACK = {
        "나눔스퀘어 네오",
        "맑은 고딕",
        Font.SANS_SERIF
    };
    
    // 폰트 크기
    /** 작은 폰트 크기 */
    public static final int FONT_SIZE_SMALL = 12;
    
    /** 기본 폰트 크기 */
    public static final int FONT_SIZE_NORMAL = 14;
    
    /** 큰 폰트 크기 */
    public static final int FONT_SIZE_LARGE = 18;
    
    /** 제목 폰트 크기 */
    public static final int FONT_SIZE_TITLE = 24;
    
    /** 큰 제목 폰트 크기 */
    public static final int FONT_SIZE_BIG_TITLE = 30;
    
    // 간격 및 여백
    /** 작은 여백 */
    public static final int PADDING_SMALL = 10;
    
    /** 기본 여백 */
    public static final int PADDING_NORMAL = 20;
    
    /** 큰 여백 */
    public static final int PADDING_LARGE = 40;
    
    /** 매우 큰 여백 */
    public static final int PADDING_XLARGE = 50;
    
    // 테두리
    /** 기본 테두리 두께 */
    public static final int BORDER_WIDTH = 1;
    
    /** 두꺼운 테두리 두께 */
    public static final int BORDER_WIDTH_THICK = 2;
    
    // 둥근 모서리
    /** 작은 둥근 모서리 */
    public static final int CORNER_RADIUS_SMALL = 5;
    
    /** 기본 둥근 모서리 */
    public static final int CORNER_RADIUS_NORMAL = 10;
    
    /** 큰 둥근 모서리 */
    public static final int CORNER_RADIUS_LARGE = 15;
    
    /**
     * 사용 가능한 폰트를 반환합니다.
     * 나눔스퀘어 네오가 없으면 시스템 기본 폰트를 반환합니다.
     */
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
    
    /**
     * 폰트를 생성합니다.
     */
    public static java.awt.Font createFont(int style, int size) {
        return new java.awt.Font(getAvailableFont(), style, size);
    }
    
    /**
     * 기본 폰트를 반환합니다.
     */
    public static java.awt.Font getDefaultFont() {
        return createFont(java.awt.Font.PLAIN, FONT_SIZE_NORMAL);
    }
    
    /**
     * 볼드 폰트를 반환합니다.
     */
    public static java.awt.Font getBoldFont(int size) {
        return createFont(java.awt.Font.BOLD, size);
    }
}

