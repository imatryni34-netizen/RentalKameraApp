package view;

import dao.UserDAO;
import model.User;
import util.Theme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("Rental Kamera - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        // Panel utama sebagai background (Abu-abu terang)
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBackground(Theme.CONTENT_BG);

        // ===== CARD LOGIN =====
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Card sudut membulat

                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(380, 430));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // --- Logo & Judul ---
        JLabel lblLogo = new JLabel(new CameraIcon());
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Welcome Back");
        lblTitle.setFont(Theme.fontBold(24));
        lblTitle.setForeground(Theme.TEXT_DARK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("LensaHub Pro - Login");
        lblSubtitle.setFont(Theme.fontPlain(14));
        lblSubtitle.setForeground(new Color(0x7F8C8D));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Input Fields ---
        txtUsername = new JTextField();
        styleInput(txtUsername);
        JPanel userWrapper = createInputGroup("Username", txtUsername);

        txtPassword = new JPasswordField();
        styleInput(txtPassword);
        JPanel passWrapper = createInputGroup("Password", txtPassword);

        // --- Tombol Login ---
        JButton btnLogin = createLoginButton("Login");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user = userDAO.login(username, password);

            if (user != null) {
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- Susun Elemen ---
        cardPanel.add(lblLogo);
        cardPanel.add(Box.createVerticalStrut(15));
        cardPanel.add(lblTitle);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(lblSubtitle);

        cardPanel.add(Box.createVerticalStrut(30));
        cardPanel.add(userWrapper);

        cardPanel.add(Box.createVerticalStrut(15));
        cardPanel.add(passWrapper);

        cardPanel.add(Box.createVerticalStrut(35));
        cardPanel.add(btnLogin);

        backgroundPanel.add(cardPanel);
        setContentPane(backgroundPanel);
    }

    // Helper: Menggabungkan Label dan TextField agar tidak bertumpuk
    private JPanel createInputGroup(String labelText, JTextField inputField) {
        // Menggunakan BorderLayout dengan jarak vertikal (vgap) 6px agar aman
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(Theme.fontSemiBold(12));
        lbl.setForeground(new Color(0x2C3E50));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(inputField, BorderLayout.CENTER);

        // Kunci tinggi maksimalnya agar BoxLayout luar tidak menariknya sampai rusak
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
    }

    private void styleInput(JTextField field) {
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(Theme.fontPlain(14));
        field.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(5, 12, 5, 12)
        ));
    }

    private JButton createLoginButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(Theme.fontBold(14));
        btn.setForeground(Color.WHITE);
        // Mengembalikan warna ke gelap elegan seperti aslinya
        btn.setBackground(new Color(0x1C2833));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0x2C3E50)); repaint(); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(0x1C2833)); repaint(); }
        });
        return btn;
    }

    class CameraIcon implements Icon {
        private final int width = 48;
        private final int height = 48;

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color logoColor = new Color(0x2C3E50);

            g2.setColor(logoColor);
            int bodyW = 40;
            int bodyH = 28;
            int bodyX = x + (width - bodyW) / 2;
            int bodyY = y + (height - bodyH) / 2 + 4;
            g2.fillRoundRect(bodyX, bodyY, bodyW, bodyH, 8, 8);

            int topW = 18;
            int topH = 8;
            int topX = x + (width - topW) / 2;
            int topY = bodyY - topH + 3;
            g2.fillRoundRect(topX, topY, topW, topH, 6, 6);

            g2.setColor(Color.WHITE);
            int lensOuter = 18;
            int lensOuterX = x + (width - lensOuter) / 2;
            int lensOuterY = bodyY + (bodyH - lensOuter) / 2;
            g2.fillOval(lensOuterX, lensOuterY, lensOuter, lensOuter);

            g2.setColor(logoColor);
            int lensInner = 8;
            int lensInnerX = x + (width - lensInner) / 2;
            int lensInnerY = bodyY + (bodyH - lensInner) / 2;
            g2.fillOval(lensInnerX, lensInnerY, lensInner, lensInner);

            g2.dispose();
        }

        @Override
        public int getIconWidth() { return width; }
        @Override
        public int getIconHeight() { return height; }
    }
}