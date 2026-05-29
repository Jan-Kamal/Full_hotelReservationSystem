import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomDAO {

    private RoomDAO() {}

    public static void create(Room room) {
        String sql =
            "INSERT INTO rooms(room_number, room_type_id, price, is_available, view_preference) " +
            "SELECT ?, rt.id, ?, ?, ? FROM room_types rt " +
            "WHERE rt.type_name = ? COLLATE NOCASE";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setDouble(2, room.getPrice());
            ps.setInt(3, room.isAvailable() ? 1 : 0);
            ps.setString(4, room.getViewPreference());
            ps.setString(5, room.getRoomType().getTypeName());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("[DB Error] createRoom: room type not found: "
                    + room.getRoomType().getTypeName());
            }
        } catch (SQLException e) {
            System.err.println("[DB Error] createRoom: " + e.getMessage());
        }
    }

    /** Updates price, availability, view, and room type for an existing room. */
    public static void update(Room room) {
        String sql =
            "UPDATE rooms SET " +
            "  room_type_id = (SELECT id FROM room_types WHERE type_name=? COLLATE NOCASE)," +
            "  price        = ?," +
            "  is_available = ?," +
            "  view_preference = ? " +
            "WHERE room_number = ? COLLATE NOCASE";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, room.getRoomType().getTypeName());
            ps.setDouble(2, room.getPrice());
            ps.setInt(3, room.isAvailable() ? 1 : 0);
            ps.setString(4, room.getViewPreference());
            ps.setString(5, room.getRoomNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] updateRoom: " + e.getMessage());
        }
    }

    /**
     * Attempts to delete a room. Returns false if FK constraint blocks it
     * (room has active reservations).
     */
    public static boolean delete(String roomNumber) {
        String sql = "DELETE FROM rooms WHERE room_number=? COLLATE NOCASE";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY")) {
                System.err.println("[DB] Cannot delete room " + roomNumber +
                    " — active reservations exist.");
                return false;
            }
            System.err.println("[DB Error] deleteRoom: " + e.getMessage());
            return false;
        }
    }

    /**
     * Links an existing amenity to a room via the room_amenities join table.
     * Safe to call multiple times — INSERT OR IGNORE prevents duplicates.
     */
    public static void addAmenity(String roomNumber, String amenityName) {
        String sql =
            "INSERT OR IGNORE INTO room_amenities(room_id, amenity_id) " +
            "SELECT r.id, a.id FROM rooms r, amenities a " +
            "WHERE r.room_number = ? COLLATE NOCASE AND a.name = ? COLLATE NOCASE";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ps.setString(2, amenityName);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] addRoomAmenity: " + e.getMessage());
        }
    }

    /**
     * Loads all rooms from the database.
     *
     * @param typeMap   map from type_name (lowercase) → RoomType object
     * @param amenityMap map from name (lowercase) → Amenity object
     */
    public static List<Room> findAll(Map<String, RoomType> typeMap,
                                     Map<String, Amenity> amenityMap) {
        List<Room> list = new ArrayList<>();

        String sql =
            "SELECT r.room_number, rt.type_name, r.price, r.is_available, r.view_preference " +
            "FROM rooms r JOIN room_types rt ON rt.id = r.room_type_id " +
            "ORDER BY r.room_number COLLATE NOCASE";

        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String typeName = rs.getString("type_name");
                RoomType rt = typeMap.get(typeName.toLowerCase());
                if (rt == null) {
                    rt = new RoomType(typeName);
                    typeMap.put(typeName.toLowerCase(), rt);
                }
                Room room = new Room(
                    rs.getString("room_number"),
                    rt,
                    rs.getDouble("price"),
                    rs.getString("view_preference")
                );
                room.setAvailable(rs.getInt("is_available") == 1);
                list.add(room);
            }
        } catch (SQLException e) {
            System.err.println("[DB Error] findAllRooms: " + e.getMessage());
        }

        // Load amenity links for all rooms
        String amenitySql =
            "SELECT r.room_number, a.name " +
            "FROM room_amenities ra " +
            "JOIN rooms    r ON r.id = ra.room_id " +
            "JOIN amenities a ON a.id = ra.amenity_id";
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(amenitySql)) {
            // Build a quick lookup of Room objects by room_number
            java.util.Map<String, Room> roomByNum = new java.util.HashMap<>();
            for (Room r : list) roomByNum.put(r.getRoomNumber().toLowerCase(), r);

            while (rs.next()) {
                Room room = roomByNum.get(rs.getString("room_number").toLowerCase());
                if (room == null) continue;
                String aName = rs.getString("name");
                Amenity a = amenityMap.get(aName.toLowerCase());
                if (a == null) a = new Amenity(aName);
                room.addAmenity(a);
            }
        } catch (SQLException e) {
            System.err.println("[DB Error] loadRoomAmenities: " + e.getMessage());
        }

        return list;
    }
}
