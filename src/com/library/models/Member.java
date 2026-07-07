package com.library.models;

public class Member {
    private static int dynamicMemberIdCounter = 1;
    private String memberId;
    private String name;
    private String email;
    private String phoneNumber;
    private double dueAmount;

    // Constructor
    public Member(String name, String email, String phoneNumber) {
        this.memberId="M"+dynamicMemberIdCounter++;
        this.name=name;
        this.email=email;
        this.phoneNumber=phoneNumber;
        dueAmount=0;
    }

    // Getters
    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getDueAmount() {
        return dueAmount;
    }

    // Setters
    public void setName(String name) {
        this.name=name;
    }

    public void setEmail(String email) {
        this.email=email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    public void setDueAmount(double dueAmount) {
        this.dueAmount=dueAmount;
    }

    // Custom Methods
    public boolean isDueCleared() {
        if(dueAmount==0){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Member{id=%s, name=%s, email=%s, phoneNumber=%s, dueAmount=%s}",
                memberId, name, email, phoneNumber, dueAmount);
    }
}