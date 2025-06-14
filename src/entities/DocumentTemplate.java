package entities;

import entities.Abstaract.BaseEntity;
import exceptions.DocumentTemplateValidationException;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentTemplate extends BaseEntity implements Cloneable {
    private String title;
    private String structure;
    private Set<String> keys;

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^\\s{}]+)}}");

    public DocumentTemplate(int id, String structure, String title) throws DocumentTemplateValidationException {
        if (title == null || title.isBlank()) {
            throw new DocumentTemplateValidationException("Title cannot be null or blank");
        }

        this.id = id;
        this.keys = validatePlaceholders(structure);
        this.structure = structure;
        this.title = title;
    }

    /**
     * Validates that text contains valid non-blank keys with valid {{ (opening) and }} (closing) braces
     * @param text Example: <code>"Hello, {{name}}! My name is {{myName}}!"</code>
     * @return Trimmed keys from the text. Example: <code>[name, myName]</code>
     * @throws DocumentTemplateValidationException If text or placeholders are null or blank
     * or there are unmatched placeholder braces in the text
     */
    public static Set<String> validatePlaceholders(String text) throws DocumentTemplateValidationException {
        if (text == null || text.isBlank()) {
            throw new DocumentTemplateValidationException("Text cannot be null or blank");
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        Set<String> keys = new HashSet<>();

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

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) throws DocumentTemplateValidationException {
        this.keys = validatePlaceholders(structure);
        this.structure = structure;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws DocumentTemplateValidationException {
        if (title == null || title.isBlank()) {
            throw new DocumentTemplateValidationException("Title cannot be null or blank");
        }

        this.title = title;
    }

    public Set<String> getKeys() {
        return new HashSet<>(keys);
    }

    @Override
    public DocumentTemplate clone() {
        try {
            DocumentTemplate clone = (DocumentTemplate) super.clone();

            if (this.keys != null) {
                clone.keys = new HashSet<>(this.keys);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "DocumentTemplate{" +
                "title='" + title + '\'' +
                ", structure='" + structure + '\'' +
                ", keys=" + keys +
                '}';
    }
}
