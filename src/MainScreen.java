import javax.swing.*;
import java.awt.event.*;

public class MainScreen extends JFrame {
    private String username;

    public MainScreen(String username) {
        this.username = username;

        setTitle("Αρχική - Library System");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel welcomeLabel = new JLabel("Καλώς ήρθες: " + username);
        welcomeLabel.setBounds(50, 20, 200, 25);
        add(welcomeLabel);

        JButton searchButton = new JButton("Αναζήτηση");
        searchButton.setBounds(80, 60, 120, 30);
        add(searchButton);

        JButton myBooksButton = new JButton("Τα Βιβλία μου");
        myBooksButton.setBounds(80, 100, 120, 30);
        add(myBooksButton);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SearchForm(username);  // Κλήση στη φόρμα αναζήτησης
            }
        });

        myBooksButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MyBooksForm(username);  // Κλήση στη φόρμα δανεισμένων
            }
        });

        setVisible(true);
    }
}
