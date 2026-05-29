import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO {

    private RoomTypeDAO() {}

    public static void create(RoomType rt) {
        String sql = "INSERT OR IGNORE INTO room_types(type_name) VALUES(?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, rt.getTypeName());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] createRoomType: " + e.getMessage());
        }
    }

    /** Renames a room type; must pass the OLD name so we can locate the record. */
    public static void rename(String oldName, String newName) {
        String sql = "UPDATE room_types SET type_name=? WHERE type_name=? COLLATE NOCASE";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] renameRoomType: " + e.getMessage());
        }
    }

    public static List<RoomType> findAll() {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT type_name FROM room_types ORDER BY type_name COLLATE NOCASE";
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(new RoomType(rs.getString("type_name")));
        } catch (SQLException e) {
            System.err.println("[DB Error] findAllRoomTypes: " + e.getMessage());
        }
        return list;
    }
}
