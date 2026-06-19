package dao;

import model.Kamera;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KameraDAO {

    public List<Kamera> getAll() {
        List<Kamera> list = new ArrayList<>();
        String sql = "SELECT * FROM kamera";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Kamera k = new Kamera();
                k.setIdKamera(rs.getInt("id_kamera"));
                k.setNamaKamera(rs.getString("nama_kamera"));
                k.setMerk(rs.getString("merk"));
                k.setHargaSewa(rs.getInt("harga_sewa"));
                k.setStok(rs.getInt("stok"));
                k.setStatus(rs.getString("status"));
                list.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Kamera> getByStatus(String status) {
        List<Kamera> list = new ArrayList<>();
        String sql = "SELECT * FROM kamera WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Kamera k = new Kamera();
                k.setIdKamera(rs.getInt("id_kamera"));
                k.setNamaKamera(rs.getString("nama_kamera"));
                k.setMerk(rs.getString("merk"));
                k.setHargaSewa(rs.getInt("harga_sewa"));
                k.setStok(rs.getInt("stok"));
                k.setStatus(rs.getString("status"));
                list.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- INI METHOD YANG SEMPAT HILANG ---
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM kamera";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM kamera WHERE status=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Method hitung alternatif (biar aman kalau sewaktu-waktu dipakai)
    public int getJumlahTersedia() {
        return countByStatus("tersedia");
    }

    public int getJumlahDisewa() {
        return countByStatus("disewa");
    }
    // -------------------------------------

    public boolean tambah(Kamera k) {
        String sql = "INSERT INTO kamera (nama_kamera, merk, harga_sewa, stok, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getNamaKamera());
            ps.setString(2, k.getMerk());
            ps.setInt(3, k.getHargaSewa());
            ps.setInt(4, k.getStok());
            ps.setString(5, k.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Kamera k) {
        String sql = "UPDATE kamera SET nama_kamera=?, merk=?, harga_sewa=?, stok=?, status=? WHERE id_kamera=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getNamaKamera());
            ps.setString(2, k.getMerk());
            ps.setInt(3, k.getHargaSewa());
            ps.setInt(4, k.getStok());
            ps.setString(5, k.getStatus()); // Perbaikan: Simpan status terbaru ke database
            ps.setInt(6, k.getIdKamera());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hapus(int id) {
        String sql = "DELETE FROM kamera WHERE id_kamera=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}