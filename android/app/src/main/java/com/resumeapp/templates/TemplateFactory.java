package com.resumeapp.templates;

import com.facebook.react.bridge.ReadableMap;

public class TemplateFactory {
    public static ResumeTemplate getTemplate(int templateId) throws IllegalArgumentException {
        switch(templateId){
            case 1:
                return new TemplateOne();
            case 2:
                return new TemplateTwo();
            default:
                throw new IllegalArgumentException("Invalid template ID: " + templateId);
        }
    }
}