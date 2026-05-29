import java.time.LocalDate;


public class Invoice {
    public enum PaymentMethod { CASH, CREDIT_CARD, ONLINE }

    private double amount;
    private PaymentMethod method;
    private LocalDate checkInDate; 

 
    public Invoice(double amount, PaymentMethod method, LocalDate checkIn) {
        this.amount = amount;
        this.method = method;
        this.checkInDate = checkIn;
    }

    public double getAmount()          { return amount; }
    public PaymentMethod getMethod()   { return method; }
    public java.time.LocalDate getCheckInDate() { return checkInDate; }

    public void printReceipt() {
        System.out.println("\n------- PAYMENT RECEIPT -------");
        System.out.println("Check In Date: " + checkInDate);
        System.out.println("Total Paid: $" + amount);
        System.out.println("Method Used: " + method);
        System.out.println("Status: COMPLETED");
        System.out.println("-------------------------------");
    }
}