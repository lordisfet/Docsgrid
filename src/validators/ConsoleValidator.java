package validators;

import dao.CompanyDAO;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

public class ConsoleValidator {
    private static final Scanner scanner = new Scanner(System.in);

    public static String askStringValue(String question, boolean canBeBlank) {
        String answer;

        do {
            System.out.print(question + ": ");
            answer = scanner.nextLine();
        } while (!canBeBlank && answer.isBlank());

        return answer;
    }

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

    public static String askTINValue() {
        String answer;
        final String TIN_FORMAT = "\\d{3}-\\d{2}-\\d{4}";

        System.out.println("TIN format is NNN-NN-NNNN. N is number from 0 to 9.");
        answer = askStringValue("Enter TIN", false);

        while (!answer.matches(TIN_FORMAT)) {
            System.out.println("You entered a TIN which does not follow the format NNN-NN-NNNN. Try again");
            answer = askStringValue("Enter TIN", false);
        }

        return answer;
    }

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

    public static String askEmailValue() {
        String answer = askStringValue("Enter email", false);
        while (!answer.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            System.out.println("Invalid email format. Try again.");
            answer = askStringValue("Enter email", false);
        }
        return answer;
    }

    public static String askNumberValue() {
        String answer = askStringValue("Enter number", false);
        while (!answer.matches("\\d+(\\.\\d+)?")) {
            System.out.println("Invalid number format. Try again.");
            answer = askStringValue("Enter number", false);
        }
        return answer;
    }

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

    public static Map<String, String> setValuesForDocument(List<String> keys) {
        Map<String, String> values = new HashMap<>();

        for (String key : keys) {
            String fieldType = extractFieldType(key);

            String value;
            switch (fieldType.toLowerCase()) {
                case "tin":
                    value = ConsoleValidator.askTINValue();
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

    private static String extractFieldType(String key) {
        var matcher = Pattern.compile("[A-Z][a-zA-Z]*$").matcher(key);
        return matcher.find() ? matcher.group() : key;
    }
}


