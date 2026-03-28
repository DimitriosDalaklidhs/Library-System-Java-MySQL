// Import Swing components for building the GUI
import javax.swing.*;
// Import event handling classes for button click listener
import java.awt.event.*;
// Import SQL classes for database connectivity
import java.sql.*;

/**
 * LoginForm - A Swing-based login window for the Library Management System.
 * Authenticates users against a MySQL database before granting access.
 */
public class LoginForm extends JFrame {

    // Input field for the user's email/username
    private JTextField usernameField;
    // Input field for the password (masks characters for security)
    private JPasswordField passwordField;

    /**
     * Constructor: Builds and configures the login form UI.
     */
    public LoginForm() {
        // Set the window title shown in the title bar
        setTitle("Login - Library System");
        // Define the initial window dimensions (width x height in pixels)
        setSize(350, 200);
        // Terminate the application when the window is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Center the window on screen
        setLocationRelativeTo(null);
        // Use absolute positioning (null layout) for manual component placement
        setLayout(null);

        // --- Username Row ---
        JLabel userLabel = new JLabel("Username (email):");
        userLabel.setBounds(20, 20, 120, 25);   // x, y, width, height
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 20, 150, 25);
        add(usernameField);

        // --- Password Row ---
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 60, 120, 25);
        add(passLabel);

        // JPasswordField hides typed characters with bullet points
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 60, 150, 25);
        add(passwordField);

        // --- Login Button ---
        JButton loginButton = new JButton("Είσοδος");  // "Login" in Greek
        loginButton.setBounds(110, 100, 120, 30);
        add(loginButton);

        // Register an action listener to handle login button clicks
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Read the username from the text field
                String username = usernameField.getText();
                // Convert the password char array to a String
                // Note: getPassword() returns char[] for better security than getText()
                String password = new String(passwordField.getPassword());

                if (authenticateUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "✅ Είσοδος επιτυχής!");  // "Login successful!"
                    dispose();                  // Close the login window
                    new MainScreen(username);   // Open the main application screen
                } else {
                    // Notify the user of invalid credentials
                    JOptionPane.showMessageDialog(null, "❌ Λάθος στοιχεία σύνδεσης.");  // "Wrong credentials."
                }
            }
        });
    }

    /**
     * Validates the provided credentials against the Users table in MySQL.
     *
     * @param username The email/username entered by the user
     * @param password The plaintext password entered by the user
     * @return true if a matching record is found; false otherwise
     *
     * ⚠️ Security note: Passwords should be stored hashed (e.g. BCrypt),
     *    not as plaintext. This implementation is for educational purposes only.
     */
    private boolean authenticateUser(String username, String password) {
        // JDBC connection string: protocol, host, port, and database name
        String url = "jdbc:mysql://localhost:3306/LibraryDB";
        String dbUser = "root";
        String dbPass = "123456oofSecHazardd";

        // try-with-resources ensures the connection is closed automatically
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            // Use a PreparedStatement to prevent SQL injection attacks
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);   // Bind the username parameter
            pstmt.setString(2, password);   // Bind the password parameter
            ResultSet rs = pstmt.executeQuery();

            // rs.next() returns true if at least one matching row was found
            return rs.next();
        } catch (SQLException e) {
            // Log the full stack trace for debugging database errors
            e.printStackTrace();
            return false;   // Treat any DB error as a failed login
        }
    }

    /**
     * Application entry point. Launches the login form on the Event Dispatch Thread (EDT).
     */
    public static void main(String[] args) {
        new LoginForm().setVisible(true);
    }
}
