package com.example.daniel.lloyds_bank_team2_app;

/**
 * Created by danielbaranowski on 06/02/15.
 */
public class Account {
    private int accountNumber;
    private String sortCode;
    private String accountName;
    private String accountType;
    private double accountBalance;
    private double availableBalance;
    private double overdraft;
    private int ownerId;

    public Account(int accountNumber, String sortCode, String accountName, String accountType, double accountBalance, double availableBalance, double overdraft, int ownerId) {
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.accountName = accountName;
        this.accountType = accountType;
        this.accountBalance = accountBalance;
        this.availableBalance = availableBalance;
        this.overdraft = overdraft;
        this.ownerId = ownerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public double getOverdraft() {
        return overdraft;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getSortCode() {
        return sortCode;
    }

    public int getAccountNumber() {
        return accountNumber;
    }
}
