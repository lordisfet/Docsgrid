import entities.Company;
import entities.user.Employee;
import exceptions.ConsoleDriverException;
import exceptions.UserValidationError;
import menuAction.EmployeeMenuAction;
import menuAction.GuestMenuAction;

import java.util.Scanner;

public class ConsoleDriver {
    private static final Scanner scanner = new Scanner(System.in);
    // private static database ?
    // private static Repository repository ?

    public static void main(String[] args) {
        guestMenu();
    }

    private static Employee fakeAuthorizeEmployee() {
        try {
            return new Employee("12345-174678-500", "1212", "Johny",
                    "Manager", new Company("SSU"));
        } catch (UserValidationError e) {
            System.out.println("Validation error. Message: " + e.getMessage());
            return null;
        }
    }

    private static void guestMenu() {
        System.out.println("\n[DocsGrid]");

        System.out.println("Welcome to DocsGrid!");

        int actionsLength = GuestMenuAction.values().length;
        GuestMenuAction action;

        do {
            System.out.println("\n----- Guest -----");
            System.out.println("1) Register");
            System.out.println("2) Log in");
            System.out.println("3) Exit\n");

            action = GuestMenuAction.values()[askIntegerValue("Action", 1, actionsLength) - 1];
            switch (action) {
                case GuestMenuAction.REGISTER -> {
                    // todo register
                }
                case GuestMenuAction.LOGIN -> {
                    // todo login
                    // authorizationMenu?
                    // -> companyMenu ?
                    employeeMenu(fakeAuthorizeEmployee());
                }
                case GuestMenuAction.EXIT -> {
                    System.out.println("Bye!");
                }
            }
        } while (action != GuestMenuAction.EXIT);
    }

    private static void employeeMenu(Employee emp) {
        if (emp == null) {
            throw new ConsoleDriverException("Employee is null");
        }

        System.out.println("\nID: " + emp.getId());
        System.out.println("Name: " + emp.getFullName());
        // TODO: System.out.println("Company: " + emp.getCompany().getName());
        System.out.println("Job position: " + emp.getJobPosition());
        System.out.println("TIN: " + emp.getTIN());

        System.out.println("\n----- Menu -----");
        System.out.println("1) Create new document");
        System.out.println("2) List signed documents");
        System.out.println("3) Show unsigned documents");
        System.out.println("4) Log out\n");

        int actionsLength = EmployeeMenuAction.values().length;
        EmployeeMenuAction action;

        do {
            action = EmployeeMenuAction.values()[askIntegerValue("Action", 1, actionsLength) - 1];
            switch (action) {
                case EmployeeMenuAction.CREATE_DOC -> {
                    // ? -> select template -> filling data -> choose signatories -> this menu
                    System.out.println("Create new document");
                }
                case EmployeeMenuAction.LIST_DOCS -> System.out.println("List my signed documents");
                case EmployeeMenuAction.SHOW_UNSIGNED_DOCS -> System.out.println("Show unsigned documents");
                case EmployeeMenuAction.LOG_OUT -> {
                    System.out.println("Logging out...");
                }
            }
        } while (action != EmployeeMenuAction.LOG_OUT);
    }

    // console helpers

    private static String askStringValue(String question, boolean canBeBlank) {
        String answer = "";

        do {
            System.out.print(question + ": ");
            answer = scanner.nextLine();
        } while (!canBeBlank && answer.isBlank());

        return answer;
    }

    private static int askIntegerValue(String question) {
        while (true) {
            try {
                System.out.print(question + ": ");
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid value. Please, try again!");
            }
        }
    }

    private static int askIntegerValue(String question, int minValue, int maxValue) {
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
}
