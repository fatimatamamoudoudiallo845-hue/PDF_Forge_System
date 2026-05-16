package com.example.gateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PDFProcessorService {
    private static final Logger log = LoggerFactory.getLogger(PDFProcessorService.class);

    public String extractText(String filePath) {
        log.info("[CORBA-SIM] extractText: {}", filePath);
        return "=== Texte extrait de : " + filePath + " ===\n\n"
             + "Simulation CORBA active.\nOpération : extractText(\"" + filePath + "\")\n"
             + "Timestamp : " + java.time.LocalDateTime.now();
    }

    public boolean protectPDF(String filePath, String password) {
        log.info("[CORBA-SIM] protectPDF: {}", filePath);
        if (filePath == null || filePath.isBlank()) return false;
        if (password == null || password.length() < 4) return false;
        return true;
    }
}
