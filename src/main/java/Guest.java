import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Guest implements Payable {

    public enum Gender {
        MALE, FEMALE
    }

    private String username;
    private String password;
    private LocalDate dateOfBirth;
    private double balance;
    private String address;
    private Gender gender;
    private List<String> roomPreferences;

    public Guest(String username, String password, LocalDate dateOfBirth,
                 double balance, String address, Gender gender) throws NegativeBalanceException {
        this.username = username;
        this.password = validatePassword(password);
        this.dateOfBirth = dateOfBirth;
        setBalance(balance);
        this.address = address;
        this.gender = gender;
        this.roomPreferences = new ArrayList<>();
    }

    private String validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        return password;
    }


    public boolean login(String username, String password) {
        if (this.username.equals(username) && PasswordUtils.verify(password, this.password)) {
            System.out.println("Login successful. Welcome, " + this.username + "!");
            return true;
        }
        System.out.println("Invalid username or password.");
        return false;
    }

    /** Returns the stored password value — used only by the persistence layer. */
    public String getPasswordForStorage() { return password; }

    @Override
   public boolean canAfford(double amount) {
        return this.balance >= amount;
    }
@Override
    public void deductBalance(double amount) {
        this.balance -= amount;
    }


    public void addRoomPreference(String preference) {
        roomPreferences.add(preference);
    }
    public void topUpBalance(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println("Successfully added $" + amount + ". New balance: $" + this.balance);
        }
    }



    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) throws NegativeBalanceException {
    if (balance < 0) {
        throw new NegativeBalanceException("Balance cannot be negative.");
    }
    this.balance = balance;
}

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public List<String> getRoomPreferences() { return roomPreferences; }

    public void setPassword(String password) { this.password = validatePassword(password); }

    @Override
    public String toString() {
        return "Guest{" +
                "username='" + username + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", balance=" + balance +
                ", address='" + address + '\'' +
                ", gender=" + gender +
                ", roomPreferences=" + roomPreferences +
                '}';
    }
   public void requestExtraAmenities(Scanner scanner) {
    System.out.println("\n--- Request Extra Amenities ---");
    
    
    ArrayList<Reservation> guestActiveRes = new ArrayList<>();
    for (Reservation res : HotelDatabase.reservations) {
        
        if (res.getGuest().equals(this) && 
            res.getStatus() != Reservation.ReservationStatus.CANCELLED) {
            guestActiveRes.add(res);
        }
    }

    if (guestActiveRes.isEmpty()) {
        System.out.println("You have no active reservations to upgrade.");
        return; 
    }

    
    Reservation activeRes = null;
    if (guestActiveRes.size() == 1) {
        activeRes = guestActiveRes.get(0);
        System.out.println("Upgrading Room: " + activeRes.getRoom().getRoomNumber());
    } else {
        System.out.println("You have multiple reservations. Which one do you want to upgrade?");
        for (int i = 0; i < guestActiveRes.size(); i++) {
            Reservation r = guestActiveRes.get(i);
            System.out.println(i + ". Room " + r.getRoom().getRoomNumber() + " (" + r.getRoom().getRoomType().getTypeName() + ")");
        }
        System.out.print("Choice: ");
        int choice = scanner.nextInt(); scanner.nextLine();
        
        if (choice >= 0 && choice < guestActiveRes.size()) {
            activeRes = guestActiveRes.get(choice);
        } else {
            System.out.println("Invalid selection.");
            return;
        }
    }

    
    boolean isSuite = activeRes.getRoom().getRoomType().getTypeName().toLowerCase().contains("suite");
    
    System.out.println("1. Add Extra Bed");
    if (!isSuite) System.out.println("2. Add Unlimited WiFi (+$50)");
    System.out.print("Choice: ");
    int amChoice = scanner.nextInt(); scanner.nextLine();

    if (amChoice == 1) {
        double bedPrice = isSuite ? 50.0 : 100.0; 
        if (this.canAfford(bedPrice)) { 
            this.deductBalance(bedPrice);
            activeRes.addExtraAmenity(new Amenity("Extra Bed"));
            System.out.println("Extra Bed added! Charged $" + bedPrice);
        } else { System.out.println("Insufficient funds!"); }
    } else if (amChoice == 2 && !isSuite) {
        if (this.canAfford(50.0)) {
            this.deductBalance(50.0);
            activeRes.addExtraAmenity(new Amenity("Unlimited WiFi"));
            System.out.println("Unlimited WiFi added! Charged $50.0");
        } else { System.out.println("Insufficient funds!"); }
    }
}
 public void ReserveRoom(Scanner scanner){
System.out.print("Preferred Room Type (e.g., Suite, Single): ");
    String typePref = scanner.nextLine();
    System.out.print("Preferred View (e.g., Sea View, Pool View): ");
    String viewPref = scanner.nextLine();

  

    if (!this.getRoomPreferences().contains(viewPref)) {
        this.getRoomPreferences().add(viewPref);
    }

    boolean autoReserved = false;
    
    
    for (Room r : HotelDatabase.rooms) {
        if (r.isAvailable() && 
            r.getRoomType().getTypeName().equalsIgnoreCase(typePref) && 
            r.getViewPreference().equalsIgnoreCase(viewPref)) {
            
            System.out.println("\n>>> PERFECT MATCH FOUND! <<<");
            
            if (this.canAfford(r.getPrice())) {
    
                System.out.println("Select Payment Method: 1.CASH 2.CREDIT_CARD 3.ONLINE");
                int pChoice = scanner.nextInt(); scanner.nextLine();
                Invoice.PaymentMethod m = (pChoice == 1) ? Invoice.PaymentMethod.CASH : 
                                         (pChoice == 2) ? Invoice.PaymentMethod.CREDIT_CARD : 
                                         Invoice.PaymentMethod.ONLINE;

                
                LocalDate checkIn = null;
                LocalDate checkOut = null;
                while (true) {
                    checkIn = LocalDate.parse(Receptionist.askForDate("Check In", scanner));
                    checkOut = LocalDate.parse(Receptionist.askForDate("Check Out", scanner));

                    if (checkOut.isAfter(checkIn)) break; 
                    System.out.println("\n[ERROR]: Check-out must be after Check-in! Try again.");
                }

                
                this.deductBalance(r.getPrice());
               Invoice inv = new Invoice(r.getPrice(), m, checkIn);
                Reservation res = new Reservation(this, r, checkIn, checkOut);
                
                HotelDatabase.reservations.add(res);
                r.setAvailable(false); 
                
                System.out.println("\n--- AUTO-RESERVATION CONFIRMED ---");
                inv.printReceipt();
                autoReserved = true;
            } else {
                System.out.println("Match found, but you have insufficient funds.");
            }
            break; 
        }
    }

    
    if (!autoReserved) {
        System.out.println("\nNo exact match could be auto-reserved. Available options:");
        for (Room r : HotelDatabase.rooms) {
            if (r.isAvailable()) System.out.println(r);
        }
        
        System.out.print("Enter Room Number to manually reserve (or 'exit'): ");
        String rNum = scanner.nextLine();
        if (rNum.equalsIgnoreCase("exit")) return;

        for (Room r : HotelDatabase.rooms) {
            if (r.getRoomNumber().equalsIgnoreCase(rNum) && r.isAvailable()) {
                if (this.canAfford(r.getPrice())) {
                    
                    System.out.println("Select Payment Method: 1.CASH 2.CREDIT_CARD 3.ONLINE");
                    int pChoice = scanner.nextInt(); scanner.nextLine();
                    Invoice.PaymentMethod m = (pChoice == 1) ? Invoice.PaymentMethod.CASH : 
                                             (pChoice == 2) ? Invoice.PaymentMethod.CREDIT_CARD : 
                                             Invoice.PaymentMethod.ONLINE;

                    
                    LocalDate checkIn = null;
                    LocalDate checkOut = null;
                    while (true) {
                        checkIn = LocalDate.parse(Receptionist.askForDate("Check In", scanner));
                        checkOut = LocalDate.parse(Receptionist.askForDate("Check Out", scanner));

                        if (checkOut.isAfter(checkIn)) break;
                        System.out.println("\n[ERROR]: Check-out must be after Check-in! Try again.");
                    }

                    this.deductBalance(r.getPrice());
                    Invoice inv = new Invoice(r.getPrice(), m, checkIn);
                    Reservation res = new Reservation(this, r, checkIn, checkOut);
                    
                    HotelDatabase.reservations.add(res);
                    r.setAvailable(false); 
                    
                    System.out.println("\n--- MANUAL RESERVATION CONFIRMED ---");
                    inv.printReceipt();
                } else {
                    System.out.println("Insufficient funds!");
                }
                break;
            }
        }
    }
    }
   public void View_CancelReservation(Scanner scanner) {
    if (HotelDatabase.reservations.isEmpty()) {
        System.out.println("No reservations found in the system.");
    } else {
        ArrayList<Reservation> myRes = new ArrayList<>();
        for (Reservation res : HotelDatabase.reservations) {
            if (res.getGuest().equals(this)) {
                myRes.add(res);
            }
        }

        if (myRes.isEmpty()) {
            System.out.println("You have no active reservations.");
        } else {
            System.out.println("\n--- Your Reservations ---");
            for (int i = 0; i < myRes.size(); i++) {
                System.out.println((i + 1) + ". " + myRes.get(i).toString());
            }

            System.out.print("\nEnter number to CANCEL (or 0 to go back): ");
            
         
            int choice = -1; 
            try {
                 choice = scanner.nextInt();
                scanner.nextLine();
            } catch (java.util.InputMismatchException e) {
                System.out.println("\n[ERROR]: Please enter a valid number (e.g. 1, 2), not text.");
                return;
            }
        

            if (choice > 0 && choice <= myRes.size()) {
                Reservation toCancel = myRes.get(choice - 1);
                
                
                toCancel.cancel();
                
                
                HotelDatabase.reservations.remove(toCancel);
                System.out.println("Reservation removed from system.");
            } else if (choice != 0) {
                System.out.println("Invalid selection.");
            }
        }
    }
}
public static void registerNewGuest(Scanner scanner) {

    System.out.println("Please enter your name:");
    String name = scanner.nextLine();

    System.out.println("Enter your password:");
    String p = scanner.nextLine();

    System.out.println("Your Initial Balance:");
    double bal = 0.0;
    boolean validBalance = false;
    while (!validBalance) {
        try {
            bal = scanner.nextDouble();
            scanner.nextLine();
            if (bal < 0) throw new NegativeBalanceException("Initial balance cannot be negative.");
            validBalance = true;
        } catch (java.util.InputMismatchException e) {
            System.out.println("[INPUT ERROR]: Please enter a valid number.");
            scanner.nextLine();
        } catch (NegativeBalanceException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }

    System.out.println("Address:");
    String addr = scanner.nextLine();

    Guest.Gender gender = null;
    while (gender == null) {
        System.out.println("Gender (1. MALE / 2. FEMALE):");
        String gChoice = scanner.nextLine().trim();
        if (gChoice.equals("1") || gChoice.equalsIgnoreCase("MALE")) {
            gender = Guest.Gender.MALE;
        } else if (gChoice.equals("2") || gChoice.equalsIgnoreCase("FEMALE")) {
            gender = Guest.Gender.FEMALE;
        } else {
            System.out.println("[ERROR]: Please enter 1 for MALE or 2 for FEMALE.");
        }
    }

    try {
        Guest newGuest = new Guest(name, p, LocalDate.now(), bal, addr, gender);
        HotelDatabase.guests.add(newGuest);
        System.out.println("Registration successful! Welcome, " + name + "!");
    } catch (IllegalArgumentException e) {
        System.out.println("[SECURITY ERROR]: " + e.getMessage());
    } catch (NegativeBalanceException e) {
        System.out.println("[BALANCE ERROR]: " + e.getMessage());
    }
}
}
