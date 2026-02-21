import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Attendance System - Login");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 900, 600, 30, 30));
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        contentPanel.add(titleLabel, gbc);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 20));
        formPanel.setOpaque(false);

        usernameField = createStyledTextField("Username");
        passwordField = createStyledPasswordField("Password");

        formPanel.add(createInputGroup(usernameField, "user"));
        formPanel.add(createInputGroup(passwordField, "lock"));

        gbc.gridy = 1;
        contentPanel.add(formPanel, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);

        ModernButton loginButton = new ModernButton("Sign In");
        loginButton.addActionListener(e -> performLogin());

        ModernButton registerButton = new ModernButton("Create Account");
        registerButton.setBackground(new Color(76, 81, 191));
        registerButton.addActionListener(e -> showRegistrationForm());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridy = 2;
        contentPanel.add(buttonPanel, gbc);

        mainPanel.add(contentPanel);
        add(mainPanel);
    }

    private JPanel createInputGroup(JComponent field, String iconName) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JLabel icon = new JLabel(new ImageIcon("icons/" + iconName + ".png"));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        panel.add(icon, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        styleBaseField(field, placeholder);
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20);
        styleBaseField(field, placeholder);

        field.setEchoChar((char) 0); // Show placeholder text
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                }
            }
        });

        return field;
    }

    private void styleBaseField(JComponent field, String placeholder) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        field.setOpaque(false);
        field.setForeground(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        if (field instanceof JTextField) {
            JTextField tf = (JTextField) field;
            tf.setText(placeholder);
            tf.setCaretColor(Color.WHITE);

            tf.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (tf.getText().equals(placeholder)) {
                        tf.setText("");
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (tf.getText().isEmpty()) {
                        tf.setText(placeholder);
                    }
                }
            });
        }
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.equals("Username") || password.equals("Password")) {
            JOptionPane.showMessageDialog(this, "Please enter valid credentials!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE username = ? AND password = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                dispose();
                new DashboardFrame(role).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegistrationForm() {
        RegistrationFrame registrationFrame = new RegistrationFrame();
        registrationFrame.setVisible(true);
    }
}