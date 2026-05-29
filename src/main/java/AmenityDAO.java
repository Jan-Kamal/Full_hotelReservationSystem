import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmenityDAO {

    private AmenityDAO() {}

    public static void create(Amenity amenity) {
        String sql = "INSERT OR IGNORE INTO amenities(name) VALUES(?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, amenity.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] createAmenity: " + e.getMessage());
        }
    }

    /** Renames an amenity. Pass the OLD name to locate the record. */
    public static void rename(String oldName, String newName) {
        String sql = "UPDATE amenities SET name=? WHERE name=? COLLATE NOCASE";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] renameAmenity: " + e.getMessage());
        }
    }

    public static void delete(String name) {
        String sql = "DELETE FROM amenities WHERE name=? COLLATE NOCASE";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] deleteAmenity: " + e.getMessage());
        }
    }

    public static List<Amenity> findAll() {
        List<Amenity> list = new ArrayList<>();
        String sql = "SELECT name FROM amenities ORDER BY name COLLATE NOCASE";
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(new Amenity(rs.getString("name")));
        } catch (SQLException e) {
            System.err.println("[DB Error] findAllAmenities: " + e.getMessage());
        }
        return list;
    }
}
