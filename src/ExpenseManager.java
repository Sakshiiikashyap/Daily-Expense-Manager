package src;

import java.io.*;
import java.util.*;

public class ExpenseManager {
    static Scanner scanner = new Scanner(System.in);
    static Map<String, User> users = new HashMap<>();
    static User currentUser = null;
    static final String USERS_FILE = "data/users.txt";

    public static void main(String[] args) {
        new File("data").mkdirs();
        new File("output").mkdirs();
        loadUsers();

        while (true) {
            System.out.println("\n=== EXPENSE MANAGER ===");
            if (currentUser == null) {
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Select option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> register();
                    case 2 -> login();
                    case 3 -> {
                        saveUsers();
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } else {
                System.out.println("Welcome, " + currentUser.username + "!");
                System.out.println("1. Add Expense");
                System.out.println("2. View Expenses");
                System.out.println("3. Delete Expense");
                System.out.println("4. Logout");
                System.out.println("5. Calculate Total Expense");
                System.out.println("6. Export to CSV");
                System.out.print("Select option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> addExpense();
                    case 2 -> viewExpenses();
                    case 3 -> deleteExpense();
                    case 4 -> logout();
                    case 5 -> calculateTotalExpense();
                    case 6 -> exportToCSV();
                    default -> System.out.println("Invalid choice.");
                }
            }
        }
    }

    static void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    users.put(parts[0], new User(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users.");
        }
    }

    static void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users.values()) {
                writer.write(user.username + "," + user.password);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users.");
        }
    }

    static void register() {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        if (users.containsKey(username)) {
            System.out.println("Username already exists.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        User newUser = new User(username, password);
        users.put(username, newUser);
        saveUsers();
        System.out.println("Registration successful!");
    }

    static void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = users.get(username);
        if (user != null && user.password.equals(password)) {
            currentUser = user;
            currentUser.loadExpensesFromFile();
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    static void logout() {
        currentUser.saveExpensesToFile();
        currentUser = null;
        System.out.println("Logged out.");
    }

    static void addExpense() {
        System.out.print("Description: ");
        String desc = scanner.nextLine();
        System.out.print("Amount (in ₹): ");
        double amt = scanner.nextDouble();
        scanner.nextLine();

        currentUser.expenses.add(new Expense(desc, amt));
        currentUser.saveExpensesToFile();
        System.out.println("Expense added and saved.");
    }

    static void viewExpenses() {
        if (currentUser.expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {
            System.out.println("Your Expenses:");
            for (int i = 0; i < currentUser.expenses.size(); i++) {
                System.out.println((i + 1) + ". " + currentUser.expenses.get(i));
            }
        }
    }

    static void deleteExpense() {
        viewExpenses();
        if (currentUser.expenses.isEmpty()) return;

        System.out.print("Enter expense number to delete: ");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index < 1 || index > currentUser.expenses.size()) {
            System.out.println("Invalid selection.");
        } else {
            currentUser.expenses.remove(index - 1);
            currentUser.saveExpensesToFile();
            System.out.println("Expense deleted and saved.");
        }
    }

    static void calculateTotalExpense() {
        double total = currentUser.expenses.stream().mapToDouble(e -> e.amount).sum();
        System.out.println("Total Expenses: ₹" + total);
    }

    static void exportToCSV() {
        String filename = "output/expenses_" + currentUser.username + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Description,Amount\n");
            for (Expense e : currentUser.expenses) {
                writer.write(e.description + "," + e.amount + "\n");
            }
            System.out.println("Expenses exported to " + filename);
        } catch (IOException e) {
            System.out.println("Error exporting to CSV.");
        }
    }
}
