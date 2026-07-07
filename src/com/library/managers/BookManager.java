package com.library.managers;

import com.library.models.Book;
import com.library.enums.Category;
import com.library.enums.BookStatus;
import com.library.exceptions.BookNotFoundException;
import com.library.exceptions.InvalidBookException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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
        if(!books.containsKey(bookId)){
            throw new BookNotFoundException("Book Not Found");
        }
        else{
            books.remove(bookId);
        }
    }

    public Book getBookById(String bookId) throws BookNotFoundException {
        if(!books.containsKey(bookId)){
            throw new BookNotFoundException("Book Not Found");
        }
        else{
            return books.get(bookId);
        }
    }

    public List<Book> searchByTitle(String title) {
        List<Book> bookList = new ArrayList<>();

        for (Book book : books.values()){
            if(book.getTitle().equalsIgnoreCase(title)){
                bookList.add(book);
            }
        }

        return bookList;
    }

    public List<Book> searchByAuthor(String author) {
        List<Book> bookList = new ArrayList<>();

        for(Book book : books.values()){
            if(book.getAuthor().equalsIgnoreCase(author)){
                bookList.add(book);
            }
        }

        return bookList;
    }

    public List<Book> searchByCategory(Category category) {
        List<Book> bookList = new ArrayList<>();

        for(Book book : books.values()){
            if(book.getCategory() == category){
                bookList.add(book);
            }
        }

        return bookList;
    }

    public List<Book> getAvailableBooks() {
        List<Book> bookList = new ArrayList<>();

        for(Book book : books.values()){
            if(book.getBookStatus() == BookStatus.AVAILABLE){
                bookList.add(book);
            }
        }

        return bookList;
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
}