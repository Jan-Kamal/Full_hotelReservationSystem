import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reservation {
  
    public enum ReservationStatus {
        PENDING, CONFIRMED, CANCELLED
    }

    private String reservationId;
    private Guest guest;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private ReservationStatus status;
    private List<Amenity> extraAmenities;

    public Reservation(Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.reservationId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = ReservationStatus.PENDING;
        this.extraAmenities = new ArrayList<>();
        room.setAvailable(false);
        System.out.println("Reservation created! ID: " + reservationId + " [Status: PENDING]");
    }

    /** Used by the DB layer to restore a persisted reservation code. */
    public void setReservationId(String id) { this.reservationId = id; }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
        System.out.println("Reservation " + reservationId + " CONFIRMED by Receptionist.");
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        room.setAvailable(true); 
        guest.topUpBalance(room.getPrice());
        System.out.println("Reservation " + reservationId + " CANCELLED. Room is available.");
        System.out.println("REFUND ISSUED: $" + room.getPrice() + " returned to your wallet.");
    }

    public void addExtraAmenity(Amenity amenity) {
        extraAmenities.add(amenity);
    }

    public String getReservationId()  { return reservationId; }
    public Guest getGuest()           { return guest; }
    public Room getRoom()             { return room; }
    public ReservationStatus getStatus() { return status; }
    public LocalDate getCheckInDate()  { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }

    @Override
    public String toString() {
        return "Reservation{id='" + reservationId + "', room=" + room.getRoomNumber() + 
               ", status=" + status + ", extras=" + extraAmenities + "}";
    }

}