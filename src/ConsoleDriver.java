import dao.CompanyDAO;
import dao.DocumentDAO;
import dao.DocumentTemplateDAO;
import dao.EmployeeDAO;
import entities.Company;
import entities.Document;
import entities.DocumentTemplate;
import entities.Signatory;
import entities.user.Employee;
import exceptions.CompanyValidationException;
import exceptions.ConsoleDriverException;
import exceptions.UserValidationException;
import menuAction.DocumentCreationMenuAction;
import menuAction.EmployeeMenuAction;
import menuAction.GuestMenuAction;

import javax.print.Doc;
import java.util.*;

import static validators.ConsoleValidator.*;


public class ConsoleDriver {
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
                    createDocument(emp);
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
        String companyName = askStringValue("Enter company name", false);

        CompanyDAO dao = new CompanyDAO();

        if (!dao.existsByName(companyName)) {
            dao.insert(new Company(companyName));
            System.out.println("\nCompany registered successful\n");
        } else {
            System.out.println("\nCompany with name " + companyName + " already exists\n");
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

    private static Document createDocument(Employee employee) {
        DocumentTemplateDAO documentTemplateDAO = new DocumentTemplateDAO();
        DocumentDAO documentDAO = new DocumentDAO();
        EmployeeDAO employeeDAO = new EmployeeDAO();

        DocumentTemplate documentTemplate;

        int id;
        int actionLenght = DocumentCreationMenuAction.values().length;
        DocumentCreationMenuAction action;

        do {
            showAllDocumentTemplates();

            id = askIntegerValue("\nChoose document`s template by id");
            documentTemplate = documentTemplateDAO.readById(id);
            System.out.println('\n' + documentTemplate.getStructure());

            System.out.println("""
                    ----- Document actions -----
                    1) Select this template
                    2) Exit from creation document
                    """);
//            2) Select other template

            action = DocumentCreationMenuAction.values()[askIntegerValue("Document action", 1, actionLenght) - 1];

            switch (action) {
                case SELECT_THIS_TEMPLATE -> {
//                    FIXME: Now fields of document added for keys no in order how in document.
//                     I guess we need use List for this.
                    List<String> keys = documentTemplateDAO.readById(id).getKeys();
                    List<Signatory> signatories = new ArrayList<>();
                    signatories.add(new Signatory(employee, true));

                    System.out.println("\n----- Filling out a document -----");
                    Map<String, String> values = setValuesForDocument(keys);
//                    TODO: Add adding signatory

                    return new Document(documentTemplate, values, signatories);
                }
                case LEAVE -> {
                    System.out.println("Leaving from document creation...");
                }
                default -> throw new IllegalStateException("Unexpected value: " + action);
            }

        } while (action != DocumentCreationMenuAction.LEAVE);

        return null;
    }
}
