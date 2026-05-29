import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReservationDAO {

    private ReservationDAO() {}

    /**
     * Persists a new reservation and its invoice in a single transaction.
     * The Invoice payment method and check-in date are required for audit records.
     */
    public static void create(Reservation res, Invoice inv) {
        Connection c;
        try { c = DatabaseManager.getConnection(); } catch (SQLException e) {
            System.err.println("[DB Error] getConnection: " + e.getMessage()); return;
        }
        String resSql =
            "INSERT INTO reservations(reservation_code, guest_id, room_id, " +
            "                         check_in_date, check_out_date, status) " +
            "SELECT ?, g.id, r.id, ?, ?, ? " +
            "FROM guests g, rooms r " +
            "WHERE g.username=? COLLATE NOCASE AND r.room_number=? COLLATE NOCASE";
        String invSql =
            "INSERT INTO invoices(reservation_id, amount, payment_method, check_in_date) " +
            "SELECT id, ?, ?, ? FROM reservations WHERE reservation_code=?";
        try {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(resSql)) {
                ps.setString(1, res.getReservationId());
                ps.setString(2, res.getCheckInDate() != null  ? res.getCheckInDate().toString()  : LocalDate.now().toString());
                ps.setString(3, res.getCheckOutDate() != null ? res.getCheckOutDate().toString() : LocalDate.now().plusDays(1).toString());
                ps.setString(4, res.getStatus().name());
                ps.setString(5, res.getGuest().getUsername());
                ps.setString(6, res.getRoom().getRoomNumber());
                ps.executeUpdate();
            }
            if (inv != null) {
                try (PreparedStatement ps = c.prepareStatement(invSql)) {
                    ps.setDouble(1, inv.getAmount());
                    ps.setString(2, inv.getMethod().name());
                    ps.setString(3, inv.getCheckInDate() != null ? inv.getCheckInDate().toString() : LocalDate.now().toString());
                    ps.setString(4, res.getReservationId());
                    ps.executeUpdate();
                }
            }
            c.commit();
        } catch (SQLException e) {
            try { c.rollback(); } catch (SQLException ignored) {}
            System.err.println("[DB Error] createReservation: " + e.getMessage());
        } finally {
            try { c.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    /** Updates only the status field. */
    public static void updateStatus(String reservationCode, String newStatus) {
        String sql = "UPDATE reservations SET status=? WHERE reservation_code=?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, reservationCode);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] updateReservationStatus: " + e.getMessage());
        }
    }

    /** Deletes a reservation record (invoices/amenities cascade). */
    public static void delete(String reservationCode) {
        String sql = "DELETE FROM reservations WHERE reservation_code=?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, reservationCode);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] deleteReservation: " + e.getMessage());
        }
    }

    /** Adds an extra amenity text entry for a reservation. */
    public static void addExtraAmenity(String reservationCode, String amenityName) {
        String sql =
            "INSERT OR IGNORE INTO reservation_amenities(reservation_id, amenity_name) " +
            "SELECT id, ? FROM reservations WHERE reservation_code=?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, amenityName);
            ps.setString(2, reservationCode);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB Error] addExtraAmenity: " + e.getMessage());
        }
    }

    /**
     * Loads all non-cancelled reservations from the database, resolving
     * Guest and Room references from the supplied maps.
     *
     * @param guestMap  map from username (lower-case) → Guest
     * @param roomMap   map from room_number (lower-case) → Room
     */
    public static List<Reservation> findActive(Map<String, Guest> guestMap,
                                               Map<String, Room>  roomMap) {
        List<Reservation> list = new ArrayList<>();
        String sql =
            "SELECT res.reservation_code, res.check_in_date, res.check_out_date, res.status," +
            "       g.username AS guest_username, r.room_number " +
            "FROM reservations res " +
            "JOIN guests g ON g.id = res.guest_id " +
            "JOIN rooms  r ON r.id = res.room_id " +
            "WHERE res.status IN ('PENDING','CONFIRMED') " +
            "ORDER BY res.created_at";
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Guest guest = guestMap.get(rs.getString("guest_username").toLowerCase());
                Room  room  = roomMap.get(rs.getString("room_number").toLowerCase());
                if (guest == null || room == null) continue;

                LocalDate ci = LocalDate.parse(rs.getString("check_in_date"));
                LocalDate co = LocalDate.parse(rs.getString("check_out_date"));
                Reservation res2 = new Reservation(guest, room, ci, co);
                res2.setReservationId(rs.getString("reservation_code"));

                String status = rs.getString("status");
                if ("CONFIRMED".equals(status)) res2.confirm();

                list.add(res2);
            }
        } catch (SQLException e) {
            System.err.println("[DB Error] findActiveReservations: " + e.getMessage());
        }

        // Load extra amenities for each reservation
        loadExtraAmenities(list);
        return list;
    }

    private static void loadExtraAmenities(List<Reservation> reservations) {
        if (reservations.isEmpty()) return;
        // Build a quick map: code → Reservation
        Map<String, Reservation> byCode = new java.util.HashMap<>();
        for (Reservation r : reservations) byCode.put(r.getReservationId(), r);

        String sql =
            "SELECT res.reservation_code, ra.amenity_name " +
            "FROM reservation_amenities ra " +
            "JOIN reservations res ON res.id = ra.reservation_id";
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Reservation r = byCode.get(rs.getString("reservation_code"));
                if (r != null) r.addExtraAmenity(new Amenity(rs.getString("amenity_name")));
            }
        } catch (SQLException e) {
            System.err.println("[DB Error] loadExtraAmenities: " + e.getMessage());
        }
    }
}
