package view;

import dao.KameraDAO;
import dao.PenyewaDAO;
import dao.TransaksiDAO;
import model.*;
import util.Theme;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class TransaksiPanel extends JPanel {

    private User currentUser;
    private TransaksiDAO transaksiDAO = new TransaksiDAO();
    private KameraDAO kameraDAO = new KameraDAO();
    private PenyewaDAO penyewaDAO = new PenyewaDAO();

    private JTable table;
    private DefaultTableModel tableModel;
    private final NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public TransaksiPanel(User user) {
        this.currentUser = user;
        setBackground(Theme.CONTENT_BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));
        initUI();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.CONTENT_BG);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Data Transaksi");
        title.setFont(Theme.fontBold(22));
        title.setForeground(Theme.TEXT_DARK);

        JButton btnBaru = new JButton("+ Transaksi Baru");
        styleBtn(btnBaru, Theme.PRIMARY);
        btnBaru.addActionListener(e -> showFormTransaksi());

        header.add(title, BorderLayout.WEST);
        header.add(btnBaru, BorderLayout.EAST);

        String[] cols = {"No", "Penyewa", "Tgl Sewa", "Tgl Kembali", "Total", "Bayar", "Kembalian"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.setBackground(Theme.CONTENT_BG);

        JButton btnDetail = new JButton("Lihat Detail");
        JButton btnKembali = new JButton("Kembalikan");
        JButton btnHapus  = new JButton("Hapus");

        styleBtn(btnDetail, new Color(0x3498DB));
        styleBtn(btnKembali, new Color(0x27AE60));
        styleBtn(btnHapus, Theme.DANGER);

        btnDetail.addActionListener(e -> lihatDetail());
        btnKembali.addActionListener(e -> kembalikanKamera());
        btnHapus.addActionListener(e -> hapusSelected());

        actions.add(btnDetail);
        actions.add(btnKembali);
        if (currentUser.getRole().equals("admin")) actions.add(btnHapus);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        refresh();
    }

    private void showFormTransaksi() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Transaksi Baru", true);
        dialog.setSize(860, 590);
        dialog.setLocationRelativeTo(this);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Theme.WHITE);
        main.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel ttl = new JLabel("Transaksi Baru");
        ttl.setFont(Theme.fontBold(20));
        ttl.setBorder(new EmptyBorder(0, 0, 16, 0));
        main.add(ttl, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2, 20, 0));
        body.setBackground(Theme.WHITE);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(Theme.WHITE);

        JTextField txtNama   = inputField();
        JTextField txtHp     = inputField();
        JTextField txtAlamat = inputField();

        left.add(sectionLabel("Data Penyewa"));
        left.add(Box.createVerticalStrut(8));
        left.add(fieldRow("Nama Penyewa", txtNama));
        left.add(Box.createVerticalStrut(6));
        left.add(fieldRow("No. HP", txtHp));
        left.add(Box.createVerticalStrut(6));
        left.add(fieldRow("Alamat", txtAlamat));
        left.add(Box.createVerticalStrut(14));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        left.add(sep);
        left.add(Box.createVerticalStrut(12));

        left.add(sectionLabel("Periode Sewa"));
        left.add(Box.createVerticalStrut(8));

        Date today = new Date();
        Calendar tmr = Calendar.getInstance();
        tmr.add(Calendar.DAY_OF_MONTH, 1);

        SpinnerDateModel mdlSewa = new SpinnerDateModel(today, null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinSewa = new JSpinner(mdlSewa);
        spinSewa.setEditor(new JSpinner.DateEditor(spinSewa, "dd / MM / yyyy"));
        styleSpinner(spinSewa);

        SpinnerDateModel mdlKembali = new SpinnerDateModel(tmr.getTime(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinKembali = new JSpinner(mdlKembali);
        spinKembali.setEditor(new JSpinner.DateEditor(spinKembali, "dd / MM / yyyy"));
        styleSpinner(spinKembali);

        left.add(fieldRowSpinner("Tanggal Sewa", spinSewa));
        left.add(Box.createVerticalStrut(6));
        left.add(fieldRowSpinner("Tanggal Kembali", spinKembali));
        left.add(Box.createVerticalGlue());

        JPanel right = new JPanel(new BorderLayout(0, 8));
        right.setBackground(Theme.WHITE);

        List<Kamera> kameraList = kameraDAO.getAll();
        List<Kamera> kameraTersedia = new ArrayList<>();
        for (Kamera k : kameraList) {
            if (k.getStok() > 0) kameraTersedia.add(k);
        }

        // PERUBAHAN: Nama kolom diperjelas
        String[] kamCols = {"Kamera & Merk", "Harga/Hari", "Jumlah"};
        DefaultTableModel kamModel = new DefaultTableModel(kamCols, 0) {
            public Class getColumnClass(int c) { return c == 2 ? Integer.class : String.class; }
            public boolean isCellEditable(int r, int c) { return c == 2; }
        };

        // PERUBAHAN: Menggabungkan Nama Kamera dan Merk (contoh: DSLR - Canon EOS 600D)
        for (Kamera k : kameraTersedia) {
            String namaLengkap = k.getNamaKamera() + " - " + k.getMerk();
            kamModel.addRow(new Object[]{namaLengkap, "Rp " + nf.format(k.getHargaSewa()), 0});
        }

        JTable tblKamera = new JTable(kamModel);
        tblKamera.setFont(Theme.fontPlain(12));
        tblKamera.setRowHeight(32);
        tblKamera.setShowGrid(false);
        tblKamera.getTableHeader().setFont(Theme.fontBold(12));
        tblKamera.getTableHeader().setBackground(new Color(0xF7F8FA));

        // Atur lebar kolom agar teks merk tidak terpotong
        tblKamera.getColumnModel().getColumn(0).setPreferredWidth(200);
        tblKamera.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblKamera.getColumnModel().getColumn(2).setPreferredWidth(60);

        SpinnerNumberModel spModel = new SpinnerNumberModel(0, 0, 999, 1);
        JSpinner spJumlah = new JSpinner(spModel);
        tblKamera.getColumnModel().getColumn(2).setCellEditor(new SpinnerEditor(spJumlah));

        JScrollPane kScroll = new JScrollPane(tblKamera);
        kScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        JLabel lblTotalVal    = new JLabel("Rp 0");
        JLabel lblKembalianVal = new JLabel("Rp 0");
        lblTotalVal.setFont(Theme.fontBold(15));
        lblTotalVal.setForeground(Theme.PRIMARY);
        lblKembalianVal.setFont(Theme.fontBold(15));
        lblKembalianVal.setForeground(Theme.SUCCESS);

        JTextField txtBayar = new JTextField("0");
        txtBayar.setFont(Theme.fontBold(14));
        txtBayar.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER), new EmptyBorder(6, 10, 6, 10)));

        txtBayar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                doRecalc(kameraTersedia, kamModel, spinSewa, spinKembali, lblTotalVal, txtBayar, lblKembalianVal);
            }
        });
        txtBayar.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String raw = txtBayar.getText().replaceAll("[^0-9]", "");
                long val = raw.isEmpty() ? 0 : Long.parseLong(raw);
                txtBayar.setText(nf.format(val));
                doRecalc(kameraTersedia, kamModel, spinSewa, spinKembali, lblTotalVal, txtBayar, lblKembalianVal);
            }
            public void focusGained(FocusEvent e) {
                txtBayar.setText(txtBayar.getText().replaceAll("[^0-9]", ""));
                txtBayar.selectAll();
            }
        });

        kamModel.addTableModelListener(ev ->
                doRecalc(kameraTersedia, kamModel, spinSewa, spinKembali, lblTotalVal, txtBayar, lblKembalianVal));
        ChangeListener cl = e ->
                doRecalc(kameraTersedia, kamModel, spinSewa, spinKembali, lblTotalVal, txtBayar, lblKembalianVal);
        spinSewa.addChangeListener(cl);
        spinKembali.addChangeListener(cl);

        JPanel summary = new JPanel(new GridBagLayout());
        summary.setBackground(Theme.WHITE);
        summary.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, Theme.BORDER),
                new EmptyBorder(10, 0, 0, 0)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 0, 5, 10);
        g.anchor = GridBagConstraints.WEST;

        g.gridx=0; g.gridy=0; g.weightx=0;
        summary.add(summaryLbl("Total Harga"), g);
        g.gridx=1; g.weightx=1; g.fill=GridBagConstraints.HORIZONTAL;
        summary.add(lblTotalVal, g);

        g.gridx=0; g.gridy=1; g.weightx=0; g.fill=GridBagConstraints.NONE;
        summary.add(summaryLbl("Bayar"), g);
        g.gridx=1; g.weightx=1; g.fill=GridBagConstraints.HORIZONTAL;
        summary.add(txtBayar, g);

        g.gridx=0; g.gridy=2; g.weightx=0; g.fill=GridBagConstraints.NONE;
        summary.add(summaryLbl("Kembalian"), g);
        g.gridx=1; g.weightx=1; g.fill=GridBagConstraints.HORIZONTAL;
        summary.add(lblKembalianVal, g);

        right.add(sectionLabel("Pilih Kamera"), BorderLayout.NORTH);
        right.add(kScroll, BorderLayout.CENTER);
        right.add(summary, BorderLayout.SOUTH);

        body.add(left);
        body.add(right);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setBackground(Theme.WHITE);
        btns.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton btnBatal = new JButton("Batal");
        btnBatal.setFocusPainted(false);
        btnBatal.addActionListener(e -> dialog.dispose());

        JButton btnSimpan = new JButton("Simpan Transaksi");
        styleBtn(btnSimpan, Theme.PRIMARY);
        btnSimpan.addActionListener(e -> {
            if (txtNama.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nama penyewa wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                txtNama.requestFocus();
                return;
            }
            if (txtHp.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "No. HP wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                txtHp.requestFocus();
                return;
            }
            if (txtAlamat.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Alamat wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                txtAlamat.requestFocus();
                return;
            }
            if (tblKamera.isEditing()) tblKamera.getCellEditor().stopCellEditing();

            Date dSewa    = (Date) spinSewa.getValue();
            Date dKembali = (Date) spinKembali.getValue();
            if (!dKembali.after(dSewa)) {
                JOptionPane.showMessageDialog(dialog, "Tanggal kembali harus setelah tanggal sewa!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            long total = hitungTotal(kameraTersedia, kamModel, dSewa, dKembali);
            if (total == 0) {
                JOptionPane.showMessageDialog(dialog, "Pilih minimal 1 kamera dengan jumlah > 0!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String rawBayar = txtBayar.getText().replaceAll("[^0-9]", "");
            long bayar = rawBayar.isEmpty() ? 0 : Long.parseLong(rawBayar);
            if (bayar < total) {
                JOptionPane.showMessageDialog(dialog,
                        "Uang bayar kurang!\nTotal: Rp " + nf.format(total) +
                                "\nBayar: Rp " + nf.format(bayar),
                        "Bayar Kurang", JOptionPane.ERROR_MESSAGE);
                txtBayar.requestFocus();
                return;
            }

            Penyewa p = new Penyewa();
            p.setNama(txtNama.getText().trim());
            p.setNoHp(txtHp.getText().trim());
            p.setAlamat(txtAlamat.getText().trim());
            int idPenyewa = penyewaDAO.tambahDanGetId(p);

            Transaksi t = new Transaksi();
            t.setIdPenyewa(idPenyewa);
            t.setTanggalSewa(dSewa);
            t.setTanggalKembali(dKembali);
            t.setTotalHarga((int) total);
            t.setBayar((int) bayar);
            t.setKembalian((int) (bayar - total));
            int idTrx = transaksiDAO.tambahDanGetId(t);

            for (int i = 0; i < kamModel.getRowCount(); i++) {
                Object jObj = kamModel.getValueAt(i, 2);
                int jumlah = jObj instanceof Integer ? (Integer) jObj : 0;
                if (jumlah > 0) {
                    Kamera k = kameraTersedia.get(i);
                    if (jumlah > k.getStok()) {
                        JOptionPane.showMessageDialog(dialog, "Stok " + k.getNamaKamera() + " tidak cukup!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    transaksiDAO.tambahDetail(new DetailTransaksi(idTrx, k.getIdKamera(), jumlah));

                    k.setStok(k.getStok() - jumlah);
                    k.setStatus("disewa");
                    kameraDAO.update(k);
                }
            }

            JOptionPane.showMessageDialog(dialog, "Transaksi berhasil disimpan, Stok & Status Kamera Berubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            refresh();
        });

        btns.add(btnBatal);
        btns.add(btnSimpan);
        main.add(body, BorderLayout.CENTER);
        main.add(btns, BorderLayout.SOUTH);
        dialog.add(main);
        dialog.setVisible(true);
    }

    private void doRecalc(List<Kamera> kameraList, DefaultTableModel kamModel,
                          JSpinner spinSewa, JSpinner spinKembali,
                          JLabel lblTotal, JTextField txtBayar, JLabel lblKembalian) {
        Date d1 = (Date) spinSewa.getValue();
        Date d2 = (Date) spinKembali.getValue();
        long total = hitungTotal(kameraList, kamModel, d1, d2);
        lblTotal.setText("Rp " + nf.format(total));
        String raw = txtBayar.getText().replaceAll("[^0-9]", "");
        long bayar = raw.isEmpty() ? 0 : Long.parseLong(raw);
        long kembalian = bayar - total;
        lblKembalian.setText("Rp " + nf.format(kembalian));
        lblKembalian.setForeground(kembalian < 0 ? Theme.DANGER : Theme.SUCCESS);
    }

    private long hitungTotal(List<Kamera> kameraList, DefaultTableModel model, Date d1, Date d2) {
        if (d1 == null || d2 == null || !d2.after(d1)) return 0;
        long hari = Math.max(1, (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
        long total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object j = model.getValueAt(i, 2);
            int jumlah = j instanceof Integer ? (Integer) j : 0;
            total += (long) kameraList.get(i).getHargaSewa() * jumlah * hari;
        }
        return total;
    }

    private void kembalikanKamera() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih transaksi yang mau dikembalikan dulu!"); return; }

        List<Transaksi> listTrans = transaksiDAO.getAll();
        int idReal = listTrans.get(row).getIdTransaksi();

        int opt = JOptionPane.showConfirmDialog(this,
                "Selesaikan transaksi #" + idReal + "?\nKamera akan dikembalikan dan status menjadi 'tersedia'.",
                "Konfirmasi Pengembalian", JOptionPane.YES_NO_OPTION);

        if (opt == JOptionPane.YES_OPTION) {
            List<DetailTransaksi> details = transaksiDAO.getDetail(idReal);

            for (DetailTransaksi dt : details) {
                Kamera k = kameraDAO.getAll().stream().filter(x -> x.getIdKamera() == dt.getIdKamera()).findFirst().orElse(null);
                if (k != null) {
                    k.setStok(k.getStok() + dt.getJumlah());
                    k.setStatus("tersedia");
                    kameraDAO.update(k);
                }
            }

            transaksiDAO.hapus(idReal);
            refresh();
            JOptionPane.showMessageDialog(this, "Kamera berhasil dikembalikan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void lihatDetail() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih transaksi dulu!"); return; }

        List<Transaksi> listTrans = transaksiDAO.getAll();
        int idReal = listTrans.get(row).getIdTransaksi();

        List<DetailTransaksi> details = transaksiDAO.getDetail(idReal);
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detail Transaksi #" + idReal, true);
        d.setSize(550, 300); // Lebar dialog sedikit ditambah agar tabel lebih lega
        d.setLocationRelativeTo(this);
        String[] cols = {"Kamera & Merk", "Harga/Hari", "Jumlah", "Subtotal"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        for (DetailTransaksi dt : details) {
            // PERUBAHAN: Mencari merk dari tabel kamera agar di Detail juga tampil lengkap
            Kamera kamDb = kameraDAO.getAll().stream().filter(x -> x.getIdKamera() == dt.getIdKamera()).findFirst().orElse(null);
            String namaLengkap = kamDb != null ? kamDb.getNamaKamera() + " - " + kamDb.getMerk() : dt.getNamaKamera();

            m.addRow(new Object[]{namaLengkap, "Rp " + nf.format(dt.getHargaSewa()),
                    dt.getJumlah(), "Rp " + nf.format((long) dt.getHargaSewa() * dt.getJumlah())});
        }
        JTable tbl = new JTable(m);
        tbl.setFont(Theme.fontPlain(12)); tbl.setRowHeight(34);
        tbl.getTableHeader().setFont(Theme.fontBold(12));
        tbl.getColumnModel().getColumn(0).setPreferredWidth(200); // Lebarkan kolom nama
        d.add(new JScrollPane(tbl));
        d.setVisible(true);
    }

    private void hapusSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih transaksi dulu!"); return; }

        List<Transaksi> listTrans = transaksiDAO.getAll();
        int idReal = listTrans.get(row).getIdTransaksi();

        if (JOptionPane.showConfirmDialog(this, "Hapus transaksi ini? (Stok TIDAK akan dikembalikan otomatis)", "Konfirmasi",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            transaksiDAO.hapus(idReal); refresh();
        }
    }

    public void refresh() {
        tableModel.setRowCount(0);
        int no = 1;
        for (Transaksi t : transaksiDAO.getAll()) {
            tableModel.addRow(new Object[]{
                    no++,
                    t.getNamaPenyewa(),
                    t.getTanggalSewa()    != null ? sdf.format(t.getTanggalSewa())    : "-",
                    t.getTanggalKembali() != null ? sdf.format(t.getTanggalKembali()) : "-",
                    "Rp " + nf.format(t.getTotalHarga()),
                    "Rp " + nf.format(t.getBayar()),
                    "Rp " + nf.format(t.getKembalian())
            });
        }
    }

    private JLabel sectionLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Theme.fontBold(14));
        l.setForeground(Theme.TEXT_DARK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel summaryLbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Theme.fontSemiBold(12));
        l.setForeground(Theme.TEXT_GRAY);
        return l;
    }

    private JTextField inputField() {
        JTextField tf = new JTextField();
        tf.setFont(Theme.fontPlain(13));
        tf.setBorder(new CompoundBorder(new LineBorder(Theme.BORDER), new EmptyBorder(6, 10, 6, 10)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return tf;
    }

    private JPanel fieldRow(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(Theme.fontSemiBold(11));
        l.setForeground(Theme.TEXT_GRAY);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JPanel fieldRowSpinner(String label, JSpinner sp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(Theme.fontSemiBold(11));
        l.setForeground(Theme.TEXT_GRAY);
        p.add(l, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void styleSpinner(JSpinner sp) {
        sp.setFont(Theme.fontPlain(13));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        JSpinner.DateEditor ed = (JSpinner.DateEditor) sp.getEditor();
        ed.getTextField().setFont(Theme.fontPlain(13));
        ed.getTextField().setBorder(new EmptyBorder(4, 10, 4, 4));
        ed.getTextField().setBackground(Color.WHITE);
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
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        });
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
    }

    static class SpinnerEditor extends DefaultCellEditor {
        private final JSpinner spinner;
        public SpinnerEditor(JSpinner sp) {
            super(new JTextField());
            this.spinner = sp;
            spinner.setBorder(BorderFactory.createEmptyBorder());
        }
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            spinner.setValue(value instanceof Integer ? value : 0);
            return spinner;
        }
        public Object getCellEditorValue() { return spinner.getValue(); }
        public boolean stopCellEditing() {
            try { spinner.commitEdit(); } catch (Exception ignored) {}
            return super.stopCellEditing();
        }
    }
}