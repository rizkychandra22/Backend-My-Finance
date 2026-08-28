# Spesifikasi Sistem & Arsitektur Aplikasi My-Finance

Aplikasi **My-Finance** adalah platform pengelolaan keuangan pribadi (*Personal Finance Management*) berbasis web *fullstack* yang dirancang menggunakan pendekatan **Enterprise Architecture** (**Angular + PrimeNG**, **Java EE 6 JAX-RS**, dan **MongoDB**). 

Aplikasi ini berfokus pada **pemisahan peran rekening fisik (*3-Bucket Account Strategy*)**, **pencatatan transaksi presisi tanpa distorsi *cashflow***, dan **analisis visual bertingkat (*Drill-Down Analytics*)**.

---

## 1. Arsitektur & Spesifikasi Teknologi (*Tech Stack*)

Aplikasi menggunakan arsitektur terpisah (*decoupled architecture*) dengan integrasi RESTful API yang aman dan *stateless*.

### Spesifikasi Layer Utama
* **Frontend:** Angular 18, PrimeNG v18+ UI Component Suite, Tailwind CSS, Chart.js, RxJS.
* **Backend:** Java EE 6 (JAX-RS RESTful API Web Services, EJB Stateless Session Beans untuk pemrosesan transaksi atomik, CDI Inject).
* **Database:** MongoDB NoSQL (`my_finance_db`), memanfaatkan **MongoDB Aggregation Framework** dan operasi update atomik (`$inc`).
* **Security & Auth:** Autentikasi berbasis **JSON Web Token (JWT)** *stateless* dengan durasi *session* panjang, serta enkripsi sandi **BCrypt**.

---

## 2. Struktur Data & Model Kategori Sistem

### A. Tipe Rekening (`TipeRekening`)
Digunakan untuk mengelompokkan jenis entitas penyimpan saldo:
* **`BANK`**: Rekening perbankan konvensional dan digital (BCA, Mandiri, Bank Jago, Seabank, Superbank).
* **`E_WALLET`**: Dompet digital operasional (Gopay, DANA, OVO, ShopeePay).

### B. Tipe Transaksi (`TipeTransaksi`)
Determinan arah arus kas pada sistem backend:
* **`INCOME`**: Transaksi masuk yang menambah total saldo rekening tujuan dan menambah agregat pendapatan.
* **`EXPENSE`**: Transaksi keluar yang memotong saldo rekening asal dan menambah agregat pengeluaran.
* **`MUTASI`**: Pemindahan saldo antar-rekening internal (misal: BCA ke Seabank atau BCA ke Gopay). **Dinetralkan dari statistik untung-rugi (*Net Cashflow*)** agar tidak merusak laporan keuangan.

---

## 3. Skema Kategori Transaksi (Pemasukan vs Pengeluaran)

### A. Transaksi Pemasukan (`INCOME`)
Menggunakan 2 atribut klasifikasi untuk pencatatan yang presisi:

1. **Kategori Sumber (`KategoriSumberIncome`)**:
   * **`WORK_SALARY`**: Pemasukan dari hasil kerja, layanan, proyek, atau gaji rutin.
   * **`PASSIVE_INCOME`**: Hasil pendapatan pasif/investasi.
   * **`REWARDS`**: Bonus kinerja, THR, insentif, cashback e-wallet & promo.
   * **`ASSET_SALE`**: Hasil penjualan barang bekas/pribadi.

2. **Kategori Jenis (`KategoriJenisIncome`)**:
   * **`PERSONAL`**: Pemasukan murni urusan pribadi (gaji utama kampus, bonus pribadi).
   * **`FREELANCE`**: Pemasukan dari pekerjaan sampingan (proyek web dev, jasa pasang wifi).
   * **`ORGANIZATION`**: Pemasukan dari kelolaan organisasi/komunitas (iuran Cloudinary UKM-LISES, dll.).

### B. Transaksi Pengeluaran (`EXPENSE`)
Murni menggunakan **1 Kategori Sumber** tanpa Kategori Jenis (seluruh pengeluaran bersifat personal/konsumtif):

1. **Kategori Sumber (`KategoriSumberExpense`)**:
   * **`DAILY_EXPENSE`**: Makan, bensin, belanja kebutuhan harian/pokok.
   * **`SUBSCRIPTION`**: Gemini AI Pro, internet, software & tagihan rutin.
   * **`SHOPPING`**: Belanja barang (marketplace online / offline).
   * **`ENTERTAINMENT`**: Hiburan, jajan, rokok, nongkrong, hobi/alat musik.
   * **`HEALTH_CARE`**: Obat-obatan, vitamin, biaya dokter.
   * **`SOCIAL_GIVING`**: Sedekah, infaq, kado, kondangan.
   * **`MAINTENANCE`**: Perawatan/servis global (motor, laptop, HP, gitar, dll.).
   * **`OPERATIONAL`**: Biaya operasional proyek/kebutuhan teknis (domain/hosting awal).

---

## 4. Fitur Utama & Modul Aplikasi

### 1. Modul Strategi Rekening (*3-Bucket Account Strategy*)
Sistem mengelompokkan rekening ke dalam fungsi spesifik untuk mengontrol alur dana:
* **Vault Account (Seabank / Superbank):** Dedicated Rekening Khusus Tabungan & Uang Dingin yang terkunci dari aktivitas harian.
* **Daily Pocket (BCA / Mandiri):** Rekening operasional harian, tarik tunai, transaksi QRIS, dan kebutuhan konsumtif.
* **Subscription Pocket (Bank Jago):** Rekening khusus pembayaran otomatis tagihan rutin (*Auto-Debit* Gemini AI Pro, internet, lisensi software).

### 2. Modul Pencatatan Transaksi Smart Engine
* **Form Input 3-Tab (INCOME, EXPENSE, MUTASI):** Antarmuka responsif dengan `p-inputNumber` bertanda `Rp` dan format titik ribuan otomatis.
* **Struktur Form Dinamis:**
  * **Tab INCOME:** Menampilkan dropdown *Kategori Sumber*, *Kategori Jenis*, *Tipe Rekening* (`BANK`/`E_WALLET`), dan *Akun Rekening Spesifik*.
  * **Tab EXPENSE:** Menampilkan dropdown *Kategori Sumber*, *Tipe Rekening* (`BANK`/`E_WALLET`), dan *Akun Rekening Spesifik* (tanpa Kategori Jenis).
  * **Tab MUTASI:** Menampilkan pasangan *Rekening Asal* (Tipe & Akun) dan *Rekening Tujuan* (Tipe & Akun).
* **Atomic Balance Update:** Setiap input transaksi menjalankan operasi atomik `$inc` ke MongoDB sehingga saldo rekening diperbarui secara *real-time*.

### 3. Modul Tabungan (*Dedicated Savings Vault*)
* Menampilkan *progress bar* dan target nominal tabungan berdasarkan total akumulasi saldo yang berada pada Rekening Khusus Tabungan (Seabank/Superbank).
* Memungkinkan pemantauan progres dana dingin tanpa perlu pemisahan fisik yang membingungkan.

### 4. Modul Analitik & Dashboard (*Drill-Down Analytics*)
Visualisasi berbasis **Chart.js** dengan 3 tingkatan analisis:
* **Level 1 (Global Macro View):** Ringkasan Total Kekayaan (*Total Wealth*), serta Grafik Cashflow Bulanan (Pemasukan vs Pengeluaran).
* **Level 2 (Group Cashflow View):** Perbandingan proporsi dan statistik alur dana kelompok **BANK** vs **E-WALLET**.
* **Level 3 (Individual Account Deep Dive):** Detail breakdown persentase pengeluaran dan histori mutasi khusus pada 1 rekening tertentu.

---

## 5. Skema Database MongoDB (`my_finance_db`)

### A. Koleksi `rekening`
Menyimpan entitas rekening fisik dan dompet digital beserta saldo *real-time*:

```json
{
  "_id": ObjectId("65f1a2b3c4d5e6f7a8b9c001"),
  "userId": "65f1a2b3c4d5e6f7a8b9c000",
  "namaBank": "BCA",
  "tipeRekening": "BANK",
  "fungsiKhusus": "DAILY_OPERATIONAL",
  "saldoSaatIni": 5000000.0,
  "keterangan": "Rekening Utama Transaksi Harian"
}
```

### B. Koleksi `transaksi`
Menyimpan riwayat mutasi pergerakan uang.

```json
{
  "_id": ObjectId("65f1a2b3c4d5e6f7a8b9c002"),
  "userId": "65f1a2b3c4d5e6f7a8b9c000",
  "tipeTransaksi": "INCOME",
  "kategoriSumberIncome": "WORK_SALARY",
  "kategoriJenisIncome": "FREELANCE",
  "tipeRekeningTujuan": "BANK",
  "rekeningTujuanId": ObjectId("65f1a2b3c4d5e6f7a8b9c001"),
  "nominal": 1500000.0,
  "deskripsi": "Pelunasan Project Web Dev Company Profile",
  "tanggal": "2026-08-28T10:00:00Z"
}
```

## 6. Alur Kerja Logika Sistem (*Workflow Example*)

1. **User Input di Form Angular:**
   * User memilih Tab `INCOME` atau `EXPENSE`.
   * **Pilih Tipe Rekening:** User memilih Tipe Rekening (`BANK` atau `E_WALLET`).
   * **Pilih Akun Rekening:** Dropdown akun otomatis memfilter dan menampilkan daftar akun yang sesuai (misal memilih `E_WALLET` -> dropdown menampilkan *Gopay, OVO, DANA, ShopeePay*).
   * User memilih Kategori Sumber (`PERSONAL`, `FREELANCE`, `ORGANIZATION`), Kategori Transaksi, Nominal, dan Deskripsi, lalu menyimpan transaksi.

2. **HTTP POST Request ke Java EE 6 REST API:**
   * Backend memvalidasi Token Auth JWT dan hak akses pemilik rekening.
   * Backend mengubah saldo rekening asal/tujuan yang dipilih secara atomik (`$inc`).
   * Backend mencatat dokumen riwayat transaksi lengkap dengan reference ID akun dan tipe rekening ke koleksi `transaksi` MongoDB.

3. **Dashboard Analytics Update:**
   * Tabel histori mutasi pada rekening spesifik langsung ter-update secara *real-time*.
   * Laporan Grafik Cashflow Global dapat difilter berdasarkan Tipe Rekening (`BANK` vs `E_WALLET`) maupun Kategori Sumber (`PERSONAL`, `FREELANCE`, `ORGANIZATION`).
