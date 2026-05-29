import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    private GuestDAO() {}

    public static void create(Guest guest) {
        String sql =
            "INSERT INTO guests(username,password,date_of_birth,balance,address,gender) " +
            "VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, guest.getUsername());
            String raw = guest.getPasswordForStorage();
            ps.setString(2, raw.startsWith("$PBKDF2$") ? raw : PasswordUtils.hash(raw));
            ps.setString(3, guest.getDateOfBirth().toString());
            ps.setDouble(4, guest.getBalance());
            ps.setString(5, guest.getAddress() != null ? guest.getAddress() : "");
            ps.setString(6, guest.getGender().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] createGuest: " + e.getMessage());
        }
    }

    /** Updates mutable fields: balance, address, gender. */
    public static void update(Guest guest) {
        String sql =
            "UPDATE guests SET balance=?, address=?, gender=? " +
            "WHERE username=? COLLATE NOCASE";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, guest.getBalance());
            ps.setString(2, guest.getAddress() != null ? guest.getAddress() : "");
            ps.setString(3, guest.getGender().name());
            ps.setString(4, guest.getUsername());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] updateGuest: " + e.getMessage());
        }
    }

    public static List<Guest> findAll() {
        List<Guest> list = new ArrayList<>();
        String sql =
            "SELECT username, password, date_of_birth, balance, address, gender " +
            "FROM guests ORDER BY username COLLATE NOCASE";
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                try {
                    Guest g = new Guest(
                        rs.getString("username"),
                        rs.getString("password"),
                        LocalDate.parse(rs.getString("date_of_birth")),
                        rs.getDouble("balance"),
                        rs.getString("address"),
                        Guest.Gender.valueOf(rs.getString("gender"))
                    );
                    list.add(g);
                } catch (NegativeBalanceException | IllegalArgumentException e) {
                    System.err.println("[DB Warning] Skipping malformed guest row: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB Error] findAllGuests: " + e.getMessage());
        }
        return list;
    }
}
