package com.example.a17019181.c300_ocbcmobile.Model;

public class Conf {
    private String atm;
    private int transaction;
    private int amount;
    private String date;

    public String getAtm() {
        return atm;
    }

    public int getTransaction() {
        return transaction;
    }

    public int getAmount() {
        return amount;
    }

    public String getDate(){
        return date;
    }

    public int getDay() {
        String[] splitDate = getDate().split(" ");
        int day = Integer.parseInt(splitDate[0]);
        return day;
    }

    public String getMonth() {
        String[] splitDate = date.split(" ");
        String month = splitDate[1];
        return month;
    }

}
