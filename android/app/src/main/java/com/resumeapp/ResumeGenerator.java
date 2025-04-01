package com.resumeapp;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Promise;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.resumeapp.templates.ResumeTemplate;
import com.resumeapp.templates.TemplateFactory;

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
            // Validate template id: valid IDs are assumed to be 1 and 2.
            if (templateId < 1 || templateId > 2) {
                promise.reject("INVALID_TEMPLATE", "Invalid template ID: " + templateId);
                return;
            }

            // Generate a unique file name.
            String pdfFilename = "resume_" + System.currentTimeMillis() + ".pdf";
            String tempPdfPath = getReactApplicationContext().getCacheDir() + "/" + pdfFilename;

            // Create document and writer.
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(tempPdfPath));
            document.open();

            // Choose the resume template via the factory.
            ResumeTemplate template = TemplateFactory.getTemplate(templateId);
            template.build(document, resumeData);

            document.close();

            // Read the temporary PDF file into a byte array.
            byte[] pdfData = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(tempPdfPath));
            String pdfPath = savePdfToDownloads(pdfFilename, pdfData);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, pdfFilename);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            try {
                Uri uri = getReactApplicationContext().getContentResolver().insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
                );
                if (uri == null) return null;
                OutputStream out = getReactApplicationContext().getContentResolver().openOutputStream(uri);
                if (out == null) return null;
                out.write(pdfData);
                out.close();
                return uri.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + pdfFilename;
            try (FileOutputStream fos = new FileOutputStream(path)) {
                fos.write(pdfData);
                return path;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
