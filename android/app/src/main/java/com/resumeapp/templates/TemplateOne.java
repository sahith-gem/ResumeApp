// TemplateOne.java
package com.resumeapp.templates;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.facebook.react.bridge.ReadableMap;

public class TemplateOne implements ResumeTemplate {

    @Override
    public void build(Document document, ReadableMap resumeData) throws DocumentException {
        // Define fonts
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLUE);
        Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
        Font subHeadingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Font linkFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.UNDERLINE, BaseColor.BLUE);

        // Header: Name centered
        Paragraph header = new Paragraph(resumeData.getString("name"), headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        // Contact details on one line
        String contactInfo = "";
        if (resumeData.hasKey("address") && !resumeData.getString("address").isEmpty()) {
            contactInfo += resumeData.getString("address") + " | ";
        }
        contactInfo += resumeData.getString("mobile") + " | " + resumeData.getString("email");
        Paragraph contactPara = new Paragraph(contactInfo, contentFont);
        // Add clickable links on the same line
        if (resumeData.hasKey("linkedin") && !resumeData.getString("linkedin").isEmpty()) {
            contactPara.add(" | ");
            Anchor linkedin = new Anchor("LinkedIn", linkFont);
            linkedin.setReference(resumeData.getString("linkedin"));
            contactPara.add(linkedin);
        }
        if (resumeData.hasKey("github") && !resumeData.getString("github").isEmpty()) {
            contactPara.add(" | ");
            Anchor github = new Anchor("GitHub", linkFont);
            github.setReference(resumeData.getString("github"));
            contactPara.add(github);
        }
        contactPara.setAlignment(Element.ALIGN_CENTER);
        document.add(contactPara);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        // Objective Section
        Paragraph objectiveTitle = new Paragraph("OBJECTIVE", sectionTitleFont);
        document.add(objectiveTitle);
        Paragraph objectiveContent = new Paragraph(resumeData.getString("Summary"), contentFont);
        objectiveContent.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(objectiveContent);
        document.add(Chunk.NEWLINE);

        // Experience Section rendered as bullet points
        if (resumeData.hasKey("experience") && !resumeData.getString("experience").isEmpty()) {
            Paragraph experienceTitle = new Paragraph("EXPERIENCE", sectionTitleFont);
            document.add(experienceTitle);
            com.itextpdf.text.List experienceList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            String expStr = resumeData.getString("experience");
            String[] expItems = expStr.split("\n");
            for (String item : expItems) {
                if (!item.trim().isEmpty()) {
                    experienceList.add(new ListItem(item.trim(), contentFont));
                }
            }
            document.add(experienceList);
            document.add(Chunk.NEWLINE);
        }

        // Education Section – using common keys for data (assumes front-end passes unified keys)
        Paragraph educationTitle = new Paragraph("EDUCATION", sectionTitleFont);
        document.add(educationTitle);
        Paragraph eduParagraph = new Paragraph();
        eduParagraph.setLeading(16);
        // combine various education fields (e.g., undergrad, higher, secondary)
        eduParagraph.add(new Chunk("Undergraduate: " + resumeData.getString("undergradDegree") + "\n", subHeadingFont));
        eduParagraph.add(new Chunk(resumeData.getString("undergradInstitution") + "\n", contentFont));
        eduParagraph.add(new Chunk("CGPA: " + resumeData.getString("undergradCgpa") + "\n", contentFont));
        // You can add similar blocks for higher and secondary if present.
        

        document.add(eduParagraph);
        document.add(Chunk.NEWLINE);

        // Skills Section
        if (resumeData.hasKey("skills") && !resumeData.getString("skills").isEmpty()) {
            Paragraph skillsTitle = new Paragraph("SKILLS", sectionTitleFont);
            document.add(skillsTitle);
            Paragraph skillsContent = new Paragraph(resumeData.getString("skills"), contentFont);
            document.add(skillsContent);
            document.add(Chunk.NEWLINE);
        }

        // Projects Section with a subheading for project title and bullet list for details
        if (resumeData.hasKey("projects") && !resumeData.getString("projects").isEmpty()) {
            Paragraph projectsTitle = new Paragraph("PROJECTS", sectionTitleFont);
            document.add(projectsTitle);
            if (resumeData.hasKey("projectTitle") && !resumeData.getString("projectTitle").isEmpty()) {
                Paragraph projTitle = new Paragraph(resumeData.getString("projectTitle"), subHeadingFont);
                document.add(projTitle);
            }
            com.itextpdf.text.List projectList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            String projStr = resumeData.getString("projects");
            String[] projItems = projStr.split("\n");
            for (String item : projItems) {
                if (!item.trim().isEmpty()) {
                    projectList.add(new ListItem(item.trim(), contentFont));
                }
            }
            document.add(projectList);
            document.add(Chunk.NEWLINE);
        }

        // Awards & Certifications Section as bullet points
        if (resumeData.hasKey("awards") && !resumeData.getString("awards").isEmpty()) {
            Paragraph awardsTitle = new Paragraph("AWARDS & CERTIFICATIONS", sectionTitleFont);
            document.add(awardsTitle);
            com.itextpdf.text.List awardsList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            String awardsStr = resumeData.getString("awards");
            String[] awardsItems = awardsStr.split("\n");
            for (String item : awardsItems) {
                if (!item.trim().isEmpty()) {
                    awardsList.add(new ListItem(item.trim(), contentFont));
                }
            }
            document.add(awardsList);
            document.add(Chunk.NEWLINE);
        }

        // Custom Dynamic Sections from UI – each section is rendered with its heading and bullet points
        if (resumeData.hasKey("sections")) {
            // Assume sections is an array of maps with "heading" and "content"
            com.facebook.react.bridge.ReadableArray customSections = resumeData.getArray("sections");
            for (int i = 0; i < customSections.size(); i++) {
                com.facebook.react.bridge.ReadableMap sectionMap = customSections.getMap(i);
                if (sectionMap.hasKey("heading") && !sectionMap.getString("heading").isEmpty()) {
                    Paragraph customSectionTitle = new Paragraph(sectionMap.getString("heading"), subHeadingFont);
                    document.add(customSectionTitle);
                }
                if (sectionMap.hasKey("content") && !sectionMap.getString("content").isEmpty()) {
                    com.itextpdf.text.List customList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
                    String contentStr = sectionMap.getString("content");
                    String[] contentItems = contentStr.split("\n");
                    for (String item : contentItems) {
                        if (!item.trim().isEmpty()) {
                            customList.add(new ListItem(item.trim(), contentFont));
                        }
                    }
                    document.add(customList);
                    document.add(Chunk.NEWLINE);
                }
            }
        }

        // Final separator line
        document.add(new Chunk(new LineSeparator()));
    }
}
