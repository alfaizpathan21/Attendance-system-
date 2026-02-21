import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class RegistrationFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;

    public RegistrationFrame() {
        setTitle("Register New User");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 400, 350, 30, 30));
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

        JLabel titleLabel = new JLabel("New Registration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        contentPanel.add(titleLabel, gbc);

        usernameField = createStyledTextField("Username");
        passwordField = createStyledPasswordField("Password");
        roleCombo = new JComboBox<>(new String[]{"admin", "teacher"});
        styleComboBox(roleCombo);

        gbc.gridy = 1;
        contentPanel.add(createInputGroup(usernameField, "user"), gbc);
        gbc.gridy = 2;
        contentPanel.add(createInputGroup(passwordField, "lock"), gbc);
        gbc.gridy = 3;
        contentPanel.add(createInputGroup(roleCombo, "role"), gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        ModernButton registerButton = new ModernButton("Register");
        registerButton.addActionListener(e -> performRegistration());

        ModernButton cancelButton = new ModernButton("Cancel");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 4;
        contentPanel.add(buttonPanel, gbc);

        mainPanel.add(contentPanel);
        add(mainPanel);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(15);
        styleBaseField(field, placeholder);
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(15);
        styleBaseField(field, placeholder);

        field.setEchoChar((char) 0);
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
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        field.setOpaque(false);
        field.setForeground(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

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

    private void styleComboBox(JComboBox<String> combo) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setForeground(Color.DARK_GRAY);
                return label;
            }
        });
        combo.setBackground(Color.WHITE);
    }

    private JPanel createInputGroup(JComponent component, String iconName) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JLabel icon = new JLabel(new ImageIcon("icons/" + iconName + ".png"));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        panel.add(icon, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void performRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (username.equals("Username") || password.equals("Password")) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration successful!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Username already exists!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}