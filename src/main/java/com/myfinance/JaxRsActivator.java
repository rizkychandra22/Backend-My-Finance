package com.myfinance;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Mengaktifkan JAX-RS dan menyetel path dasar REST API ke /api
 */
@ApplicationPath("/api")
public class JaxRsActivator extends Application {
    // Kelas kosong ini digunakan untuk mengaktifkan RESTful web service
}
