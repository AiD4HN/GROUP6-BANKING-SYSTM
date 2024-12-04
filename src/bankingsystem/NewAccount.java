package bankingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewAccount extends JDialog {

    public NewAccount(JFrame parent) {

        // JFrame Framework
        super(parent);
        setContentPane(NewAccount);
        setMinimumSize(new Dimension(650, 590));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Exit Button Action
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                System.out.println("NewAccount closed");
                System.out.println("**********************************");
                dispose();
            }
        });

        // Open Account button
        openAccountBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // Checks if textfield has been imported
                String username = tfUsername.getText();
                String pinString = tfPin.getText();
                String confirmPinString = tfConfirmPin.getText();

                // Check if the PIN and Confirm PIN match
                if (!pinString.equals(confirmPinString)) {
                    JOptionPane.showMessageDialog(null, "PINs do not match. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int pin;
                try {
                    pin = Integer.parseInt(pinString);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid numeric PIN.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String lastname = tfLastname.getText();
                String firstname = tfFirstname.getText();
                String birthday = tfBirthday.getText();
                String gender = cbGender.getSelectedItem().toString();
                String question = cbSecQuestion.getSelectedItem().toString();
                String answer = tfAnswer.getText();
                String accountNumber = generateRandomAccountNumber();
                double accountBalance = 0.0;

                String sql = "INSERT INTO open_account (account_number, username, user_pin, lastname, firstname, birthday, gender, security_question, answer, sa_balance, ca_balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver"); // connection of mysql

                    try (
                            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "bank", "12345");
                            PreparedStatement pst = conn.prepareStatement(sql)
                    ) {
                        pst.setString(1, accountNumber);
                        pst.setString(2, username);
                        pst.setInt(3, pin);
                        pst.setString(4, lastname);
                        pst.setString(5, firstname);
                        pst.setString(6, birthday);
                        pst.setString(7, gender);
                        pst.setString(8, question);
                        pst.setString(9, answer);
                        pst.setDouble(10, accountBalance);
                        pst.setDouble(11, accountBalance);

                        pst.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Your new account number is: \n" + accountNumber + "\n!! PLEASE SAVE ACCOUNT NUMBER !!", "Account Number", JOptionPane.INFORMATION_MESSAGE);
                        tfUsername.setText("");
                        tfPin.setText("");
                        tfConfirmPin.setText("");
                        tfFirstname.setText("");
                        tfLastname.setText("");
                        cbGender.setSelectedItem(null);
                        tfAnswer.setText("");
                        tfBirthday.setText("");

                        NewAccount.this.dispose();

                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                } catch (ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found.");
                }
            }
        });

        // button to check if username is available
        checkUsernameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String username = tfUsername.getText();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a Username");
                } else {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver"); // connection of mysql

                        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "bank", "12345");
                             PreparedStatement pst = conn.prepareStatement("SELECT * FROM open_account WHERE username = ?")) {

                            pst.setString(1, username);
                            ResultSet rs = pst.executeQuery();

                            if (!rs.next()) {
                                JOptionPane.showMessageDialog(null, "Username is available");
                                tfPin.requestFocus();
                            } else {
                                JOptionPane.showMessageDialog(null, "Username already exists");
                                tfUsername.setText("");
                            }
                            rs.close();

                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(null, e);
                        }
                    } catch (ClassNotFoundException e) {
                        JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found.");
                    }
                }
            }
        });

        setVisible(true);
    }

    // Random accountNumberGenerator logic
    private String generateRandomAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();

        // Generate 9 random digits
        for (int i = 0; i < 9; i++) {
            accountNumber.append(random.nextInt(10));
        }

        return accountNumber.toString();
    }

    // Validate the date format MM/DD/YYYY
    private boolean isValidDate(String date) {
        String regex = "^((0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/(\\d{4}))$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        if (!matcher.matches()) {
            return false;
        }

        String[] parts = date.split("/");
        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return day <= 31;
            case 4: case 6: case 9: case 11:
                return day <= 30;
            case 2:
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    return day <= 29; // Leap year
                } else {
                    return day <= 28; // Non-leap year
                }
            default:
                return false; // Invalid month
        }
    }

    // Variables declaration
    private JPanel NewAccount;
    private JTextField tfUsername;
    private JTextField tfPin;
    private JTextField tfConfirmPin;
    private JTextField tfBirthday;
    private JButton openAccountBtn;
    private JButton exitBtn;
    private JComboBox<String> cbGender;
    private JComboBox<String> cbSecQuestion;
    private JTextField tfLastname;
    private JTextField tfFirstname;
    private JTextField tfAnswer;
    private JButton checkUsernameBtn;

}