package com.myfinance.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

/**
 * Utilitas untuk men-generate dan memvalidasi JSON Web Token (JWT).
 */
public class JwtUtil {

    // Kunci rahasia minimal harus 256-bit (32 karakter/byte) untuk algoritma HS256
    private static final String SECRET_STRING = "myfinance-super-secret-key-1234567890-must-be-long-enough-32bytes";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());
    
    // Masa berlaku token JWT: 24 jam (dalam milidetik)
    private static final long EXPIRATION_TIME_MS = 24 * 60 * 60 * 1000;

    /**
     * Membuat token JWT baru berdasarkan identitas user (email/nomor telepon).
     * @param subject Identitas user (email atau no telpon).
     * @return String token JWT yang ditandatangani.
     */
    public static String generateToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_MS);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Memvalidasi token JWT dan mengambil identitas user (subject) dari dalamnya.
     * @param token Token JWT yang dikirimkan klien.
     * @return Identitas user (email/no telpon), atau null jika token tidak valid/kedaluwarsa.
     */
    public static String validateTokenAndGetSubject(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Memastikan token belum kedaluwarsa
            if (claims.getExpiration().before(new Date())) {
                return null;
            }

            return claims.getSubject();
        } catch (Exception e) {
            // Token tidak valid, telah dimodifikasi, atau kedaluwarsa
            return null;
        }
    }
}
