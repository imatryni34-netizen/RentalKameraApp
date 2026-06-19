# Rental Kamera App - Java Swing

## Struktur Project
```
RentalKamera/
└── src/
    ├── Main.java                    ← Entry point
    ├── model/
    │   ├── User.java
    │   ├── Kamera.java
    │   ├── Penyewa.java
    │   ├── Transaksi.java
    │   └── DetailTransaksi.java
    ├── dao/
    │   ├── UserDAO.java
    │   ├── KameraDAO.java
    │   ├── PenyewaDAO.java
    │   └── TransaksiDAO.java
    ├── view/
    │   ├── LoginFrame.java
    │   ├── MainFrame.java
    │   ├── DashboardPanel.java
    │   ├── KameraPanel.java
    │   ├── PenyewaPanel.java
    │   ├── TransaksiPanel.java
    │   ├── LaporanPanel.java
    │   └── UserPanel.java
    └── util/
        ├── DBConnection.java
        └── Theme.java
```

## Cara Setup di IntelliJ IDEA

### 1. Buat Project Baru
- File → New → Project
- Pilih **Java** (bukan Maven/Gradle dulu)
- Project Name: `RentalKamera`
- Klik **Create**

### 2. Copy Semua File
- Copy seluruh isi folder `src/` ke dalam folder `src/` project IntelliJ
- Pastikan struktur package sesuai (model, dao, view, util)

### 3. Tambahkan MySQL JDBC Driver
- Download `mysql-connector-j-8.x.x.jar` dari: https://dev.mysql.com/downloads/connector/j/
- Di IntelliJ: **File → Project Structure → Libraries → + → Java**
- Pilih file `.jar` yang sudah didownload
- Klik **OK**

### 4. Setup Database
- Pastikan XAMPP/MySQL sudah berjalan
- Buka phpMyAdmin → Import file `rental_kamera.sql`
- Database akan otomatis terbuat

### 5. Konfigurasi Koneksi
- Buka `src/util/DBConnection.java`
- Sesuaikan:
  ```java
  private static final String USER = "root";     // username MySQL kamu
  private static final String PASSWORD = "";      // password MySQL kamu (kosong jika XAMPP default)
  ```

### 6. Set Main Class & Run
- Klik kanan `Main.java` → **Run 'Main.main()'**
- Atau: **Run → Edit Configurations → Main class: Main**

## Login Default
| Role  | Username | Password |
|-------|----------|----------|
| Admin | admin    | 123      |
| User  | user     | 321      |

## Fitur Aplikasi
- ✅ Login dengan role (Admin / User)
- ✅ Dashboard dengan statistik real-time
- ✅ CRUD Data Kamera (tambah, edit, hapus, filter status)
- ✅ CRUD Data Penyewa
- ✅ Form Transaksi Baru dengan perhitungan otomatis
- ✅ Lihat Detail Transaksi
- ✅ Laporan pendapatan bulan ini
- ✅ Manajemen User (khusus Admin)
