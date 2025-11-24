import java.sql.*;

public class DatabaseInitializer {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "LibraryDB";
        String username = "root";
        String password = "123tetes";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Σύνδεση στον server (χωρίς βάση)
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Συνδέθηκα στον MySQL Server.");

            // Δημιουργία βάσης αν δεν υπάρχει
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            System.out.println("✅ Δημιουργήθηκε η βάση '" + dbName + "'.");

            stmt.close();
            connection.close();

            // Σύνδεση στη βάση
            connection = DriverManager.getConnection(url + dbName, username, password);
            stmt = connection.createStatement();

            // Πίνακας Users
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Users (
                    username VARCHAR(100) PRIMARY KEY,
                    password VARCHAR(100) NOT NULL,
                    fullname VARCHAR(100),
                    birthdate DATE,
                    address TEXT,
                    city VARCHAR(100)
                )
            """);
            System.out.println("✅ Ο πίνακας Users δημιουργήθηκε.");

            // Πίνακας Books
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Books (
                    isbn VARCHAR(20) PRIMARY KEY,
                    title VARCHAR(200),
                    year INT,
                    summary TEXT
                )
            """);
            System.out.println("✅ Ο πίνακας Books δημιουργήθηκε.");

            // Πίνακας Authors (με σχέση με Books)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Authors (
                    isbn VARCHAR(20),
                    name VARCHAR(100),
                    PRIMARY KEY (isbn, name),
                    FOREIGN KEY (isbn) REFERENCES Books(isbn)
                )
            """);
            System.out.println("✅ Ο πίνακας Authors δημιουργήθηκε.");

            // Πίνακας Loans (με σχέση με Users & Books)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Loans (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    isbn VARCHAR(20),
                    username VARCHAR(100),
                    loan_date DATE,
                    FOREIGN KEY (isbn) REFERENCES Books(isbn),
                    FOREIGN KEY (username) REFERENCES Users(username)
                )
            """);
            System.out.println("✅ Ο πίνακας Loans δημιουργήθηκε.");

            stmt.close();
            connection.close();
            System.out.println("✅ Όλα ολοκληρώθηκαν με επιτυχία.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
