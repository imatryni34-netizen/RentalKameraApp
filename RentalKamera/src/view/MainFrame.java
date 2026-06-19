package view;

import model.User;
import util.Theme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton[] sidebarButtons;

    // Panel pages
    private DashboardPanel dashboardPanel;
    private KameraPanel kameraPanel;
    private PenyewaPanel penyewaPanel;
    private TransaksiPanel transaksiPanel;
    private LaporanPanel laporanPanel;
    private UserPanel userPanel;

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("Rental Kamera - " + user.getRole().toUpperCase());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());

        // === NAVBAR ===
        JPanel navbar = createNavbar();
        root.add(navbar, BorderLayout.NORTH);

        // === SIDEBAR ===
        JPanel sidebar = createSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // === CONTENT ===
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Theme.CONTENT_BG);

        dashboardPanel  = new DashboardPanel(currentUser);
        kameraPanel     = new KameraPanel(currentUser.getRole());
        penyewaPanel    = new PenyewaPanel(currentUser.getRole());
        transaksiPanel  = new TransaksiPanel(currentUser);
        laporanPanel    = new LaporanPanel();
        userPanel       = new UserPanel();

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(kameraPanel,    "kamera");
        contentPanel.add(penyewaPanel,   "penyewa");
        contentPanel.add(transaksiPanel, "transaksi");
        contentPanel.add(laporanPanel,   "laporan");
        if (currentUser.getRole().equals("admin")) {
            contentPanel.add(userPanel, "user");
        }

        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);

        showPage("dashboard", 0);
    }

    private JPanel createNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(Theme.NAVBAR_BG);
        navbar.setPreferredSize(new Dimension(0, 50));
        navbar.setBorder(new EmptyBorder(0, 20, 0, 20));

        // === BRAND DENGAN LOGO CUSTOM ===
        JLabel brand = new JLabel(" Rental Kamera");
        brand.setForeground(Color.WHITE);
        brand.setFont(Theme.fontBold(18));
        // Memasang ikon yang digambar manual lewat kode
        brand.setIcon(new CameraIcon());

        JLabel userLabel = new JLabel(currentUser.getUsername());
        userLabel.setForeground(new Color(0xBDC3C7));
        userLabel.setFont(Theme.fontPlain(13));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(Theme.fontPlain(12));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(Theme.DANGER);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        right.setBackground(Theme.NAVBAR_BG);
        right.add(userLabel);
        right.add(btnLogout);

        navbar.add(brand, BorderLayout.WEST);
        navbar.add(right, BorderLayout.EAST);
        return navbar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(190, 0));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, Theme.BORDER));

        String[] pages = {"Dashboard", "Kamera", "Penyewa", "Transaksi", "Laporan"};
        String[] keys  = {"dashboard", "kamera", "penyewa", "transaksi", "laporan"};

        java.util.List<String> pageList = new java.util.ArrayList<>(java.util.Arrays.asList(pages));
        java.util.List<String> keyList  = new java.util.ArrayList<>(java.util.Arrays.asList(keys));

        if (currentUser.getRole().equals("admin")) {
            pageList.add("User");
            keyList.add("user");
        }

        sidebarButtons = new JButton[pageList.size()];
        sidebar.add(Box.createVerticalStrut(10));

        for (int i = 0; i < pageList.size(); i++) {
            final int idx = i;
            final String key = keyList.get(i);

            JButton btn = new JButton(pageList.get(i));
            btn.setFont(Theme.fontPlain(13));
            btn.setForeground(Theme.TEXT_DARK);
            btn.setBackground(Theme.SIDEBAR_BG);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorder(new EmptyBorder(10, 20, 10, 10));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> showPage(key, idx));
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (!btn.getBackground().equals(Theme.PRIMARY))
                        btn.setBackground(new Color(0xE8ECF0));
                }
                public void mouseExited(MouseEvent e) {
                    if (!btn.getBackground().equals(Theme.PRIMARY))
                        btn.setBackground(Theme.SIDEBAR_BG);
                }
            });

            sidebarButtons[i] = btn;
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    public void showPage(String key, int idx) {
        cardLayout.show(contentPanel, key);
        // Refresh panels on show
        if (key.equals("dashboard"))  dashboardPanel.refresh();
        if (key.equals("kamera"))     kameraPanel.refresh();
        if (key.equals("penyewa"))    penyewaPanel.refresh();
        if (key.equals("transaksi"))  transaksiPanel.refresh();
        if (key.equals("laporan"))    laporanPanel.refresh();
        if (key.equals("user"))       userPanel.refresh();

        // Update sidebar style
        for (int i = 0; i < sidebarButtons.length; i++) {
            if (i == idx) {
                sidebarButtons[i].setBackground(Theme.PRIMARY);
                sidebarButtons[i].setForeground(Color.WHITE);
            } else {
                sidebarButtons[i].setBackground(Theme.SIDEBAR_BG);
                sidebarButtons[i].setForeground(Theme.TEXT_DARK);
            }
        }
    }

    // =================================================================
    // KELAS INNER UNTUK MENGGAMBAR LOGO KAMERA SECARA MANUAL (PURE CODE)
    // =================================================================
    class CameraIcon implements Icon {
        private final int width = 28;
        private final int height = 28;

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            // Agar gambar halus tidak bergerigi
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. Gambar body kamera utama (Kotak rounded putih)
            g2.setColor(Color.WHITE);
            int bodyW = 24;
            int bodyH = 16;
            int bodyX = x + (width - bodyW) / 2;
            int bodyY = y + (height - bodyH) / 2 + 2;
            g2.fillRoundRect(bodyX, bodyY, bodyW, bodyH, 6, 6);

            // 2. Gambar bagian atas kamera (Flash/tombol)
            int topW = 12;
            int topH = 5;
            int topX = x + (width - topW) / 2;
            int topY = bodyY - topH + 2;
            g2.fillRoundRect(topX, topY, topW, topH, 4, 4);

            // 3. Gambar lensa luar (Lingkaran transparan / sewarna background navbar)
            g2.setColor(Theme.NAVBAR_BG);
            int lensOuter = 10;
            int lensOuterX = x + (width - lensOuter) / 2;
            int lensOuterY = bodyY + (bodyH - lensOuter) / 2;
            g2.fillOval(lensOuterX, lensOuterY, lensOuter, lensOuter);

            // 4. Gambar lensa dalam (Titik putih di tengah)
            g2.setColor(Color.WHITE);
            int lensInner = 4;
            int lensInnerX = x + (width - lensInner) / 2;
            int lensInnerY = bodyY + (bodyH - lensInner) / 2;
            g2.fillOval(lensInnerX, lensInnerY, lensInner, lensInner);

            // 5. Gambar flash kecil di sudut kanan atas body
            g2.setColor(Theme.NAVBAR_BG);
            g2.fillOval(bodyX + bodyW - 6, bodyY + 3, 3, 3);

            g2.dispose();
        }

        @Override
        public int getIconWidth() { return width; }

        @Override
        public int getIconHeight() { return height; }
    }
}