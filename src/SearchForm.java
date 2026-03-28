// Import Swing components for building the GUI
import javax.swing.*;
// Import DefaultTableModel for managing the results table data
import javax.swing.table.DefaultTableModel;
// Import AWT layout managers and components
import java.awt.*;
// Import event handling for button listeners
import java.awt.event.*;
// Import SQL classes for database connectivity
import java.sql.*;

/**
 * SearchForm - Allows the logged-in user to search for books by title and/or author,
 * and to place a loan reservation on a selected book.
 * Results are displayed in a sortable table fetched live from the database.
 */
public class SearchForm extends JFrame {

    // Input fields for filtering search results
    private JTextField titleField, authorField;
    // Table component for displaying search results
    private JTable table;
    // Backing model that holds the table's row data
    private DefaultTableModel tableModel;
    // The logged-in user's username, used when registering a loan
    private String username;

    /**
     * Constructor: Builds the search UI and registers button listeners.
     *
     * @param username The email/username of the logged-in user,
     *                 forwarded to performLoan() to record who borrowed the book
     */
    public SearchForm(String username) {
        this.username = username;

        // "Αναζήτηση Βιβλίου" means "Book Search" in Greek
        setTitle("Αναζήτηση Βιβλίου");
        setSize(700, 400);
        // Center the window on screen
        setLocationRelativeTo(null);
        // DISPOSE_ON_CLOSE closes only this window, keeping MainScreen open
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // BorderLayout: search controls go NORTH, results table goes CENTER
        setLayout(new BorderLayout());

        // --- Top Panel: Search Controls ---
        // GridLayout(2 rows, 3 cols) neatly aligns labels, inputs, and buttons
        JPanel topPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        // Add 10px padding on all sides so controls don't touch the window edges
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Row 1: Title label | title input field | Search button
        topPanel.add(new JLabel("Τίτλος:"));        // "Title"
        titleField = new JTextField();
        topPanel.add(titleField);
        JButton searchBtn = new JButton("Αναζήτηση");  // "Search"
        topPanel.add(searchBtn);

        // Row 2: Author label | author input field | Reserve button
        topPanel.add(new JLabel("Συγγραφέας:"));    // "Author"
        authorField = new JTextField();
        topPanel.add(authorField);
        JButton loanBtn = new JButton("Κράτηση");   // "Reserve/Loan"
        topPanel.add(loanBtn);

        add(topPanel, BorderLayout.NORTH);

        // --- Results Table ---
        // Column headers: ISBN | Title | Author | Year
        String[] columns = {"ISBN", "Τίτλος", "Συγγραφέας", "Έτος"};  // "Title", "Author", "Year"
        tableModel = new DefaultTableModel(columns, 0); // 0 = start with no rows
        table = new JTable(tableModel);
        // Wrap in JScrollPane so column headers stay visible and rows are scrollable
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Trigger a live database search whenever the Search button is clicked
        searchBtn.addActionListener(e -> performSearch());

        // Attempt to register a loan for the currently selected table row
        loanBtn.addActionListener(e -> performLoan());

        setVisible(true);
    }

    /**
     * Queries the database for books matching the entered title and/or author filters.
     * Both filters are optional — leaving a field empty matches all values for that column.
     * Results replace any previously displayed rows in the table.
     */
    private void performSearch() {
        // Clear any previously displayed results before loading new ones
        tableModel.setRowCount(0);

        String title  = titleField.getText().trim();
        String author = authorField.getText().trim();

        // The WHERE clause uses a "bypass" pattern:
        // If the input is empty (''). the condition is always true, effectively ignoring that filter.
        // Otherwise, a LIKE '%value%' partial match is applied.
        String query =
            "SELECT b.isbn, b.title, a.name, b.year " +
            "FROM Books b " +
            "JOIN Authors a ON b.isbn = a.isbn " +
            "WHERE (? = '' OR b.title LIKE ?) " +    // Title filter (optional)
            "AND   (? = '' OR a.name  LIKE ?) " +    // Author filter (optional)
            "ORDER BY b.title ASC";                  // Sort results alphabetically by title

        // try-with-resources ensures both conn and stmt are closed automatically
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/LibraryDB", "root", "123tetes");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Bind parameters: each filter needs two bindings (empty check + LIKE value)
            stmt.setString(1, title);               // Plain value for empty check
            stmt.setString(2, "%" + title + "%");   // Wildcard-wrapped value for LIKE
            stmt.setString(3, author);
            stmt.setString(4, "%" + author + "%");

            ResultSet rs = stmt.executeQuery();

            // Add one table row per book returned by the query
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("name"),   // Author name from Authors table
                    rs.getInt("year")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // "Σφάλμα κατά την αναζήτηση" means "Error during search"
            JOptionPane.showMessageDialog(this, "❌ Σφάλμα κατά την αναζήτηση.");
        }
    }

    /**
     * Attempts to register a loan for the book selected in the results table.
     * A book is considered unavailable if it has been loaned within the last 30 days.
     * If available, a new Loans record is inserted with today's date.
     */
    private void performLoan() {
        int selected = table.getSelectedRow();

        // Guard: ensure the user has actually selected a row before proceeding
        if (selected == -1) {
            // "Παρακαλώ επιλέξτε ένα βιβλίο" means "Please select a book"
            JOptionPane.showMessageDialog(this, "❗ Παρακαλώ επιλέξτε ένα βιβλίο.");
            return;
        }

        // Retrieve the ISBN from column 0 of the selected row
        String isbn = (String) tableModel.getValueAt(selected, 0);

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/LibraryDB", "root", "123tetes")) {

            // --- Availability Check ---
            // Fetch the most recent loan for this ISBN to check if it's still active
            String checkQuery =
                "SELECT loan_date FROM Loans WHERE isbn = ? ORDER BY loan_date DESC LIMIT 1";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, isbn);
            ResultSet rs = checkStmt.executeQuery();

            boolean available = true;   // Assume available unless proven otherwise

            if (rs.next()) {
                Date lastLoan = rs.getDate("loan_date");
                Date now      = new Date(System.currentTimeMillis());

                // Calculate the number of days since the last loan
                long diff = now.getTime() - lastLoan.getTime();
                long days = diff / (1000 * 60 * 60 * 24); // Convert milliseconds → days

                // If the book was loaned less than 30 days ago, it is still on loan
                if (days < 30) {
                    available = false;
                }
            }
            // If no loan record exists at all, the book has never been borrowed → available

            if (available) {
                // --- Insert Loan Record ---
                String insertQuery =
                    "INSERT INTO Loans (isbn, username, loan_date) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, isbn);
                insertStmt.setString(2, username);                          // Logged-in user
                insertStmt.setDate(3, new Date(System.currentTimeMillis())); // Today's date
                insertStmt.executeUpdate();

                // "Επιτυχής Δανεισμός Βιβλίου" means "Successful Book Loan"
                JOptionPane.showMessageDialog(this, "✅ Επιτυχής Δανεισμός Βιβλίου.");
            } else {
                // "Το βιβλίο δεν είναι διαθέσιμο" means "The book is not available"
                JOptionPane.showMessageDialog(this, "⚠️ Το βιβλίο δεν είναι διαθέσιμο. Παρακαλώ επιλέξτε άλλο.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // "Σφάλμα κατά τη διαδικασία κράτησης" means "Error during reservation process"
            JOptionPane.showMessageDialog(this, "❌ Σφάλμα κατά τη διαδικασία κράτησης.");
        }
    }
}
