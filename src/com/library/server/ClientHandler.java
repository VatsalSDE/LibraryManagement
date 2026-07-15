package com.library.server;

import com.library.exceptions.BookNotFoundException;
import com.library.exceptions.InvalidFineException;
import com.library.exceptions.InvalidMemberException;
import com.library.exceptions.MemberNotFoundException;
import com.library.service.LibraryService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;

    private final LibraryService libraryService;

    public ClientHandler(Socket socket, LibraryService libraryService) {

        this.socket = socket;

        this.libraryService = libraryService;

    }

    @Override
    public void run() {

        try (Socket clientSocket = socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request;

            while ((request = in.readLine()) != null) {

                if ("EXIT".equalsIgnoreCase(request.trim())) {

                    sendResponse(out, "Goodbye.");
                    break;

                }

                sendResponse(out, handleRequest(request));

            }

        } catch (IOException e) {

            System.out.println("Client disconnected: " + e.getMessage());

        }

    }

    private String handleRequest(String request) {

        try {

            String[] parts = request.split("\\|", -1);
            String command = parts[0].trim().toUpperCase();

            switch (command) {

                case "ADD_BOOK":
                    return libraryService.addBook(parts[1], parts[2], parts[3], com.library.enums.Category.valueOf(parts[4].trim().toUpperCase()));
                case "REMOVE_BOOK":
                    return libraryService.removeBook(parts[1]);
                case "ADD_MEMBER":
                    return libraryService.addMember(parts[1], parts[2], parts[3]);
                case "REMOVE_MEMBER":
                    return libraryService.removeMember(parts[1]);
                case "ISSUE_BOOK":
                case "BORROW_BOOK":
                    return libraryService.issueBook(parts[1], parts[2], parts[3]);
                case "RETURN_BOOK":
                    return libraryService.returnBook(parts[1], parts[2], parts[3]);
                case "PAY_FINE":
                    return libraryService.payFine(parts[1]);
                case "VIEW_MEMBER_TXNS":
                    return libraryService.viewMemberTransactions(parts[1]);
                case "LIST_BOOKS":
                    return libraryService.listAllBooks();
                case "LIST_TRANSACTIONS":
                    return libraryService.listAllTransactions();
                case "LIST_AUTHORS":
                    return libraryService.listAllAuthors();
                case "UNDO_LAST":
                    return libraryService.undoLastTransaction();
                case "SEARCH_TITLE":
                    return libraryService.searchByTitle(parts[1]);
                case "SEARCH_AUTHOR":
                    return libraryService.searchByAuthor(parts[1]);
                case "SEARCH_CATEGORY":
                    return libraryService.searchByCategory(parts[1]);
                default:
                    return "ERROR: Unknown command";

            }

        } catch (ArrayIndexOutOfBoundsException e) {

            return "ERROR: Missing command arguments";

        } catch (BookNotFoundException | MemberNotFoundException | InvalidFineException
                 | InvalidMemberException | IllegalArgumentException e) {

            return "ERROR: " + e.getMessage();

        } catch (Exception e) {

            return "ERROR: " + e.getMessage();

        }

    }

    private void sendResponse(PrintWriter out, String response) {

        for (String line : response.split("\\R")) {

            out.println(line);

        }

        out.println("END");
    }
}
