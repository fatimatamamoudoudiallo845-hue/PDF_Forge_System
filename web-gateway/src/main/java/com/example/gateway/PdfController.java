package com.example.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    // ── Statut ────────────────────────────────────────────────
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean ok = pdfService.ping();
            response.put("gateway", ok ? "OK" : "DÉGRADÉ");
            response.put("corba", ok ? "CONNECTÉ" : "DÉCONNECTÉ");
            response.put("status", ok ? "healthy" : "degraded");
        } catch (Exception e) {
            response.put("gateway", "ERREUR");
            response.put("corba", "ERREUR");
            response.put("status", "down");
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // ── Ping ──────────────────────────────────────────────────
    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean ok = pdfService.ping();
            response.put("status", ok ? "OK" : "CORBA non disponible");
            response.put("corba", ok);
        } catch (Exception e) {
            response.put("status", "ERREUR");
            response.put("corba", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // ── Créer PDF ─────────────────────────────────────────────
    @PostMapping("/create")
    public ResponseEntity<byte[]> createPDF(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "author", defaultValue = "PDF Forge") String author)
            throws Exception {
        byte[] result = pdfService.createPDF(title, content, author);
        return pdfResponse(result, title + ".pdf");
    }

    // ── Fusionner ─────────────────────────────────────────────
    @PostMapping("/merge")
    public ResponseEntity<byte[]> mergePDFs(
            @RequestParam("files") MultipartFile[] files) throws Exception {
        byte[][] pdfFiles = new byte[files.length][];
        for (int i = 0; i < files.length; i++) {
            pdfFiles[i] = files[i].getBytes();
        }
        byte[] result = pdfService.mergePDFs(pdfFiles);
        return pdfResponse(result, "merged.pdf");
    }

    // ── Découper ──────────────────────────────────────────────
    @PostMapping("/split")
    public ResponseEntity<Map<String, Object>> splitPDF(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "pagesPerChunk", defaultValue = "1") int pagesPerChunk)
            throws Exception {
        byte[][] parts = pdfService.splitPDF(file.getBytes(), pagesPerChunk);
        Map<String, Object> response = new HashMap<>();
        response.put("parts", parts.length);
        response.put("message", "PDF découpé en " + parts.length + " parties");
        return ResponseEntity.ok(response);
    }

    // ── Extraire pages ────────────────────────────────────────
    @PostMapping("/extract-pages")
    public ResponseEntity<byte[]> extractPages(
            @RequestParam("file") MultipartFile file,
            @RequestParam("pages") int[] pages) throws Exception {
        byte[] result = pdfService.extractPages(file.getBytes(), pages);
        return pdfResponse(result, "extracted.pdf");
    }

    // ── Supprimer pages ───────────────────────────────────────
    @PostMapping("/delete-pages")
    public ResponseEntity<byte[]> deletePages(
            @RequestParam("file") MultipartFile file,
            @RequestParam("pages") int[] pages) throws Exception {
        byte[] result = pdfService.deletePages(file.getBytes(), pages);
        return pdfResponse(result, "modified.pdf");
    }

    // ── Mot de passe ──────────────────────────────────────────
    @PostMapping("/add-password")
    public ResponseEntity<byte[]> addPassword(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ownerPassword") String ownerPwd,
            @RequestParam("userPassword") String userPwd) throws Exception {
        byte[] result = pdfService.addPassword(file.getBytes(), ownerPwd, userPwd);
        return pdfResponse(result, "protected.pdf");
    }

    // ── Convertir en images ───────────────────────────────────
    @PostMapping("/convert-to-images")
    public ResponseEntity<Map<String, Object>> convertToImages(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "PNG") String format,
            @RequestParam(value = "dpi", defaultValue = "150") int dpi) throws Exception {
        String[] images = pdfService.convertToImages(file.getBytes(), format, dpi);
        Map<String, Object> response = new HashMap<>();
        response.put("count", images.length);
        response.put("images", images);
        return ResponseEntity.ok(response);
    }

    // ── Extraire texte ────────────────────────────────────────
    @PostMapping("/extract-text")
    public ResponseEntity<Map<String, Object>> extractText(
            @RequestParam("file") MultipartFile file) throws Exception {
        String text = pdfService.extractText(file.getBytes());
        Map<String, Object> response = new HashMap<>();
        response.put("text", text);
        response.put("characters", text.length());
        return ResponseEntity.ok(response);
    }

    // ── Nombre de pages ───────────────────────────────────────
    @PostMapping("/page-count")
    public ResponseEntity<Map<String, Object>> getPageCount(
            @RequestParam("file") MultipartFile file) throws Exception {
        int count = pdfService.getPageCount(file.getBytes());
        Map<String, Object> response = new HashMap<>();
        response.put("pageCount", count);
        return ResponseEntity.ok(response);
    }

    // ── Utilitaire ────────────────────────────────────────────
    private ResponseEntity<byte[]> pdfResponse(byte[] data, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(data.length);
        return ResponseEntity.ok().headers(headers).body(data);
    }
}

