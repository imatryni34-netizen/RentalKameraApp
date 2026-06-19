package dao;

import model.DetailTransaksi;
import model.Transaksi;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

    public List<Transaksi> getAll() {
        List<Transaksi> list = new ArrayList<>();
        // Perbaikan: Menggunakan ORDER BY ASC agar ID terurut rapi dari bawah ke atas
        String sql = "SELECT t.*, p.nama FROM transaksi t JOIN penyewa p ON t.id_penyewa = p.id_penyewa ORDER BY t.id_transaksi ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Transaksi t = new Transaksi();
                t.setIdTransaksi(rs.getInt("id_transaksi"));
                t.setIdPenyewa(rs.getInt("id_penyewa"));
                t.setNamaPenyewa(rs.getString("nama"));
                t.setTanggalSewa(rs.getDate("tanggal_sewa"));
                t.setTanggalKembali(rs.getDate("tanggal_kembali"));
                t.setTotalHarga(rs.getInt("total_harga"));
                t.setBayar(rs.getInt("bayar"));
                t.setKembalian(rs.getInt("kembalian"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Transaksi> getRecent(int limit) {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT t.*, p.nama FROM transaksi t JOIN penyewa p ON t.id_penyewa = p.id_penyewa ORDER BY t.id_transaksi DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaksi t = new Transaksi();
                t.setIdTransaksi(rs.getInt("id_transaksi"));
                t.setNamaPenyewa(rs.getString("nama"));
                t.setTanggalSewa(rs.getDate("tanggal_sewa"));
                t.setTotalHarga(rs.getInt("total_harga"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getPendapatanBulanIni() {
        String sql = "SELECT COALESCE(SUM(total_harga),0) FROM transaksi WHERE MONTH(tanggal_sewa)=MONTH(NOW()) AND YEAR(tanggal_sewa)=YEAR(NOW())";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int tambahDanGetId(Transaksi t) {
        String sql = "INSERT INTO transaksi (id_penyewa, tanggal_sewa, tanggal_kembali, total_harga, bayar, kembalian) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getIdPenyewa());
            ps.setDate(2, new java.sql.Date(t.getTanggalSewa().getTime()));
            ps.setDate(3, new java.sql.Date(t.getTanggalKembali().getTime()));
            ps.setInt(4, t.getTotalHarga());
            ps.setInt(5, t.getBayar());
            ps.setInt(6, t.getKembalian());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean tambahDetail(DetailTransaksi d) {
        String sql = "INSERT INTO detail_transaksi (id_transaksi, id_kamera, jumlah) VALUES (?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getIdTransaksi());
            ps.setInt(2, d.getIdKamera());
            ps.setInt(3, d.getJumlah());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<DetailTransaksi> getDetail(int idTransaksi) {
        List<DetailTransaksi> list = new ArrayList<>();
        String sql = "SELECT dt.*, k.nama_kamera, k.harga_sewa FROM detail_transaksi dt JOIN kamera k ON dt.id_kamera = k.id_kamera WHERE dt.id_transaksi = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DetailTransaksi d = new DetailTransaksi();
                d.setIdDetail(rs.getInt("id_detail"));
                d.setIdTransaksi(rs.getInt("id_transaksi"));
                d.setIdKamera(rs.getInt("id_kamera"));
                d.setJumlah(rs.getInt("jumlah"));
                d.setNamaKamera(rs.getString("nama_kamera"));
                d.setHargaSewa(rs.getInt("harga_sewa"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean hapus(int id) {
        String sql = "DELETE FROM transaksi WHERE id_transaksi=?";
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