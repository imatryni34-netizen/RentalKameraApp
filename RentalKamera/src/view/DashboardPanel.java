package view;

import dao.KameraDAO;
import dao.TransaksiDAO;
import model.DetailTransaksi;
import model.Transaksi;
import model.User;
import util.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class DashboardPanel extends JPanel {

    private User currentUser;
    private KameraDAO kameraDAO = new KameraDAO();
    private TransaksiDAO transaksiDAO = new TransaksiDAO();

    private JLabel lblTotalKamera;
    private JLabel lblTersedia;
    private JLabel lblDisewa;
    private JTable table;
    private DefaultTableModel tableModel;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public DashboardPanel(User user) {
        this.currentUser = user;
        setBackground(Theme.CONTENT_BG);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(24, 24, 24, 24));
        initUI();
        refresh();
    }

    private void initUI() {
        JLabel title = new JLabel("Dashboard");
        title.setFont(Theme.fontBold(24));
        title.setForeground(Theme.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setBackground(Theme.CONTENT_BG);

        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 0));
        cards.setBackground(Theme.CONTENT_BG);

        lblTotalKamera = createLabel("0", 36, Theme.TEXT_DARK);
        lblTersedia = createLabel("0", 36, Theme.TEXT_DARK);
        lblDisewa = createLabel("0", 36, Theme.TEXT_DARK);

        cards.add(createCard("Total Kamera", lblTotalKamera, new Color(0x3498DB)));
        cards.add(createCard("Kamera Tersedia", lblTersedia, new Color(0x2ECC71)));
        cards.add(createCard("Kamera Disewa", lblDisewa, new Color(0xE67E22)));

        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(Theme.CONTENT_BG);

        JLabel tblTitle = new JLabel("Transaksi Terbaru");
        tblTitle.setFont(Theme.fontBold(16));
        tblTitle.setForeground(Theme.TEXT_DARK);

        // PERUBAHAN: Ubah "ID Transaksi" menjadi "No"
        String[] cols = {"No", "Penyewa", "Kamera", "Tgl Pinjam", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        tablePanel.add(tblTitle, BorderLayout.NORTH);
        tablePanel.add(scroll, BorderLayout.CENTER);

        center.add(cards, BorderLayout.NORTH);
        center.add(tablePanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, JLabel valueLabel, Color topColor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.WHITE);
        p.setBorder(new CompoundBorder(
                new MatteBorder(4, 1, 1, 1, topColor),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(Theme.fontBold(14));
        lblTitle.setForeground(Theme.TEXT_GRAY);

        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(lblTitle, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    private JLabel createLabel(String text, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.fontBold(size));
        l.setForeground(color);
        return l;
    }

    private void styleTable(JTable t) {
        t.setFont(Theme.fontPlain(12));
        t.setRowHeight(36);
        t.setShowGrid(false);
        t.setBackground(Theme.WHITE);
        t.setSelectionBackground(new Color(0xEBF5FB));
        t.getTableHeader().setFont(Theme.fontBold(12));
        t.getTableHeader().setBackground(new Color(0xF7F8FA));
        t.getTableHeader().setForeground(Theme.TEXT_GRAY);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        });
    }

    public void refresh() {
        lblTotalKamera.setText(String.valueOf(kameraDAO.countAll()));
        lblTersedia.setText(String.valueOf(kameraDAO.countByStatus("tersedia")));
        lblDisewa.setText(String.valueOf(kameraDAO.countByStatus("disewa")));

        tableModel.setRowCount(0);

        List<Transaksi> list = transaksiDAO.getAll();

        int limit = 5;
        int count = 0;
        int no = 1; // PERUBAHAN: Penomoran manual untuk urutan di tabel

        for (int i = list.size() - 1; i >= 0; i--) {
            if (count >= limit) break;

            Transaksi t = list.get(i);

            List<DetailTransaksi> details = transaksiDAO.getDetail(t.getIdTransaksi());
            StringBuilder kameraStr = new StringBuilder();
            for (int j = 0; j < details.size(); j++) {
                kameraStr.append(details.get(j).getNamaKamera());
                if (j < details.size() - 1) kameraStr.append(", ");
            }
            if (kameraStr.length() == 0) kameraStr.append("-");

            // PERUBAHAN: Memasukkan angka urut ke baris tabel
            tableModel.addRow(new Object[]{
                    no++,
                    t.getNamaPenyewa(),
                    kameraStr.toString(),
                    t.getTanggalSewa() != null ? sdf.format(t.getTanggalSewa()) : "-",
                    "Disewa"
            });

            count++;
        }
    }
}