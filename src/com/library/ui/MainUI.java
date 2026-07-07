// MainUI.java
package com.library.ui;

import com.library.managers.BookManager;
import com.library.managers.MemberManager;
import com.library.managers.TransactionManager;
import com.library.utilities.InputHandler;

public class MainUI {
    private BookManager bookManager;
    private MemberManager memberManager;
    private TransactionManager transactionManager;
    private AdminUI adminUI;
    private MemberUI memberUI;

    public MainUI(BookManager bookManager, MemberManager memberManager, TransactionManager transactionManager) {
        this.bookManager = bookManager;
        this.memberManager = memberManager;
        this.transactionManager = transactionManager;
        this.adminUI = new AdminUI(bookManager, memberManager, transactionManager);
        this.memberUI = new MemberUI(bookManager, memberManager, transactionManager);
    }

    public void start() {
        boolean running = true;

        while (running) {
            displayMainMenu();
            int choice = InputHandler.getIntInput();

            switch (choice) {
                case 1:
                    adminUI.displayAdminMenu();
                    break;
                case 2:
                    memberUI.displayMemberMenu();
                    break;
                case 3:
                    running = false;
                    System.out.println("Thank you for using Library Management System!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }

        InputHandler.closeScanner();
    }

    private void displayMainMenu() {
        System.out.println("\n========== LIBRARY MANAGEMENT SYSTEM ==========");
        System.out.println("1. Admin Operations");
        System.out.println("2. Member Operations");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }
}