// ResumeTemplate.java
package com.resumeapp.templates;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.facebook.react.bridge.ReadableMap;

public interface ResumeTemplate {
    void build(Document document, ReadableMap resumeData) throws DocumentException;
}