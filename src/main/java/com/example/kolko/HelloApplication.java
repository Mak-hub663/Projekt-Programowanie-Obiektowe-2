package com.example.kolko;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HelloApplication extends Application {
    private static final int PORT = 5001;
    private Button[][] plansza = new Button[3][3];
    private boolean czyTuraX = true;
    private Stage stage;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isServer;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private TextArea chatArea;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        boolean chceBycSerwerem = true;

        if (chceBycSerwerem) {
            startServer();
        } else {
            startClient();
        }
    }

    private void startServer() {
        isServer = true;
        executorService.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Serwer nasłuchuje na porcie " + PORT);
                socket = serverSocket.accept();
                setupStreams(socket);
                Platform.runLater(this::runGame);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void startClient() {
        isServer = false;
        executorService.submit(() -> {
            try {
                socket = new Socket("localhost", PORT);
                setupStreams(socket);
                Platform.runLater(this::runGame);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setupStreams(Socket socket) throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        listenForMoves();
    }

    private void runGame() {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: black;");
        for (int wiersz = 0; wiersz < 3; wiersz++) {
            for (int kolumna = 0; kolumna < 3; kolumna++) {
                Button przycisk = new Button("");
                przycisk.setMinSize(100, 100);
                int finalWiersz = wiersz;
                int finalKolumna = kolumna;
                przycisk.setOnAction(e -> handleMove(finalWiersz, finalKolumna));
                plansza[wiersz][kolumna] = przycisk;
                przycisk.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-text-fill: white;"); // Tło kwadratów na czarne, tekst na biały
                grid.add(przycisk, kolumna, wiersz);
            }
        }

        this.chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPrefHeight(150);
        chatArea.setStyle("-fx-background-color: white; -fx-text-fill: black;");

        TextField messageField = new TextField();
        messageField.setStyle("-fx-background-color: white; -fx-text-fill: black;");

        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        sendButton.setOnAction(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                sendMessage(message);
                chatArea.appendText("You: " + message + "\n");
                messageField.clear();
            }
        });

        VBox chatBox = new VBox(chatArea, messageField, sendButton);
        chatBox.setSpacing(10);
        chatBox.setPrefHeight(200);

        HBox mainLayout = new HBox(grid, chatBox);
        VBox layout = new VBox(createMenu(), mainLayout);
        stage.setScene(new Scene(layout, 600, 330));
        stage.show();
    }



    private void handleMove(int wiersz, int kolumna) {
        if (!plansza[wiersz][kolumna].getText().isEmpty()) return;
        if ((czyTuraX && !isServer) || (!czyTuraX && isServer)) return;

        String symbol = czyTuraX ? "X" : "O";
        plansza[wiersz][kolumna].setText(symbol);


        plansza[wiersz][kolumna].setStyle("-fx-background-color: black; -fx-border-color: white; -fx-text-fill: "
                + (czyTuraX ? "red;" : "blue;"));

        out.println(wiersz + "," + kolumna);

        if (checkWinner()) {
            Platform.runLater(() -> {
                chatArea.appendText("Gracz " + (czyTuraX ? "X" : "O") + " wygrał!\n");
            });
            return;
        }

        czyTuraX = !czyTuraX;
    }



    private boolean checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (!plansza[i][0].getText().isEmpty() && plansza[i][0].getText().equals(plansza[i][1].getText()) && plansza[i][1].getText().equals(plansza[i][2].getText())) {

                Platform.runLater(() -> {
                    chatArea.appendText("Gracz " + (czyTuraX ? "X" : "O") + " wygrał!\n");
                    resetGame(true);
                });
                return true;
            }
            if (!plansza[0][i].getText().isEmpty() && plansza[0][i].getText().equals(plansza[1][i].getText()) && plansza[1][i].getText().equals(plansza[2][i].getText())) {

                Platform.runLater(() -> {
                    chatArea.appendText("Gracz " + (czyTuraX ? "X" : "O") + " wygrał!\n");
                    resetGame(true);
                });
                return true;
            }
        }
        if (!plansza[0][0].getText().isEmpty() && plansza[0][0].getText().equals(plansza[1][1].getText()) && plansza[1][1].getText().equals(plansza[2][2].getText())) {

            Platform.runLater(() -> {
                chatArea.appendText("Gracz " + (czyTuraX ? "X" : "O") + " wygrał!\n");
                resetGame(true);
            });
            return true;
        }
        if (!plansza[0][2].getText().isEmpty() && plansza[0][2].getText().equals(plansza[1][1].getText()) && plansza[1][1].getText().equals(plansza[2][0].getText())) {

            Platform.runLater(() -> {
                chatArea.appendText("Gracz " + (czyTuraX ? "X" : "O") + " wygrał!\n");
                resetGame(true);
            });
            return true;
        }
        return false;
    }


    private boolean gameReset = false;

    private void resetGame(boolean przegrana) {

        if (gameReset) {
            return;
        }


        gameReset = true;

        Platform.runLater(() -> {
            // Reset planszy
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    plansza[i][j].setText("");
                }
            }


            czyTuraX = true;


            if (przegrana) {
                chatArea.appendText("Gracz " + (isServer ? "O" : "X") + " wygrał, ponieważ przeciwnik zrestartował grę!\n");
            } else {
                chatArea.appendText("Gra została zresetowana!\n");
            }
        });


        if (out != null) {
            out.println("RESET");
        }


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                gameReset = false;
            }
        }, 500);
    }






    private MenuBar createMenu() {
        Menu fileMenu = new Menu("File");

        MenuItem startGameItem = new MenuItem("Restart Game");
        startGameItem.setOnAction(e -> resetGame(true));

        MenuItem saveGameItem = new MenuItem("Save Game");
        saveGameItem.setOnAction(e -> saveGameState());

        MenuItem loadGameItem = new MenuItem("Load Game");
        loadGameItem.setOnAction(e -> loadGameState());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());


        fileMenu.getItems().addAll(startGameItem, saveGameItem, loadGameItem, exitItem);


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);
        menuBar.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        return menuBar;
    }



    private void listenForMoves() {
        executorService.submit(() -> {
            try {
                String linia;
                while ((linia = in.readLine()) != null) {
                    if (linia.startsWith("CHAT:")) {
                        String message = linia.substring(5);
                        Platform.runLater(() -> chatArea.appendText("Opponent: " + message + "\n"));
                    } else if (linia.equals("RESET")) {
                        if (!gameReset) {
                            Platform.runLater(() -> resetGame(false));
                        }
                    } else {
                        String[] parts = linia.split(",");
                        int wiersz = Integer.parseInt(parts[0]);
                        int kolumna = Integer.parseInt(parts[1]);
                        Platform.runLater(() -> {
                            plansza[wiersz][kolumna].setText(czyTuraX ? "X" : "O");
                            czyTuraX = !czyTuraX;
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }



    private void sendMessage(String message) {
        out.println("CHAT:" + message);
    }

    private void saveGameState() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String[][] boardState = new String[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    boardState[i][j] = plansza[i][j].getText();
                }
            }
            GameState gameState = new GameState(boardState, czyTuraX);
            mapper.writeValue(new File("game_state.json"), gameState);
            chatArea.appendText("Game saved!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadGameState() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GameState gameState = mapper.readValue(new File("game_state.json"), GameState.class);
            String[][] boardState = gameState.getBoard();
            czyTuraX = gameState.isTurnX();

            Platform.runLater(() -> {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        plansza[i][j].setText(boardState[i][j]);
                    }
                }
                chatArea.appendText("Game loaded!\n");
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}





class GameState {
    private String[][] board;
    private boolean turnX;

    public GameState() { }

    public GameState(String[][] board, boolean turnX) {
        this.board = board;
        this.turnX = turnX;
    }

    public String[][] getBoard() {
        return board;
    }

    public boolean isTurnX() {
        return turnX;
    }
}

