import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SearchForm extends JFrame {
    private JTextField titleField, authorField;
    private JTable table;
    private DefaultTableModel tableModel;
    private String username;

    public SearchForm(String username) {
        this.username = username;

        setTitle("Αναζήτηση Βιβλίου");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Πάνω μέρος: πεδία αναζήτησης
        JPanel topPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(new JLabel("Τίτλος:"));
        titleField = new JTextField();
        topPanel.add(titleField);

        JButton searchBtn = new JButton("Αναζήτηση");
        topPanel.add(searchBtn);

        topPanel.add(new JLabel("Συγγραφέας:"));
        authorField = new JTextField();
        topPanel.add(authorField);

        JButton loanBtn = new JButton("Κράτηση");
        topPanel.add(loanBtn);

        add(topPanel, BorderLayout.NORTH);

        // Πίνακας αποτελεσμάτων
        String[] columns = {"ISBN", "Τίτλος", "Συγγραφέας", "Έτος"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Αναζήτηση
        searchBtn.addActionListener(e -> performSearch());

        // Κράτηση
        loanBtn.addActionListener(e -> performLoan());

        setVisible(true);
    }

    private void performSearch() {
        tableModel.setRowCount(0);
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();

        String query =
            "SELECT b.isbn, b.title, a.name, b.year " +
            "FROM Books b " +
            "JOIN Authors a ON b.isbn = a.isbn " +
            "WHERE (? = '' OR b.title LIKE ?) " +
            "AND (? = '' OR a.name LIKE ?) " +
            "ORDER BY b.title ASC";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LibraryDB", "root", "123tetes");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.setString(2, "%" + title + "%");
            stmt.setString(3, author);
            stmt.setString(4, "%" + author + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("name"),
                    rs.getInt("year")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Σφάλμα κατά την αναζήτηση.");
        }
    }

    private void performLoan() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "❗ Παρακαλώ επιλέξτε ένα βιβλίο.");
            return;
        }

        String isbn = (String) tableModel.getValueAt(selected, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LibraryDB", "root", "123tetes")) {
            String checkQuery =
                "SELECT loan_date FROM Loans WHERE isbn = ? ORDER BY loan_date DESC LIMIT 1";

            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, isbn);
            ResultSet rs = checkStmt.executeQuery();

            boolean available = true;
            if (rs.next()) {
                Date lastLoan = rs.getDate("loan_date");
                Date now = new Date(System.currentTimeMillis());
                long diff = now.getTime() - lastLoan.getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                if (days < 30) {
                    available = false;
                }
            }

            if (available) {
                String insertQuery =
                    "INSERT INTO Loans (isbn, username, loan_date) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, isbn);
                insertStmt.setString(2, username);
                insertStmt.setDate(3, new Date(System.currentTimeMillis()));
                insertStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Επιτυχής Δανεισμός Βιβλίου.");
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Το βιβλίο δεν είναι διαθέσιμο. Παρακαλώ επιλέξτε άλλο.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Σφάλμα κατά τη διαδικασία κράτησης.");
        }
    }
}

