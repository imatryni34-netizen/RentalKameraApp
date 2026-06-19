package model;

import java.util.Date;

public class Transaksi {
    private int idTransaksi;
    private int idPenyewa;
    private String namaPenyewa;
    private Date tanggalSewa;
    private Date tanggalKembali;
    private int totalHarga;
    private int bayar;
    private int kembalian;

    public Transaksi() {}

    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int idTransaksi) { this.idTransaksi = idTransaksi; }
    public int getIdPenyewa() { return idPenyewa; }
    public void setIdPenyewa(int idPenyewa) { this.idPenyewa = idPenyewa; }
    public String getNamaPenyewa() { return namaPenyewa; }
    public void setNamaPenyewa(String namaPenyewa) { this.namaPenyewa = namaPenyewa; }
    public Date getTanggalSewa() { return tanggalSewa; }
    public void setTanggalSewa(Date tanggalSewa) { this.tanggalSewa = tanggalSewa; }
    public Date getTanggalKembali() { return tanggalKembali; }
    public void setTanggalKembali(Date tanggalKembali) { this.tanggalKembali = tanggalKembali; }
    public int getTotalHarga() { return totalHarga; }
    public void setTotalHarga(int totalHarga) { this.totalHarga = totalHarga; }
    public int getBayar() { return bayar; }
    public void setBayar(int bayar) { this.bayar = bayar; }
    public int getKembalian() { return kembalian; }
    public void setKembalian(int kembalian) { this.kembalian = kembalian; }
}
