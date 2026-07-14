// InputHandler.java

package com.library.utilities;

import com.library.enums.Category;

import java.util.Scanner;

public class InputHandler {

    private static Scanner scanner = new Scanner(System.in);

    public static int getIntInput() {

        try {

            return Integer.parseInt(scanner.nextLine());

        } catch (NumberFormatException e) {

            return -1;

        }

    }

    public static String getStringInput() {

        return scanner.nextLine();

    }

    public static Category getCategoryInput() {

        System.out.println("Select Category:");

        for (int i = 0; i < Category.values().length; i++) {

            System.out.println((i + 1) + ". " + Category.values()[i]);

        }

        System.out.print("Enter category number: ");

        int choice = getIntInput();

        if (choice < 1 || choice > Category.values().length) {

            throw new IllegalArgumentException("Invalid category selected");

        }

        return Category.values()[choice - 1];
    }

    public static void closeScanner() {

        scanner.close();

    }

}
