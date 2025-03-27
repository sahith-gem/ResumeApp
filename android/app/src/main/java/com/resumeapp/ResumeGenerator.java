package com.resumeapp;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Promise;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class ResumeGenerator extends ReactContextBaseJavaModule {

    public ResumeGenerator(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "ResumeGenerator";
    }

    @ReactMethod
    public void generateResume(ReadableMap resumeData, int templateId, Promise promise) {
        try {
            // Validate template ID
            if (templateId != 1 && templateId != 2) {
                promise.reject("INVALID_TEMPLATE", "Invalid template ID: " + templateId);
                return;
            }

            // Generate a unique file name
            String pdfFilename = "resume_" + System.currentTimeMillis() + ".pdf";

            // Create a temporary PDF file in app's cache directory
            String tempPdfPath = getReactApplicationContext().getCacheDir() + "/" + pdfFilename;

            // Initialize the document and write content
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(tempPdfPath));
            document.open();

            // Choose the template layout
            if (templateId == 1) {
                addTemplateOne(document, resumeData);
            } else if (templateId == 2) {
                addTemplateTwo(document, resumeData);
            }

            // Close the document
            document.close();

            // Read the temporary PDF file as a byte array
            byte[] pdfData = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(tempPdfPath));

            // Save the PDF to Downloads folder
            String pdfPath = savePdfToDownloads(pdfFilename, pdfData);

            // If the PDF was saved successfully, resolve the promise with its path
            if (pdfPath != null) {
                promise.resolve(pdfPath);
            } else {
                promise.reject("PDF_ERROR", "Failed to save PDF to downloads folder");
            }
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("PDF_ERROR", "Failed to generate resume: " + e.getMessage());
        }
    }

    public String savePdfToDownloads(String pdfFilename, byte[] pdfData) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10 and above (scoped storage)
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, pdfFilename);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            try {
                Uri uri = getReactApplicationContext().getContentResolver().insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
                );
                if (uri == null) {
                    return null;
                }
                OutputStream out = getReactApplicationContext().getContentResolver().openOutputStream(uri);
                if (out == null) {
                    return null;
                }
                out.write(pdfData); // Write the PDF data to the shared Downloads folder
                out.close();
                // Returning the path is less straightforward with scoped storage;
                // you may choose to return the Uri.toString() or a custom path reference.
                return uri.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null; // Handle the error properly in your app
            }
        } else { // Android 9 (API 28) and below
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + pdfFilename;
            try (FileOutputStream fos = new FileOutputStream(path)) {
                fos.write(pdfData); // Save the PDF file
                return path;
            } catch (Exception e) {
                e.printStackTrace();
                return null; // Handle the error properly in your app
            }
        }
    }

    private void addTemplateOne(Document document, ReadableMap resumeData) throws DocumentException {
        // Add a centered header for the resume
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLUE);
        Paragraph header = new Paragraph(resumeData.getString("name"), headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        // Spacer
        document.add(Chunk.NEWLINE);

        // Section: Summary
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
        Paragraph summaryTitle = new Paragraph("Summary", sectionFont);
        document.add(summaryTitle);

        // Content: Summary
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Paragraph summaryContent = new Paragraph(resumeData.getString("Summary"), contentFont);
        summaryContent.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(summaryContent);

        // Spacer
        document.add(Chunk.NEWLINE);

        // Add contact details in a table for better alignment
        PdfPTable contactTable = new PdfPTable(2); // 2 columns
        contactTable.setWidthPercentage(100); // Full width
        contactTable.setSpacingBefore(10f); // Space before the table
        contactTable.setWidths(new float[]{1, 3}); // Column widths

        // Add rows to the contact table
        contactTable.addCell(new PdfPCell(new Phrase("Mobile:", sectionFont)));
        contactTable.addCell(new PdfPCell(new Phrase(resumeData.getString("mobile"), contentFont)));
        document.add(contactTable);

        // Add a separator
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(separator));
    }

    private void addTemplateTwo(Document document, ReadableMap resumeData) throws DocumentException {
        // Header with unique styling
        Font headerFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 22, BaseColor.MAGENTA);
        Paragraph header = new Paragraph("Resume for " + resumeData.getString("name"), headerFont);
        header.setAlignment(Element.ALIGN_LEFT);
        document.add(header);

        // Spacer
        document.add(Chunk.NEWLINE);

        // Section: Email
        Font emailFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLUE);
        Paragraph email = new Paragraph("Email: " + resumeData.getString("email"), emailFont);
        document.add(email);

        // Spacer
        document.add(Chunk.NEWLINE);

        // Section: Highlighted Summary
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        PdfPTable table = new PdfPTable(1); // 1 column table for the summary
        table.setWidthPercentage(100); // Full width
        PdfPCell cell = new PdfPCell(new Phrase(resumeData.getString("Summary"), contentFont));
        cell.setBackgroundColor(BaseColor.YELLOW); // Highlight with yellow
        cell.setPadding(10f); // Add padding inside the cell
        table.addCell(cell);
        document.add(table);

        // Spacer
        document.add(Chunk.NEWLINE);

        // Skills or Additional Info
        Font skillsFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
        Paragraph skillsTitle = new Paragraph("Skills", skillsFont);
        document.add(skillsTitle);

        // List of Skills
        com.itextpdf.text.List skillList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
        skillList.add(new ListItem("React Native Development", contentFont));
        skillList.add(new ListItem("Java Expertise", contentFont));
        document.add(skillList);
    }
}
