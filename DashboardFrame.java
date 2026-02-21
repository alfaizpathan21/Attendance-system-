import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DashboardFrame extends JFrame {
    private String userRole;

    public DashboardFrame(String role) {
        this.userRole = role;
        setTitle("Attendance Management Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new GradientPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("Attendance Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        ModernButton logoutButton = new ModernButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new ModernTabbedPaneUI());
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        if (userRole.equals("admin")) {
            tabbedPane.addTab("Users", createUsersPanel());
        }

        tabbedPane.addTab("Take Attendance", createAttendancePanel());
        tabbedPane.addTab("View Records", createRecordsPanel());

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(listModel);
        userList.setCellRenderer(new UserListRenderer());
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshUserList(listModel);

        ModernButton deleteButton = new ModernButton("Delete User");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.addActionListener(e -> {
            String selected = userList.getSelectedValue();
            if (selected != null) {
                deleteUser(selected.split(" - ")[0], listModel);
            }
        });

        panel.add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        inputPanel.setBorder(BorderFactory.createTitledBorder("New Attendance Entry"));
        inputPanel.setBackground(Color.WHITE);

        JTextField studentIdField = createStyledField("Student ID");
        JTextField dateField = createStyledField("YYYY-MM-DD");
        JComboBox<String> statusCombo = createStyledCombo(new String[]{"Present", "Absent"});
        JComboBox<String> subjectCombo = createStyledCombo(new String[]{"Math", "Science", "English"});

        inputPanel.add(createInputLabel("Student ID:"));
        inputPanel.add(studentIdField);
        inputPanel.add(createInputLabel("Date:"));
        inputPanel.add(dateField);
        inputPanel.add(createInputLabel("Status:"));
        inputPanel.add(statusCombo);
        inputPanel.add(createInputLabel("Subject:"));
        inputPanel.add(subjectCombo);

        ModernButton saveButton = new ModernButton("Save Attendance");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            saveAttendance(
                    studentIdField.getText(),
                    dateField.getText(),
                    (String) statusCombo.getSelectedItem(),
                    (String) subjectCombo.getSelectedItem()
            );
            studentIdField.setText("");
            dateField.setText("");
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTable table = new JTable();
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JTextField searchField = createStyledField("Enter Student ID");
        ModernButton searchButton = new ModernButton("Search");
        searchButton.addActionListener(e -> loadAttendanceRecords(table, searchField.getText()));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        loadAttendanceRecords(table, "");
        return panel;
    }

    private JLabel createInputLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private JTextField createStyledField(String placeholder) {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    private JComboBox<String> createStyledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setRenderer(new StyledComboRenderer());
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return combo;
    }

    private void refreshUserList(DefaultListModel<String> model) {
        model.clear();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                model.addElement(rs.getString("username") + " - " + rs.getString("role"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteUser(String username, DefaultListModel<String> model) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM users WHERE username = ?")) {

            stmt.setString(1, username);
            stmt.executeUpdate();
            refreshUserList(model);
            JOptionPane.showMessageDialog(this, "User deleted successfully!");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting user!");
        }
    }

    private void saveAttendance(String studentId, String date, String status, String subject) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO attendance (student_id, date, status, subject) VALUES (?, ?, ?, ?)")) {

            stmt.setString(1, studentId);
            stmt.setString(2, date);
            stmt.setString(3, status);
            stmt.setString(4, subject);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Attendance saved successfully!");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving attendance!");
        }
    }

    private void loadAttendanceRecords(JTable table, String searchQuery) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM attendance WHERE student_id LIKE ?")) {

            stmt.setString(1, "%" + searchQuery + "%");
            ResultSet rs = stmt.executeQuery();
            table.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    static class UserListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            return label;
        }
    }

    static class StyledComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return label;
        }
    }
}