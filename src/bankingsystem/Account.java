package bankingsystem;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Account extends JFrame {

    public Account(String accountName, String accountNumber) {

        // JFrame Framework
        setContentPane(Account);
        setMinimumSize(new Dimension(600, 450));
        setLocationRelativeTo(Account);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Set the account name and number
        lbl_accountName = new JLabel(accountName);
        lbl_accountNumber = new JLabel(accountNumber);

        displayAccountName.setText(accountName);
        displayAccountNumber.setText(accountNumber);

        // Exit Button Action
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Account closed");
                System.out.println("**********************************");
                dispose();
            }
        });

        // combo box account
        cmb_account.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
                String account = cmb_account.getSelectedItem().toString();
                int account_number = Integer.parseInt(displayAccountNumber.getText());
                String sql = "SELECT * FROM open_account WHERE account_number='"+account_number+"'";
                try{
                    // connection of mysql
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "bank", "12345");
                    PreparedStatement pst = conn.prepareStatement(sql);
                    ResultSet rs = pst.executeQuery();
                    if(rs.next()) {
                            if (cmb_account.getSelectedItem() == "Savings Account") {
                                double amount = rs.getDouble("sa_balance");
                                lbl_amount.setText(String.format("%.2f", amount));
                            } else {
                                double amount = rs.getDouble("ca_balance");
                                lbl_amount.setText(String.format("%.2f", amount));
                            }
                    }
                }catch (ClassNotFoundException | SQLException e){
                    JOptionPane.showMessageDialog(null, e);
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        // Deposit button to open deposit module
        depositBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                String name = displayAccountName.getText();
                String acctnumber = displayAccountNumber.getText();
                new Deposit(name, acctnumber);
            }
        });

        // Withdraw button to open withdraw module
        withdrawBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = displayAccountName.getText();
                String acctnumber = displayAccountNumber.getText();
                new Withdraw(name, acctnumber);
            }
        });

        // Transaction button to open transaction module
        transactionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = displayAccountName.getText();
                String acctnumber = displayAccountNumber.getText();
                new Transaction(name, acctnumber);
            }
        });

        setVisible(true);

    }

    // Variables declaration
    private JPanel Account;
    private JButton exitBtn;
    private JButton depositBtn;
    private JButton withdrawBtn;
    private JButton transactionBtn;
    public  JLabel displayAccountName;
    public  JLabel displayAccountNumber;
    private JComboBox cmb_account;
    private JLabel lbl_amount;

    private JLabel bankIcon;
    private JLabel bankName;
    private JLabel lbl_accountName;
    private JLabel lbl_accountNumber;
    private JLabel lbl_accountBalance;
    private JLabel displayBalance;


}