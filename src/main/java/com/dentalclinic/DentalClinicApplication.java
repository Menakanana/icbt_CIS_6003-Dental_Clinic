package com.dentalclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@SpringBootApplication
public class DentalClinicApplication {

    public static void main(String[] args) {
        loadDotEnvFile();
        SpringApplication.run(DentalClinicApplication.class, args);
    }

    private static void loadDotEnvFile() {
        File envFile = new File(".env");
        if (envFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
                        continue;
                    }
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (System.getProperty(key) == null && System.getenv(key) == null) {
                        System.setProperty(key, value);
                    }
                }
                System.out.println("[APPLICATION] Environment credentials loaded from .env file successfully.");
            } catch (Exception ex) {
                System.err.println("[APPLICATION] Warning: Could not load .env file: " + ex.getMessage());
            }
        }
    }
}
