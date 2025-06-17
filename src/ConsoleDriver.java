import dao.CompanyDAO;
import dao.DocumentTemplateDAO;
import dao.EmployeeDAO;
import entities.Company;
import entities.Document;
import entities.DocumentTemplate;
import entities.user.Employee;
import exceptions.CompanyValidationException;
import exceptions.ConsoleDriverException;
import exceptions.UserValidationException;
import menuAction.DocumentCreationMenuAction;
import menuAction.EmployeeMenuAction;
import menuAction.GuestMenuAction;

import javax.print.Doc;
import java.util.*;

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
        } catch (UserValidationException e) {
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
            System.out.println("1) Sign up");
            System.out.println("2) Log in");
            System.out.println("3) Register Company");
            System.out.println("4) Exit\n");

            action = GuestMenuAction.values()[askIntegerValue("Action", 1, actionsLength) - 1];
            switch (action) {
                case GuestMenuAction.SIGN_UP -> {
                    registrationUser();
                }
                case GuestMenuAction.LOGIN -> {
                    Employee employee = loginUser();
                    try {
                        employeeMenu(employee);
                        System.out.println("\nLogin was successful");
                    } catch (ConsoleDriverException e) {
                        System.out.println("\nLogin with this TIN or/and password not exists");
                    }
                }
                case GuestMenuAction.REGISTER_COMPANY -> {
                    registrationCompany();
                }
                case GuestMenuAction.EXIT -> {
                    System.out.println("\nBye!");
                }
            }
        } while (action != GuestMenuAction.EXIT);
    }

    private static void employeeMenu(Employee emp) {
        if (emp == null) {
            throw new ConsoleDriverException("Employee is null");
        }

        System.out.println("\nName: " + emp.getFullName());
        System.out.println("Job position: " + emp.getJobPosition());
        System.out.println("TIN: " + emp.getTIN());
        System.out.println("Company: " + emp.getCompany().getCompanyName());

        int actionsLength = EmployeeMenuAction.values().length;
        EmployeeMenuAction action;

        do {
            System.out.println("\n----- Menu -----");
            System.out.println("1) Create new document");
            System.out.println("2) List signed documents");
            System.out.println("3) Show unsigned documents");
            System.out.println("4) Log out\n");

            action = EmployeeMenuAction.values()[askIntegerValue("Action", 1, actionsLength) - 1];
            switch (action) {
                case EmployeeMenuAction.CREATE_DOC -> {
                    // ? -> select template -> filling data -> choose signatories -> this menu
                    System.out.println("\n----- Create new document -----");
                    createDocument();
                }
                case EmployeeMenuAction.LIST_DOCS -> System.out.println("List my signed documents");
                case EmployeeMenuAction.SHOW_UNSIGNED_DOCS -> System.out.println("Show unsigned documents");
                case EmployeeMenuAction.LOG_OUT -> {
                    System.out.println("Logging out...");
                }
            }
        } while (action != EmployeeMenuAction.LOG_OUT);
    }

    public static void registrationUser() {
        Company company;
        CompanyDAO companyDAO = new CompanyDAO();
        EmployeeDAO employeeDAO = new EmployeeDAO();

        System.out.println("\n----- Employee sign up -----");

        try {
            String companyName;

            do {
                companyName = askStringValue("Enter company name", true);
                if (companyName.isBlank()) {
                    System.out.println("\nReturning...");
                    return;
                }
                if (!companyDAO.existsByName(companyName)) {
                    System.out.println("Company with name: " + companyName +
                            " not exists. Pls try again or add company if you are owner");
                }
            } while (!companyDAO.existsByName(companyName));

            company = companyDAO.readByName(companyName);

            String tin;

            do {
                tin = askTINValue();
                if (tin.isBlank()) {
                    System.out.println("\nReturning...");
                    return;
                }
                if (employeeDAO.existsByTIN(tin)) {
                    System.out.println("Employee with this TIN:" + tin + " exists. Try else or login");
                }
            } while (employeeDAO.existsByTIN(tin));

            String fullName = askStringValue("Enter full name", false);
            String jobPosition = askStringValue("Enter job", false);
            String password = askStringValue("Enter password", false);

            Employee employee = new Employee(tin, password, fullName, jobPosition, company);
            EmployeeDAO dao = new EmployeeDAO();

            dao.insert(employee);
        } catch (CompanyValidationException e) {
            System.out.println("Company not exists: " + e.getMessage());
        } catch (UserValidationException e) {
            System.out.println("Employee exists: " + e.getMessage());
        }

        System.out.println("Employee registered successful");
    }

    public static void registrationCompany() {
        System.out.println("\n----- Company registration -----");
        Company company = new Company(askStringValue("Enter company name", false));
        CompanyDAO dao = new CompanyDAO();

        if (!dao.existsByName(company.getCompanyName())) {
            dao.insert(company);
            System.out.println("\nCompany registered successful\n");
        } else {
            System.out.println("\nCompany with name " + company.getCompanyName() + " already exists\n");
        }
    }

    public static Employee loginUser() {
        Employee employee;
        EmployeeDAO employeeDAO = new EmployeeDAO();

        System.out.println("\n----- Employee login -----");

        // NOTE: uncomment for real using
        /*String tin = askTINValue();
        String password = askStringValue("Enter password", false);
        employee = employeeDAO.readByTINandPasswordHash(tin, password);*/

        // NOTE: test data
        employee = employeeDAO.readByTINandPasswordHash("000-00-0000", "test");

        return employee;
    }

    private static void showAllDocumentTemplates() {
        DocumentTemplateDAO dao = new DocumentTemplateDAO();
        ArrayList<DocumentTemplate> templates = dao.readAll();

        System.out.println("List of all templates:");
        for (int i = 0; i < templates.size(); i++) {
            System.out.println("\t" + templates.get(i).getId() + ") " + templates.get(i).getTitle());
        }
    }

    private static Document createDocument() {
        DocumentTemplateDAO documentTemplateDAO = new DocumentTemplateDAO();
        DocumentTemplate documentTemplate;

        int id;
        int actionLenght = DocumentCreationMenuAction.values().length;
        DocumentCreationMenuAction action;

        do {
            showAllDocumentTemplates();

            id = askIntegerValue("\nChoose document`s template by id");
            documentTemplate = documentTemplateDAO.readById(id);
            System.out.println('\n' + documentTemplate.showStructure());

            System.out.println("""
                    ----- Document actions -----
                    1) Select this template
                    2) Select other template
                    3) Exit from creation document
                    """);
            action = DocumentCreationMenuAction.values()[askIntegerValue("Document action", 1, actionLenght) - 1];

            switch (action) {
                case SELECT_THIS_TEMPLATE -> {
                    Set<String> keys = documentTemplateDAO.readById(id).getKeys();
                    Map<String, String> values = new HashMap<>();

                    System.out.println("----- Filling out a document -----");
                    for (String key : keys) {
                        values.put(key, askStringValue("Enter " + key, false));
                    }

                }
                case CHANGE_TEMPLATE -> {

                }
                case LEAVE -> {
                    System.out.println("Leaving from document creation...");
                }
                default -> throw new IllegalStateException("Unexpected value: " + action);
            }

        } while (action != DocumentCreationMenuAction.LEAVE);


        // TODO: Add logic for creation document
        return null;
    }


    // console helpers

    private static String askTINValue() {
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

    private static String askStringValue(String question, boolean canBeBlank) {
        String answer;

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
