// MemberUI.java

package com.library.ui;

import com.library.managers.BookManager;

import com.library.managers.MemberManager;

import com.library.managers.TransactionManager;

import com.library.models.Book;

import com.library.models.Transaction;

import com.library.enums.BookStatus;

import com.library.exceptions.*;

import com.library.utilities.InputHandler;

import java.time.format.DateTimeParseException;

import java.util.List;

public class MemberUI {

    private BookManager bookManager;

    private MemberManager memberManager;

    private TransactionManager transactionManager;

    public MemberUI(BookManager bookManager, MemberManager memberManager, TransactionManager transactionManager) {

        this.bookManager = bookManager;

        this.memberManager = memberManager;

        this.transactionManager = transactionManager;

    }

    public void displayMemberMenu() {

        boolean inMember = true;

        while (inMember) {

            System.out.println("\n========== MEMBER MENU ==========");

            System.out.println("1. Search Book");

            System.out.println("2. Borrow Book");

            System.out.println("3. Return Book");

            System.out.println("4. Pay Fine");

            System.out.println("5. View My Transactions");

            System.out.println("6. Back to Main Menu");

            System.out.print("Enter your choice: ");

            int choice = InputHandler.getIntInput();

            switch (choice) {

                case 1:

                    searchBook();

                    break;

                case 2:

                    borrowBook();

                    break;

                case 3:

                    returnBook();

                    break;

                case 4:

                    payFine();

                    break;

                case 5:

                    viewTransactions();

                    break;

                case 6:

                    inMember = false;

                    break;

                default:

                    System.out.println("Invalid choice. Try again.");

            }

        }

    }

    private void searchBook() {

        System.out.println("\nSearch By:");

        System.out.println("1. Title");

        System.out.println("2. Author");

        System.out.println("3. Category");

        System.out.print("Enter your choice: ");

        int choice = InputHandler.getIntInput();

        List<Book> results = null;

        try {

            switch (choice) {

                case 1:

                    System.out.print("Enter Title: ");

                    results = bookManager.searchByTitle(InputHandler.getStringInput());

                    break;

                case 2:

                    System.out.print("Enter Author: ");

                    results = bookManager.searchByAuthor(InputHandler.getStringInput());

                    break;

                case 3:

                    results = bookManager.searchByCategory(InputHandler.getCategoryInput());

                    break;

            }

        } catch (IllegalArgumentException e) {

            System.out.println("Error: " + e.getMessage());

            return;

        }

        if (results != null && !results.isEmpty()) {

            System.out.println("\nSearch Results:");

            for (Book book : results) {

                System.out.println(book);

            }

        } else {

            System.out.println("No books found.");

        }

    }

    private void borrowBook() {

        System.out.print("Enter Member ID: ");

        String memberId = InputHandler.getStringInput();

        System.out.print("Enter Book ID: ");

        String bookId = InputHandler.getStringInput();

        System.out.print("Enter Issue Date (dd-MM-yyyy): ");

        String issueDate = InputHandler.getStringInput();

        try {

            if (!memberManager.canBorrow(memberId)) {

                System.out.println("Error: You have pending dues. Please pay fine first.");

                return;

            }

            Book book = bookManager.getBookById(bookId);

            if (book.getBookStatus() != BookStatus.AVAILABLE) {

                System.out.println("Error: Book is not available.");

                return;

            }

            String result = transactionManager.issueBook(bookId, memberId, issueDate);

            bookManager.markAsIssued(bookId);

            System.out.println("Book borrowed successfully!");

            System.out.println(result);

        } catch (BookNotFoundException | MemberNotFoundException e) {

            System.out.println("Error: " + e.getMessage());

        } catch (DateTimeParseException e) {

            System.out.println("Error: Invalid date format. Use dd-MM-yyyy.");

        }

    }

    private void returnBook() {

        System.out.print("Enter Member ID: ");

        String memberId = InputHandler.getStringInput();

        System.out.print("Enter Book ID: ");

        String bookId = InputHandler.getStringInput();

        System.out.print("Enter Return Date (dd-MM-yyyy): ");

        String returnDate = InputHandler.getStringInput();

        try {

            Transaction transaction = transactionManager.returnBook(bookId, memberId, returnDate);

            bookManager.markAsAvailable(bookId);

            System.out.println("Book returned successfully!");

            System.out.println(transaction);

            if (transaction.getFineAmount() > 0) {

                System.out.println("Fine Amount: ₹" + transaction.getFineAmount());

            }

        } catch (BookNotFoundException | MemberNotFoundException | InvalidFineException e) {

            System.out.println("Error: " + e.getMessage());

        } catch (DateTimeParseException e) {

            System.out.println("Error: Invalid date format. Use dd-MM-yyyy.");

        }

    }

    private void payFine() {

        System.out.print("Enter Member ID: ");

        String memberId = InputHandler.getStringInput();

        try {

            double dueAmount = memberManager.getMemberById(memberId).getDueAmount();

            if (dueAmount == 0) {

                System.out.println("No pending dues.");

            } else {

                System.out.println("Current Due Amount: ₹" + dueAmount);

                memberManager.clearDue(memberId);

                System.out.println("Fine paid successfully!");

            }

        } catch (MemberNotFoundException e) {

            System.out.println("Error: " + e.getMessage());

        }

    }

    private void viewTransactions() {

        System.out.print("Enter Member ID: ");

        String memberId = InputHandler.getStringInput();

        try {

            memberManager.getMemberById(memberId);

            List<Transaction> transactions = transactionManager.getTransactionsByMember(memberId);

            System.out.println("\n========== YOUR TRANSACTIONS ==========");

            if (transactions.isEmpty()) {

                System.out.println("No transactions.");

            } else {

                for (Transaction transaction : transactions) {

                    System.out.println(transaction);

                }

            }

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());

        }

    }

}
