package view;

import dao.UserDAO;
import model.User;
import util.Theme;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class UserPanel extends JPanel {

    private UserDAO userDAO = new UserDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    public UserPanel() {
        setBackground(Theme.CONTENT_BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));
        initUI();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.CONTENT_BG);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Manajemen User");
        title.setFont(Theme.fontBold(22));
        title.setForeground(Theme.TEXT_DARK);

        JButton btnTambah = new JButton("+ Tambah User");
        styleBtn(btnTambah, Theme.PRIMARY);
        btnTambah.addActionListener(e -> showForm(null));

        header.add(title, BorderLayout.WEST);
        header.add(btnTambah, BorderLayout.EAST);

        String[] cols = {"ID", "Username", "Password", "Role"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.setBackground(Theme.CONTENT_BG);
        JButton btnEdit  = new JButton("Edit");
        JButton btnHapus = new JButton("Hapus");
        styleBtn(btnEdit, new Color(0x3498DB));
        styleBtn(btnHapus, Theme.DANGER);
        btnEdit.addActionListener(e -> editSelected());
        btnHapus.addActionListener(e -> hapusSelected());
        actions.add(btnEdit);
        actions.add(btnHapus);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
    }

    private void showForm(User u) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            u == null ? "Tambah User" : "Edit User", true);
        dialog.setSize(380, 240);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        form.setBackground(Theme.WHITE);

        JTextField txtUsername = new JTextField(u != null ? u.getUsername() : "");
        JTextField txtPassword = new JTextField(u != null ? u.getPassword() : "");
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"admin", "user"});
        if (u != null) cmbRole.setSelectedItem(u.getRole());

        form.add(lbl("Username:")); form.add(txtUsername);
        form.add(lbl("Password:")); form.add(txtPassword);
        form.add(lbl("Role:"));     form.add(cmbRole);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(Theme.WHITE);
        JButton batal = new JButton("Batal");
        JButton simpan = new JButton("Simpan");
        styleBtn(simpan, Theme.PRIMARY);
        batal.addActionListener(e -> dialog.dispose());
        simpan.addActionListener(e -> {
            User user = u != null ? u : new User();
            user.setUsername(txtUsername.getText().trim());
            user.setPassword(txtPassword.getText().trim());
            user.setRole(cmbRole.getSelectedItem().toString());
            if (u == null) userDAO.tambah(user);
            else userDAO.update(user);
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
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih user dulu!"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        User u = userDAO.getAll().stream().filter(x -> x.getIdUser() == id).findFirst().orElse(null);
        if (u != null) showForm(u);
    }

    private void hapusSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih user dulu!"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int opt = JOptionPane.showConfirmDialog(this, "Hapus user ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) { userDAO.hapus(id); refresh(); }
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (User u : userDAO.getAll()) {
            tableModel.addRow(new Object[]{u.getIdUser(), u.getUsername(), u.getPassword(), u.getRole()});
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
