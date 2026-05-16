package server;

import PDFModule.PDFProcessorPOA;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class PDFProcessorImpl extends PDFProcessorPOA {

    // 1. FUSION
    @Override
    public byte[] mergePDFs(byte[][] pdfFiles) throws PDFModule.PDFException {
        System.out.println("[CORBA] → mergePDFs");
        try (PDDocument merged = new PDDocument()) {
            for (byte[] data : pdfFiles) {
                try (PDDocument doc = PDDocument.load(data)) {
                    for (PDPage page : doc.getPages())
                        merged.addPage(page);
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            merged.save(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new PDFModule.PDFException("Erreur fusion : " + e.getMessage(), 100);
        }
    }

    // 2. DÉCOUPAGE
    @Override
    public byte[][] splitPDF(byte[] pdfFile, int pagesPerChunk)
            throws PDFModule.PDFException {
        System.out.println("[CORBA] → splitPDF");
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
        } catch (Exception e) {
            throw new PDFModule.PDFException("Erreur découpage : " + e.getMessage(), 200);
        }
    }

    // 3. EXTRACTION DE PAGES
    @Override
    public byte[] extractPages(byte[] pdfFile, int[] pages)
            throws PDFModule.PDFException, PDFModule.PageNotFoundException {
        System.out.println("[CORBA] → extractPages");
        try (PDDocument source = PDDocument.load(pdfFile);
             PDDocument result = new PDDocument()) {
            int total = source.getNumberOfPages();
            for (int p : pages) {
                if (p < 1 || p > total)
                    throw new PDFModule.PageNotFoundException("Page " + p + " introuvable", p);
                result.addPage(source.getPage(p - 1));
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            result.save(out);
            return out.toByteArray();
        } catch (PDFModule.PageNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PDFModule.PDFException("Erreur extraction : " + e.getMessage(), 300);
        }
    }

    // 4. SUPPRESSION DE PAGES
    @Override
    public byte[] deletePages(byte[] pdfFile, int[] pages)
            throws PDFModule.PDFException, PDFModule.PageNotFoundException {
        System.out.println("[CORBA] → deletePages");
        try (PDDocument doc = PDDocument.load(pdfFile)) {
            int total = doc.getNumberOfPages();
            List<Integer> toDelete = new ArrayList<>();
            for (int p : pages) {
                if (p < 1 || p > total)
                    throw new PDFModule.PageNotFoundException("Page " + p + " introuvable", p);
                toDelete.add(p - 1);
            }
            toDelete.sort(Collections.reverseOrder());
            for (int idx : toDelete) doc.removePage(idx);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (PDFModule.PageNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PDFModule.PDFException("Erreur suppression : " + e.getMessage(), 400);
        }
    }

    // 5. MOT DE PASSE
    @Override
    public byte[] addPassword(byte[] pdfFile, String ownerPwd, String userPwd)
            throws PDFModule.PDFException, PDFModule.InvalidPasswordException {
        System.out.println("[CORBA] → addPassword");
        if (ownerPwd == null || ownerPwd.isEmpty() ||
            userPwd  == null || userPwd.isEmpty())
            throw new PDFModule.InvalidPasswordException("Mots de passe vides");
        try (PDDocument doc = PDDocument.load(pdfFile)) {
            AccessPermission ap = new AccessPermission();
            StandardProtectionPolicy policy =
                new StandardProtectionPolicy(ownerPwd, userPwd, ap);
            policy.setEncryptionKeyLength(128);
            doc.protect(policy);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new PDFModule.PDFException("Erreur protection : " + e.getMessage(), 500);
        }
    }

    // 6. CONVERSION PDF → IMAGES
    @Override
    public String[] convertToImages(byte[] pdfFile, String format, int dpi)
            throws PDFModule.PDFException, PDFModule.ConversionException {
        System.out.println("[CORBA] → convertToImages");
        try (PDDocument doc = PDDocument.load(pdfFile)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            int count = doc.getNumberOfPages();
            String[] images = new String[count];
            float scale = dpi / 72.0f;
            String fmt = (format == null || format.isEmpty()) ? "PNG" : format.toUpperCase();
            for (int i = 0; i < count; i++) {
                BufferedImage img = renderer.renderImage(i, scale);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(img, fmt, out);
                images[i] = Base64.getEncoder().encodeToString(out.toByteArray());
            }
            return images;
        } catch (Exception e) {
            throw new PDFModule.ConversionException("Erreur conversion : " + e.getMessage());
        }
    }

    // 7. EXTRACTION DE TEXTE
    @Override
    public String extractText(byte[] pdfFile) throws PDFModule.PDFException {
        System.out.println("[CORBA] → extractText");
        try (PDDocument doc = PDDocument.load(pdfFile)) {
            return new PDFTextStripper().getText(doc);
        } catch (Exception e) {
            throw new PDFModule.PDFException("Erreur extraction texte : " + e.getMessage(), 700);
        }
    }

    // 8. CRÉATION DE PDF
    @Override
    public byte[] createPDF(String title, String content, String author)
            throws PDFModule.PDFException {
        System.out.println("[CORBA] → createPDF : " + title);
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDDocumentInformation info = doc.getDocumentInformation();
            info.setTitle(title);
            info.setAuthor(author);
            info.setCreator("PDF_Forge CORBA Server");

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 20);
                cs.newLineAtOffset(50, 780);
                cs.showText(title != null ? title : "Sans titre");
                cs.endText();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                cs.newLineAtOffset(50, 755);
                cs.showText("Auteur : " + (author != null ? author : "Inconnu"));
                cs.endText();

                cs.moveTo(50, 745); cs.lineTo(545, 745); cs.stroke();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 11);
                cs.setLeading(16f);
                cs.newLineAtOffset(50, 725);
                String[] lines = content != null ? content.split("\n") : new String[]{"(vide)"};
                for (String line : lines) {
                    while (line.length() > 90) {
                        cs.showText(line.substring(0, 90));
                        cs.newLine();
                        line = line.substring(90);
                    }
                    cs.showText(line);
                    cs.newLine();
                }
                cs.endText();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 8);
                cs.newLineAtOffset(50, 30);
                cs.showText("PDF_Forge · Serveur CORBA 1.8 · Apache PDFBox");
                cs.endText();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new PDFModule.PDFException("Erreur création : " + e.getMessage(), 800);
        }
    }

    // UTILITAIRES
    @Override
    public int getPageCount(byte[] pdfFile) throws PDFModule.PDFException {
        try (PDDocument doc = PDDocument.load(pdfFile)) {
            return doc.getNumberOfPages();
        } catch (Exception e) {
            throw new PDFModule.PDFException("Erreur lecture : " + e.getMessage(), 900);
        }
    }

    @Override
    public boolean ping() {
        System.out.println("[CORBA] ping → pong ✓");
        return true;
    }
}
