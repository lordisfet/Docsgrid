/*
Студенти: Костян О. В., Савченко М. О., Остапенко О. В., Коноплянченко Д. Є., Зуєва Х. В.
Дисципліна: ООП на мові Java
Проект на тему: "Патерн проектування Prototype. DocsGrid — система управління документами"

Часу витрачено: 7 днів

Це наша власна робота. Штучний інтелект  використовувався:
Генерація шаблонів
Генерація документації
Пояснення можливої реалізації
Пояснення для Bcrypt

Опис програми:
Дана програма призначена для роботи з патерном проектування
Це система для управління документами, яка дозволяє:
Реєструвати компанії та співробітників
Створювати документи на основі шаблонів
Додавати підписантів до документів
Підписувати документи
Переглядати список підписаних та непідписаних документів
*/

import dao.*;
import entities.Company;
import entities.Document;
import entities.DocumentTemplate;
import entities.Signatory;
import entities.user.BaseUser;
import entities.user.Employee;
import exceptions.CompanyValidationException;
import exceptions.ConsoleDriverException;
import exceptions.DocumentTemplateValidationException;
import exceptions.UserValidationException;

import menuAction.AdminMenuAction;
import menuAction.DocumentCreationMenuAction;
import menuAction.EmployeeMenuAction;
import menuAction.GuestMenuAction;
import org.postgresql.util.PasswordUtil;
import validators.ConsoleValidator;

import java.util.*;

import static entities.user.BaseUser.PasswordUtils.hashPassword;
import static entities.user.BaseUser.PasswordUtils.verifyPassword;
import static validators.ConsoleValidator.*;

/**
 * ConsoleDriver serves as the main entry point for the DocsGrid application,
 * handling user interactions via console menus for guests and employees.
 */
public class ConsoleDriver {

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
            return new Employee("111-22-3333", "123", "Johny",
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
            System.out.println("3) Log in how admin");
            System.out.println("4) Register Company");
            System.out.println("5) Exit\n");

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
                case LOGIN_ADMIN -> {
                    String login = askStringValue("Enter admin login", false);
                    String password = askStringValue("Enter admin password", false);
/*                    String login = "admin";
                    String password = "123";*/

                    if (login.equals("admin") && verifyPassword(password, hashPassword("123"))) {
                        adminMenu();
                    } else {
                        System.out.println("\nNon correct data to logging how admin");
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
            System.out.println("1) Who");
            System.out.println("2) Create new document");
            System.out.println("3) List signed documents");
            System.out.println("4) Show unsigned documents");
            System.out.println("5) Sign document");
            System.out.println("6) Log out\n");

            action = EmployeeMenuAction.values()[askIntegerValue("Action", 1, actionsLength) - 1];
            switch (action) {
                case WHO -> {
                    System.out.println("\n" + emp);
                }
                case EmployeeMenuAction.CREATE_DOC -> {
                    createDocument(emp);
                }
                case EmployeeMenuAction.LIST_DOCS -> showDocumentsList(emp, true);
                case EmployeeMenuAction.SHOW_UNSIGNED_DOCS -> showDocumentsList(emp, false);
                case EmployeeMenuAction.SIGN_DOC -> signDocumentAction(emp);
                case EmployeeMenuAction.LOG_OUT -> System.out.println("Logging out...");
            }
        } while (action != EmployeeMenuAction.LOG_OUT);
    }

    public static void adminMenu() {
        System.out.println("\nYou are admin. congratulation!!!\n");

        System.out.println("""
                ----- Admin menu -----
                1) Create document template
                2) Edit document template
                3) Log out""");

        int actionLength = AdminMenuAction.values().length;
        AdminMenuAction action;
        do {
            action = AdminMenuAction.values()
                    [askIntegerValue("\nWhat are you want do", 1, actionLength) - 1];
            switch (action) {
                case CREATE_TEMPLATE -> {
                    System.out.println("\n----- Document template creating -----");
                    String title = askStringValue("Enter tempalte title", false);
                    System.out.println("The document contains keys under {{adminName}}." +
                            "\nEach key follows this structure: whose <admin> + which <Name>");

                    DocumentTemplateDAO dao = new DocumentTemplateDAO();
                    DocumentTemplate documentTemplate = null;
                    do {
                        try {
                            String structure = askStringValue("Enter structure", false);
                            documentTemplate = new DocumentTemplate(title, structure);
                            System.out.println("\nNext template was added:\n" + documentTemplate);
                            dao.insert(documentTemplate);
                        } catch (DocumentTemplateValidationException e) {
                            System.out.println("\nCreating template: " + e.getMessage());
                        }
                    } while (documentTemplate == null);
                }
                case EDIT_TEMPLATE -> {
                    System.out.println("\nSoon");
                }
                case LOG_OUT -> System.out.println("Logging out...");
            }
        } while (action != AdminMenuAction.LOG_OUT);
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
                tin = askTINValue("Enter TIN");
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
            } else {
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
        String companyName = askStringValue("Enter company name", true);
        if (companyName.isBlank()) {
            System.out.println("\nReturning...");
            return;
        }

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

        String tin = askTINValue("Enter TIN");
        String password = askStringValue("Enter password", false);

        EmployeeDAO dao = new EmployeeDAO();
        Employee employee = dao.readByTIN(tin);

        if (employee == null) {
            System.out.println("No user found with TIN " + tin);
            return null;
        }

        if (verifyPassword(password, employee.getPasswordHash())) {
            System.out.println("Login successful. Welcome, " + employee.getFullName());
            return employee;
        } else {
            System.out.println("Incorrect password");
            return null;
        }
    }

    /**
     * Get all available document templates.
     */
    private static ArrayList<DocumentTemplate> getAllDocumentTemplates() {
        DocumentTemplateDAO dao = new DocumentTemplateDAO();
        ArrayList<DocumentTemplate> templates = dao.readAll();

        return templates;
    }

    /**
     * Guides the employee through document creation, adds signatories by TIN,
     * persists the document, and returns the new Document.
     *
     * @param creator the Employee creating the document
     * @return void
     */
    private static void createDocument(Employee creator) {
        DocumentTemplateDAO templateDAO = new DocumentTemplateDAO();
        DocumentDAO documentDAO = new DocumentDAO();
        EmployeeDAO employeeDAO = new EmployeeDAO();

        DocumentTemplate template;
        int id;
        int menuSize = DocumentCreationMenuAction.values().length;

        System.out.println("""
                \n----- Document actions -----
                1) Create by template
                2) Create from previous document
                3) Leave
                """);

        DocumentCreationMenuAction action = DocumentCreationMenuAction.values()
                [askIntegerValue("Action", 1, menuSize) - 1];

        switch (action) {
            case CREATE_BY_TEMPLATE -> {
                ArrayList<DocumentTemplate> templates = getAllDocumentTemplates();
                System.out.println("List of all templates:");
                for (int i = 0; i < templates.size(); i++) {
                    System.out.println("\t" + templates.get(i).getId() + ") " + templates.get(i).getTitle());
                }
                int lastTemplate = templates.getLast().getId();
                System.out.println("\t" + (lastTemplate + 1) + ") Leave");

                id = askIntegerValue("\nChoose document's template by id");
                if (id == lastTemplate + 1) {
                    return;
                }

                try {
                    template = templateDAO.readById(id);
                } catch (NullPointerException e) {
                    System.out.println(e.getMessage());
                    return;
                }

                if (template == null) {
                    System.out.println("Template not found. Try again.");
                }
                System.out.println("\nStructure: " + template.getStructure());

                List<String> keys = template.getKeys();
                System.out.println("\n----- Filling out the document -----");
                Map<String, String> values = setValuesForDocument(keys);

                List<Signatory> signatories = new ArrayList<>();
                signatories.add(new Signatory(creator, true)); // creator signs automatically
                Set<String> mentionedTINs = extractTINsFromData(values);
                for (String mentionedTIN : mentionedTINs) {
                    if (mentionedTIN == null || mentionedTIN.isBlank() || creator.getTIN().equals(mentionedTIN)) {
                        continue;
                    }

                    Employee mentionedEmp = employeeDAO.readByTIN(mentionedTIN);
                    if (mentionedEmp == null) {
                        System.out.println("Signatory with this TIN not exists. We cannot add him");
                        continue;
                    }

                    signatories.add(new Signatory(mentionedEmp, false));

                    System.out.println("Signatory " + mentionedEmp.getFullName() + " (TIN: " + mentionedTIN + ") " +
                            "has been automatically added");
                }

                System.out.println("\n----- Add additional signatories -----");
                while (true) {
                    String tin = askStringValue("Enter signatory TIN (blank to finish)", true);
                    if (tin.isBlank()) break;
                    if (!employeeDAO.existsByTIN(tin)) {
                        System.out.println("No employee found with TIN: " + tin);
                        continue;
                    }
                    if (mentionedTINs.contains(tin)) {
                        System.out.println("This signatory is already mentioned");
                        continue;
                    }

                    Employee emp = employeeDAO.readByTIN(tin);
                    if (emp == null) {
                        System.out.println("Error loading employee with TIN: " + tin);
                        continue;
                    }

                    mentionedTINs.add(emp.getTIN());
                    signatories.add(new Signatory(emp, false));
                    System.out.println("Added signatory: " + emp.getFullName() + " (TIN: " + emp.getTIN() + ") ");
                }

                Document document = new Document(template, values, signatories);
                documentDAO.insert(document);
                System.out.println("\nDocument created with ID: " + document.getId());
            }
            case CREATE_BASED_ON -> {
                DocumentDAO dao = new DocumentDAO();
                Map<Integer, String> documentsTitles = dao.readAllTitleBySignatoryEmployeeId(creator.getId());
                List<Integer> ids = new ArrayList<>();

                System.out.println("\nList of title your documents: ");
                for (Map.Entry<Integer, String> entry : documentsTitles.entrySet()) {
                    id = entry.getKey();
                    ids.add(id);
                    String title = entry.getValue();
                    System.out.println("ID: " + id + ", Title: " + title);
                }

                do {
                    id = askIntegerValue("\nEnter what document you want use how base");
                    if (!ids.contains(id)) {
                        System.out.println("Invalid document`s ID. Please try again.");
                    }
                } while (!ids.contains(id));

                List<Signatory> signatories = new ArrayList<>();
                printDocumentOverview(creator, dao.readById(id));
                Document copy = editDocumentContent(dao.readById(id));
                Set<String> mentionedTINs = extractTINsFromData(copy.getContent());
                for (String mentionedTIN : mentionedTINs) {
                    if (mentionedTIN == null || mentionedTIN.isBlank() || creator.getTIN().equals(mentionedTIN)) {
                        continue;
                    }

                    Employee mentionedEmp = employeeDAO.readByTIN(mentionedTIN);
                    if (mentionedEmp == null) {
                        System.out.println("Signatory with this TIN not exists. We cannot add him");
                        continue;
                    }

                    signatories.add(new Signatory(mentionedEmp, false));

                    System.out.println("Signatory " + mentionedEmp.getFullName() + " (TIN: " + mentionedTIN + ") " +
                            "has been automatically added");
                }

                copy.setSignatories(signatories);
                dao.insert(copy);
            }
            case LEAVE -> {
                return;
            }
        }
    }

    /**
     * Interactively edits the content fields of the specified {@code Document} via console input.
     * <p>
     * This method displays all current key-value content fields and allows the user to modify them
     * by entering the field name and providing a new value, validated by type (e.g., TIN, date, email, number).
     * The field type is inferred via {@code extractFieldType}, and appropriate validators from
     * {@code ConsoleValidator} are used to ensure input correctness.
     * </p>
     *
     * <p>
     * The method returns a new {@code Document} instance as a deep copy of the input document,
     * preserving updated content. If the input document has no editable content,
     * the method logs a message and returns {@code null}.
     * </p>
     *
     * @param document the {@code Document} whose content is to be edited
     * @return a new {@code Document} reflecting the edited content, or {@code null} if no content is present
     */

    public static Document editDocumentContent(Document document) {
        Map<String, String> content = document.getContent();
        if (content == null || content.isEmpty()) {
            System.out.println("Document has no content to edit.");
            return null;
        }

        String input;

        while (true) {
            System.out.println("\nCurrent fields in the document:");
            for (Map.Entry<String, String> entry : content.entrySet()) {
                System.out.println("- " + entry.getKey() + ": " + entry.getValue());
            }

            input = askStringValue("\nEnter the field name to change(press Enter to finish)", true);

            if (input.isBlank()) {
                System.out.println("\nReturning...");
                break;
            }

            if (!content.containsKey(input)) {
                System.out.println("Field \"" + input + "\" does not exist. Please try again.");
                continue;
            }

            String newValue;
            switch (extractFieldType(input)) {
                case "tin":
                    newValue = ConsoleValidator.askTINValue("Enter new TIN");
                    break;
                case "date":
                    newValue = ConsoleValidator.askDateValue();
                    break;
                case "email":
                case "gmail":
                    newValue = ConsoleValidator.askEmailValue();
                    break;
                case "paymentamount":
                case "number":
                    newValue = ConsoleValidator.askNumberValue();
                    break;
                case "companyname":
                    newValue = ConsoleValidator.askExistingCompanyName();
                    break;
                default:
                    newValue = ConsoleValidator.askStringValue("Enter new \"" + input + "\"", false);
            }

            content.put(input, newValue);
            System.out.println("\nField updated.");
        }

        return new Document(document);
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
