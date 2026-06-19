package view;

import dao.TransaksiDAO;
import model.Transaksi;
import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LaporanPanel extends JPanel {

    private TransaksiDAO transaksiDAO = new TransaksiDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblPendapatan;

    private final NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public LaporanPanel() {
        setBackground(Theme.CONTENT_BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));
        initUI();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.CONTENT_BG);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Laporan Transaksi");
        title.setFont(Theme.fontBold(22));
        title.setForeground(Theme.TEXT_DARK);

        lblPendapatan = new JLabel("Total Pendapatan Bulan Ini: Rp 0");
        lblPendapatan.setFont(Theme.fontBold(16));
        lblPendapatan.setForeground(Theme.SUCCESS);

        header.add(title, BorderLayout.WEST);
        header.add(lblPendapatan, BorderLayout.EAST);

        // PERUBAHAN: Ubah kolom "ID" menjadi "No"
        String[] cols = {"No", "Penyewa", "Tgl Sewa", "Tgl Kembali", "Total Harga"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.WHITE);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        refresh(); // Panggil data saat panel dibuka
    }

    public void refresh() {
        tableModel.setRowCount(0);
        int no = 1; // PERUBAHAN: Mulai penomoran dari 1

        for (Transaksi t : transaksiDAO.getAll()) {
            tableModel.addRow(new Object[]{
                    no++, // PERUBAHAN: Gunakan variabel 'no' lalu tambahkan nilainya
                    t.getNamaPenyewa(),
                    t.getTanggalSewa()    != null ? sdf.format(t.getTanggalSewa())    : "-",
                    t.getTanggalKembali() != null ? sdf.format(t.getTanggalKembali()) : "-",
                    "Rp " + nf.format(t.getTotalHarga())
            });
        }

        // Update label pendapatan
        int totalPendapatan = transaksiDAO.getPendapatanBulanIni();
        lblPendapatan.setText("Total Pendapatan Bulan Ini: Rp " + nf.format(totalPendapatan));
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
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        });
    }
}