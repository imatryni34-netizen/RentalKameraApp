package model;

public class DetailTransaksi {
    private int idDetail;
    private int idTransaksi;
    private int idKamera;
    private int jumlah;
    private String namaKamera;
    private int hargaSewa;

    public DetailTransaksi() {}

    public DetailTransaksi(int idTransaksi, int idKamera, int jumlah) {
        this.idTransaksi = idTransaksi;
        this.idKamera = idKamera;
        this.jumlah = jumlah;
    }

    public int getIdDetail() { return idDetail; }
    public void setIdDetail(int idDetail) { this.idDetail = idDetail; }
    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int idTransaksi) { this.idTransaksi = idTransaksi; }
    public int getIdKamera() { return idKamera; }
    public void setIdKamera(int idKamera) { this.idKamera = idKamera; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
    public String getNamaKamera() { return namaKamera; }
    public void setNamaKamera(String namaKamera) { this.namaKamera = namaKamera; }
    public int getHargaSewa() { return hargaSewa; }
    public void setHargaSewa(int hargaSewa) { this.hargaSewa = hargaSewa; }
}
