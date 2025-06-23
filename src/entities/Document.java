package entities;

import entities.abstracts.BaseEntity;
import entities.user.Employee;
import exceptions.DocumentValidationException;

import java.util.*;

/**
 * Represents a document based on a template, with content data and signatories.
 */
public class Document extends BaseEntity {
    private final DocumentTemplate template;
    private Map<String, String> content;
    private List<Signatory> signatories;

    /**
     * Constructs a Document with an existing ID, template, content, and signatories.
     *
     * @param id          document ID; must not be null or less than 1
     * @param template    document template; must not be null
     * @param content     map of template keys to values; must not be null or empty
     * @param signatories list of signatories; must not be null or empty
     * @throws DocumentValidationException if any validation fails
     */
    public Document(Integer id, DocumentTemplate template, Map<String, String> content, List<Signatory> signatories)
            throws DocumentValidationException {
        if (id == null || id < 1) {
            throw new DocumentValidationException("ID cannot be null or less than 1");
        }
        if (template == null) {
            throw new DocumentValidationException("Template cannot be null");
        }
        if (content == null || content.isEmpty()) {
            throw new DocumentValidationException("Content cannot be null or empty");
        }
        if (signatories == null || signatories.isEmpty()) {
            throw new DocumentValidationException("Signatories cannot be null or empty");
        }

        this.id = id;
        this.template = template;
        this.fillContent(content);
        this.signatories = signatories;
    }

    /**
     * Constructs a new Document with template, content, and signatories.
     *
     * @param template    document template; must not be null
     * @param content     map of template keys to values; must not be null or empty
     * @param signatories list of signatories; must not be null or empty
     * @throws DocumentValidationException if any validation fails
     */
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

    /**
     * Constructs a deep copy of the specified {@code Document}.
     * <p>
     * This copy constructor performs deep copies of the {@code template},
     * {@code content}, and {@code signatories} fields, ensuring that
     * the new instance is fully independent of the original.
     * </p>
     *
     * @param document the {@code Document} to copy
     * @throws IllegalArgumentException if the input {@code document} is {@code null}
     */
    public Document(Document document) {
        super(document);

        if (document == null) {
            throw new IllegalArgumentException("Document to copy cannot be null");
        }

        this.template = new DocumentTemplate(document.template);

        this.content = new HashMap<>(document.content);

        this.signatories = new ArrayList<>();
        for (Signatory s : document.signatories) {
            this.signatories.add(new Signatory(s));
        }
    }

    /**
     * Validates and sets the document content based on the template keys.
     *
     * @param data map of values; keys must match template keys exactly
     * @throws DocumentValidationException if provided keys do not match template
     */
    public void fillContent(Map<String, String> data) throws DocumentValidationException {
        Set<String> dataKeys = data.keySet();
        List<String> templateKeys = template.getKeys();

        if (!new HashSet<>(templateKeys).equals(dataKeys)) {
            throw new DocumentValidationException("Provided data keys don't match the template keys");
        }

        this.content = new HashMap<>(data);
    }

    /**
     * Returns the document template.
     *
     * @return DocumentTemplate instance
     */
    public DocumentTemplate getTemplate() {
        return template;
    }

    /**
     * Returns the document content map.
     *
     * @return map of template keys to values
     */
    public Map<String, String> getContent() {
        return content;
    }

   /* public void setContent(Map<String, String> content) {
        if (content == null) {
            throw new DocumentValidationException("Content is empty");
        }
        this.content = content;
    }*/

    public void setSignatories(List<Signatory> signatories) {
        if (signatories == null) {
            throw new DocumentValidationException("Content is empty");
        }
        this.signatories = signatories;
    }

    /**
     * Returns the list of signatories.
     *
     * @return list of Signatory instances
     */
    public List<Signatory> getSignatories() {
        return signatories;
    }

    /**
     * Marks the document as signed by the specified employee.
     *
     * @param emp employee who signs the document
     */
    public void signByEmployee(Employee emp) {
        for (Signatory signatory : signatories) {
            if (signatory.getEmployee().getId().equals(emp.getId())) {
                signatory.setSignStatus(true);
            }
        }
    }

    /**
     * Checks if all signatories have signed the document.
     *
     * @return true if all signed, false otherwise
     */
    public boolean isCompleted() {
        for (Signatory signatory : this.signatories) {
            if (!signatory.isSigned()) return false;
        }

        return true;
    }

    /**
     * Renders the document by replacing template placeholders with content values.
     *
     * @return rendered document string
     */
    public String render() {
        String renderedResult = this.template.getStructure();
        for (String dataKey : this.content.keySet()) {
            renderedResult = renderedResult.replace("{{" + dataKey + "}}", this.content.get(dataKey));
        }

        return renderedResult;
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