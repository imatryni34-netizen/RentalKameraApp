package view;

import dao.KameraDAO;
import model.Kamera;
import util.Theme;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class KameraPanel extends JPanel {

    private KameraDAO kameraDAO = new KameraDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private String filterStatus = null;
    private String currentRole = "user";

    private JButton tabSemua, tabTersedia, tabDisewa;
    private JButton activeTab = null;

    public KameraPanel(String role) {
        this.currentRole = role;
        setBackground(Theme.CONTENT_BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));
        initUI();
        refresh();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.CONTENT_BG);
        header.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("Data Kamera");
        title.setFont(Theme.fontBold(22));
        title.setForeground(Theme.TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        if (currentRole.equals("admin")) {
            JButton btnTambah = createPrimaryBtn("+ Tambah Kamera");
            btnTambah.addActionListener(e -> showForm(null));
            header.add(btnTambah, BorderLayout.EAST);
        }

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tabs.setBackground(Theme.CONTENT_BG);
        tabs.setBorder(new EmptyBorder(0, 0, 14, 0));

        tabSemua    = createTab("Semua");
        tabTersedia = createTab("Tersedia");
        tabDisewa   = createTab("Disewa");

        tabSemua.addActionListener(e -> { filterStatus = null; setActiveTab(tabSemua); refresh(); });
        tabTersedia.addActionListener(e -> { filterStatus = "tersedia"; setActiveTab(tabTersedia); refresh(); });
        tabDisewa.addActionListener(e -> { filterStatus = "disewa"; setActiveTab(tabDisewa); refresh(); });

        tabs.add(tabSemua);
        tabs.add(tabTersedia);
        tabs.add(tabDisewa);
        updateTabCounts();
        setActiveTab(tabSemua);

        // ===== PERBAIKAN KOLOM TABEL =====
        // Menentukan nama kolom berdasarkan role yang sedang login
        String[] cols;
        if (currentRole.equals("admin")) {
            cols = new String[]{"ID", "Nama Kamera", "Merk", "Harga/Hari", "Stok", "Status", "Aksi"};
        } else {
            cols = new String[]{"ID", "Nama Kamera", "Merk", "Harga/Hari", "Stok", "Status"};
        }

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return currentRole.equals("admin") && c == 6; // Hanya kolom Aksi yang bisa diedit (diklik)
            }
        };
        table = new JTable(tableModel);
        styleTable(table);

        // Render tombol di dalam tabel HANYA jika role adalah admin
        if (currentRole.equals("admin")) {
            table.getColumnModel().getColumn(6).setCellRenderer(new AksiRenderer());
            table.getColumnModel().getColumn(6).setCellEditor(new AksiEditor());
            table.getColumnModel().getColumn(6).setPreferredWidth(160);
        }

        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.WHITE);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(Theme.CONTENT_BG);
        top.add(header);
        top.add(tabs);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
            String status = value != null ? value.toString().toLowerCase() : "";
            if (status.equals("disewa")) setForeground(new Color(0xE67E22));
            else if (status.equals("tersedia")) setForeground(new Color(0x27AE60));
            else setForeground(Theme.TEXT_DARK);

            if (isSelected) setForeground(status.equals("disewa") ? new Color(0xE67E22) : new Color(0x27AE60));
            setFont(Theme.fontSemiBold(12));
            setBorder(new EmptyBorder(0, 6, 0, 0));
            return this;
        }
    }

    class AksiEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        private JButton btnEdit = createTableActionBtn("Edit", new Color(0x3498DB), new Color(0x00E5FF)); // Cyan neon hover
        private JButton btnHapus = createTableActionBtn("Hapus", Theme.DANGER, new Color(0xFF4C4C));

        AksiEditor() {
            panel.setOpaque(true);
            btnEdit.addActionListener(e -> {
                int row = table.getEditingRow();
                if (row != -1) {
                    int id = (int) table.getValueAt(row, 0);
                    fireEditingStopped();
                    Kamera k = kameraDAO.getAll().stream().filter(x -> x.getIdKamera() == id).findFirst().orElse(null);
                    if (k != null) showForm(k);
                }
            });

            btnHapus.addActionListener(e -> {
                int row = table.getEditingRow();
                if (row != -1) {
                    int id = (int) table.getValueAt(row, 0);
                    int opt = JOptionPane.showConfirmDialog(null, "Hapus kamera ID " + id + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                    if (opt == JOptionPane.YES_OPTION) {
                        kameraDAO.hapus(id);
                        fireEditingStopped();
                        refresh();
                    } else fireEditingCanceled();
                }
            });

            panel.add(btnEdit);
            panel.add(btnHapus);
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            panel.setBackground(t.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return ""; }
    }

    class AksiRenderer extends JPanel implements TableCellRenderer {
        private JButton btnEdit = createTableActionBtn("Edit", new Color(0x3498DB), new Color(0x00E5FF));
        private JButton btnHapus = createTableActionBtn("Hapus", Theme.DANGER, new Color(0xFF4C4C));

        AksiRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
            setOpaque(true);
            add(btnEdit);
            add(btnHapus);
        }

        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            setBackground(sel ? t.getSelectionBackground() : Theme.WHITE);
            return this;
        }
    }

    private JButton createTableActionBtn(String text, Color baseColor, Color hoverColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hoverColor : baseColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(Theme.fontBold(11));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(4, 12, 4, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createPrimaryBtn(String text) {
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
        btn.setFont(Theme.fontBold(12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0x2C3E50));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0x00E5FF)); btn.setForeground(Color.BLACK); repaint(); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(0x2C3E50)); btn.setForeground(Color.WHITE); repaint(); }
        });
        return btn;
    }

    private JButton createTab(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                if (getBackground().equals(Theme.WHITE)) {
                    g2.setColor(Theme.BORDER);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(Theme.fontSemiBold(12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private void setActiveTab(JButton tab) {
        for (JButton t : new JButton[]{tabSemua, tabTersedia, tabDisewa}) {
            t.setBackground(Theme.WHITE);
            t.setForeground(Theme.TEXT_DARK);
        }
        tab.setBackground(new Color(0x2C3E50));
        tab.setForeground(Color.WHITE);
        activeTab = tab;
        tabSemua.repaint(); tabTersedia.repaint(); tabDisewa.repaint();
    }

    private void updateTabCounts() {
        tabSemua.setText("Semua (" + kameraDAO.countAll() + ")");
        tabTersedia.setText("Tersedia (" + kameraDAO.countByStatus("tersedia") + ")");
        tabDisewa.setText("Disewa (" + kameraDAO.countByStatus("disewa") + ")");
    }

    public void refresh() {
        updateTabCounts();
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        List<Kamera> list = filterStatus == null ? kameraDAO.getAll() : kameraDAO.getByStatus(filterStatus);
        tableModel.setRowCount(0);

        // ===== PERBAIKAN PENGISIAN DATA TABEL =====
        for (Kamera k : list) {
            if (currentRole.equals("admin")) {
                // Admin dapat 7 kolom (ada kolom aksi)
                tableModel.addRow(new Object[]{
                        k.getIdKamera(), k.getNamaKamera(), k.getMerk(),
                        "Rp " + nf.format(k.getHargaSewa()), k.getStok(), k.getStatus(), ""
                });
            } else {
                // User biasa hanya 6 kolom
                tableModel.addRow(new Object[]{
                        k.getIdKamera(), k.getNamaKamera(), k.getMerk(),
                        "Rp " + nf.format(k.getHargaSewa()), k.getStok(), k.getStatus()
                });
            }
        }
    }

    private void styleTable(JTable t) {
        t.setRowHeight(42);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(0xE8F8F5)); // Highlight neon-ish light green saat diklik
        t.setSelectionForeground(Theme.TEXT_DARK);

        JTableHeader th = t.getTableHeader();
        th.setFont(Theme.fontBold(12));
        th.setBackground(new Color(0xF2F3F4));
        th.setForeground(new Color(0x555555));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        th.setReorderingAllowed(false);
    }

    private JButton createDialogBtn(String text, Color bg, Color fg) {
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
        btn.setFont(Theme.fontBold(12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private void showForm(Kamera k) {
        boolean isEdit = (k != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), isEdit ? "Edit Kamera" : "Tambah Kamera", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Theme.WHITE);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBackground(Theme.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField txtNama = new JTextField(isEdit ? k.getNamaKamera() : "");
        JTextField txtMerk = new JTextField(isEdit ? k.getMerk() : "");
        JTextField txtHarga = new JTextField(isEdit ? String.valueOf(k.getHargaSewa()) : "");
        JTextField txtStok = new JTextField(isEdit ? String.valueOf(k.getStok()) : "");
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"tersedia", "disewa"});
        if (isEdit) cmbStatus.setSelectedItem(k.getStatus());

        formPanel.add(new JLabel("Nama Kamera:")); formPanel.add(txtNama);
        formPanel.add(new JLabel("Merk:")); formPanel.add(txtMerk);
        formPanel.add(new JLabel("Harga Sewa/Hari:")); formPanel.add(txtHarga);
        formPanel.add(new JLabel("Stok:")); formPanel.add(txtStok);
        formPanel.add(new JLabel("Status:")); formPanel.add(cmbStatus);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Theme.WHITE);
        btnPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        JButton btnSimpan = createDialogBtn("Simpan", new Color(0x27AE60), Color.WHITE);
        JButton btnBatal = createDialogBtn("Batal", Theme.DANGER, Color.WHITE);

        btnBatal.addActionListener(e -> dialog.dispose());
        btnSimpan.addActionListener(e -> {
            try {
                String nama = txtNama.getText();
                String merk = txtMerk.getText();
                int harga = Integer.parseInt(txtHarga.getText());
                int stok = Integer.parseInt(txtStok.getText());
                String status = cmbStatus.getSelectedItem().toString();

                Kamera newKamera = new Kamera(isEdit ? k.getIdKamera() : 0, nama, merk, harga, stok, status);
                boolean success = isEdit ? kameraDAO.update(newKamera) : kameraDAO.tambah(newKamera);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Data berhasil disimpan!");
                    refresh();
                    dialog.dispose();
                } else JOptionPane.showMessageDialog(dialog, "Gagal menyimpan data.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Harga dan Stok harus angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnBatal);
        btnPanel.add(btnSimpan);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}