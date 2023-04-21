import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentInfoGUI extends JFrame implements ActionListener {
    private JLabel prnLabel, nameLabel, branchLabel, mobileLabel, emailLabel;
    private JTextField prnField, nameField, branchField, mobileField, emailField;
    private JButton saveButton, displayButton, clearButton;
    private JCheckBox deleteCheckbox;
    private JButton deleteButton;
    private JButton searchButton;

    public StudentInfoGUI() {
        // Set up the frame
        super("Student Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Create the labels and text fields
        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        prnLabel = new JLabel("PRN:");
        prnLabel.setFont(labelFont);
        prnField = new JTextField(20);
        prnField.setFont(fieldFont);
        nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        nameField = new JTextField(30);
        nameField.setFont(fieldFont);
        branchLabel = new JLabel("Branch:");
        branchLabel.setFont(labelFont);
        branchField = new JTextField(30);
        branchField.setFont(fieldFont);
        mobileLabel = new JLabel("Mobile:");
        mobileLabel.setFont(labelFont);
        mobileField = new JTextField(20);
        mobileField.setFont(fieldFont);
        emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailField = new JTextField(30);
        emailField.setFont(fieldFont);

        // Create the buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        saveButton.setFont(labelFont);
        displayButton = new JButton("Display");
        displayButton.addActionListener(this);
        displayButton.setFont(labelFont);
        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
        clearButton.setFont(labelFont);
        deleteCheckbox = new JCheckBox("Delete");
        deleteCheckbox.addActionListener(this);
        deleteCheckbox.setFont(labelFont);
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        deleteButton.setFont(labelFont);
        deleteButton.setEnabled(false);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        searchButton.setFont(labelFont);


        // Set button background color
        Color buttonColor = new Color(227, 227, 227); // light grey
        saveButton.setBackground(buttonColor);
        displayButton.setBackground(buttonColor);
        clearButton.setBackground(buttonColor);
        deleteButton.setBackground(buttonColor);

        // Create the panel and add the components
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new GridLayout(8, 5, 10, 10));
        panel.add(prnLabel);
        panel.add(prnField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(branchLabel);
        panel.add(branchField);
        panel.add(mobileLabel);
        panel.add(mobileField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(saveButton);
        panel.add(displayButton);
        panel.add(searchButton);
        panel.add(clearButton);
        panel.add(deleteCheckbox);
        panel.add(deleteButton);

        // Add the panel to the frame
        setContentPane(panel);
        setVisible(true);
    }

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Get number of columns
        int columnCount = metaData.getColumnCount();

        // Create a vector to hold column names
        Vector<String> columnNames = new Vector<String>();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // Create a vector to hold table data
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> row = new Vector<Object>();
            for (int column = 1; column <= columnCount; column++) {
                row.add(rs.getObject(column));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == saveButton) {
            // Get the information from the text fields
            String prn = prnField.getText();
            String name = nameField.getText();
            String branch = branchField.getText();
            String mobile = mobileField.getText();
            String email = emailField.getText();

            // Insert the information into the database
            try {
                Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "sys as sysdba", "ketan");
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO student_info(prn, name, branch, mobile, email) VALUES (?, ?, ?, ?, ?)");
                stmt.setString(1, prn);
                stmt.setString(2, name);
                stmt.setString(3, branch);
                stmt.setString(4, mobile);
                stmt.setString(5, email);
                stmt.executeUpdate();
                conn.close();
                JOptionPane.showMessageDialog(this, "Information saved successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error saving information: " + ex.getMessage());
            }
        }
        else if (e.getSource() == searchButton) {
// Get the search query from the user
            String query = JOptionPane.showInputDialog(this, "Enter search query:");
            // Search the database for matching records
            try {
                Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "sys as sysdba", "ketan");
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM student_info WHERE prn LIKE ? OR name LIKE ? OR branch LIKE ? OR mobile LIKE ? OR email LIKE ?");
                for (int i = 1; i <= 5; i++) {
                    stmt.setString(i, "%" + query + "%");
                }
                ResultSet rs = stmt.executeQuery();
                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    sb.append("PRN: ").append(rs.getString("prn")).append("\n")
                            .append("Name: ").append(rs.getString("name")).append("\n")
                            .append("Branch: ").append(rs.getString("branch")).append("\n")
                            .append("Mobile: ").append(rs.getString("mobile")).append("\n")
                            .append("Email: ").append(rs.getString("email")).append("\n\n");
                }
                if (sb.length() > 0) {
                    JOptionPane.showMessageDialog(this, sb.toString());
                } else {
                    JOptionPane.showMessageDialog(this, "No matching records found.");
                }
                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error searching information: " + ex.getMessage());
            }

        }
        else if (e.getSource() == displayButton) {
            // Display the information from the database
            try {
                Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "sys as sysdba", "ketan");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM student_info");

                // Create a JTable and populate it with the data from the ResultSet
                JTable table = new JTable(buildTableModel(rs));

                // Create a scroll pane to hold the table
                JScrollPane scrollPane = new JScrollPane(table);
                // Display the scroll pane in a dialog box
                JOptionPane.showMessageDialog(this, scrollPane);

                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error displaying information: " + ex.getMessage());
            }
        }

// Method to build a table model from a ResultSet

else if (e.getSource() == clearButton) {
// Clear the text fields
            prnField.setText("");
            nameField.setText("");
            branchField.setText("");
            mobileField.setText("");
            emailField.setText("");
        } else if (e.getSource() == deleteCheckbox) {
// Enable or disable the delete button based on the checkbox selection
            deleteButton.setEnabled(deleteCheckbox.isSelected());
        } else if (e.getSource() == deleteButton) {
// Delete the information from the database
            String prn = prnField.getText();
            if (!prn.equals("")) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this information?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "sys as sysdba", "ketan");
                        PreparedStatement stmt = conn.prepareStatement("DELETE FROM student_info WHERE prn = ?");
                        stmt.setString(1, prn);
                        int result = stmt.executeUpdate();
                        conn.close();
                        if (result > 0) {
                            JOptionPane.showMessageDialog(this, "Information deleted successfully!");
                        } else {
                            JOptionPane.showMessageDialog(this, "No information found with that PRN.");
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error deleting information: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a PRN to delete.");
            }
        }
    }
}
