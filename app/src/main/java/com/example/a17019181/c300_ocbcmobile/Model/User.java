package com.example.a17019181.c300_ocbcmobile.Model;

import java.io.Serializable;

public class User implements Serializable {

    private String androiduid;
    private double balance;
    private String email;
    private String password;
    private String username;
    private double preconfigure;
    private String accountType;
    private String firstName;
    private String lastName;
    private double frequent;
    private Object configuration;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public double getFrequent() {
        return frequent;
    }

    public void setFrequent(double frequent) {
        this.frequent = frequent;
    }

    public double getPreconfigure() {
        return preconfigure;
    }

    public void setPreconfigure(double preconfigure) {
        this.preconfigure = preconfigure;
    }

    public String getAndroiduid() {
        return androiduid;
    }

    public void setAndroiduid(String androiduid) {
        this.androiduid = androiduid;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Object getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Object configuration) {
        this.configuration = configuration;
    }
}
