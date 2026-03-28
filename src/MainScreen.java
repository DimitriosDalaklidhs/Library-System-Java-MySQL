// Import Swing components for building the GUI
import javax.swing.*;
// Import event handling classes for button click listeners
import java.awt.event.*;

/**
 * MainScreen - The home screen of the Library Management System.
 * Displayed after a successful login, offering navigation to the
 * book search and personal loans sections.
 */
public class MainScreen extends JFrame {

    // Stores the logged-in user's email/username for passing to child screens
    private String username;

    /**
     * Constructor: Builds and displays the main navigation screen.
     *
     * @param username The email/username of the authenticated user,
     *                 received from LoginForm after successful login
     */
    public MainScreen(String username) {
        // Store the username so it can be forwarded to other screens
        this.username = username;

        // Set the window title — "Αρχική" means "Home" in Greek
        setTitle("Αρχική - Library System");
        setSize(300, 200);
        // Terminate the application when the window is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Center the window on screen
        setLocationRelativeTo(null);
        // Use absolute positioning for manual component placement
        setLayout(null);

        // --- Welcome Label ---
        // Greets the user by name; "Καλώς ήρθες" means "Welcome" in Greek
        JLabel welcomeLabel = new JLabel("Καλώς ήρθες: " + username);
        welcomeLabel.setBounds(50, 20, 200, 25);    // x, y, width, height
        add(welcomeLabel);

        // --- Search Button ---
        // "Αναζήτηση" means "Search" — navigates to the book search screen
        JButton searchButton = new JButton("Αναζήτηση");
        searchButton.setBounds(80, 60, 120, 30);
        add(searchButton);

        // --- My Books Button ---
        // "Τα Βιβλία μου" means "My Books" — navigates to the user's borrowed books
        JButton myBooksButton = new JButton("Τα Βιβλία μου");
        myBooksButton.setBounds(80, 100, 120, 30);
        add(myBooksButton);

        // Open the book search form, passing the username for session context
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SearchForm(username);
            }
        });

        // Open the borrowed books form, passing the username to load that user's loans
        myBooksButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MyBooksForm(username);
            }
        });

        // Make the window visible (called here since MainScreen manages its own visibility,
        // unlike LoginForm where setVisible is called externally in main())
        setVisible(true);
    }
}
