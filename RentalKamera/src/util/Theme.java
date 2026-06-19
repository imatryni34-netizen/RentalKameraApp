package util;

import java.awt.*;

public class Theme {
    // Colors matching Figma design
    public static final Color NAVBAR_BG    = new Color(0x2C3E50);
    public static final Color SIDEBAR_BG   = new Color(0xF7F8FA);
    public static final Color SIDEBAR_SEL  = new Color(0xFFFFFF);
    public static final Color CONTENT_BG   = new Color(0xF0F2F5);
    public static final Color WHITE        = Color.WHITE;
    public static final Color PRIMARY      = new Color(0x2C3E50);
    public static final Color ACCENT       = new Color(0xE67E22);
    public static final Color TEXT_DARK    = new Color(0x2C3E50);
    public static final Color TEXT_GRAY    = new Color(0x7F8C8D);
    public static final Color BORDER       = new Color(0xE0E4E8);
    public static final Color SUCCESS      = new Color(0x27AE60);
    public static final Color WARNING      = new Color(0xF39C12);
    public static final Color DANGER       = new Color(0xE74C3C);

    // Fonts
    public static Font fontBold(int size)   { return new Font("Segoe UI", Font.BOLD, size); }
    public static Font fontPlain(int size)  { return new Font("Segoe UI", Font.PLAIN, size); }
    public static Font fontSemiBold(int size){ return new Font("Segoe UI", Font.BOLD, size); }
}
