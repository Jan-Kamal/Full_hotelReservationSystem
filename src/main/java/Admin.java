import java.time.LocalDate;
import java.util.Scanner;


public class Admin extends Staff {

    public Admin(String username, String password,
                 LocalDate dateOfBirth, int workingHours) {
       
        super(username, password, dateOfBirth, Role.ADMIN, workingHours);
    }

   
    @Override
    public void performDuties() {
        System.out.println("Admin " + getUsername() + " is managing rooms, amenities, and room types.");
    }

  
    public RoomType createRoomType(String typeName) {
        RoomType rt = new RoomType(typeName);
        System.out.println("Admin created room type: " + typeName);
        return rt;
    }
public void viewAllData() {
    System.out.println("\n========= SYSTEM DATA REPORT =========");

    // 1. VIEW ALL GUESTS
    System.out.println("\n--- REGISTERED GUESTS ---");
    if (HotelDatabase.guests.isEmpty()) {
        System.out.println("No guests registered yet.");
    } else {
        for (Guest g : HotelDatabase.guests) {
           System.out.println(g);
    }
}
    

    
    System.out.println("\n--- HOTEL ROOMS ---");
    if (HotelDatabase.rooms.isEmpty()) {
        System.out.println("No rooms created yet.");
    } else {
        for (Room r : HotelDatabase.rooms) {
          System.out.println(r);
        }
    }

   
    System.out.println("\n--- ACTIVE RESERVATIONS ---");
    if (HotelDatabase.reservations.isEmpty()) {
        System.out.println("No reservations made yet.");
    } else {
        for (Reservation res : HotelDatabase.reservations) {
            System.out.println(res); 
        }
    }       
}


public void updateRoomPrice(String roomNum, double newPrice) {
    try {
        for (Room r : HotelDatabase.rooms) {
            if (r.getRoomNumber().equalsIgnoreCase(roomNum)) {
                
                r.setPrice(newPrice);
                
            
                System.out.println("Success: Room " + roomNum + " price updated to $" + newPrice);
                return;
            }
        }
        System.out.println("Error: Room " + roomNum + " not found.");

    } catch (IllegalArgumentException e) {
        
        System.out.println("\n>>> UPDATE FAILED: " + e.getMessage());
        System.out.println("The price for Room " + roomNum + " remains unchanged.");
    }
}

public void deleteRoom(String roomNum) {
    if (HotelDatabase.deleteRoom(roomNum)) {
        System.out.println("Success: Room " + roomNum + " deleted from system.");
    } else {
        System.out.println("Error: Room not found or has active reservations.");
    }
}

public void handleRoomTypeUpdate(Scanner scanner) {
    if (HotelDatabase.rooms.isEmpty()) {
        System.out.println("Error: There are no rooms in the system to change.");
        return; 
    }

    System.out.print("Enter Room Number to change its type: ");
    String rNum = scanner.nextLine();
    
    Room foundRoom = null;
    for (Room r : HotelDatabase.rooms) {
        if (r.getRoomNumber().equalsIgnoreCase(rNum)) {
            foundRoom = r;
            break;
        }
    }

    if (foundRoom == null) {
        System.out.println("Error: Room not found.");
        return;
    }

   
    System.out.println("Available New Types (Current: " + foundRoom.getRoomType().getTypeName() + "):");
    boolean otherTypesExist = false;
    for (RoomType rt : HotelDatabase.roomTypes) {
      
        if (!rt.getTypeName().equalsIgnoreCase(foundRoom.getRoomType().getTypeName())) {
            System.out.println("- " + rt.getTypeName());
            otherTypesExist = true;
        }
    }

    if (!otherTypesExist) {
        System.out.println("No other room types available to switch to.");
        return;
    }

    System.out.print("Enter the NEW type name: ");
    String newTypeName = scanner.nextLine();
    
    RoomType selectedType = null;
    for (RoomType rt : HotelDatabase.roomTypes) {
        if (rt.getTypeName().equalsIgnoreCase(newTypeName)) {
            selectedType = rt;
            break;
        }
    }
    
    if (selectedType != null) {
        foundRoom.setRoomType(selectedType);
        System.out.println("Success: Room " + rNum + " is now a " + selectedType.getTypeName());
    } else {
        System.out.println("Error: That Room Type does not exist!");
    }
}
public void linkAmenityToRoom(Scanner sc) {
    
    if (HotelDatabase.rooms.isEmpty()) {
        System.out.println("Error: No rooms created yet.");
        return;
    }
    if (HotelDatabase.amenitys.isEmpty()) {
        System.out.println("Error: No amenities created in the database yet.");
        return;
    }

    System.out.print("Enter Room Number to add amenity to: ");
    String rNum = sc.nextLine();
    Room selectedRoom = null;
    for (Room r : HotelDatabase.rooms) {
        if (r.getRoomNumber().equalsIgnoreCase(rNum)) {
            selectedRoom = r;
            break;
        }
    }

    if (selectedRoom == null) {
        System.out.println("Room not found.");
        return;
    }

    System.out.println("\n--- Available Amenities ---");
    for (int i = 0; i < HotelDatabase.amenitys.size(); i++) {
        System.out.println((i + 1) + ". " + HotelDatabase.amenitys.get(i).getName());
    }

   System.out.print("Select Amenity number: ");
if (!sc.hasNextInt()) { sc.next(); return; }
int choice = sc.nextInt(); sc.nextLine(); 

if (choice > 0 && choice <= HotelDatabase.amenitys.size()) {
    Amenity chosen = HotelDatabase.amenitys.get(choice - 1);
    
    
    if (selectedRoom.getRoomAmenities().contains(chosen)) {
        System.out.println("Error: Room " + rNum + " already has " + chosen.getName() + "!");
    } else {
        
        selectedRoom.addAmenity(chosen);
        System.out.println("Success: " + chosen.getName() + " added to Room " + rNum);
    }
}       else  {
        System.out.println("Invalid selection.");
    }
}

public void createAmenity(Scanner sc) {
    System.out.print("Enter name of the new amenity: ");
    String name = sc.nextLine();
    
    Amenity newAmenity = new Amenity(name);
    HotelDatabase.amenitys.add(newAmenity);
    System.out.println("Success: '" + name + "' added to database.");
}


public void updateAmenity(Scanner sc) {
    if (HotelDatabase.amenitys.isEmpty()) {
        System.out.println("No amenities found to update.");
        return;
    }

    
    for (int i = 0; i < HotelDatabase.amenitys.size(); i++) {
        System.out.println((i + 1) + ". " + HotelDatabase.amenitys.get(i).getName());
    }

    System.out.print("Select amenity number to rename: ");
    int choice = sc.nextInt(); sc.nextLine();

    if (choice > 0 && choice <= HotelDatabase.amenitys.size()) {
        System.out.print("Enter the NEW name: ");
        String newName = sc.nextLine();
        
        HotelDatabase.amenitys.get(choice - 1).setName(newName);
        System.out.println("Success: Amenity updated.");
    } else {
        System.out.println("Invalid choice.");
    }
}

public void deleteAmenity(Scanner sc) {
    if (HotelDatabase.amenitys.isEmpty()) {
        System.out.println("No amenities found to delete.");
        return;
    }

    for (int i = 0; i < HotelDatabase.amenitys.size(); i++) {
        System.out.println((i + 1) + ". " + HotelDatabase.amenitys.get(i).getName());
    }

    System.out.print("Select amenity number to DELETE: ");
    int choice = sc.nextInt(); sc.nextLine();

    if (choice > 0 && choice <= HotelDatabase.amenitys.size()) {
        Amenity removed = HotelDatabase.amenitys.remove(choice - 1);
        System.out.println("Success: '" + removed.getName() + "' removed from system.");
    } else {
        System.out.println("Invalid choice.");
    }
}
public void createRoom(Scanner scanner) {
        if (HotelDatabase.roomTypes.isEmpty()) {
            System.out.println("Error: Create a Room Type first!");
            return;
        }

        System.out.print("Room Number: ");
        String num = scanner.nextLine();

   
        for (Room r : HotelDatabase.rooms) {
            if (r.getRoomNumber().equalsIgnoreCase(num)) {
                System.out.println("Error: Room " + num + " already exists!");
                return;
            }
        }

        try {
            System.out.print("Set Room Price: ");
            double price = scanner.nextDouble(); scanner.nextLine();

            System.out.print("Set Room View (e.g., Sea View, Pool View): ");
            String view = scanner.nextLine();

            System.out.println("\nSelect Room Type:");
            for (int i = 0; i < HotelDatabase.roomTypes.size(); i++) {
                System.out.println(i + ". " + HotelDatabase.roomTypes.get(i).getTypeName());
            }
            System.out.print("Choice: ");
            int typeChoice = scanner.nextInt(); scanner.nextLine();

            if (typeChoice >= 0 && typeChoice < HotelDatabase.roomTypes.size()) {
                RoomType selectedType = HotelDatabase.roomTypes.get(typeChoice);
                
               
                Room newRoom = new Room(num, selectedType, price, view); 
                
                HotelDatabase.rooms.add(newRoom);
                System.out.println("Room " + num + " created successfully!");
            } else {
                System.out.println("Invalid Room Type selection.");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("\n>>> VALIDATION ERROR: " + e.getMessage());
        } catch (java.util.InputMismatchException e) {
            System.out.println("\n>>> ERROR: Invalid numeric input.");
            scanner.nextLine(); 
        }
    }
}
