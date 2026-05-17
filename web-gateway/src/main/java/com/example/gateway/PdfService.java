package com.example.gateway;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class PdfService {

    @Autowired(required = false)
    private PDFModule.PDFProcessor pdfProcessor;

    private void checkAvailable() {
        if (pdfProcessor == null) {
            throw new RuntimeException("CORBA non disponible");
        }
    }

    // ── Ping ──────────────────────────────────────────────────
    public boolean ping() {
        if (pdfProcessor == null) return false;
        try {
            return pdfProcessor.ping();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Créer PDF ─────────────────────────────────────────────
    public byte[] createPDF(String title, String content, String author) throws Exception {
        try {
            checkAvailable();
            return pdfProcessor.createPDF(title, content, author);
        } catch (Exception e) {
            // Fallback local avec PDFBox
            try (PDDocument doc = new PDDocument()) {
                org.apache.pdfbox.pdmodel.PDPage page =
                    new org.apache.pdfbox.pdmodel.PDPage();
                doc.addPage(page);

                org.apache.pdfbox.pdmodel.font.PDType1Font font =
                    org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD;
                org.apache.pdfbox.pdmodel.font.PDType1Font fontNormal =
                    org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;

                try (org.apache.pdfbox.pdmodel.PDPageContentStream cs =
                         new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page)) {

                    // Titre
                    cs.beginText();
                    cs.setFont(font, 20);
                    cs.newLineAtOffset(50, 750);
                    cs.showText(title != null ? title : "");
                    cs.endText();

                    // Auteur
                    cs.beginText();
                    cs.setFont(fontNormal, 12);
                    cs.newLineAtOffset(50, 720);
                    cs.showText("Auteur : " + (author != null ? author : "PDF Forge"));
                    cs.endText();

                    // Contenu (découpage en lignes de 80 chars)
                    if (content != null && !content.isEmpty()) {
                        cs.setFont(fontNormal, 11);
                        String[] words = content.split(" ");
                        StringBuilder line = new StringBuilder();
                        float y = 690;
                        for (String word : words) {
                            if (line.length() + word.length() > 80) {
                                cs.beginText();
                                cs.newLineAtOffset(50, y);
                                cs.showText(line.toString().trim());
                                cs.endText();
                                line = new StringBuilder();
                                y -= 16;
                                if (y < 50) break;
                            }
                            line.append(word).append(" ");
                        }
                        if (line.length() > 0 && y > 50) {
                            cs.beginText();
                            cs.newLineAtOffset(50, y);
                            cs.showText(line.toString().trim());
                            cs.endText();
                        }
                    }
                }

                // Métadonnées
                org.apache.pdfbox.pdmodel.PDDocumentInformation info =
                    doc.getDocumentInformation();
                info.setTitle(title);
                info.setAuthor(author);
                info.setCreator("PDF_Forge_System");

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                doc.save(out);
                return out.toByteArray();
            }
        }
    }

    // ── Fusionner ─────────────────────────────────────────────
    public byte[] mergePDFs(byte[][] pdfFiles) throws Exception {
        try {
            checkAvailable();
            return pdfProcessor.mergePDFs(pdfFiles);
        } catch (Exception e) {
            // Fallback local PDFBox
            PDFMergerUtility merger = new PDFMergerUtility();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            merger.setDestinationStream(out);
            for (byte[] pdf : pdfFiles) {
                merger.addSource(new ByteArrayInputStream(pdf));
            }
            merger.mergeDocuments(null);
            return out.toByteArray();
        }
    }

    // ── Découper ──────────────────────────────────────────────
    public byte[][] splitPDF(byte[] pdfFile, int pagesPerChunk) throws Exception {
        try {
            checkAvailable();
            return pdfProcessor.splitPDF(pdfFile, pagesPerChunk);
        } catch (Exception e) {
            // Fallback local PDFBox
            try (PDDocument doc = PDDocument.load(pdfFile)) {
                Splitter splitter = new Splitter();
                splitter.setSplitAtPage(pagesPerChunk);
                List<PDDocument> parts = splitter.split(doc);
                byte[][] result = new byte[parts.size()][];
                for (int i = 0; i < parts.size(); i++) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    parts.get(i).save(out);
                    parts.get(i).close();
                    result[i] = out.toByteArray();
                }
                return result;
            }
        }
    }

    // ── Extraire pages ────────────────────────────────────────
    public byte[] extractPages(byte[] pdfFile, int[] pages) throws Exception {
        try {
            checkAvailable();
            return pdfProcessor.extractPages(pdfFile, pages);
        } catch (Exception e) {
            // Fallback local PDFBox
            try (PDDocument source = PDDocument.load(pdfFile);
                 PDDocument result = new PDDocument()) {
                for (int page : pages) {
                    int idx = page - 1; // pages 1-indexées → 0-indexées
                    if (idx >= 0 && idx < source.getNumberOfPages()) {
                        result.addPage(source.getPage(idx));
                    }
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                result.save(out);
                return out.toByteArray();
            }
        }
    }

    // ── Supprimer pages ───────────────────────────────────────
    public byte[] deletePages(byte[] pdfFile, int[] pages) throws Exception {
        try {
            checkAvailable();
            return pdfProcessor.deletePages(pdfFile, pages);
        } catch (Exception e) {
            // Fallback local PDFBox
            try (PDDocument source = PDDocument.load(pdfFile);
                 PDDocument result = new PDDocument()) {

                // Construire un Set des pages à supprimer (1-indexé)
                java.util.Set<Integer> toDelete = new java.util.HashSet<>();
                for (int p : pages) toDelete.add(p);

                for (int i = 1; i <= source.getNumberOfPages(); i++) {
                    if (!toDelete.contains(i)) {
                        result.addPage(source.getPage(i - 1));
                    }
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                result.save(out);
                return out.toByteArray();
            }
        }
    }

    // ── Mot de passe ──────────────────────────────────────────
    public byte[] addPassword(byte[] pdfFile, String ownerPwd, String userPwd) throws Exception {
        try {
            checkAvailable();
            return pdfProcessor.addPassword(pdfFile, ownerPwd, userPwd);
        } catch (Exception e) {
            // Fallback local PDFBox
            try (PDDocument doc = PDDocument.load(pdfFile)) {
                AccessPermission ap = new AccessPermission();
                StandardProtectionPolicy policy =
                    new StandardProtectionPolicy(ownerPwd, userPwd, ap);
                policy.setEncryptionKeyLength(128);
                doc.protect(policy);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                doc.save(out);
                return out.toByteArray();
            }
        }
    }

    // ── Convertir en images ───────────────────────────────────
    public String[] convertToImages(byte[] pdfFile, String format, int dpi) throws Exception {
        try {
            checkAvailable();
            return pdfProcessor.convertToImages(pdfFile, format, dpi);
        } catch (Exception e) {
            // Fallback local PDFBox
            try (PDDocument doc = PDDocument.load(pdfFile)) {
                PDFRenderer renderer = new PDFRenderer(doc);
                List<String> images = new ArrayList<>();
                for (int i = 0; i < doc.getNumberOfPages(); i++) {
                    BufferedImage img = renderer.renderImageWithDPI(
                        i, dpi, ImageType.RGB);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIOUtil.writeImage(img, format.toLowerCase(), out, dpi);
                    images.add(Base64.getEncoder().encodeToString(out.toByteArray()));
                }
                return images.toArray(new String[0]);
            }
        }
    }

    // ── Extraire texte ────────────────────────────────────────
    public String extractText(byte[] pdfFile) throws Exception {
        try {
            checkAvailable();
            return pdfProcessor.extractText(pdfFile);
        } catch (Exception e) {
            // Fallback local PDFBox
            try (PDDocument doc = PDDocument.load(pdfFile)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(doc);
            }
        }
    }

    // ── Nombre de pages ───────────────────────────────────────
    public int getPageCount(byte[] pdfFile) throws Exception {
        try {
            checkAvailable();
            return pdfProcessor.getPageCount(pdfFile);
        } catch (Exception e) {
            // Fallback local PDFBox
            try (PDDocument doc = PDDocument.load(pdfFile)) {
                return doc.getNumberOfPages();
            }
        }
    }
}
