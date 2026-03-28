// Import Swing components for building the GUI
import javax.swing.*;
// Import DefaultTableModel for managing the table's data rows and columns
import javax.swing.table.DefaultTableModel;
// Import AWT layout manager
import java.awt.*;
// Import SQL classes for database connectivity
import java.sql.*;

/**
 * MyBooksForm - Displays the currently logged-in user's borrowed books.
 * Fetches loan records from the database and presents them in a scrollable table
 * showing each book's ISBN, title, and loan date, sorted from newest to oldest.
 */
public class MyBooksForm extends JFrame {

    // Stores the logged-in user's username for use in the database query
    private String username;

    /**
     * Constructor: Builds the form, queries the database, and populates the table.
     *
     * @param username The email/username of the logged-in user,
     *                 used to filter loans belonging to this user only
     */
    public MyBooksForm(String username) {
        this.username = username;

        // "Τα Βιβλία μου" means "My Books" in Greek
        setTitle("Τα Βιβλία μου");
        setSize(600, 400);
        // Center the window on screen
        setLocationRelativeTo(null);
        // DISPOSE_ON_CLOSE only closes this window, keeping the MainScreen alive
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // BorderLayout allows the table to fill the full window area
        setLayout(new BorderLayout());

        // --- Table Setup ---
        // Define the three visible column headers (ISBN, Title, Loan Date)
        String[] columnNames = {"ISBN", "Τίτλος", "Ημερομηνία Δανεισμού"};  // "Title", "Loan Date"
        // DefaultTableModel holds the row data and notifies the table of any changes
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0); // 0 = start with no rows
        JTable table = new JTable(tableModel);

        // --- Database Query ---
        try {
            // Open a connection to the LibraryDB MySQL database
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/LibraryDB", "root", "123tetes"
            );

            // JOIN Loans with Books to retrieve the book title alongside loan info.
            // Results are filtered by username and sorted newest-first by loan date.
            String sql = "SELECT b.isbn, b.title, l.loan_date " +
                         "FROM Loans l " +
                         "JOIN Books b ON l.isbn = b.isbn " +
                         "WHERE l.username = ? " +
                         "ORDER BY l.loan_date DESC";

            // PreparedStatement prevents SQL injection by binding parameters safely
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);    // Bind the logged-in user's username
            ResultSet rs = stmt.executeQuery();

            // Iterate over each loan record and add it as a row in the table
            while (rs.next()) {
                String isbn  = rs.getString("isbn");
                String title = rs.getString("title");
                String date  = rs.getString("loan_date");
                tableModel.addRow(new Object[]{isbn, title, date});
            }

            // Explicitly close resources in order: ResultSet → Statement → Connection
            // Note: consider try-with-resources in future to handle this automatically
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            // Print the full stack trace to the console for debugging
            e.printStackTrace();
            // "Σφάλμα κατά τη φόρτωση των βιβλίων" means "Error loading books"
            JOptionPane.showMessageDialog(this, "Σφάλμα κατά τη φόρτωση των βιβλίων.");
        }

        // --- Layout ---
        // Wrap the table in a JScrollPane so headers remain visible and rows are scrollable
        JScrollPane scrollPane = new JScrollPane(table);
        // Add to CENTER so the table expands to fill the entire window
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
