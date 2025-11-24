import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyBooksForm extends JFrame {
    private String username;

    public MyBooksForm(String username) {
        this.username = username;

        setTitle("Τα Βιβλία μου");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnNames = {"ISBN", "Τίτλος", "Ημερομηνία Δανεισμού"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/LibraryDB", "root", "123tetes"
            );

            String sql = "SELECT b.isbn, b.title, l.loan_date " +
                         "FROM Loans l " +
                         "JOIN Books b ON l.isbn = b.isbn " +
                         "WHERE l.username = ? " +
                         "ORDER BY l.loan_date DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                String date = rs.getString("loan_date");

                tableModel.addRow(new Object[]{isbn, title, date});
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Σφάλμα κατά τη φόρτωση των βιβλίων.");
        }

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }
}
