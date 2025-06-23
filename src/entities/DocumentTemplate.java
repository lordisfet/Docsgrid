package entities;

import entities.abstracts.BaseEntity;
import exceptions.DocumentTemplateValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a template for documents, defining a title, structure with placeholders, and extracted keys.
 */
public class DocumentTemplate extends BaseEntity implements Cloneable {
    private String title;
    private String structure;
    private List<String> keys;

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^\\s{}]+)}}");

    /**
     * Constructs a DocumentTemplate with an ID, structure, and title.
     * Extracts and validates placeholders from the structure.
     *
     * @param id        template ID; passed to BaseEntity
     * @param structure structure string containing placeholders like {{key}}
     * @param title     title of the template; must not be null or blank
     * @throws DocumentTemplateValidationException if title is invalid or placeholders malformed
     */
    public DocumentTemplate(int id, String structure, String title) throws DocumentTemplateValidationException {
        super(id);

        if (title == null || title.isBlank()) {
            throw new DocumentTemplateValidationException("Title cannot be null or blank");
        }

        this.keys = validatePlaceholders(structure);
        this.structure = structure;
        this.title = title;
    }

    /**
     * Constructs a DocumentTemplate with an ID and title only.
     * Used when structure is set later.
     *
     * @param id    template ID; passed to BaseEntity
     * @param title title of the template; must not be null or blank
     * @throws DocumentTemplateValidationException if title is invalid
     */
    public DocumentTemplate(int id, String title) throws DocumentTemplateValidationException {
        super(id);

        if (title == null || title.isBlank()) {
            throw new DocumentTemplateValidationException("Title cannot be null or blank");
        }

        this.title = title;
    }

    /**
     * Constructs a {@code DocumentTemplate} instance with the specified structure and title.
     * <p>
     * This constructor validates both parameters to ensure they are neither {@code null} nor blank,
     * then initializes the {@code structure}, {@code title}, and extracts placeholder keys
     * from the structure using {@code validatePlaceholders}.
     * </p>
     *
     * @param structure the template structure string containing placeholders
     * @param title the title of the document template
     * @throws DocumentTemplateValidationException if either {@code structure} or {@code title}
     *         is {@code null} or blank
     */
    public DocumentTemplate(String title, String structure) throws DocumentTemplateValidationException {
        if (title == null || title.isBlank()) {
            throw new DocumentTemplateValidationException("Title cannot be null or blank");
        }
        if (structure == null || structure.isBlank()) {
            throw new DocumentTemplateValidationException("Structure cannot be null or blank");
        }

        this.keys = validatePlaceholders(structure);
        this.structure = structure;
        this.title = title;
    }

    /**
     * Copy constructor.
     * Creates a deep copy of keys list.
     *
     * @param other DocumentTemplate to copy; must not be null
     */
    public DocumentTemplate(DocumentTemplate other) {
        super(other);
        this.title = other.title;
        this.structure = other.structure;
        this.keys = new ArrayList<>(other.keys);
    }

    /**
     * Validates and extracts placeholder keys from the given text.
     * Placeholders must match {{key}} pattern and no unmatched braces remain.
     *
     * @param text structure text containing placeholders
     * @return list of placeholder keys in the order found
     * @throws DocumentTemplateValidationException if text is null/blank or placeholders malformed
     */
    public static List<String> validatePlaceholders(String text) throws DocumentTemplateValidationException {
        if (text == null || text.isBlank()) {
            throw new DocumentTemplateValidationException("Text cannot be null or blank");
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        List<String> keys = new ArrayList<>();

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            if (key.isEmpty()) {
                throw new DocumentTemplateValidationException("Placeholder key cannot be blank");
            }
            keys.add(key);
        }

        String withoutValid = PLACEHOLDER_PATTERN.matcher(text).replaceAll("");

        if (withoutValid.contains("{{") || withoutValid.contains("}}")) {
            throw new DocumentTemplateValidationException("Invalid placeholder format or unmatched braces");
        }

        return keys;
    }

    /**
     * Returns the template structure string.
     *
     * @return structure with placeholders
     */
    public String getStructure() {
        return structure;
    }

    /**
     * Sets a new structure and revalidates placeholders.
     *
     * @param structure new structure string; must not be null or blank
     * @throws DocumentTemplateValidationException if placeholders invalid
     */
    public void setStructure(String structure) throws DocumentTemplateValidationException {
        this.keys = validatePlaceholders(structure);
        this.structure = structure;
    }

    /**
     * Returns the template title.
     *
     * @return title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets a new title for the template.
     *
     * @param title new title; must not be null or blank
     * @throws DocumentTemplateValidationException if title is invalid
     */
    public void setTitle(String title) throws DocumentTemplateValidationException {
        if (title == null || title.isBlank()) {
            throw new DocumentTemplateValidationException("Title cannot be null or blank");
        }

        this.title = title;
    }

    /**
     * Returns a copy of the placeholder keys.
     *
     * @return list of keys
     */
    public List<String> getKeys() {
        return new ArrayList<>(keys);
    }

    /**
     * Creates and returns a deep copy of this DocumentTemplate.
     *
     * @return cloned DocumentTemplate
     */
    @Override
    public DocumentTemplate clone() {
        try {
            DocumentTemplate clone = (DocumentTemplate) super.clone();

            if (this.keys != null) {
                clone.keys = new ArrayList<>(this.keys);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Returns a string representation of the {@code DocumentTemplate} object.
     * <p>
     * The returned string includes the values of the {@code title}, {@code structure},
     * and {@code keys} fields, formatted in a concise and readable form.
     * Useful for debugging and logging.
     * </p>
     *
     * @return a string describing the current state of this {@code DocumentTemplate}
     */
    @Override
    public String toString() {
        return "DocumentTemplate{" +
                "title='" + title + '\'' +
                ", structure='" + structure + '\'' +
                ", keys=" + keys +
                '}';
    }
}
