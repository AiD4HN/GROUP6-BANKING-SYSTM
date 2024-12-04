package bankingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GUI extends JDialog {

    public GUI(JFrame parent) {

        // JFrame Framework
        super(parent);
        setContentPane(GUI);
        setMinimumSize(new Dimension(800, 450));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // This is the Exit Button
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("MainGui closed");
                System.out.println("**********************************");
                dispose();
            }
        });

        // This is the Open New Account Button
        openNewAccBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewAccount newAccountForm = new NewAccount(parent);
            }
        });

        // login button to proceed to Account.java
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String account_number = tfAccountNumber.getText();
                String pin = tfPin.getText();
                String sql = "SELECT * FROM open_account WHERE account_number='" + account_number + "' AND user_pin='" + pin + "'";

                try {
                    // connection of mysql
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "bank", "12345");
                    PreparedStatement pst = conn.prepareStatement(sql);
                    ResultSet rs = pst.executeQuery();
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null, "Invalid Account Number and PIN");
                    } else {
                        JOptionPane.showMessageDialog(null, "Login Successful");
                        String name = rs.getString(6) + " " + rs.getString(5);
                        account_number = String.valueOf(rs.getInt(2));
                        Account accountInstance = new Account(name, account_number);
                        accountInstance.setVisible(true);
                        dispose();
                    }

                } catch (HeadlessException | ClassNotFoundException | SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        });

        setVisible(true);
    }

    // Variables declaration
    private JPanel GUI;
    private JButton exitBtn;
    private JButton openNewAccBtn;
    private JButton loginBtn;
    private JTextField tfAccountNumber;
    private JTextField tfPin;

    private JLabel newaccIcon;
    private JLabel bankIcon;
    private JLabel bankName;

    // runner
    public static void main(String[] args) {
        GUI guiForm = new GUI(null);
    }

}