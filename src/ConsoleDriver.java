import dao.*;
import entities.Company;
import entities.Document;
import entities.DocumentTemplate;
import entities.Signatory;
import entities.user.BaseUser;
import entities.user.Employee;
import exceptions.CompanyValidationException;
import exceptions.ConsoleDriverException;
import exceptions.UserValidationException;
import menuAction.DocumentCreationMenuAction;
import menuAction.EmployeeMenuAction;
import menuAction.GuestMenuAction;

import javax.print.Doc;
import java.util.*;

import static entities.user.BaseUser.PasswordUtils.hashPassword;
import static entities.user.BaseUser.PasswordUtils.verifyPassword;
import static validators.ConsoleValidator.*;

/**
 * ConsoleDriver serves as the main entry point for the DocsGrid application,
 * handling user interactions via console menus for guests and employees.
 */
public class ConsoleDriver {
    // private static database ?
    // private static Repository repository ?

    /**
     * Main method launching the guest menu.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        guestMenu();
    }

    /**
     * Creates a fake authorized Employee instance for demo/testing.
     *
     * @return a fake Employee or null if validation fails
     */
    private static Employee fakeAuthorizeEmployee() {
        try {
            return new Employee("12345-174678-500", "1212", "Johny",
                    "Manager", new Company("SSU"));
        } catch (UserValidationException e) {
            System.out.println("Validation error. Message: " + e.getMessage());
            return null;
        }
    }

    /**
     * Displays the guest menu and handles sign up, login, and company registration.
     */
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
                    registrationUser(false);
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
                case GuestMenuAction.REGISTER_COMPANY -> registrationCompany();
                case GuestMenuAction.EXIT -> System.out.println("\nBye!");
            }
        } while (action != GuestMenuAction.EXIT);
    }

    /**
     * Displays the employee menu and handles document operations and logout.
     *
     * @param emp the logged-in Employee; must not be null
     * @throws ConsoleDriverException if Employee is null
     */
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
            System.out.println("4) Sign document");
            System.out.println("5) Log out\n");

            action = EmployeeMenuAction.values()[askIntegerValue("Action", 1, actionsLength) - 1];
            switch (action) {
                case EmployeeMenuAction.CREATE_DOC -> {
                    System.out.println("\n----- Create new document -----");
                    createDocument(emp);
                }
                case EmployeeMenuAction.LIST_DOCS -> showDocumentsList(emp, true);
                case EmployeeMenuAction.SHOW_UNSIGNED_DOCS -> showDocumentsList(emp, false);
                case EmployeeMenuAction.SIGN_DOC -> signDocumentAction(emp);
                case EmployeeMenuAction.LOG_OUT -> System.out.println("Logging out...");
            }
        } while (action != EmployeeMenuAction.LOG_OUT);
    }

    /**
     * Handles employee registration flow via console prompts.
     */
    public static void registrationUser(boolean ownerAdding) {
        Company company = null;
        CompanyDAO companyDAO = new CompanyDAO();
        EmployeeDAO employeeDAO = new EmployeeDAO();

        System.out.println("\n----- Employee sign up -----");

        try {
            String companyName;

            do {
//                TODO: I guess need to change "without company" on more non-realistic company name
//                 and add trigger for add null value to DB
                companyName = askStringValue("Enter company name or \"without company\"", true);
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
                    System.out.println("Employee with this TIN: " + tin + " exists. Try else or login");
                }
            } while (employeeDAO.existsByTIN(tin));

            String fullName = askStringValue("Enter full name", false);
            String jobPosition;
            if (ownerAdding) {
                jobPosition = "Owner";
                System.out.println("Enter job: Owner");
            }
            else {
                jobPosition = askStringValue("Enter job", false);
            }
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

    /**
     * Handles company registration flow via console prompts.
     */
    public static void registrationCompany() {
        System.out.println("\n----- Company registration -----");
        String companyName = askStringValue("Enter company name", false);

        CompanyDAO dao = new CompanyDAO();

        if (!dao.existsByName(companyName)) {
            dao.insert(new Company(companyName));
            registrationUser(true);
            System.out.println("\nCompany registered successful\n");
        } else {
            System.out.println("\nCompany with name " + companyName + " already exists\n");
        }
    }

    /**
     * Prompts user for login credentials and returns the authenticated employee.
     *
     * @return authenticated Employee or null
     */
    public static Employee loginUser() {
        System.out.println("\n----- Employee login -----");

        String tin = askTINValue();
        String password = askStringValue("Enter password", false);

        EmployeeDAO dao = new EmployeeDAO();
        Employee employee = dao.readByTIN(tin);

        if (employee == null) {
            System.out.println("No user found with TIN " + tin);
            return null;
        }

        if (BaseUser.PasswordUtils.verifyPassword(password, employee.getPasswordHash())) {
            System.out.println("Login successful. Welcome, " + employee.getFullName());
            return employee;
        } else {
            System.out.println("Incorrect password");
            return null;
        }
    }

    /**
     * Displays all available document templates.
     */
    private static void showAllDocumentTemplates() {
        DocumentTemplateDAO dao = new DocumentTemplateDAO();
        ArrayList<DocumentTemplate> templates = dao.readAll();

        System.out.println("List of all templates:");
        for (int i = 0; i < templates.size(); i++) {
            System.out.println("\t" + templates.get(i).getId() + ") " + templates.get(i).getTitle());
        }
    }

    /**
     * Guides the employee through document creation, adds signatories by TIN,
     * persists the document, and returns the new Document.
     *
     * @param creator the Employee creating the document
     * @return the persisted Document or null if creation is aborted
     */
    private static Document createDocument(Employee creator) {
        DocumentTemplateDAO templateDAO = new DocumentTemplateDAO();
        DocumentDAO documentDAO = new DocumentDAO();
        EmployeeDAO employeeDAO = new EmployeeDAO();

        DocumentTemplate template;
        int id;
        int menuSize = DocumentCreationMenuAction.values().length;
        DocumentCreationMenuAction action = null;

        do {
            showAllDocumentTemplates();

            id = askIntegerValue("\nChoose document's template by id");
            template = templateDAO.readById(id);
            if (template == null) {
                System.out.println("Template not found. Try again.");
                continue;
            }
            System.out.println("\nStructure: " + template.getStructure());
            System.out.println("----- Document actions -----");
            System.out.println("1) Select this template");
            System.out.println("2) Exit from creation document");

            action = DocumentCreationMenuAction.values()[askIntegerValue("Document action", 1, menuSize) - 1];
            if (action == DocumentCreationMenuAction.LEAVE) {
                System.out.println("Leaving document creation...");
                return null;
            }
        } while (action != DocumentCreationMenuAction.SELECT_THIS_TEMPLATE);

        List<String> keys = template.getKeys();
        System.out.println("\n----- Filling out the document -----");
        Map<String, String> values = setValuesForDocument(keys);

        List<Signatory> signatories = new ArrayList<>();
        signatories.add(new Signatory(creator, true)); // creator signs automatically

        System.out.println("\n----- Add additional signatories -----");
        while (true) {
            String tin = askStringValue("Enter signatory TIN (blank to finish)", true);
            if (tin.isBlank()) break;
            if (!employeeDAO.existsByTIN(tin)) {
                System.out.println("No employee found with TIN: " + tin);
                continue;
            }

            Employee emp = employeeDAO.readByTIN(tin);
            if (emp == null) {
                System.out.println("Error loading employee with TIN: " + tin);
                continue;
            }
            signatories.add(new Signatory(emp, false));
            System.out.println("Added signatory: " + emp.getFullName());
        }

        Document document = new Document(template, values, signatories);
        documentDAO.insert(document);
        System.out.println("Document created with ID: " + document.getId());
        return document;
    }

    /**
     * Handles the sign document action for the employee.
     *
     * @param emp the Employee signing the document
     */
    private static void signDocumentAction(Employee emp) {
        DocumentDAO documentDAO = new DocumentDAO();

        int answer = askIntegerValue("Enter document ID (0 for leave)");
        if (answer == 0) {
            System.out.println("Canceled!");
            return;
        }

        Document document = documentDAO.readById(answer);
        if (document == null) {
            System.out.println("There is no document with this ID");
            return;
        }

        boolean isSignatory = false;
        for (Signatory signatory : document.getSignatories()) {
            if (signatory.getEmployee().getId().equals(emp.getId())) {
                if (!signatory.isSigned()) {
                    isSignatory = true;
                }
                break;
            }
        }

        if (!isSignatory) {
            System.out.println("Your signature is not needed on this document or you already signed this document");
            return;
        }

        printDocumentOverview(emp, document);
        String confirmation = askStringValue(
                "Are you sure that you want to sign this document (y - YES, other - NO)", false);
        if (!confirmation.toLowerCase().trim().equals("y")) {
            System.out.println("Canceled!");
        }

        document.signByEmployee(emp);
        documentDAO.update(document);

        System.out.println("Document has been successfully signed!");
    }

    /**
     * Shows a list of documents filtered by sign status for the employee.
     *
     * @param employee the Employee viewing documents
     * @param signed   true for signed, false for unsigned
     */
    private static void showDocumentsList(Employee employee, boolean signed) {
        DocumentDAO documentDAO = new DocumentDAO();
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<Document> documents = documentDAO.listBySignatoryEmployeeId(employee.getId(), signed);

        for (Document doc : documents) {
            printDocumentOverview(employee, doc);
        }
    }

    /**
     * Builds representations of signatories' statuses for display.
     *
     * @param currEmployee current Employee context
     * @param doc          Document for which to display signatories
     * @return list of formatted signatory status strings
     */
    private static List<String> getSignatoriesRepresentations(Employee currEmployee, Document doc) {
        List<String> signatoriesStatuses = new ArrayList<>();
        for (Signatory signatory : doc.getSignatories()) {
            Employee signatoryEmp = signatory.getEmployee();
            String signatorySubstr = signatoryEmp.getFullName() + " (TIN: " + signatoryEmp.getTIN()
                    + (Objects.equals(signatoryEmp.getId(), currEmployee.getId()) ? ", you) " : ") ")
                    + (signatory.isSigned() ? "(signed)" : "(unsigned)");

            signatoriesStatuses.add(signatorySubstr);
        }
        return signatoriesStatuses;
    }

    /**
     * Prints an overview of the document including header, content, and sign status.
     *
     * @param currEmployee current Employee context
     * @param doc          Document to display
     */
    private static void printDocumentOverview(Employee currEmployee, Document doc) {
        List<String> signatoriesStatuses = getSignatoriesRepresentations(currEmployee, doc);

        System.out.println("--- DOC (ID: " + doc.getId() + ") ---");
        System.out.println("TITLE:       " + doc.getTemplate().getTitle());
        System.out.println("SIGNATORIES: " + String.join(", ", signatoriesStatuses));
        System.out.println("IS VALID?:   " + (doc.isCompleted() ? "YES" : "NO"));
        System.out.println("\nCONTENT:\n***\n" + doc.render() + "\n***");
        System.out.println("--- DOC (ID: " + doc.getId() + ") ---\n");
    }
}
