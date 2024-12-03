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

public class Withdraw extends JFrame {

    public Withdraw(String accountName, String accountNumber) {
        super();
        setContentPane(WithdrawGUI);
        setMinimumSize(new Dimension(650, 340));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Set the account name and number
        lbl_accountName = new JLabel(accountName);
        lbl_accountNumber = new JLabel(accountNumber);

        displayAccountName.setText(accountName);
        displayAccountNumber.setText(accountNumber);

        // Exit Button Action
        exitBtn.addActionListener(e -> {
            System.out.println("WithdrawGui closed");
            System.out.println("**********************************");
            dispose();
        });

        cmb_account.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
                String account = cmb_account.getSelectedItem().toString();
                int account_number = Integer.parseInt(displayAccountNumber.getText());
                String sql = "SELECT * FROM open_account WHERE account_number='" + account_number + "'";
                try {
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

        withdrawBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int account_number = Integer.parseInt(displayAccountNumber.getText());
                double amount = Double.parseDouble(tfamountWithdraw.getText());
                String amount1 = String.format("%.2f", amount);
                double n = Double.parseDouble(amount1);

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDate = now.format(formatter);

                if (cmb_account.getSelectedItem().equals("Savings Account")) {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "bank", "12345");

                        String sql1 = "SELECT * FROM open_account WHERE account_number = ?";
                        PreparedStatement pst1 = conn.prepareStatement(sql1);
                        pst1.setInt(1, account_number);
                        ResultSet rs1 = pst1.executeQuery();

                        if (rs1.next()) {
                            JPasswordField pf = new JPasswordField();
                            int m = JOptionPane.showConfirmDialog(null, pf, "Please enter pin to proceed", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE);

                            if (m == JOptionPane.OK_OPTION) {
                                String password = String.valueOf(pf.getPassword());
                                int pin = Integer.parseInt(password);

                                if (pin != rs1.getInt("user_pin")) {
                                    JOptionPane.showMessageDialog(null, "Invalid PIN");
                                } else {
                                    double n1 = rs1.getDouble("sa_balance");
                                    double sum = n1 - n;

                                    if (sum < 0) {
                                        JOptionPane.showMessageDialog(null, "Insufficient Balance");
                                    } else {
                                        String num = String.format("%.2f", sum);
                                        double sa_amount = Double.parseDouble(num);

                                        try {
                                            String sqlupdatebalance = "UPDATE open_account SET sa_balance = ? WHERE account_number = ?";
                                            PreparedStatement pstupdate = conn.prepareStatement(sqlupdatebalance);
                                            pstupdate.setDouble(1, sa_amount);
                                            pstupdate.setInt(2, account_number);
                                            pstupdate.executeUpdate();

                                            String sql = "INSERT INTO bank_transaction (bank_transactionaccountnumber, savings_account_amount, checking_account_amount, bank_transaction, transaction_date) VALUES (?, ?, ?, ?, ?)";
                                            PreparedStatement pst = conn.prepareStatement(sql);
                                            pst.setInt(1, account_number);
                                            pst.setDouble(2, n);
                                            pst.setDouble(3, 0.00);
                                            pst.setString(4, "WITHDRAW");
                                            pst.setString(5, formattedDate); // Set the transaction date
                                            pst.executeUpdate();
                                            JOptionPane.showMessageDialog(null, "Savings Account Withdrawals Complete");
                                            tfamountWithdraw.setText("0.00");
                                        } catch (SQLException e) {
                                            JOptionPane.showMessageDialog(null, e);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                        Logger.getLogger(Withdraw.class.getName()).log(Level.SEVERE, null, e);
                    }
                } else {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "bank", "12345");

                        String sql1 = "SELECT * FROM open_account WHERE account_number = ?";
                        PreparedStatement pst1 = conn.prepareStatement(sql1);
                        pst1.setInt(1, account_number);
                        ResultSet rs1 = pst1.executeQuery();

                        if (rs1.next()) {
                            JPasswordField pf = new JPasswordField();
                            int m = JOptionPane.showConfirmDialog(null, pf, "Please enter pin to proceed", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE);
                            if (m == JOptionPane.OK_OPTION) {
                                String password = String.valueOf(pf.getPassword());
                                int pin = Integer.parseInt(password);

                                if (pin != rs1.getInt("user_pin")) {
                                    JOptionPane.showMessageDialog(null, "Invalid PIN");
                                } else {
                                    double n1 = rs1.getDouble("ca_balance");
                                    double sum = n1 - n;

                                    if (sum < 0) {
                                        JOptionPane.showMessageDialog(null, "Insufficient Balance");
                                    } else {
                                        String num = String.format("%.2f", sum);
                                        double ca_amount = Double.parseDouble(num);
                                        try {
                                            String sqlupdatebalance = "UPDATE open_account SET ca_balance = ? WHERE account_number = ?";
                                            PreparedStatement pstupdate = conn.prepareStatement(sqlupdatebalance);
                                            pstupdate.setDouble(1, ca_amount);
                                            pstupdate.setInt(2, account_number);
                                            pstupdate.executeUpdate();

                                            String sql = "INSERT INTO bank_transaction (bank_transactionaccountnumber, savings_account_amount, checking_account_amount, bank_transaction, transaction_date) VALUES (?, ?, ?, ?, ?)";
                                            PreparedStatement pst = conn.prepareStatement(sql);
                                            pst.setInt(1, account_number);
                                            pst.setDouble(2, 0.00);
                                            pst.setDouble(3, n);
                                            pst.setString(4, "WITHDRAW");
                                            pst.setString(5, formattedDate);
                                            pst.executeUpdate();
                                            JOptionPane.showMessageDialog(null, "Checking Account Withdrawals Complete");
                                            tfamountWithdraw.setText("0.00");
                                        } catch (SQLException e) {
                                            JOptionPane.showMessageDialog(null, e);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                        Logger.getLogger(Withdraw.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        });

        setVisible(true);
    }


    private JLabel displayAccountName;
    private JLabel displayAccountNumber;
    private JComboBox<String> cmb_account;
    private JLabel lbl_amount;
    private JTextField tfamountWithdraw;
    private JButton withdrawBtn;
    private JButton exitBtn;
    private JPanel WithdrawGUI;

    private JLabel lbl_accountName;
    private JLabel lbl_accountNumber;
    private JLabel withdrawText;
    private JLabel depositIcon;
    private JLabel lbl_amountWithdraw;
    private JLabel pesoicon;
    private JLabel lbl_accountBalance;

}