package validators;

import dao.CompanyDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility class for reading and validating console input values.
 */
public class ConsoleValidator {
    /**
     * Scanner for reading console input from the user.
     */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Prompts the user with a question and reads a string value.
     *
     * @param question   prompt text to display
     * @param canBeBlank whether empty input is allowed
     * @return entered string (may be empty if allowed)
     */
    public static String askStringValue(String question, boolean canBeBlank) {
        String answer;

        do {
            System.out.print(question + ": ");
            answer = scanner.nextLine();
        } while (!canBeBlank && answer.isBlank());

        return answer;
    }

    /**
     * Prompts the user with a question and reads an integer value.
     * Repeats until a valid integer is entered.
     *
     * @param question prompt text to display
     * @return parsed integer
     */
    public static int askIntegerValue(String question) {
        while (true) {
            try {
                System.out.print(question + ": ");
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid value. Please, try again!");
            }
        }
    }

    /**
     * Prompts the user to enter a TIN in format NNN-NN-NNNN.
     * Repeats until a valid format is provided.
     *
     * @return validated TIN string
     */
    public static String askTINValue(String question) {
        String answer;
        final String TIN_FORMAT = "\\d{3}-\\d{2}-\\d{4}";

        System.out.println("TIN format is NNN-NN-NNNN. N is number from 0 to 9.");
        answer = askStringValue(question, true);

        while (!answer.matches(TIN_FORMAT) || answer.isBlank()) {
            System.out.println("You entered a TIN which does not follow the format NNN-NN-NNNN. Try again");
            answer = askStringValue(question, true);
        }

        return answer;
    }

    /**
     * Prompts the user to enter a date in yyyy-MM-dd format.
     * Repeats until a valid date is parsed.
     *
     * @return date string in yyyy-MM-dd format
     */
    public static String askDateValue() {
        String answer;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("Date format is yyyy-MM-dd.");
        while (true) {
            answer = askStringValue("Enter date", false);
            try {
                LocalDate parsedDate = LocalDate.parse(answer, formatter);
                return parsedDate.toString(); // повертає правильну дату у форматі yyyy-MM-dd
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date or format. Make sure it exists and is in yyyy-MM-dd format. Try again.");
            }
        }
    }

    /**
     * Prompts the user for an integer within a specified range.
     * Repeats until a valid integer is entered.
     *
     * @param question prompt text to display
     * @param minValue minimum allowed value (inclusive)
     * @param maxValue maximum allowed value (inclusive)
     * @return validated integer within range
     */
    public static int askIntegerValue(String question, int minValue, int maxValue) {
        while (true) {
            try {
                System.out.print(question + ": ");
                int answer = Integer.parseInt(scanner.nextLine());
                if (answer < minValue || answer > maxValue) {
                    System.out.println("Answer cannot be less than " + minValue + " and more than " + maxValue);
                } else {
                    return answer;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid value. Please, try again!");
            }
        }
    }

    /**
     * Prompts the user to enter an email address.
     * Validates basic email pattern.
     *
     * @return validated email string
     */
    public static String askEmailValue() {
        String answer = askStringValue("Enter email", false);
        while (!answer.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            System.out.println("Invalid email format. Try again.");
            answer = askStringValue("Enter email", false);
        }
        return answer;
    }

    /**
     * Prompts the user to enter a numeric value (integer or decimal).
     * Validates numeric format.
     *
     * @return validated number string
     */
    public static String askNumberValue() {
        String answer = askStringValue("Enter number", false);
        while (!answer.matches("\\d+(\\.\\d+)?")) {
            System.out.println("Invalid number format. Try again.");
            answer = askStringValue("Enter number", false);
        }
        return answer;
    }

    /**
     * Prompts the user to enter an existing company name.
     * Checks existence via CompanyDAO.
     *
     * @return validated company name
     */
    public static String askExistingCompanyName() {
        String companyName;
        CompanyDAO companyDAO = new CompanyDAO();

        do {
            companyName = askStringValue("Enter company name", false);
            if (!companyDAO.existsByName(companyName)) {
                System.out.println("Company doesn't exist. Try again.");
            }
        } while (!companyDAO.existsByName(companyName));

        return companyName;
    }

    /**
     * Prompts the user to set values for each document key according to its type.
     * Supported types: TIN, date, email, number, companyName, default string.
     *
     * @param keys list of document field keys
     * @return map of key to entered value
     */
    public static Map<String, String> setValuesForDocument(List<String> keys) {
        Map<String, String> values = new HashMap<>();

        for (String key : keys) {
            String fieldType = extractFieldType(key);

            String value;
            switch (fieldType.toLowerCase()) {
                case "tin":
                    value = ConsoleValidator.askTINValue("Enter TIN (" + key + ")");
                    break;
                case "date":
                    value = ConsoleValidator.askDateValue();
                    break;
                case "email":
                case "gmail":
                    value = ConsoleValidator.askEmailValue();
                    break;
                case "paymentamount":
                case "number":
                    value = ConsoleValidator.askNumberValue();
                    break;
                case "companyname":
                    value = ConsoleValidator.askExistingCompanyName();
                    break;
                default:
                    value = ConsoleValidator.askStringValue("Enter " + key, false);
            }

            values.put(key, value);
        }

        return values;
    }

    /**
     * Extract TINs from data after setValuesForDocument handling
     * @param validData data after setValuesForDocument handling
     * @return Set with TINs
     */
    public static Set<String> extractTINsFromData(Map<String, String> validData) {
        Set<String> TINs = new HashSet<>();
        for (String field : validData.keySet()) {
            if (extractFieldType(field).equalsIgnoreCase("tin")) {
                TINs.add(validData.get(field));
            }
        }

        return TINs;
    }

    /**
     * Extracts the trailing capitalized word from a key to determine field type.
     * Defaults to the full key if no match.
     *
     * @param key placeholder key
     * @return extracted field type or original key
     */
    public static String extractFieldType(String key) {
        var matcher = Pattern.compile("[A-Z][a-zA-Z]*$").matcher(key);
        return matcher.find() ? matcher.group() : key;
    }
}


