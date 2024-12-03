package bankingsystem;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Transaction extends JFrame {

    public Transaction(String accountName, String accountNumber) {
        super();
        setContentPane(bankTransactionPanel);
        setMinimumSize(new Dimension(600, 450));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Set the account name and number
        lbl_accountName = new JLabel(accountName);
        lbl_accountNumber = new JLabel(accountNumber);

        displayAccountName.setText(accountName);
        displayAccountNumber.setText(accountNumber);

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("TransactionGui closed");
                System.out.println("**********************************");
                dispose();
            }
        });

        cmb_account.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
                int account_number = Integer.parseInt(displayAccountNumber.getText());
                String sql;

                // Display the balance for the selected account type
                if (cmb_account.getSelectedItem().equals("Savings Account")) {
                    sql = "SELECT sa_balance FROM open_account WHERE account_number = ?";
                    lbl_amount.setText(""); // Clear previous amount

                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "bank", "12345");
                        PreparedStatement pst = conn.prepareStatement(sql);
                        pst.setInt(1, account_number);
                        ResultSet rs = pst.executeQuery();

                        if (rs.next()) {
                            double balance = rs.getDouble("sa_balance");
                            lbl_amount.setText(String.format("%.2f", balance));
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                        JOptionPane.showMessageDialog(null, e);
                    }

                    // SQL to fetch transaction data
                    sql = "SELECT transaction_date, bank_transaction, savings_account_amount FROM bank_transaction WHERE savings_account_amount > 0 AND bank_transactionaccountnumber = ?";
                } else {
                    sql = "SELECT ca_balance FROM open_account WHERE account_number = ?";
                    lbl_amount.setText("");

                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "bank", "12345");
                        PreparedStatement pst = conn.prepareStatement(sql);
                        pst.setInt(1, account_number);
                        ResultSet rs = pst.executeQuery();

                        if (rs.next()) {
                            double balance = rs.getDouble("ca_balance");
                            lbl_amount.setText(String.format("%.2f", balance));
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                        JOptionPane.showMessageDialog(null, e);
                    }

                    // SQL to fetch transaction data
                    sql = "SELECT transaction_date, bank_transaction, checking_account_amount FROM bank_transaction WHERE checking_account_amount > 0 AND bank_transactionaccountnumber = ?";
                }

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "bank", "12345");
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, account_number);
                    ResultSet rs = pst.executeQuery();

                    tbl_data.setModel(DbUtils.resultSetToTableModel(rs));

                } catch (ClassNotFoundException | SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        initTable();
        setVisible(true);
    }

    private void initTable() {
        final String[] BankTableColumns = {
                "Date", "Transaction Type", "Amount"
        };

        MainTableModel = (DefaultTableModel) tbl_data.getModel();
        MainTableModel.setRowCount(0);

        for (String BankTableColumn : BankTableColumns) {
            MainTableModel.addColumn(BankTableColumn);
        }
    }

    private JPanel bankTransactionPanel;
    private JTable tbl_data;
    private JButton exitBtn;
    private JComboBox<String> cmb_account;
    public JLabel displayAccountName;
    public JLabel displayAccountNumber;
    private DefaultTableModel MainTableModel;
    private JLabel lbl_amount;

    private JLabel lbl_accountName;
    private JLabel lbl_accountBalance;
    private JLabel pesoIcon;
    private JLabel lbl_accountNumber;

}