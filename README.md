# ☕ My-Finance Java Backend (Enterprise RESTful API)

Backend layanan web (*Web Services*) untuk aplikasi **My-Finance**, dibangun menggunakan standar **Java EE 6** (JAX-RS RESTful API), **MongoDB**, dan dikompilasi menggunakan **Java 17** dengan **Maven**.

> [!IMPORTANT]
> **Dokumen Spesifikasi Utama:**
> Untuk memahami seluruh aturan bisnis keuangan, tipe rekening, kategori transaksi, dan skema database proyek ini:
> 👉 **[Baca Spesifikasi Lengkap My-Finance](file:///d:/%21%60Learn-Programmer%60/My-Finance/Java-Backend/my_finance_specification.md)**

---

## 🛠️ Spesifikasi Teknologi (Tech Stack)

*   **Runtime Environment:** Java JDK 17 (LTS)
*   **Application Server:** WildFly 26.1.3.Final (Mendukung spesifikasi `javax.*` dan Java 17)
*   **REST API Framework:** JAX-RS (RESTEasy bawaan WildFly)
*   **Contexts & Dependency Injection (CDI):** Weld (Aktivasi via `WEB-INF/beans.xml`)
*   **Database Driver:** MongoDB Java Driver Sync (4.11.1)
*   **Security:** JJWT (0.11.5) untuk token stateless & jBCrypt (0.4) untuk enkripsi sandi.
*   **Build Tool:** Maven (Lokal di `.maven/`)

---

## 🔒 Fitur Autentikasi yang Telah Diimplementasikan
1.  **Pendaftaran (Register):** Mencegah duplikasi email/nomor telepon, melakukan *hash* password satu arah menggunakan BCrypt, dan menyimpan dokumen ke koleksi `users`.
2.  **Login Ganda (Dual Login):** Pengguna bisa login menggunakan **Email** ATAU **Nomor Telepon** sebagai identitas utama.
3.  **Sesi Keamanan JWT:** Login sukses mengembalikan token JWT yang ditandatangani secara kriptografis menggunakan algoritma HS256.
4.  **Lupa Password (Reset Flow):** Membuat token pemulihan acak UUID berdurasi 15 menit, mensimulasikan email pemulihan lewat log konsol, dan memverifikasi token sebelum menyetel sandi baru.

---

## 📂 Struktur Proyek Backend
```text
Java-Backend/
│
├── .maven/                     # Maven Lokal (Self-contained build tool)
├── src/main/java/com/myfinance/
│   ├── config/
│   │   └── MongoConfig.java    # Koneksi DB Dinamis via CDI
│   ├── controller/
│   │   ├── HealthController.java # Health check endpoint
│   │   └── AuthController.java   # Endpoint REST API (/api/auth/*)
│   ├── service/
│   │   └── UserService.java    # Logika bisnis inti user & keamanan
│   ├── util/
│   │   ├── PasswordUtil.java   # Hashing & verifikasi sandi BCrypt
│   │   └── JwtUtil.java        # Manajemen token JWT (Base64 Secret)
│   └── JaxRsActivator.java     # Aktivasi JAX-RS REST Path (/api)
│
├── src/main/webapp/WEB-INF/
│   └── beans.xml               # Berkas aktivasi Dependency Injection (CDI)
│
├── pom.xml                     # Maven configuration & dependencies
├── notes.md                    # Catatan panduan kompilasi lokal
└── .env                        # Catatan referensi variabel lingkungan (Git Ignored)
```

---

## 💻 Panduan Pengembangan & Pengujian Lokal

Seluruh panduan langkah-demi-langkah mengenai cara menyalakan server WildFly lokal, melakukan kompilasi kodingan Java, dan memindahkannya ke server (*deploy*) dapat Anda pelajari langsung di:
👉 **[Buka Catatan Panduan Build & Deploy Lokal](file:///d:/%21%60Learn-Programmer%60/My-Finance/Java-Backend/notes.md)**
