public interface Payable {
    boolean canAfford(double amount);
    void deductBalance(double amount);
}
