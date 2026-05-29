import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    private StaffDAO() {}

    public static void createAdmin(Admin admin) {
        insert(admin.getUsername(), admin.getPasswordForStorage(),
               admin.getDateOfBirth(), "ADMIN", admin.getWorkingHours());
    }

    public static void createReceptionist(Receptionist rec) {
        insert(rec.getUsername(), rec.getPasswordForStorage(),
               rec.getDateOfBirth(), "RECEPTIONIST", rec.getWorkingHours());
    }

    private static void insert(String username, String rawPassword,
                                LocalDate dob, String role, int hours) {
        String sql =
            "INSERT INTO staff(username,password,date_of_birth,role,working_hours) " +
            "VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            String pwd = rawPassword.startsWith("$PBKDF2$") ? rawPassword : PasswordUtils.hash(rawPassword);
            ps.setString(2, pwd);
            ps.setString(3, dob.toString());
            ps.setString(4, role);
            ps.setInt(5, hours);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] createStaff: " + e.getMessage());
        }
    }

    public static List<Admin> findAllAdmins() {
        List<Admin> list = new ArrayList<>();
        String sql =
            "SELECT username, password, date_of_birth, working_hours " +
            "FROM staff WHERE role='ADMIN' ORDER BY username COLLATE NOCASE";
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Admin a = new Admin(
                    rs.getString("username"),
                    rs.getString("password"),
                    LocalDate.parse(rs.getString("date_of_birth")),
                    rs.getInt("working_hours")
                );
                list.add(a);
            }
        } catch (SQLException e) {
            System.err.println("[DB Error] findAllAdmins: " + e.getMessage());
        }
        return list;
    }

    public static List<Receptionist> findAllReceptionists() {
        List<Receptionist> list = new ArrayList<>();
        String sql =
            "SELECT username, password, date_of_birth, working_hours " +
            "FROM staff WHERE role='RECEPTIONIST' ORDER BY username COLLATE NOCASE";
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Receptionist r = new Receptionist(
                    rs.getString("username"),
                    rs.getString("password"),
                    LocalDate.parse(rs.getString("date_of_birth")),
                    rs.getInt("working_hours")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("[DB Error] findAllReceptionists: " + e.getMessage());
        }
        return list;
    }
}
