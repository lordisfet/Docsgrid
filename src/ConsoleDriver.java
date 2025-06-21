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
            System.out.println("4) Sign document");
            System.out.println("5) Log out\n");

            action = EmployeeMenuAction.values()[askIntegerValue("Action", 1, actionsLength) - 1];
            switch (action) {
                case EmployeeMenuAction.CREATE_DOC -> {
                    // ? -> select template -> filling data -> choose signatories -> this menu
                    System.out.println("\n----- Create new document -----");
                    createDocument(emp);
                }
                case EmployeeMenuAction.LIST_DOCS -> showDocumentsList(emp, true);
                case EmployeeMenuAction.SHOW_UNSIGNED_DOCS -> showDocumentsList(emp, false);
                case EmployeeMenuAction.SIGN_DOC -> signDocumentAction(emp);
                case EmployeeMenuAction.LOG_OUT -> {
                    System.out.println("Logging out...");
                }
            }
        } while (action != EmployeeMenuAction.LOG_OUT);
    }

    public static void registrationUser() {
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
                    \n----- Document actions -----
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

    private static void showDocumentsList(Employee employee, boolean signed) {
        DocumentDAO documentDAO = new DocumentDAO();
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<Document> documents = documentDAO.listBySignatoryEmployeeId(employee.getId(), signed);

        for (Document doc : documents) {
            printDocumentOverview(employee, doc);
        }
    }

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
