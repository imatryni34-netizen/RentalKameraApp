package model;

public class Penyewa {
    private int idPenyewa;
    private String nama;
    private String noHp;
    private String alamat;

    public Penyewa() {}

    public Penyewa(int idPenyewa, String nama, String noHp, String alamat) {
        this.idPenyewa = idPenyewa;
        this.nama = nama;
        this.noHp = noHp;
        this.alamat = alamat;
    }

    public int getIdPenyewa() { return idPenyewa; }
    public void setIdPenyewa(int idPenyewa) { this.idPenyewa = idPenyewa; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getNoHp() { return noHp; }
    public void setNoHp(String noHp) { this.noHp = noHp; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    @Override
    public String toString() { return nama; }
}
