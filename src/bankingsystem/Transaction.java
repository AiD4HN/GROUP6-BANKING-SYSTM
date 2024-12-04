package bankingsystem;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Transaction extends JFrame {

    public Transaction(String accountName, String accountNumber) {
        // JFrame Framework
        super();
        setContentPane(bankTransactionPanel);
        setMinimumSize(new Dimension(700, 450));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Set the account name and number
        lbl_accountName = new JLabel(accountName);
        lbl_accountNumber = new JLabel(accountNumber);

        displayAccountName.setText(accountName);
        displayAccountNumber.setText(accountNumber);

        // Exit Button Action
        exitBtn.addActionListener(e -> {
            System.out.println("TransactionGui closed");
            System.out.println("**********************************");
            dispose();
        });

        // Combo box account
        cmb_account.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
                int account_number = Integer.parseInt(displayAccountNumber.getText());
                String selectedAccount = cmb_account.getSelectedItem().toString();
                loadTransactionData(account_number, selectedAccount);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        initTable();
        setVisible(true);
    }

    private void initTable() {
        final String[] BankTableColumns = {
                "Date", "Transaction Type", "Amount", "Balance After Transaction"
        };

        DefaultTableModel MainTableModel = (DefaultTableModel) tbl_data.getModel();
        MainTableModel.setRowCount(0); // Clear existing rows

        for (String BankTableColumn : BankTableColumns) {
            MainTableModel.addColumn(BankTableColumn);
        }
    }

    private void loadTransactionData(int account_number, String selectedAccount) {
        String sql = "SELECT transaction_date, bank_transaction, " +
                (selectedAccount.equals("Savings Account") ? "savings_account_amount" : "checking_account_amount") +
                " FROM bank_transaction WHERE bank_transactionaccountnumber = ?";

        double currentBalance = 0.0;

        try {
            // Connection to MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "bank", "12345");
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, account_number);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tbl_data.getModel();
            model.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                String transactionDate = rs.getString("transaction_date");
                String transactionType = rs.getString("bank_transaction");
                double amount = rs.getDouble(selectedAccount.equals("Savings Account") ? "savings_account_amount" : "checking_account_amount");

                // Update current balance based on transaction type
                if (transactionType.equals("DEPOSIT")) {
                    currentBalance += amount;
                } else if (transactionType.equals("WITHDRAW")) {
                    currentBalance -= amount;
                }

                // Add row to the table
                model.addRow(new Object[]{transactionDate, transactionType, amount, String.format("%.2f", currentBalance)});
            }

            // Update the lbl_amount with the current balance
            lbl_amount.setText(String.format("%.2f", currentBalance));

        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Variables declaration
    private JPanel bankTransactionPanel;
    private JTable tbl_data;
    private JButton exitBtn;
    private JComboBox<String> cmb_account;
    public JLabel displayAccountName;
    public JLabel displayAccountNumber;
    private DefaultTableModel MainTableModel;
    private JLabel lbl_amount; // Ensure this is initialized in your GUI
    private JLabel lbl_accountName;
    private JLabel lbl_accountBalance;
    private JLabel pesoIcon;
    private JLabel lbl_accountNumber;
}