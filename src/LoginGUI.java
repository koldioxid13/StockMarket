import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class LoginGUI extends JFrame {

    public LoginGUI() {
        setTitle("LOG IN TO CORP-NET");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Centrerar fönstret på skärmen

        // Huvudbakgrunden
        getContentPane().setBackground(Color.BLACK);
        setLayout(new GridBagLayout()); // Används för att centrera inloggningsrutan

        // --- 2. Gemensamma visuella element ---
        Font titleFont = new Font(Font.MONOSPACED, Font.BOLD, 24);
        Font labelFont = new Font(Font.MONOSPACED, Font.BOLD, 16);
        Font inputFont = new Font(Font.MONOSPACED, Font.PLAIN, 18);
        JLabel statusLabel;

        Color csRed = new Color(200, 0, 0); // En stark, rå röd färg
        Color csGreen = new Color(0, 255, 0); // Skrikig terminalgrön

        // --- 3. Inloggningspanelen (Rutan i mitten) ---
        JPanel loginBox = new JPanel();
        loginBox.setBackground(Color.BLACK);
        // En rå röd ram runt inloggningen
        loginBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(csRed, 2),
                BorderFactory.createEmptyBorder(20, 30, 30, 30) // Inre marginal (padding)
        ));
        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));

        // -- Titel --
        JLabel titleLabel = new JLabel("ACCESS TERMINAL");
        titleLabel.setForeground(csRed);
        titleLabel.setFont(titleFont);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBox.add(titleLabel);

        loginBox.add(Box.createRigidArea(new Dimension(0, 30))); // Mellanrum

        // -- Användarnamn --
        JLabel userLabel = new JLabel("USERNAME:");
        userLabel.setForeground(csRed);
        userLabel.setFont(labelFont);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBox.add(userLabel);

        JTextField userField = new JTextField(15);
        styleInputField(userField, csGreen, inputFont);
        loginBox.add(userField);

        loginBox.add(Box.createRigidArea(new Dimension(0, 15))); // Mellanrum

        // -- Lösenord --
        JLabel passLabel = new JLabel("PASSWORD:");
        passLabel.setForeground(csRed);
        passLabel.setFont(labelFont);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBox.add(passLabel);

        JPasswordField passField = new JPasswordField(15);
        styleInputField(passField, csGreen, inputFont);
        loginBox.add(passField);

        loginBox.add(Box.createRigidArea(new Dimension(0, 30))); // Mellanrum

        // --- Status Label ---
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.YELLOW);
        statusLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setPreferredSize(new Dimension(400, 30));
        statusLabel.setMinimumSize(new Dimension(400, 30));
        statusLabel.setMaximumSize(new Dimension(400, 30));
        loginBox.add(statusLabel);

        loginBox.add(Box.createRigidArea(new Dimension(0, 15))); // Mellanrum innan knapparna

        // --- 4. Knappar ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginButton = createStyledButton("LOGIN", csGreen);
        JButton registerButton = createStyledButton("NEW ENTITY", csRed);

        // Visuell simulering (öppnar marknaden när man klickar)
        // TODO: Byt ut detta mot riktig valideringslogik senare
        loginButton.addActionListener(e -> {
            System.out.println("User: " + userField.getText());
            System.out.println("Password: " + passField.getText());

            User newUser = new User(userField.getText(), passField.getText());
            String isValid = Authenticator.getAuthenticator().isValidUser(newUser);

            if (isValid.equals("valid")) {
                newUser.setCash(UserManager.getUserManager().getUserCash(newUser));
                UserManager.getUserManager().setCurrentUser(newUser);
                this.dispose();
                new StockMarketGUI();
            } else if (isValid.equals("wrongPassword")) {
                setStatus("Wrong password", statusLabel);
            } else if (isValid.equals("invalidUser")) {
                setStatus("User does not exist", statusLabel);
            } else {
                setStatus("Error", statusLabel);
            }
        });

        registerButton.addActionListener(e -> {
            System.out.println("User: " + userField.getText());
            System.out.println("Password: " + passField.getText());

            User newUser = new User(userField.getText(), passField.getText());

            if (newUser.getUserName().isEmpty()) {
                setStatus("Username cannot be empty!", statusLabel);
            } else if (newUser.getPassword().isEmpty()) {
                setStatus("Password cannot be empty!", statusLabel);
            } else if(newUser.getUserName().contains("|") || newUser.getPassword().contains("|")) {
                setStatus("Field can not contain the character '|'!", statusLabel);
            }
            else {
                boolean success = UserManager.getUserManager().saveUser(newUser);

                if (success) {
                    setStatus("User saved", statusLabel);
                } else {
                    setStatus("Username already taken", statusLabel);
                }
            }
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        loginBox.add(buttonPanel);

        // Lägg till inloggningsrutan i mitten av huvudfönstret
        add(loginBox);

        setVisible(true);
    }

    // --- Hjälpmetod för att styla textfälten ---
    private void styleInputField(JTextField field, Color textColor, Font font) {
        field.setBackground(Color.BLACK);
        field.setForeground(textColor);
        field.setCaretColor(textColor); // Den blinkande markören blir också grön
        field.setFont(font);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(300, 35)); // Förhindrar att fälten blir för stora i BoxLayout
    }

    // --- Hjälpmetod för att styla knapparna ---
    private JButton createStyledButton(String text, Color borderColor) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.BLACK);
        btn.setForeground(borderColor);
        btn.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        btn.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setStatus(String statusText, JLabel statusLabel) {
        statusLabel.setText(statusText);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (Objects.equals(statusLabel.getText(), statusText)) {
                            statusLabel.setText(" ");
                        }
                    }
                },
                3000
        );
    }

    // --- Main-metod för att testa bara inloggningsskärmen ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI());
    }
}