package bankingsystem;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Deposit extends JFrame {

    public Deposit(String accountName, String accountNumber) {

        // JFrame Framework
        super();
        setContentPane(DepositGUI);
        setMinimumSize(new Dimension(650, 340));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Set the account name and number
        lbl_accountName = new JLabel(accountName);
        lbl_accountNumber = new JLabel(accountNumber);

        // Set account name and number in display labels
        displayAccountName.setText(accountName);
        displayAccountNumber.setText(accountNumber);

        // Exit Button Action
        exitBtn.addActionListener(e -> {
            System.out.println("DepositGui closed");
            System.out.println("**********************************");
            dispose();
        });

        // combo box account
        cmb_account.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
                String account = cmb_account.getSelectedItem().toString();
                int account_number = Integer.parseInt(displayAccountNumber.getText());
                String sql = "SELECT * FROM open_account WHERE account_number='" + account_number + "'";
                try {
                    // connection of mysql
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "bank", "12345");
                    PreparedStatement pst = conn.prepareStatement(sql);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        if (cmb_account.getSelectedItem().equals("Savings Account")) {
                            double amount = rs.getDouble("sa_balance");
                            lbl_amount.setText(String.format("%.2f", amount));
                        } else {
                            double amount = rs.getDouble("ca_balance");
                            lbl_amount.setText(String.format("%.2f", amount));
                        }
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        // Deposit button once its clicked
        depositBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int account_number = Integer.parseInt(displayAccountNumber.getText());
                double amount = Double.parseDouble(tfamountDeposit.getText());
                String amount1 = String.format("%.2f", amount);
                double n = Double.parseDouble(amount1);

                // logic for date
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDate = now.format(formatter);

                String sql = "INSERT INTO bank_transaction (bank_transactionaccountnumber, savings_account_amount, checking_account_amount, bank_transaction, transaction_date) VALUES (?, ?, ?, ?, ?)";

                try {
                    // connection of mysql
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "bank", "12345");
                    PreparedStatement pst = conn.prepareStatement(sql);

                    pst.setInt(1, account_number);
                    if (cmb_account.getSelectedItem().equals("Savings Account")) {
                        pst.setDouble(2, n);
                        pst.setDouble(3, 0.00);
                    } else {
                        pst.setDouble(2, 0.00);
                        pst.setDouble(3, n);
                    }
                    pst.setString(4, "DEPOSIT");
                    pst.setString(5, formattedDate);
                    pst.execute();
                    JOptionPane.showMessageDialog(null, cmb_account.getSelectedItem() + " Deposit Complete ");
                    tfamountDeposit.setText("0.00");

                    // Update balance logic
                    String sql1 = "SELECT * FROM open_account WHERE account_number = ?";
                    PreparedStatement pst1 = conn.prepareStatement(sql1);
                    pst1.setInt(1, account_number);
                    ResultSet rs1 = pst1.executeQuery();
                    if (rs1.next()) {
                        double newBalance;
                        if (cmb_account.getSelectedItem().equals("Savings Account")) {
                            double currentBalance = rs1.getDouble("sa_balance");
                            newBalance = currentBalance + n;
                            String sqlUpdateBalance = "UPDATE open_account SET sa_balance = ? WHERE account_number = ?";
                            PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdateBalance);
                            pstUpdate.setDouble(1, newBalance);
                            pstUpdate.setInt(2, account_number);
                            pstUpdate.executeUpdate();
                        } else {
                            double currentBalance = rs1.getDouble("ca_balance");
                            newBalance = currentBalance + n;
                            String sqlUpdateBalance = "UPDATE open_account SET ca_balance = ? WHERE account_number = ?";
                            PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdateBalance);
                            pstUpdate.setDouble(1, newBalance);
                            pstUpdate.setInt(2, account_number);
                            pstUpdate.executeUpdate();
                        }
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    Logger.getLogger(Deposit.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        });

        setVisible(true);
    }

    // Variables declaration
    private JPanel DepositGUI;
    private JComboBox<String> cmb_account;
    private JTextField tfamountDeposit;
    private JButton depositBtn;
    public JLabel displayAccountName;
    public JLabel displayAccountNumber;
    private JLabel lbl_amount;
    private JButton exitBtn;
    private JLabel lbl_accountName;
    private JLabel lbl_accountNumber;
    private JLabel lbl_accountBalance;
    private JLabel lbl_amountDeposit;
    private JLabel depositText;
    private JLabel depositIcon;
    private JLabel pesoicon;

}