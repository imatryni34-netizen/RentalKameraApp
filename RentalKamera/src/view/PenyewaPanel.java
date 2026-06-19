package view;

import dao.PenyewaDAO;
import model.Penyewa;
import util.Theme;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PenyewaPanel extends JPanel {

    private PenyewaDAO penyewaDAO = new PenyewaDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private String currentRole = "user";

    public PenyewaPanel() { this("user"); }

    public PenyewaPanel(String role) {
        this.currentRole = role;
        setBackground(Theme.CONTENT_BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));
        initUI();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.CONTENT_BG);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Data Penyewa");
        title.setFont(Theme.fontBold(22));
        title.setForeground(Theme.TEXT_DARK);

        JButton btnTambah = new JButton("+ Tambah Penyewa");
        styleBtn(btnTambah, Theme.PRIMARY);
        btnTambah.addActionListener(e -> showForm(null));

        header.add(title, BorderLayout.WEST);
        if (currentRole.equals("admin")) header.add(btnTambah, BorderLayout.EAST);

        // PERUBAHAN: Ubah "ID" menjadi "No"
        String[] cols = {"No", "Nama", "No. HP", "Alamat"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.WHITE);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.setBackground(Theme.CONTENT_BG);
        JButton btnEdit = new JButton("Edit");
        JButton btnHapus = new JButton("Hapus");
        styleBtn(btnEdit, new Color(0x3498DB));
        styleBtn(btnHapus, Theme.DANGER);
        btnEdit.addActionListener(e -> editSelected());
        btnHapus.addActionListener(e -> hapusSelected());
        actions.add(btnEdit);
        actions.add(btnHapus);

        boolean isAdmin = currentRole.equals("admin");
        btnEdit.setVisible(isAdmin);
        btnHapus.setVisible(isAdmin);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        refresh(); // Panggil data saat panel dibuka
    }

    private void showForm(Penyewa p) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                p == null ? "Tambah Penyewa" : "Edit Penyewa", true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        form.setBackground(Theme.WHITE);

        JTextField txtNama   = new JTextField(p != null ? p.getNama() : "");
        JTextField txtHp     = new JTextField(p != null ? p.getNoHp() : "");
        JTextField txtAlamat = new JTextField(p != null ? p.getAlamat() : "");

        form.add(lbl("Nama:")); form.add(txtNama);
        form.add(lbl("No. HP:")); form.add(txtHp);
        form.add(lbl("Alamat:")); form.add(txtAlamat);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(Theme.WHITE);
        JButton batal = new JButton("Batal");
        JButton simpan = new JButton("Simpan");
        styleBtn(simpan, Theme.PRIMARY);
        batal.addActionListener(e -> dialog.dispose());
        simpan.addActionListener(e -> {
            Penyewa penyewa = p != null ? p : new Penyewa();
            penyewa.setNama(txtNama.getText().trim());
            penyewa.setNoHp(txtHp.getText().trim());
            penyewa.setAlamat(txtAlamat.getText().trim());
            if (p == null) penyewaDAO.tambah(penyewa);
            else penyewaDAO.update(penyewa);
            dialog.dispose();
            refresh();
        });
        btns.add(batal); btns.add(simpan);

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btns, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih penyewa dulu!"); return; }
        // PERUBAHAN: Ambil ID asli dari database, bukan dari tabel
        int idReal = penyewaDAO.getAll().get(row).getIdPenyewa();
        Penyewa p = penyewaDAO.getAll().stream().filter(x -> x.getIdPenyewa() == idReal).findFirst().orElse(null);
        if (p != null) showForm(p);
    }

    private void hapusSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih penyewa dulu!"); return; }
        // PERUBAHAN: Ambil ID asli dari database, bukan dari tabel
        int idReal = penyewaDAO.getAll().get(row).getIdPenyewa();
        int opt = JOptionPane.showConfirmDialog(this, "Hapus penyewa ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) { penyewaDAO.hapus(idReal); refresh(); }
    }

    public void refresh() {
        tableModel.setRowCount(0);
        int no = 1; // PERUBAHAN: Penomoran manual agar rapi
        for (Penyewa p : penyewaDAO.getAll()) {
            tableModel.addRow(new Object[]{no++, p.getNama(), p.getNoHp(), p.getAlamat()});
        }
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

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.fontSemiBold(12));
        l.setForeground(Theme.TEXT_DARK);
        return l;
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
    }
}