package com.library.managers;

import com.library.models.Member;
import com.library.models.Transaction;
import com.library.exceptions.BookNotFoundException;
import com.library.exceptions.MemberNotFoundException;
import com.library.exceptions.InvalidFineException;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Stack;
import java.util.List;

public class TransactionManager {
    private ArrayList<Transaction> transactions;
    private Stack<Transaction> undoStack;
    private MemberManager memberManager;
    private BookManager bookManager;

    public TransactionManager(MemberManager memberManager, BookManager bookManager) {
        this.transactions = new ArrayList<>();
        this.undoStack = new Stack<>();
        this.memberManager = memberManager;
        this.bookManager = bookManager;
    }

    public String issueBook(String bookId, String memberId, String issueDate) {
        Transaction transaction = new Transaction(bookId, memberId, issueDate);
        transactions.add(transaction);
        undoStack.push(transaction);
        return transaction.toString();
    }

    public Transaction returnBook(String bookId, String memberId, String returnDate) throws BookNotFoundException, MemberNotFoundException, InvalidFineException {
        Transaction transaction = null;

        for (Transaction t : transactions) {
            if (t.getBookId().equals(bookId) && t.getMemberId().equals(memberId) && t.getReturnDate() == null) {
                transaction = t;
                break;
            }
        }

        if (transaction == null) {
            throw new BookNotFoundException("Transaction not found for this book and member");
        }

        transaction.setReturnDate(returnDate);
        double fine = calculateFine(transaction.getDueDate(), returnDate);

        if (fine > 0) {
            transaction.setFineAmount(fine);
            memberManager.updateDue(memberId, fine);
        }

        undoStack.push(transaction);
        return transaction;
    }

    public List<Transaction> getTransactionsByMember(String memberId) {
        List<Transaction> memberTransactions = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.getMemberId().equals(memberId)) {
                memberTransactions.add(transaction);
            }
        }

        return memberTransactions;
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public void undoLastTransaction() throws BookNotFoundException, MemberNotFoundException, InvalidFineException {
        if (undoStack.isEmpty()) {
            System.out.println("No transactions to undo");
            return;
        }

        Transaction transaction = undoStack.pop();

        // Remove from transactions ArrayList
        transactions.remove(transaction);

        // If return transaction (has returnDate), reverse it
        if (transaction.getReturnDate() != null) {
            transaction.setReturnDate(null);
            transaction.setFineAmount(0);

            // If fine was added, remove it from member
            if (transaction.getFineAmount() > 0) {
                Member member = memberManager.getMemberById(transaction.getMemberId());
                member.setDueAmount(member.getDueAmount() - transaction.getFineAmount());
            }
        }

        // Mark book as available again
        bookManager.markAsAvailable(transaction.getBookId());
    }

    private double calculateFine(String dueDate, String returnDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate due = LocalDate.parse(dueDate, formatter);
        LocalDate returned = LocalDate.parse(returnDate, formatter);

        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(due, returned);

        if (daysLate > 0) {
            return daysLate * 10;  // ₹10 per day late
        }

        return 0;
    }

    public void saveToFile(String filename) throws IOException {
        FileWriter fw = new FileWriter(filename);
        for (Transaction transaction : transactions) {
            String line = transaction.getTransactionId() + "," + transaction.getBookId() + ","
                    + transaction.getMemberId() + "," + transaction.getIssueDate() + ","
                    + transaction.getDueDate() + "," +
                    (transaction.getReturnDate() != null ? transaction.getReturnDate() : "null") + ","
                    + transaction.getFineAmount();
            fw.write(line + "\n");
        }
        fw.close();
        System.out.println("Transactions saved to " + filename);
    }

    public void loadFromFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {

            System.out.println("No existing transactions file found. Starting fresh.");

            return;
        }

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length < 7) continue;

            String transactionId = parts[0];
            String bookId = parts[1];
            String memberId = parts[2];
            String issueDate = parts[3];
            String dueDate = parts[4];
            String returnDate = parts[5].equals("null") ? null : parts[5];
            double fineAmount = Double.parseDouble(parts[6]);

            Transaction transaction = new Transaction(bookId, memberId, issueDate);
            if (returnDate != null) {
                transaction.setReturnDate(returnDate);
            }
            transaction.setFineAmount(fineAmount);
            transactions.add(transaction);
        }
        br.close();
        System.out.println("Transactions loaded from " + filename);
    }
}