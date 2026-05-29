import java.io.File;
import java.sql.*;

public class DatabaseManager {

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found. Add sqlite-jdbc to pom.xml.", e);
        }
    }

    private static final String DB_DIR =
            System.getProperty("user.home") + File.separator + ".hotelfx";
    private static final String DB_URL =
            "jdbc:sqlite:" + DB_DIR + File.separator + "hotel.db";

    private static Connection conn;

    private DatabaseManager() {}

    public static synchronized Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            new File(DB_DIR).mkdirs();
            conn = DriverManager.getConnection(DB_URL);
            applyPragmas(conn);
        }
        return conn;
    }

    private static void applyPragmas(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA journal_mode = WAL");
            st.execute("PRAGMA synchronous  = NORMAL");
            st.execute("PRAGMA cache_size   = -8000");
            st.execute("PRAGMA temp_store   = MEMORY");
        }
    }

    public static void initSchema() {
        try (Statement st = getConnection().createStatement()) {

            st.execute(
                "CREATE TABLE IF NOT EXISTS guests (" +
                "  id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  username      TEXT    NOT NULL UNIQUE COLLATE NOCASE," +
                "  password      TEXT    NOT NULL," +
                "  date_of_birth TEXT    NOT NULL," +
                "  balance       REAL    NOT NULL DEFAULT 0.0 CHECK(balance >= 0)," +
                "  address       TEXT    NOT NULL DEFAULT ''," +
                "  gender        TEXT    NOT NULL CHECK(gender IN ('MALE','FEMALE'))," +
                "  created_at    TEXT    NOT NULL DEFAULT (datetime('now'))" +
                ")"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS staff (" +
                "  id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  username      TEXT    NOT NULL UNIQUE COLLATE NOCASE," +
                "  password      TEXT    NOT NULL," +
                "  date_of_birth TEXT    NOT NULL," +
                "  role          TEXT    NOT NULL CHECK(role IN ('ADMIN','RECEPTIONIST'))," +
                "  working_hours INTEGER NOT NULL CHECK(working_hours > 0)," +
                "  created_at    TEXT    NOT NULL DEFAULT (datetime('now'))" +
                ")"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS room_types (" +
                "  id        INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  type_name TEXT    NOT NULL UNIQUE COLLATE NOCASE" +
                ")"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS amenities (" +
                "  id   INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name TEXT    NOT NULL UNIQUE COLLATE NOCASE" +
                ")"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS rooms (" +
                "  id              INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  room_number     TEXT    NOT NULL UNIQUE COLLATE NOCASE," +
                "  room_type_id    INTEGER NOT NULL," +
                "  price           REAL    NOT NULL CHECK(price >= 0)," +
                "  is_available    INTEGER NOT NULL DEFAULT 1 CHECK(is_available IN (0,1))," +
                "  view_preference TEXT    NOT NULL DEFAULT ''," +
                "  created_at      TEXT    NOT NULL DEFAULT (datetime('now'))," +
                "  FOREIGN KEY(room_type_id) REFERENCES room_types(id) ON DELETE RESTRICT" +
                ")"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS room_amenities (" +
                "  room_id    INTEGER NOT NULL," +
                "  amenity_id INTEGER NOT NULL," +
                "  PRIMARY KEY(room_id, amenity_id)," +
                "  FOREIGN KEY(room_id)    REFERENCES rooms(id)     ON DELETE CASCADE," +
                "  FOREIGN KEY(amenity_id) REFERENCES amenities(id) ON DELETE CASCADE" +
                ")"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS reservations (" +
                "  id               INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  reservation_code TEXT    NOT NULL UNIQUE," +
                "  guest_id         INTEGER NOT NULL," +
                "  room_id          INTEGER NOT NULL," +
                "  check_in_date    TEXT    NOT NULL," +
                "  check_out_date   TEXT    NOT NULL," +
                "  status           TEXT    NOT NULL DEFAULT 'PENDING' " +
                "                   CHECK(status IN ('PENDING','CONFIRMED','CANCELLED'))," +
                "  created_at       TEXT    NOT NULL DEFAULT (datetime('now'))," +
                "  CHECK(check_out_date > check_in_date)," +
                "  FOREIGN KEY(guest_id) REFERENCES guests(id) ON DELETE RESTRICT," +
                "  FOREIGN KEY(room_id)  REFERENCES rooms(id)  ON DELETE RESTRICT" +
                ")"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS reservation_amenities (" +
                "  reservation_id INTEGER NOT NULL," +
                "  amenity_name   TEXT    NOT NULL," +
                "  PRIMARY KEY(reservation_id, amenity_name)," +
                "  FOREIGN KEY(reservation_id) REFERENCES reservations(id) ON DELETE CASCADE" +
                ")"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS invoices (" +
                "  id             INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  reservation_id INTEGER NOT NULL UNIQUE," +
                "  amount         REAL    NOT NULL CHECK(amount >= 0)," +
                "  payment_method TEXT    NOT NULL " +
                "                 CHECK(payment_method IN ('CASH','CREDIT_CARD','ONLINE'))," +
                "  check_in_date  TEXT    NOT NULL," +
                "  paid_at        TEXT    NOT NULL DEFAULT (datetime('now'))," +
                "  FOREIGN KEY(reservation_id) REFERENCES reservations(id) ON DELETE CASCADE" +
                ")"
            );

            st.execute("CREATE INDEX IF NOT EXISTS idx_res_guest  ON reservations(guest_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_res_room   ON reservations(room_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_res_status ON reservations(status)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_rooms_avail ON rooms(is_available)");

        } catch (SQLException e) {
            throw new RuntimeException("Schema initialisation failed: " + e.getMessage(), e);
        }
    }

    /** True when the staff table has no rows (fresh database). */
    public static boolean isEmpty() {
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM staff")) {
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            return true;
        }
    }

    public static void close() {
        try { if (conn != null && !conn.isClosed()) conn.close(); }
        catch (SQLException ignored) {}
    }
}
