package com.library.exceptions;

public class BookAlreadyIssuedException extends Exception{
    public BookAlreadyIssuedException(String Message){
        super(Message);
    }
}
