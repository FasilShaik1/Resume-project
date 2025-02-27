package maker.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.Style;

import maker.model.ResumeModel;
import maker.service.ResumeService;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping
    public ResponseEntity<ResumeModel> saveResume(@RequestBody ResumeModel resume) {
        ResumeModel savedResume = resumeService.saveResume(resume);
        return new ResponseEntity<>(savedResume, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeModel> getResumeById(@PathVariable int id) {
        ResumeModel resume = resumeService.getResumeById(id);
        return resume != null ? ResponseEntity.ok(resume) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<ResumeModel>> getAllResumes() {
        List<ResumeModel> resumes = resumeService.getAllResumes();
        return ResponseEntity.ok(resumes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResumeModel> updateResume(@PathVariable int id, @RequestBody ResumeModel resume) {
        resume.setId(id);
        ResumeModel updatedResume = resumeService.updateResume(resume);
        return updatedResume != null ? ResponseEntity.ok(updatedResume) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable int id) {
        resumeService.deleteResume(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadResumeAsPdf(@PathVariable int id) {
        ResumeModel resume = resumeService.getResumeById(id);
        if (resume == null) {
            return ResponseEntity.notFound().build();
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            Color headerColor = new DeviceRgb(33, 150, 243); // Blue color for headers
            Color bodyColor = new DeviceRgb(33, 33, 33); // Dark gray color for body text
            Color accentColor = new DeviceRgb(255, 152, 0); // Orange color for accents

            Style headerStyle = new Style()
                    .setFont(font)
                    .setFontColor(headerColor)
                    .setFontSize(18)
                    .setBold();

            Style bodyStyle = new Style()
                    .setFont(font)
                    .setFontColor(bodyColor)
                    .setFontSize(12);

            Style accentStyle = new Style()
                    .setFont(font)
                    .setFontColor(accentColor)
                    .setFontSize(12)
                    .setBold();

            com.itextpdf.kernel.pdf.PdfDocument pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(new com.itextpdf.kernel.pdf.PdfWriter(outputStream));
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDocument);
            document.setMargins(36, 36, 72, 36); // Set document margins

            Paragraph header = new Paragraph(resume.getName().toUpperCase()).addStyle(headerStyle).setTextAlignment(TextAlignment.CENTER);
            document.add(header);

            Paragraph jobTitle = new Paragraph(resume.getJobTitle()).addStyle(accentStyle).setTextAlignment(TextAlignment.CENTER);
            document.add(jobTitle);

            document.add(new Paragraph(" "));

            Table contactTable = new Table(2);
            contactTable.setWidth(UnitValue.createPercentValue(100));

            contactTable.addCell(new Cell().add(new Paragraph("CONTACT")).addStyle(headerStyle));
            contactTable.addCell(new Cell().add(new Paragraph("PROFILE")).addStyle(headerStyle));

            contactTable.addCell(new Cell().add(new Paragraph(resume.getPhone())).addStyle(bodyStyle));
            contactTable.addCell(new Cell().add(new Paragraph(resume.getProfileSummary())).addStyle(bodyStyle));

            contactTable.addCell(new Cell().add(new Paragraph(resume.getEmail())).addStyle(bodyStyle));
            contactTable.addCell(new Cell());

            contactTable.addCell(new Cell().add(new Paragraph(resume.getAddress())).addStyle(bodyStyle));
            contactTable.addCell(new Cell());

            contactTable.addCell(new Cell().add(new Paragraph(resume.getLinkedIn())).addStyle(bodyStyle));
            contactTable.addCell(new Cell());

            document.add(contactTable);

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Education").addStyle(headerStyle));
            document.add(new Paragraph(resume.getEducation()).addStyle(bodyStyle));

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Professional Experience").addStyle(headerStyle));
            document.add(new Paragraph(resume.getExperience()).addStyle(bodyStyle));

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Skills").addStyle(headerStyle));
            document.add(new Paragraph(resume.getSkills()).addStyle(bodyStyle));

            document.close();

            byte[] pdfBytes = outputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", resume.getName() + " resume.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }}
