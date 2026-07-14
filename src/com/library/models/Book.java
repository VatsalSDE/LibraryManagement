package com.library.models;

import com.library.enums.BookStatus;

import com.library.enums.Category;

public class Book implements Comparable<Book> {

    private static int dynamicbookId = 1; // this is the book id that will be incremented everytime new book is assigned

    private String bookID;

    private String isbn;

    private String title;

    private String author;

    private Category category;

    private BookStatus bookStatus;

    public Book(String isbn, String title, String author, Category category, BookStatus bookStatus) {

        this.bookID = "B" + dynamicbookId++;

        this.isbn = isbn;

        this.title = title;

        this.author = author;

        this.category = category;

        this.bookStatus = bookStatus;

    }

    // Getters

    public String getBookId() {

        return bookID;

    }

    public String getIsbn() {

        return isbn;

    }

    public String getTitle() {

        return title;

    }

    public String getAuthor() {

        return author;

    }

    public Category getCategory() {

        return category;

    }

    public BookStatus getBookStatus() {

        return bookStatus;

    }

    // Setters (only what makes sense to change)

    public void setTitle(String title) {

        this.title = title;

    }

    public void setAuthor(String author) {

        this.author = author;

    }

    public void setCategory(Category category) {

        this.category = category;

    }

    public void setBookStatus(BookStatus bookStatus) {

        this.bookStatus = bookStatus;

    }

    // toString

    @Override

    public String toString() {

        return String.format("Book{id=%s, isbn=%s, title=%s, author=%s, category=%s, status=%s}",

                bookID, isbn, title, author, category, bookStatus);

    }

    // here the logic for the sorting is that like

    // firstly compare the titles if found the unique title one so return it and now if same titles

    // so now check according to the author here

    @Override

    public int compareTo(Book other) {

        int titleCompare = this.title.compareTo(other.title);

        if (titleCompare != 0) {

            return titleCompare;  // different titles, return result

        }

        return this.author.compareTo(other.author);

    }

}
