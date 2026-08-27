package com.myfinance.controller;

import com.myfinance.service.UserService;
import org.bson.Document;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Endpoint JAX-RS untuk mengelola autentikasi
 * Base Path: /api/auth
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    private UserService userService;

    /**
     * Mendaftarkan pengguna baru.
     * POST http://localhost:8080/api/auth/register
     */
    @POST
    @Path("/register")
    public Response register(Document registerRequest) {
        try {
            String name = registerRequest.getString("name");
            String email = registerRequest.getString("email");
            String phone = registerRequest.getString("phone");
            String password = registerRequest.getString("password");

            // Validasi input wajib
            if (name == null || email == null || phone == null || password == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Semua field (name, email, phone, password) wajib diisi!\"}").build();
            }

            userService.register(name, email, phone, password);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Pendaftaran berhasil!\"}").build();
        } catch (IllegalArgumentException e) {
            // Error jika email/phone sudah terdaftar
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Terjadi kesalahan server: " + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Memproses login menggunakan email ATAU nomor telepon.
     * POST http://localhost:8080/api/auth/login
     */
    @POST
    @Path("/login")
    public Response login(Document loginRequest) {
        try {
            String identifier = loginRequest.getString("identifier"); // email atau no telepon
            String password = loginRequest.getString("password");

            if (identifier == null || password == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Field identifier dan password wajib diisi!\"}").build();
            }

            String token = userService.login(identifier, password);
            if (token == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Email/Nomor Telepon atau Password salah!\"}").build();
            }

            return Response.ok("{\"token\": \"" + token + "\", \"message\": \"Login berhasil!\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Terjadi kesalahan server: " + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Meminta tautan reset password (lupa password).
     * POST http://localhost:8080/api/auth/reset-request
     */
    @POST
    @Path("/reset-request")
    public Response resetRequest(Document resetRequest) {
        try {
            String emailOrPhone = resetRequest.getString("emailOrPhone");
            if (emailOrPhone == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Field emailOrPhone wajib diisi!\"}").build();
            }

            boolean success = userService.requestPasswordReset(emailOrPhone);
            if (!success) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Email atau nomor telepon tidak terdaftar!\"}").build();
            }

            return Response.ok("{\"message\": \"Tautan reset password berhasil dibuat! Periksa log konsol server Anda.\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Terjadi kesalahan server: " + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Melakukan pembaruan password dengan menyertakan token yang valid.
     * POST http://localhost:8080/api/auth/reset-password
     */
    @POST
    @Path("/reset-password")
    public Response resetPassword(Document resetRequest) {
        try {
            String token = resetRequest.getString("token");
            String newPassword = resetRequest.getString("newPassword");

            if (token == null || newPassword == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Field token dan newPassword wajib diisi!\"}").build();
            }

            boolean success = userService.resetPassword(token, newPassword);
            if (!success) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Token reset salah, kadaluwarsa, atau sudah pernah digunakan!\"}").build();
            }

            return Response.ok("{\"message\": \"Password berhasil diperbarui! Silakan login kembali.\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Terjadi kesalahan server: " + e.getMessage() + "\"}").build();
        }
    }
}
