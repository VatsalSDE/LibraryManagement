package com.library.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static int dynamicTransactionIdCounter = 1;
    private String transactionId;
    private String bookId;
    private String memberId;
    private String issueDate;
    private String dueDate;
    private String returnDate;
    private double fineAmount;

    public Transaction(String bookId, String memberId, String issueDate) {
        this.transactionId = "T" + dynamicTransactionIdCounter++;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = calculateDueDate(issueDate);
        this.returnDate = null;
        this.fineAmount = 0;
    }

    private String calculateDueDate(String issueDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate issueDate = LocalDate.parse(issueDateString, formatter);
        LocalDate dueDate = issueDate.plusDays(30);
        return dueDate.format(formatter);
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    // Setters
    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    @Override
    public String toString() {
        return String.format("Transaction{id=%s, bookId=%s, memberId=%s, issueDate=%s, dueDate=%s, returnDate=%s, fineAmount=%.2f}",
                transactionId, bookId, memberId, issueDate, dueDate, returnDate, fineAmount);
    }
}