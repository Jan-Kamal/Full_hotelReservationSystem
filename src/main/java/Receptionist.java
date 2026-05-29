import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;


public class Receptionist extends Staff {

    
    public Receptionist(String username, String password,
                        LocalDate dateOfBirth, int workingHours) {
       
        super(username, password, dateOfBirth, Role.RECEPTIONIST, workingHours);
    }

    @Override
    public void performDuties() {
        System.out.println("Receptionist " + getUsername() + " is managing check-ins and check-outs.");
    }
    public static String askForDate(String label, Scanner scan) {
    while (true) {
        System.out.print("Please enter " + label + " Date (YYYY-MM-DD): ");
        String input = scan.nextLine();
        try {
           
            LocalDate.parse(input); 
            return input;
        } catch (DateTimeParseException e) {
            System.out.println("\n[ERROR]: Invalid format! Please use YYYY-MM-DD (e.g., 2026-06-06).");
        }
    }
}
    public void checkIn(Reservation reservation) {
      
        if (reservation.getStatus() == Reservation.ReservationStatus.PENDING) {
            reservation.confirm(); 
            System.out.println("Receptionist " + getUsername() +
                    " checked in guest for reservation ID: " + reservation.getReservationId());
        } else {
            System.out.println("Cannot check in. Reservation status is currently: " + reservation.getStatus());
        }
    }

public void checkOut(Reservation reservation) {
    System.out.println("Receptionist " + getUsername() +
            " is checking out guest: " + reservation.getGuest().getUsername());
    reservation.getRoom().setAvailable(true);
    System.out.println("Check-out complete for ID: " + reservation.getReservationId());
    
    }
    public void viewAReservations(){
        System.out.println("\n--- ALL HOTEL RESERVATIONS ---");
    
    if (HotelDatabase.reservations.isEmpty()) {
        System.out.println("No reservations found in the system.");
    } else {
        
        for (Reservation res : HotelDatabase.reservations) {
            System.out.println(res); 
            System.out.println("-----------------------------------");
        }
    }
    }
   
}
