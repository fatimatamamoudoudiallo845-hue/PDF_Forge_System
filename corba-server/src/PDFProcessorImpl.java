import PDFModule.PDFProcessorPOA;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;

public class PDFProcessorImpl extends PDFProcessorPOA {
    @Override
    public String extractText(String filePath) {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            return new PDFTextStripper().getText(document);
        } catch (Exception e) {
            return "Erreur Linux : " + e.getMessage();
        }
    }

    @Override
    public boolean protectPDF(String filePath, String password) {
        // Logique de protection ici
        return true;
    }
}
