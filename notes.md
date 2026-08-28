# 📝 Panduan Build & Deploy Lokal - My Finance Backend

Berkas ini berisi petunjuk cara merakit (build) dan menjalankan (deploy) kode Java EE 6 Anda di komputer lokal (*localhost*) agar Anda tidak lupa saat proses belajar.

---

## 🚀 Langkah 1: Pastikan Layanan Database & Server Aktif

1.  **MongoDB Lokal:** Pastikan MongoDB Server Anda menyala. 
    *(Anda bisa membukanya melalui MongoDB Compass -> hubungkan ke `mongodb://localhost:27017`)*.
2.  **Server WildFly:** Nyalakan server WildFly Anda di terminal:
    ```powershell
    cd D:\wildfly-2613f\bin
    .\standalone.bat
    ```
    *Biarkan terminal ini tetap berjalan di latar belakang.*

---

## 📦 Langkah 2: Build Proyek (Merakit file `.war`)

Buka terminal kedua di folder root `Java-Backend/` Anda, lalu jalankan perintah kompilasi menggunakan Maven lokal:

```powershell
.\.maven\bin\mvn clean package -DskipTests
```
*Tunggu hingga proses selesai dan memunculkan log berwarna hijau:* **`BUILD SUCCESS`**.

---

## 🚚 Langkah 3: Deploy Berkas ke WildFly (Hot-Redeploy)

Setelah rilis sukses, jalankan perintah di bawah ini untuk memindahkan file hasil kompilasi ke folder WildFly:

```powershell
Copy-Item -Path 'D:\!`Learn-Programmer`\My-Finance\Java-Backend\target\my-finance-backend.war' -Destination 'D:\wildfly-2613f\standalone\deployments\ROOT.war' -Force
```
*WildFly akan otomatis mendeteksi perubahan berkas dan me-load ulang kode Anda dalam 2 detik tanpa Anda harus mematikan/me-restart server WildFly.*

---

## 🔄 Kapan Harus Me-restart Server WildFly?

*   **TIDAK PERLU RESTART:** Saat Anda mengubah kode Java biasa di dalam folder `src/`. Cukup lakukan Langkah 2 dan Langkah 3 saja.
*   **WAJIB RESTART:** Hanya jika Anda mengedit file konfigurasi WildFly (seperti menyetel environment variable di file `D:\wildfly-2613f\bin\standalone.conf.bat`).
    *   *Cara restart:* Tekan `Ctrl + C` pada terminal WildFly, ketik `Y` lalu tekan Enter untuk mematikan. Setelah itu jalankan kembali `.\standalone.bat`.
