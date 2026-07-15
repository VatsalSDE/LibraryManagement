package com.library.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class LibraryClient {

    private final String host;

    private final int port;

    public LibraryClient(String host, int port) {

        this.host = host;
        this.port = port;

    }

    public void start() throws IOException {

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // this have used to recieve messages form the server
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);    // this we have used for send messages to the server
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to library server at " + host + ":" + port);

            boolean running = true;

            while (running) {

                System.out.println("\n========== CLIENT MENU ==========");
                System.out.println("1. Admin Mode");
                System.out.println("2. Member Mode");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");

                int choice = readInt(scanner);

                switch (choice) {

                    case 1:
                        running = adminMenu(scanner, in, out);
                        break;
                    case 2:
                        running = memberMenu(scanner, in, out);
                        break;
                    case 3:
                        out.println("EXIT");
                        printResponse(readResponse(in));
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");

                }

            }

        }

    }

    private boolean adminMenu(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {

        boolean inAdmin = true;

        while (inAdmin) {

            System.out.println("\n========== ADMIN CLIENT ==========");
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
            System.out.println("13. Back");
            System.out.print("Enter your choice: ");

            int choice = readInt(scanner);

            switch (choice) {

                case 1:
                    out.println("ADD_BOOK|" + prompt(scanner, "Enter ISBN: ") + "|"
                            + prompt(scanner, "Enter Title: ") + "|"
                            + prompt(scanner, "Enter Author: ") + "|"
                            + promptCategory(scanner));
                    printResponse(readResponse(in));
                    break;
                case 2:
                    out.println("REMOVE_BOOK|" + prompt(scanner, "Enter Book ID to remove: "));
                    printResponse(readResponse(in));
                    break;
                case 3:
                    searchBook(scanner, in, out);
                    break;
                case 4:
                    out.println("LIST_BOOKS");
                    printResponse(readResponse(in));
                    break;
                case 5:
                    out.println("ADD_MEMBER|" + prompt(scanner, "Enter Name: ") + "|"
                            + prompt(scanner, "Enter Email: ") + "|"
                            + prompt(scanner, "Enter Phone Number: "));
                    printResponse(readResponse(in));
                    break;
                case 6:
                    out.println("REMOVE_MEMBER|" + prompt(scanner, "Enter Member ID to remove: "));
                    printResponse(readResponse(in));
                    break;
                case 7:
                    out.println("ISSUE_BOOK|" + prompt(scanner, "Enter Book ID: ") + "|"
                            + prompt(scanner, "Enter Member ID: ") + "|"
                            + prompt(scanner, "Enter Issue Date (dd-MM-yyyy): "));
                    printResponse(readResponse(in));
                    break;
                case 8:
                    out.println("RETURN_BOOK|" + prompt(scanner, "Enter Book ID: ") + "|"
                            + prompt(scanner, "Enter Member ID: ") + "|"
                            + prompt(scanner, "Enter Return Date (dd-MM-yyyy): "));
                    printResponse(readResponse(in));
                    break;
                case 9:
                    out.println("LIST_TRANSACTIONS");
                    printResponse(readResponse(in));
                    break;
                case 10:
                    out.println("VIEW_MEMBER_TXNS|" + prompt(scanner, "Enter Member ID: "));
                    printResponse(readResponse(in));
                    break;
                case 11:
                    out.println("UNDO_LAST");
                    printResponse(readResponse(in));
                    break;
                case 12:
                    out.println("LIST_AUTHORS");
                    printResponse(readResponse(in));
                    break;
                case 13:
                    inAdmin = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");

            }

        }

        return true;
    }

    private boolean memberMenu(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {

        boolean inMember = true;

        while (inMember) {

            System.out.println("\n========== MEMBER CLIENT ==========");
            System.out.println("1. Search Book");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");
            System.out.println("4. Pay Fine");
            System.out.println("5. View My Transactions");
            System.out.println("6. Back");
            System.out.print("Enter your choice: ");

            int choice = readInt(scanner);

            switch (choice) {

                case 1:
                    searchBook(scanner, in, out);
                    break;
                case 2:
                    out.println("BORROW_BOOK|" + prompt(scanner, "Enter Book ID: ") + "|"
                            + prompt(scanner, "Enter Member ID: ") + "|"
                            + prompt(scanner, "Enter Issue Date (dd-MM-yyyy): "));
                    printResponse(readResponse(in));
                    break;
                case 3:
                    out.println("RETURN_BOOK|" + prompt(scanner, "Enter Book ID: ") + "|"
                            + prompt(scanner, "Enter Member ID: ") + "|"
                            + prompt(scanner, "Enter Return Date (dd-MM-yyyy): "));
                    printResponse(readResponse(in));
                    break;
                case 4:
                    out.println("PAY_FINE|" + prompt(scanner, "Enter Member ID: "));
                    printResponse(readResponse(in));
                    break;
                case 5:
                    out.println("VIEW_MEMBER_TXNS|" + prompt(scanner, "Enter Member ID: "));
                    printResponse(readResponse(in));
                    break;
                case 6:
                    inMember = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");

            }

        }

        return true;
    }

    private void searchBook(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {

        System.out.println("\nSearch By:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. Category");
        System.out.print("Enter your choice: ");

        int choice = readInt(scanner);

        switch (choice) {

            case 1:
                out.println("SEARCH_TITLE|" + prompt(scanner, "Enter Title: "));
                printResponse(readResponse(in));
                break;
            case 2:
                out.println("SEARCH_AUTHOR|" + prompt(scanner, "Enter Author: "));
                printResponse(readResponse(in));
                break;
            case 3:
                out.println("SEARCH_CATEGORY|" + promptCategory(scanner));
                printResponse(readResponse(in));
                break;
            default:
                System.out.println("Invalid choice. Try again.");

        }

    }

    private String prompt(Scanner scanner, String message) {

        System.out.print(message);
        return scanner.nextLine();

    }

    private String promptCategory(Scanner scanner) {

        while (true) {

            System.out.println("Select Category:");
            System.out.println("1. FICTION");
            System.out.println("2. SCIENCE");
            System.out.println("3. HISTORY");
            System.out.println("4. BIOGRAPHY");
            System.out.println("5. TECHNOLOGY");
            System.out.println("6. GENERAL");
            System.out.print("Enter category number: ");

            int choice = readInt(scanner);

            switch (choice) {

                case 1:
                    return "FICTION";
                case 2:
                    return "SCIENCE";
                case 3:
                    return "HISTORY";
                case 4:
                    return "BIOGRAPHY";
                case 5:
                    return "TECHNOLOGY";
                case 6:
                    return "GENERAL";
                default:
                    System.out.println("Invalid category selected. Try again.");

            }

        }

    }

    private int readInt(Scanner scanner) {

        String value = scanner.nextLine();

        try {

            return Integer.parseInt(value.trim());

        } catch (NumberFormatException e) {

            return -1;

        }

    }

    private String readResponse(BufferedReader in) throws IOException {

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {

            if ("END".equals(line)) {

                break;

            }

            if (response.length() > 0) {

                response.append('\n');

            }

            response.append(line);

        }

        return response.toString();

    }

    private void printResponse(String response) {

        System.out.println(response);

    }
}
