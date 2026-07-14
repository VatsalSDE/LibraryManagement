// Main.java - Updated with load/save

package com.library;

import com.library.managers.BookManager;

import com.library.managers.MemberManager;

import com.library.managers.TransactionManager;

import com.library.ui.MainUI;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        BookManager bookManager = new BookManager();

        MemberManager memberManager = new MemberManager();

        TransactionManager transactionManager = new TransactionManager(memberManager, bookManager);

        try {

            // Load data from files on startup

            System.out.println("========== LOADING DATA ==========");

            bookManager.loadFromFile("books.txt");

            memberManager.loadFromFile("members.txt");

            transactionManager.loadFromFile("transactions.txt");

            System.out.println();

            // Start the application

            MainUI mainUI = new MainUI(bookManager, memberManager, transactionManager);

            mainUI.start();

            // Save data to files on exit

            System.out.println("\n========== SAVING DATA ==========");

            bookManager.saveToFile("books.txt");

            memberManager.saveToFile("members.txt");

            transactionManager.saveToFile("transactions.txt");

            System.out.println("Data saved successfully!");

        } catch (IOException e) {

            System.out.println("Error: " + e.getMessage());

            e.printStackTrace();

        }

    }

}
