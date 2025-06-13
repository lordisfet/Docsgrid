import java.sql.*;
import java.util.Scanner;

public class RegistrationApp {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "12345";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Реєстрація: 1 - Працівник, 2 - Компанія");
            int type = Integer.parseInt(scanner.nextLine());

            System.out.print("Введіть ІПН (TIN): ");
            String tin = scanner.nextLine();

            if (type == 1) {
                registerEmployee(conn, scanner, tin);
            } else if (type == 2) {
                registerCompany(conn, scanner, tin);
            } else {
                System.out.println("Невірний тип.");
            }

        } catch (SQLException e) {
            System.out.println("Помилка при підключенні або записі до БД.");
            e.printStackTrace();
        }
    }

    private static void registerCompany(Connection conn, Scanner scanner, String tin) throws SQLException {
        System.out.print("Введіть назву компанії: ");
        String companyName = scanner.nextLine();

        String insertCompanySQL = "INSERT INTO company(name, tin) VALUES (?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(insertCompanySQL)) {
            stmt.setString(1, companyName);
            stmt.setString(2, tin);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int companyId = rs.getInt("id");

                // Додаємо власника
                System.out.println("Реєстрація власника компанії:");
                System.out.print("ПІБ: ");
                String ownerName = scanner.nextLine();

                addEmployee(conn, ownerName, "owner", tin, companyId);
                System.out.println("Компанію та власника успішно зареєстровано.");
            }
        }
    }

    private static void registerEmployee(Connection conn, Scanner scanner, String tin) throws SQLException {
        System.out.print("ПІБ: ");
        String fullName = scanner.nextLine();

        System.out.print("Назва компанії: ");
        String companyName = scanner.nextLine();

        System.out.print("Посада: ");
        String job = scanner.nextLine();

        String selectCompanySQL = "SELECT id FROM company WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectCompanySQL)) {
            stmt.setString(1, companyName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int companyId = rs.getInt("id");
                addEmployee(conn, fullName, job, tin, companyId);
                System.out.println("Працівника успішно зареєстровано.");
            } else {
                System.out.println("Компанію не знайдено.");
            }
        }
    }

    private static void addEmployee(Connection conn, String fullName, String job, String tin, int companyId) throws SQLException {
        String insertEmployeeSQL = "INSERT INTO employee(full_name, job_title, tin, company_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertEmployeeSQL)) {
            stmt.setString(1, fullName);
            stmt.setString(2, job);
            stmt.setString(3, tin);
            stmt.setInt(4, companyId);
            stmt.executeUpdate();
        }
    }
}
