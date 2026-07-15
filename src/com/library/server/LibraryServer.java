package com.library.server;

import com.library.managers.BookManager;
import com.library.managers.MemberManager;
import com.library.managers.TransactionManager;
import com.library.service.LibraryService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class LibraryServer {

    private final int port;

    private final BookManager bookManager;

    private final MemberManager memberManager;

    private final TransactionManager transactionManager;

    private final LibraryService libraryService;

    // Fixed-size thread pool keeps client handling concurrent but bounded.
    private final ExecutorService threadPool;

    private final AtomicBoolean shuttingDown;

    private ServerSocket serverSocket;

    public LibraryServer(int port) {

        this.port = port;
        this.bookManager = new BookManager();
        this.memberManager = new MemberManager();
        this.transactionManager = new TransactionManager(memberManager, bookManager);
        this.libraryService = new LibraryService(bookManager, memberManager, transactionManager);
        this.threadPool = Executors.newFixedThreadPool(8);
        this.shuttingDown = new AtomicBoolean(false);

    }

    public void start() throws IOException {

        loadData();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownSafely));

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            this.serverSocket = serverSocket;

            System.out.println("Library server started on port " + port);

            while (!serverSocket.isClosed()) {

                try {

                    threadPool.submit(new ClientHandler(serverSocket.accept(), libraryService));

                } catch (SocketException e) {

                    break;

                }

            }

        } finally {

            shutdownSafely();

        }

    }

    private void loadData() throws IOException {

        System.out.println("========== LOADING DATA ==========");
        bookManager.loadFromFile("books.txt");
        memberManager.loadFromFile("members.txt");
        transactionManager.loadFromFile("transactions.txt");
        System.out.println("Server data loaded.");

    }

    private void shutdownSafely() {

        if (!shuttingDown.compareAndSet(false, true)) {

            return;

        }

        try {

            if (serverSocket != null && !serverSocket.isClosed()) {

                serverSocket.close();

            }

        } catch (IOException e) {

            System.out.println("Error closing server socket: " + e.getMessage());

        }

        threadPool.shutdownNow();

        try {

            bookManager.saveToFile("books.txt");
            memberManager.saveToFile("members.txt");
            transactionManager.saveToFile("transactions.txt");
            System.out.println("Server data saved.");

        } catch (IOException e) {

            System.out.println("Error saving server data: " + e.getMessage());

        }

    }
}
