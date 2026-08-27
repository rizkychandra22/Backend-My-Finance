package com.myfinance.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.myfinance.util.PasswordUtil;
import com.myfinance.util.JwtUtil;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

/**
 * Service class yang berisi seluruh logika bisnis autentikasi user
 * (Registrasi, Login Ganda, Request Reset Token, dan Reset Password).
 */
@ApplicationScoped
public class UserService {

    @Inject
    private MongoDatabase database;

    private MongoCollection<Document> getUserCollection() {
        return database.getCollection("users");
    }

    /**
     * Mendaftarkan pengguna baru ke database MongoDB.
     * @return true jika berhasil.
     * @throws IllegalArgumentException jika email atau nomor telepon sudah terdaftar.
     */
    public boolean register(String name, String email, String phone, String password) {
        MongoCollection<Document> collection = getUserCollection();

        // 1. Cek apakah email atau nomor telepon sudah terdaftar
        Bson filterEmail = Filters.eq("email", email);
        Bson filterPhone = Filters.eq("phone", phone);
        Bson existsFilter = Filters.or(filterEmail, filterPhone);

        if (collection.find(existsFilter).first() != null) {
            throw new IllegalArgumentException("Email atau nomor telepon sudah terdaftar!");
        }

        // 2. Hash password sebelum disimpan
        String passwordHash = PasswordUtil.hashPassword(password);

        // 3. Simpan data user baru ke MongoDB
        Document newUser = new Document()
                .append("name", name)
                .append("email", email)
                .append("phone", phone)
                .append("passwordHash", passwordHash)
                .append("createdAt", System.currentTimeMillis());

        collection.insertOne(newUser);
        return true;
    }

    /**
     * Memproses login menggunakan email ATAU nomor telepon.
     * @param identifier Email atau nomor telepon.
     * @param password Password polos dari input user.
     * @return Token JWT jika sukses, atau null jika gagal.
     */
    public String login(String identifier, String password) {
        MongoCollection<Document> collection = getUserCollection();

        // Cari user yang email atau phone-nya cocok dengan identifier
        Bson userFilter = Filters.or(
                Filters.eq("email", identifier),
                Filters.eq("phone", identifier)
        );

        Document user = collection.find(userFilter).first();
        if (user == null) {
            return null; // User tidak ditemukan
        }

        // Verifikasi kesamaan password
        String passwordHash = user.getString("passwordHash");
        if (PasswordUtil.checkPassword(password, passwordHash)) {
            // Generate token JWT menggunakan identifier (email/phone) sebagai subjeknya
            return JwtUtil.generateToken(identifier);
        }

        return null; // Password salah
    }

    /**
     * Meminta token reset password (lupa password).
     * @param emailOrPhone Email atau no telpon user yang terdaftar.
     * @return true jika user terdaftar dan token berhasil dikirim ke log konsol.
     */
    public boolean requestPasswordReset(String emailOrPhone) {
        MongoCollection<Document> collection = getUserCollection();

        Bson userFilter = Filters.or(
                Filters.eq("email", emailOrPhone),
                Filters.eq("phone", emailOrPhone)
        );

        Document user = collection.find(userFilter).first();
        if (user == null) {
            return false; // User tidak terdaftar
        }

        // Membuat token acak UUID dan menentukan masa berlaku (15 menit dari sekarang)
        String token = UUID.randomUUID().toString();
        long expiry = System.currentTimeMillis() + (15 * 60 * 1000);

        // Update token di dokumen user tersebut di MongoDB
        Bson update = Updates.combine(
                Updates.set("resetToken", token),
                Updates.set("resetTokenExpiry", expiry)
        );
        collection.updateOne(userFilter, update);

        // Simulasi pengiriman link email ke konsol server
        System.out.println("=========================================================================");
        System.out.println("SIMULASI EMAIL LUPA PASSWORD");
        System.out.println("Tujuan: " + emailOrPhone);
        System.out.println("Silakan reset password Anda melalui link berikut:");
        System.out.println("http://localhost:8080/reset-password?token=" + token);
        System.out.println("Token berlaku selama 15 menit.");
        System.out.println("=========================================================================");

        return true;
    }

    /**
     * Mereset password user menggunakan token yang valid dan belum kedaluwarsa.
     * @return true jika berhasil di-reset.
     */
    public boolean resetPassword(String token, String newPassword) {
        MongoCollection<Document> collection = getUserCollection();

        // Cari user dengan token yang cocok dan masa kadaluwarsa lebih besar dari waktu sekarang
        Bson filter = Filters.and(
                Filters.eq("resetToken", token),
                Filters.gt("resetTokenExpiry", System.currentTimeMillis())
        );

        Document user = collection.find(filter).first();
        if (user == null) {
            return false; // Token tidak cocok, kedaluwarsa, atau sudah terpakai
        }

        // Hash password baru
        String newPasswordHash = PasswordUtil.hashPassword(newPassword);

        // Update password baru dan hapus token (unset) agar token tidak bisa dipakai ulang
        Bson update = Updates.combine(
                Updates.set("passwordHash", newPasswordHash),
                Updates.unset("resetToken"),
                Updates.unset("resetTokenExpiry")
        );
        collection.updateOne(filter, update);

        return true;
    }
}
