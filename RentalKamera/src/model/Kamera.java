package model;

public class Kamera {
    private int idKamera;
    private String namaKamera;
    private String merk;
    private int hargaSewa;
    private int stok;
    private String status;

    public Kamera() {}

    public Kamera(int idKamera, String namaKamera, String merk, int hargaSewa, int stok, String status) {
        this.idKamera = idKamera;
        this.namaKamera = namaKamera;
        this.merk = merk;
        this.hargaSewa = hargaSewa;
        this.stok = stok;
        this.status = status;
    }

    public int getIdKamera() { return idKamera; }
    public void setIdKamera(int idKamera) { this.idKamera = idKamera; }
    public String getNamaKamera() { return namaKamera; }
    public void setNamaKamera(String namaKamera) { this.namaKamera = namaKamera; }
    public String getMerk() { return merk; }
    public void setMerk(String merk) { this.merk = merk; }
    public int getHargaSewa() { return hargaSewa; }
    public void setHargaSewa(int hargaSewa) { this.hargaSewa = hargaSewa; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return namaKamera; }
}
