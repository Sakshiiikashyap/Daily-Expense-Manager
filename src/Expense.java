package src;

public class Expense {
    public String description;
    public double amount;

    public Expense(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }

    public String toString() {
        return description + " - ₹" + amount;
    }
}
