package entities;

import entities.Abstaract.BaseEntity;
import entities.User.Employee;
import exceptions.DocumentTemplateValidationException;
import exceptions.DocumentValidationException;
import exceptions.SignatoryValidationException;
import exceptions.UserValidationError;

import java.util.*;

public class Document extends BaseEntity {
    private final DocumentTemplate template;
    private Map<String, String> content;
    private final List<Signatory> signatories;

    public Document(DocumentTemplate template, Map<String, String> content, List<Signatory> signatories)
            throws DocumentValidationException {
        if (template == null) {
            throw new DocumentValidationException("Template cannot be null");
        }
        if (content == null || content.isEmpty()) {
            throw new DocumentValidationException("Content cannot be null or empty");
        }
        if (signatories == null || signatories.isEmpty()) {
            throw new DocumentValidationException("Signatories cannot be null or empty");
        }

        this.template = template;
        this.fillContent(content);
        this.signatories = signatories;
    }

    public void fillContent(Map<String, String> data) throws DocumentValidationException {
        Set<String> dataKeys = data.keySet();
        Set<String> templateKeys = template.getKeys();

        if (!templateKeys.equals(dataKeys)) {
            throw new DocumentValidationException("Provided data keys don't match the template keys");
        }

        this.content = new HashMap<>(data);
    }

    public boolean isCompleted() {
        for (Signatory signatory : this.signatories) {
            if (!signatory.isSigned()) return false;
        }

        return true;
    }

    public String render() {
        String renderedResult = this.template.getStructure();
        for (String dataKey : this.content.keySet()) {
            renderedResult = renderedResult.replace("{{" + dataKey + "}}", this.content.get(dataKey));
        }

        return renderedResult;
    }

    public static void main(String[] args) {
        // Demo

        String someStructure = "I, {{name1}}, sign this document with {{name2}}.";
        try {
            Employee emp1 = new Employee("111-222-333", "12345", "John", "Manager");
            Employee emp2 = new Employee("222-111-333", "12345", "Bob", "Manager");

            DocumentTemplate docTemplate = new DocumentTemplate(0, someStructure, "Test Template");

            System.out.println("\nDocument Template: " + docTemplate);
            System.out.println("Structure: " + docTemplate.getStructure());
            System.out.println("Keys: " + docTemplate.getKeys());

            DocumentTemplate docTemplateCopy = docTemplate.clone();

            System.out.println("\nDocument Template Copy: " + docTemplateCopy);

            Map<String, String> content = new HashMap<>();

            content.put("name1", emp1.getFullName());
            content.put("name2", emp2.getFullName());

            ArrayList<Signatory> signatories = new ArrayList<>();

            signatories.add(new Signatory(emp1, true));
            signatories.add(new Signatory(emp2, false));

            Document document = new Document(docTemplate, content, signatories);

            System.out.println("\nDocument: " + document);

            String result = document.render();
            System.out.println("\n" + result);
            System.out.println("Is completed?: " + document.isCompleted());

        } catch (DocumentTemplateValidationException e) {
            System.out.println("Template is not validated. " + e.getMessage());
        } catch (DocumentValidationException e) {
            System.out.println("Document is not validated. " + e.getMessage());
        } catch (UserValidationError e) {
            System.out.println("Employees are not validated. " + e.getMessage());
        } catch (SignatoryValidationException e) {
            System.out.println("Signatories are not validated. " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Document{" +
                "template=" + template +
                ", content=" + content +
                ", signatories=" + signatories +
                '}';
    }
}
