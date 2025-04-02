package com.resumeapp.templates;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.facebook.react.bridge.ReadableMap;

public class TemplateTwo implements ResumeTemplate  {
    @Override
    public void build (Document document , ReadableMap resumeData) throws DocumentException{

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD , 20 , BaseColor.BLACK);
    }
}
