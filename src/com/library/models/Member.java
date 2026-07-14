package com.library.models;

public class Member {

    private static int dynamicMemberIdCounter = 1;

    private String memberId;

    private String name;

    private String email;

    private String phoneNumber;

    private double dueAmount;

    // now this constructor is added for when the text loaded from the file to the collection at the time of loading so the member id needs to be the same so that's why the reason is that
    public Member(String memberId, String name, String email, String phoneNumber, double dueAmount) {

        this.memberId = memberId;

        this.name = name;

        this.email = email;

        this.phoneNumber = phoneNumber;

        this.dueAmount = dueAmount;

        if (memberId.startsWith("M")) {

            int id = Integer.parseInt(memberId.substring(1));

            if (id >= dynamicMemberIdCounter) {

                dynamicMemberIdCounter = id + 1;

            }

        }

    }

    // constructor chaining here this constructor will be used when like new book to be added
    public Member(String name, String email, String phoneNumber) {

        this("M" + dynamicMemberIdCounter++, name, email, phoneNumber, 0);

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

        this.name = name;

    }

    public void setEmail(String email) {

        this.email = email;

    }

    public void setPhoneNumber(String phoneNumber) {

        this.phoneNumber = phoneNumber;

    }

    public void setDueAmount(double dueAmount) {

        this.dueAmount = dueAmount;

    }

    // Custom Methods

    public boolean isDueCleared() {

        if (dueAmount == 0) {

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
