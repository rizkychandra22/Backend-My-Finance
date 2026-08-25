package com.myfinance.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * Mengelola koneksi MongoDB secara global menggunakan CDI (Contexts and Dependency Injection).
 * Kelas ini akan membaca kredensial secara dinamis dari Environment Variable.
 */
@ApplicationScoped
public class MongoConfig {

    private MongoClient mongoClient;
    private MongoDatabase database;

    @PostConstruct
    public void init() {
        // Membaca URI koneksi dari Environment Variable (misalnya dari Railway atau OS lokal)
        // Default menggunakan localhost jika variable lingkungan tidak disetel
        String mongoUri = System.getenv("MONGODB_URI");
        if (mongoUri == null || mongoUri.isEmpty()) {
            mongoUri = "mongodb://localhost:27017";
        }
        
        System.out.println("Menghubungkan ke MongoDB: " + mongoUri);
        this.mongoClient = MongoClients.create(mongoUri);
        
        // Membaca nama database dari env, default menggunakan "myfinance"
        String dbName = System.getenv("MONGODB_DB_NAME");
        if (dbName == null || dbName.isEmpty()) {
            dbName = "myfinance";
        }
        this.database = mongoClient.getDatabase(dbName);
    }

    /**
     * Memungkinkan kita untuk langsung menyuntikkan (Inject) objek MongoDatabase ke kelas lain.
     * Contoh penggunaan di Controller/Service:
     * 
     * @Inject
     * private MongoDatabase database;
     */
    @Produces
    public MongoDatabase getDatabase() {
        return database;
    }

    @PreDestroy
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Koneksi ke MongoDB berhasil ditutup.");
        }
    }
}
