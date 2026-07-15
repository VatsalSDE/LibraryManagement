package com.library.service;

import com.library.enums.BookStatus;
import com.library.enums.Category;
import com.library.exceptions.BookNotFoundException;
import com.library.exceptions.InvalidFineException;
import com.library.exceptions.InvalidMemberException;
import com.library.exceptions.MemberNotFoundException;
import com.library.managers.BookManager;
import com.library.managers.MemberManager;
import com.library.managers.TransactionManager;
import com.library.models.Book;
import com.library.models.Member;
import com.library.models.Transaction;

import java.util.List;

public class LibraryService {

    private final BookManager bookManager;

    private final MemberManager memberManager;

    private final TransactionManager transactionManager;

    public LibraryService(BookManager bookManager, MemberManager memberManager, TransactionManager transactionManager) {

        this.bookManager = bookManager;

        this.memberManager = memberManager;

        this.transactionManager = transactionManager;

    }

    // Critical request flow is synchronized so one client cannot interleave with another.
    public synchronized String addBook(String isbn, String title, String author, Category category) {

        String bookId = bookManager.addBook(isbn, title, author, category);

        return "Book added successfully: " + bookId;
    }

    public synchronized String removeBook(String bookId) throws BookNotFoundException {

        Book book = bookManager.getBookById(bookId);

        if (book.getBookStatus() == BookStatus.ISSUED || transactionManager.hasActiveTransactionForBook(bookId)) {

            throw new IllegalStateException("Cannot remove a book that is currently issued.");

        }

        bookManager.removeBook(bookId);

        return "Book removed successfully!";
    }

    public synchronized String addMember(String name, String email, String phoneNumber) throws InvalidMemberException {

        return "Member added successfully:\n" + memberManager.addMember(name, email, phoneNumber);
    }

    public synchronized String removeMember(String memberId) throws MemberNotFoundException {

        Member member = memberManager.getMemberById(memberId);

        if (member.getDueAmount() > 0) {

            throw new IllegalStateException("Cannot remove member with pending dues.");

        }

        if (transactionManager.hasActiveTransactionForMember(memberId)) {

            throw new IllegalStateException("Cannot remove member with active borrowed books.");

        }

        memberManager.removeMember(memberId);

        return "Member removed successfully!";
    }

    public synchronized String issueBook(String bookId, String memberId, String issueDate)
            throws BookNotFoundException, MemberNotFoundException {

        Book book = bookManager.getBookById(bookId);

        if (book.getBookStatus() != BookStatus.AVAILABLE) {

            throw new IllegalStateException("Book is not available.");

        }

        if (!memberManager.canBorrow(memberId)) {

            throw new IllegalStateException("Member has pending dues.");

        }

        String result = transactionManager.issueBook(bookId, memberId, issueDate);

        bookManager.markAsIssued(bookId);

        return "Book issued successfully!\n" + result;
    }

    public synchronized String returnBook(String bookId, String memberId, String returnDate)
            throws BookNotFoundException, MemberNotFoundException, InvalidFineException {

        Transaction transaction = transactionManager.returnBook(bookId, memberId, returnDate);

        bookManager.markAsAvailable(bookId);

        StringBuilder response = new StringBuilder();
        response.append("Book returned successfully!\n");
        response.append(transaction);

        if (transaction.getFineAmount() > 0) {

            response.append("\nFine Amount: ₹").append(transaction.getFineAmount());

        }

        return response.toString();
    }

    public synchronized String payFine(String memberId) throws MemberNotFoundException {

        Member member = memberManager.getMemberById(memberId);

        if (member.getDueAmount() == 0) {

            return "No pending dues.";

        }

        memberManager.clearDue(memberId);

        return "Fine paid successfully!";
    }

    public synchronized String undoLastTransaction() throws BookNotFoundException, MemberNotFoundException, InvalidFineException {

        transactionManager.undoLastTransaction();

        return "Last transaction undone successfully!";
    }

    public synchronized String listAllBooks() {

        return formatBooks(bookManager.getAllBooks());
    }

    public synchronized String listAllTransactions() {

        return formatTransactions(transactionManager.getAllTransactions());
    }

    public synchronized String viewMemberTransactions(String memberId) throws MemberNotFoundException {

        Member member = memberManager.getMemberById(memberId);
        List<Transaction> transactions = transactionManager.getTransactionsByMember(memberId);

        StringBuilder response = new StringBuilder();
        response.append("Transactions for ").append(member.getName()).append('\n');
        response.append("Due Amount: ₹").append(member.getDueAmount()).append('\n');
        response.append(transactions.isEmpty() ? "No transactions." : formatTransactions(transactions));

        return response.toString();
    }

    public synchronized String searchByTitle(String title) {

        return formatBooks(bookManager.searchByTitle(title));
    }

    public synchronized String searchByAuthor(String author) {

        return formatBooks(bookManager.searchByAuthor(author));
    }

    public synchronized String searchByCategory(String categoryText) {

        Category category = Category.valueOf(categoryText.trim().toUpperCase());
        return formatBooks(bookManager.searchByCategory(category));
    }

    public synchronized String listAllAuthors() {

        if (bookManager.getAllAuthors().isEmpty()) {

            return "No authors in library.";

        }

        StringBuilder response = new StringBuilder("Authors:\n");
        for (String author : bookManager.getAllAuthors()) {

            response.append("- ").append(author).append('\n');

        }

        return response.toString().trim();
    }

    private String formatBooks(List<Book> books) {

        if (books.isEmpty()) {

            return "No books found.";

        }

        StringBuilder response = new StringBuilder();

        for (Book book : books) {

            response.append(book).append('\n');

        }

        return response.toString().trim();
    }

    private String formatTransactions(List<Transaction> transactions) {

        if (transactions.isEmpty()) {

            return "No transactions.";

        }

        StringBuilder response = new StringBuilder();

        for (Transaction transaction : transactions) {

            response.append(transaction).append('\n');

        }

        return response.toString().trim();
    }
}