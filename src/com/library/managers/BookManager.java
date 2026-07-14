package com.library.managers;

import com.library.models.Book;

import com.library.enums.Category;

import com.library.enums.BookStatus;

import com.library.exceptions.BookNotFoundException;

import com.library.exceptions.InvalidBookException;

import java.io.*;

import java.util.*;

public class BookManager {

    private HashMap<String, Book> books;

    public BookManager() {

        this.books = new HashMap<>();

    }

    public String addBook(String isbn, String title, String author, Category category) {

        Book book = new Book(isbn, title, author, category, BookStatus.AVAILABLE);

        books.put(book.getBookId(), book);

        return book.getBookId();

    }

    public void removeBook(String bookId) throws BookNotFoundException {

        if (!books.containsKey(bookId)) {

            throw new BookNotFoundException("Book Not Found");

        } else {

            books.remove(bookId);

        }

    }

    public Book getBookById(String bookId) throws BookNotFoundException {

        if (!books.containsKey(bookId)) {

            throw new BookNotFoundException("Book Not Found");

        } else {

            return books.get(bookId);

        }

    }

    public List<Book> searchByTitle(String title) {

        List<Book> bookList = new ArrayList<>();

        for (Book book : books.values()) {

            if (book.getTitle().equalsIgnoreCase(title)) {

                bookList.add(book);

            }

        }

        return bookList;

    }

    public List<Book> searchByAuthor(String author) {

        List<Book> bookList = new ArrayList<>();

        for (Book book : books.values()) {

            if (book.getAuthor().equalsIgnoreCase(author)) {

                bookList.add(book);

            }

        }

        return bookList;

    }

    public List<Book> searchByCategory(Category category) {

        List<Book> bookList = new ArrayList<>();

        for (Book book : books.values()) {

            if (book.getCategory() == category) {

                bookList.add(book);

            }

        }

        return bookList;

    }

    public List<Book> getAvailableBooks() {

        List<Book> bookList = new ArrayList<>();

        for (Book book : books.values()) {

            if (book.getBookStatus() == BookStatus.AVAILABLE) {

                bookList.add(book);

            }

        }

        return bookList;

    }

    public Set<String> getAllAuthors() {

        Set<String> authors = new HashSet<>();

        for (Book book : books.values()) {

            authors.add(book.getAuthor());

        }

        return authors;

    }

    public List<Book> getAllBooks() {

        return new ArrayList<>(books.values());

    }

    public void markAsIssued(String bookId) throws BookNotFoundException {

        Book book = getBookById(bookId);  // throws exception if not found

        book.setBookStatus(BookStatus.ISSUED);  // change status

        // HashMap already stores reference, so it updates automatically

    }

    public void markAsAvailable(String bookId) throws BookNotFoundException {

        Book book = getBookById(bookId);

        book.setBookStatus(BookStatus.AVAILABLE);

    }

    // here we will do the like add to the file

    public void saveToFile(String filename) throws IOException {

        FileWriter fw = new FileWriter(filename);

        for (Book book : books.values()) {

            String line = book.getBookId() + "," + book.getIsbn() + ","

                    + book.getTitle() + "," + book.getAuthor() + ","

                    + book.getCategory() + "," + book.getBookStatus();

            fw.write(line + "\n");

        }

        fw.close();

        System.out.println("Books saved to " + filename);

    }

    public void loadFromFile(String filename) throws IOException {

        File file = new File(filename);

        if (!file.exists()) {

            System.out.println("No existing books file found. Starting fresh.");

            return;

        }

        BufferedReader br = new BufferedReader(new FileReader(filename));

        String line;

        while ((line = br.readLine()) != null) {

            String[] parts = line.split(",");

            if (parts.length < 6) continue;

            String bookId = parts[0];

            String isbn = parts[1];

            String title = parts[2];

            String author = parts[3];

            Category category = Category.valueOf(parts[4]);

            BookStatus status = BookStatus.valueOf(parts[5]);

            Book book = new Book(bookId, isbn, title, author, category, status);  // this will be given to the constructor that is with the book id so liek the id will not be changed likewise

            books.put(bookId, book);

        }

        br.close();

        System.out.println("Books loaded from " + filename);

    }

}
