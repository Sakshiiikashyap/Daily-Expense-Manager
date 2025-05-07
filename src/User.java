package src;

import java.io.*;
import java.util.*;

public class User {
    public String username;
    public String password;
    public List<Expense> expenses = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void loadExpensesFromFile() {
        expenses.clear();
        File file = new File("data/expenses_" + username + ".txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    expenses.add(new Expense(parts[0], Double.parseDouble(parts[1])));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading expenses file.");
        }
    }

    public void saveExpensesToFile() {
        File file = new File("data/expenses_" + username + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Expense e : expenses) {
                writer.write(e.description + "," + e.amount);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing expenses file.");
        }
    }
}
