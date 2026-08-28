package com.myfinance.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitas untuk mengamankan password menggunakan teknik hashing BCrypt.
 */
public class PasswordUtil {

    /**
     * Melakukan hash pada password polos.
     * @param plainPassword Password mentah dari user.
     * @return String password yang sudah di-hash (aman disimpan di DB).
     */
    public static String hashPassword(String plainPassword) {
        // gensalt() membuat garam acak (salt) untuk menambah kekuatan hash
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Memverifikasi apakah password polos cocok dengan hash-nya.
     * @param plainPassword Password mentah yang diinput saat login.
     * @param hashedPassword Password ter-hash yang diambil dari database.
     * @return true jika cocok, false jika tidak cocok.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
