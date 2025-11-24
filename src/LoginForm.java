import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Login - Library System");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel userLabel = new JLabel("Username (email):");
        userLabel.setBounds(20, 20, 120, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 20, 150, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 60, 120, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 60, 150, 25);
        add(passwordField);

        JButton loginButton = new JButton("Είσοδος");
        loginButton.setBounds(110, 100, 120, 30);
        add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (authenticateUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "✅ Είσοδος επιτυχής!");
                    dispose(); // Κλείνει το login παράθυρο
                    new MainScreen(username); // Άνοιγμα αρχικής οθόνης
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Λάθος στοιχεία σύνδεσης.");
                }
            }
        });
    }

    private boolean authenticateUser(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/LibraryDB";
        String dbUser = "root";
        String dbPass = "123tetes";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Επιστρέφει true αν βρέθηκε εγγραφή

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new LoginForm().setVisible(true);
    }
}
