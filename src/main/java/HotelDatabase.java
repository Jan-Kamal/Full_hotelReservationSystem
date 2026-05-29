import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Central data layer: holds in-memory lists (used by the entire UI) and
 * delegates every mutation to the corresponding DAO so changes are persisted
 * to the SQLite database automatically.
 */
public class HotelDatabase {

    // ── In-memory caches (read by all UI screens directly) ──────────────────
    public static ArrayList<Guest>       guests       = new ArrayList<>();
    public static ArrayList<Admin>       admins       = new ArrayList<>();
    public static ArrayList<Receptionist> receptionists = new ArrayList<>();
    public static ArrayList<Room>        rooms        = new ArrayList<>();
    public static ArrayList<Reservation> reservations = new ArrayList<>();
    public static ArrayList<RoomType>    roomTypes    = new ArrayList<>();
    public static ArrayList<Amenity>     amenitys     = new ArrayList<>();

    // ── Boot sequence ────────────────────────────────────────────────────────

    public static void initializeData() {
        DatabaseManager.initSchema();

        if (DatabaseManager.isEmpty()) {
            seedInitialData();
        }

        loadAll();
    }

    private static void seedInitialData() {
        Admin  seedAdmin = new Admin("mark_admin", "password123",
                                     LocalDate.of(2000, 1, 1), 8);
        Receptionist seedRec = new Receptionist("staff_john", "staff123",
                                                 LocalDate.of(1995, 5, 10), 8);
        StaffDAO.createAdmin(seedAdmin);
        StaffDAO.createReceptionist(seedRec);

        try {
            Guest seedGuest = new Guest("m_guest", "password1234",
                    LocalDate.of(2005, 3, 15), 5000.0, "Cairo", Guest.Gender.MALE);
            GuestDAO.create(seedGuest);
        } catch (NegativeBalanceException ignored) {}
    }

    private static void loadAll() {
        guests.clear();
        admins.clear();
        receptionists.clear();
        rooms.clear();
        reservations.clear();
        roomTypes.clear();
        amenitys.clear();

        roomTypes.addAll(RoomTypeDAO.findAll());
        amenitys.addAll(AmenityDAO.findAll());

        // Build lookup maps for RoomDAO and ReservationDAO
        Map<String, RoomType> typeMap    = new HashMap<>();
        for (RoomType rt : roomTypes) typeMap.put(rt.getTypeName().toLowerCase(), rt);

        Map<String, Amenity> amenityMap  = new HashMap<>();
        for (Amenity a : amenitys) amenityMap.put(a.getName().toLowerCase(), a);

        rooms.addAll(RoomDAO.findAll(typeMap, amenityMap));

        guests.addAll(GuestDAO.findAll());
        admins.addAll(StaffDAO.findAllAdmins());
        receptionists.addAll(StaffDAO.findAllReceptionists());

        Map<String, Guest> guestMap = new HashMap<>();
        for (Guest g : guests) guestMap.put(g.getUsername().toLowerCase(), g);

        Map<String, Room> roomMap = new HashMap<>();
        for (Room r : rooms) roomMap.put(r.getRoomNumber().toLowerCase(), r);

        reservations.addAll(ReservationDAO.findActive(guestMap, roomMap));
    }

    // ── Guest mutations ──────────────────────────────────────────────────────

    public static void addGuest(Guest guest) {
        guests.add(guest);
        GuestDAO.create(guest);
    }

    /** Call after any change to guest.balance or other mutable fields. */
    public static void persistGuest(Guest guest) {
        GuestDAO.update(guest);
    }

    // ── Room mutations ───────────────────────────────────────────────────────

    public static void addRoom(Room room) {
        rooms.add(room);
        RoomDAO.create(room);
    }

    /** Call after price, type, availability, or view changes. */
    public static void persistRoom(Room room) {
        RoomDAO.update(room);
    }

    /**
     * Deletes a room from both DB and in-memory list.
     * Returns false if the room has active reservations (FK constraint).
     */
    public static boolean deleteRoom(String roomNumber) {
        if (!RoomDAO.delete(roomNumber)) return false;
        rooms.removeIf(r -> r.getRoomNumber().equalsIgnoreCase(roomNumber));
        return true;
    }

    public static void addRoomAmenity(Room room, Amenity amenity) {
        room.addAmenity(amenity);
        RoomDAO.addAmenity(room.getRoomNumber(), amenity.getName());
    }

    // ── RoomType mutations ───────────────────────────────────────────────────

    public static void addRoomType(RoomType rt) {
        roomTypes.add(rt);
        RoomTypeDAO.create(rt);
    }

    public static void renameRoomType(String oldName, String newName) {
        RoomTypeDAO.rename(oldName, newName);
    }

    // ── Amenity mutations ────────────────────────────────────────────────────

    public static void addAmenity(Amenity a) {
        amenitys.add(a);
        AmenityDAO.create(a);
    }

    public static void renameAmenity(String oldName, String newName) {
        AmenityDAO.rename(oldName, newName);
    }

    public static void deleteAmenity(Amenity a) {
        amenitys.remove(a);
        AmenityDAO.delete(a.getName());
    }

    // ── Reservation mutations ────────────────────────────────────────────────

    public static void addReservation(Reservation res, Invoice inv) {
        reservations.add(res);
        ReservationDAO.create(res, inv);
        RoomDAO.update(res.getRoom());   // persist is_available = 0
    }

    /** Call after check-in (status → CONFIRMED). */
    public static void persistReservation(Reservation res) {
        ReservationDAO.updateStatus(res.getReservationId(), res.getStatus().name());
    }

    /**
     * Cancels a reservation: updates guest balance + room availability in DB,
     * then deletes the reservation record and removes it from the in-memory list.
     * (Assumes res.cancel() was already called, so the in-memory objects are updated.)
     */
    public static void cancelReservation(Reservation res) {
        GuestDAO.update(res.getGuest());
        RoomDAO.update(res.getRoom());
        ReservationDAO.delete(res.getReservationId());
        reservations.remove(res);
    }

    /**
     * Completes a check-out: persists room availability in DB,
     * deletes the reservation record, and removes from the in-memory list.
     * (Assumes receptionist.checkOut(res) was already called.)
     */
    public static void checkOutReservation(Reservation res) {
        RoomDAO.update(res.getRoom());
        ReservationDAO.delete(res.getReservationId());
        reservations.remove(res);
    }

    /** Call after adding an extra amenity to a reservation. */
    public static void persistExtraAmenity(Reservation res, Amenity amenity) {
        ReservationDAO.addExtraAmenity(res.getReservationId(), amenity.getName());
    }
}
