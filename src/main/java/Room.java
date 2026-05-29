import java.util.ArrayList;

public class Room {
    private String roomNumber;
    private RoomType roomType;
    private double price;
    private boolean isAvailable;
    private String viewPreference;

    public Room(String roomNumber, RoomType roomType, double price, String viewPreference) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        setPrice(price);
        this.viewPreference = viewPreference;
        this.isAvailable = true;
    }
    public void setPrice(double price) {     
    if (price >= 0) { 
        this.price = price;
    } else {
        
        throw new IllegalArgumentException("Invalid Input: Price cannot be negative (" + price + ")");
    }
}
    public double getPrice() { return price; }
   public String getRoomNumber() { return roomNumber; }
    public RoomType getRoomType() { return roomType; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean status) { this.isAvailable = status; }
    public String getViewPreference() { return viewPreference; }

    @Override
public String toString() {
    return "Room " + roomNumber + " [" + roomType.getTypeName() + " | View: " + viewPreference + "] - Price: $" + price + 
           "\n   Amenities: " + (roomAmenities.isEmpty() ? "None" : roomAmenities); 
}
        public void setRoomType(RoomType roomType) {
    this.roomType = roomType;
}
private ArrayList<Amenity> roomAmenities = new ArrayList<>();
public void addAmenity(Amenity a) {
        this.roomAmenities.add(a);
    }

    public ArrayList<Amenity> getRoomAmenities() {
        return roomAmenities;
    }
}