package com.example.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PdfService {

    @Autowired(required = false)
    private PDFModule.PDFProcessor pdfProcessor;

    private void checkAvailable() {
        if (pdfProcessor == null) {
            throw new RuntimeException("CORBA non disponible");
        }
    }

    public byte[] mergePDFs(byte[][] pdfFiles) throws Exception {
        checkAvailable();
        return pdfProcessor.mergePDFs(pdfFiles);
    }

    public byte[][] splitPDF(byte[] pdfFile, int pagesPerChunk) throws Exception {
        checkAvailable();
        return pdfProcessor.splitPDF(pdfFile, pagesPerChunk);
    }

    public byte[] extractPages(byte[] pdfFile, int[] pages) throws Exception {
        checkAvailable();
        return pdfProcessor.extractPages(pdfFile, pages);
    }

    public byte[] deletePages(byte[] pdfFile, int[] pages) throws Exception {
        checkAvailable();
        return pdfProcessor.deletePages(pdfFile, pages);
    }

    public byte[] addPassword(byte[] pdfFile, String ownerPwd, String userPwd) throws Exception {
        checkAvailable();
        return pdfProcessor.addPassword(pdfFile, ownerPwd, userPwd);
    }

    public String[] convertToImages(byte[] pdfFile, String format, int dpi) throws Exception {
        checkAvailable();
        return pdfProcessor.convertToImages(pdfFile, format, dpi);
    }

    public String extractText(byte[] pdfFile) throws Exception {
        checkAvailable();
        return pdfProcessor.extractText(pdfFile);
    }

    public byte[] createPDF(String title, String content, String author) throws Exception {
        checkAvailable();
        return pdfProcessor.createPDF(title, content, author);
    }

    public int getPageCount(byte[] pdfFile) throws Exception {
        checkAvailable();
        return pdfProcessor.getPageCount(pdfFile);
    }

    public boolean ping() {
        if (pdfProcessor == null) return false;
        return pdfProcessor.ping();
    }
}
