// AdminUI.java

package com.library.ui;

import com.library.managers.BookManager;

import com.library.managers.MemberManager;

import com.library.managers.TransactionManager;

import com.library.models.Book;

import com.library.models.Member;

import com.library.models.Transaction;

import com.library.enums.BookStatus;

import com.library.exceptions.*;

import com.library.utilities.InputHandler;

import java.time.format.DateTimeParseException;

import java.util.List;

import java.util.Set;

public class AdminUI {

    private BookManager bookManager;

    private MemberManager memberManager;

    private TransactionManager transactionManager;

    public AdminUI(BookManager bookManager, MemberManager memberManager, TransactionManager transactionManager) {

        this.bookManager = bookManager;

        this.memberManager = memberManager;

        this.transactionManager = transactionManager;

    }

    public void displayAdminMenu() {

        boolean inAdmin = true;

        while (inAdmin) {

            System.out.println("\n========== ADMIN MENU ==========");

            System.out.println("1. Add Book");

            System.out.println("2. Remove Book");

            System.out.println("3. Search Book");

            System.out.println("4. View All Books");

            System.out.println("5. Add Member");

            System.out.println("6. Remove Member");

            System.out.println("7. Issue Book to Member");

            System.out.println("8. Return Book");

            System.out.println("9. View All Transactions");

            System.out.println("10. View Member Transactions");

            System.out.println("11. Undo Last Transaction");

            System.out.println("12. View All Authors");

            System.out.println("13. Back to Main Menu");

            System.out.print("Enter your choice: ");

            int choice = InputHandler.getIntInput();

            switch (choice) {

                case 1:

                    addBook();

                    break;

                case 2:

                    removeBook();

                    break;

                case 3:

                    searchBook();

                    break;

                case 4:

                    viewAllBooks();

                    break;

                case 5:

                    addMember();

                    break;

                case 6:

                    removeMember();

                    break;

                case 7:

                    issueBook();

                    break;

                case 8:

                    returnBook();

                    break;

                case 9:

                    viewAllTransactions();

                    break;

                case 10:

                    viewMemberTransactions();

                    break;

                case 11:

                    undoLastTransaction();

                    break;

                case 12:

                    viewAllAuthors();

                    break;

                case 13:

                    inAdmin = false;

                    break;

                default:

                    System.out.println("Invalid choice. Try again.");

            }

        }

    }

    private void addBook() {

        System.out.print("Enter ISBN: ");

        String isbn = InputHandler.getStringInput();

        System.out.print("Enter Title: ");

        String title = InputHandler.getStringInput();

        System.out.print("Enter Author: ");

        String author = InputHandler.getStringInput();

        try {

            String result = bookManager.addBook(isbn, title, author, InputHandler.getCategoryInput());

            System.out.println("Book added successfully!");

            System.out.println(result);

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());

        }

    }

    private void removeBook() {

        System.out.print("Enter Book ID to remove: ");

        String bookId = InputHandler.getStringInput();

        try {

            Book book = bookManager.getBookById(bookId);

            if (book.getBookStatus() == BookStatus.ISSUED || transactionManager.hasActiveTransactionForBook(bookId)) {

                System.out.println("Error: Cannot remove a book that is currently issued.");

                return;

            }

            bookManager.removeBook(bookId);

            System.out.println("Book removed successfully!");

        } catch (BookNotFoundException e) {

            System.out.println("Error: " + e.getMessage());

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

    private void viewAllBooks() {

        List<Book> books = bookManager.getAllBooks();

        if (books.isEmpty()) {

            System.out.println("No books in library.");

        } else {

            System.out.println("\n========== ALL BOOKS ==========");

            for (Book book : books) {

                System.out.println(book);

            }

        }

    }

    private void addMember() {

        System.out.print("Enter Name: ");

        String name = InputHandler.getStringInput();

        System.out.print("Enter Email: ");

        String email = InputHandler.getStringInput();

        System.out.print("Enter Phone Number (10 digits): ");

        String phone = InputHandler.getStringInput();

        try {

            String result = memberManager.addMember(name, email, phone);

            System.out.println("Member added successfully!");

            System.out.println(result);

        } catch (InvalidMemberException e) {

            System.out.println("Error: " + e.getMessage());

        }

    }

    private void removeMember() {

        System.out.print("Enter Member ID to remove: ");

        String memberId = InputHandler.getStringInput();

        try {

            Member member = memberManager.getMemberById(memberId);

            if (member.getDueAmount() > 0) {

                System.out.println("Error: Cannot remove member with pending dues.");

                return;

            }

            if (transactionManager.hasActiveTransactionForMember(memberId)) {

                System.out.println("Error: Cannot remove member with active borrowed books.");

                return;

            }

            memberManager.removeMember(memberId);

            System.out.println("Member removed successfully!");

        } catch (MemberNotFoundException e) {

            System.out.println("Error: " + e.getMessage());

        }

    }

    private void issueBook() {

        System.out.print("Enter Book ID: ");

        String bookId = InputHandler.getStringInput();

        System.out.print("Enter Member ID: ");

        String memberId = InputHandler.getStringInput();

        System.out.print("Enter Issue Date (dd-MM-yyyy): ");

        String issueDate = InputHandler.getStringInput();

        try {

            Book book = bookManager.getBookById(bookId);

            if (book.getBookStatus() != BookStatus.AVAILABLE) {

                System.out.println("Error: Book is not available.");

                return;

            }

            if (!memberManager.canBorrow(memberId)) {

                System.out.println("Error: Member has pending dues.");

                return;

            }

            String result = transactionManager.issueBook(bookId, memberId, issueDate);

            bookManager.markAsIssued(bookId);

            System.out.println("Book issued successfully!");

            System.out.println(result);

        } catch (BookNotFoundException | MemberNotFoundException e) {

            System.out.println("Error: " + e.getMessage());

        } catch (DateTimeParseException e) {

            System.out.println("Error: Invalid date format. Use dd-MM-yyyy.");

        }

    }

    private void returnBook() {

        System.out.print("Enter Book ID: ");

        String bookId = InputHandler.getStringInput();

        System.out.print("Enter Member ID: ");

        String memberId = InputHandler.getStringInput();

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

    private void viewAllTransactions() {

        List<Transaction> transactions = transactionManager.getAllTransactions();

        if (transactions.isEmpty()) {

            System.out.println("No transactions.");

        } else {

            System.out.println("\n========== ALL TRANSACTIONS ==========");

            for (Transaction transaction : transactions) {

                System.out.println(transaction);

            }

        }

    }

    private void viewMemberTransactions() {

        System.out.print("Enter Member ID: ");

        String memberId = InputHandler.getStringInput();

        try {

            Member member = memberManager.getMemberById(memberId);

            List<Transaction> transactions = transactionManager.getTransactionsByMember(memberId);

            System.out.println("\n========== TRANSACTIONS FOR " + member.getName() + " ==========");

            System.out.println("Due Amount: ₹" + member.getDueAmount());

            if (transactions.isEmpty()) {

                System.out.println("No transactions.");

            } else {

                for (Transaction transaction : transactions) {

                    System.out.println(transaction);

                }

            }

        } catch (MemberNotFoundException e) {

            System.out.println("Error: " + e.getMessage());

        }

    }

    private void undoLastTransaction() {

        try {

            transactionManager.undoLastTransaction();

            System.out.println("Last transaction undone successfully!");

        } catch (BookNotFoundException | MemberNotFoundException | InvalidFineException e) {

            System.out.println("Error: " + e.getMessage());

        }

    }

    private void viewAllAuthors() {

        Set<String> authors = bookManager.getAllAuthors();

        if (authors.isEmpty()) {

            System.out.println("No authors in library.");

        } else {

            System.out.println("\n========== ALL AUTHORS ==========");

            for (String author : authors) {

                System.out.println("- " + author);

            }

        }

    }

}
