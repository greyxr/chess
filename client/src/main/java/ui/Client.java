package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.ServerError;
import model.*;
import server.ServerFacade;
import server.ServerMessageObserver;
import server.WSCommunicator;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Client implements ServerMessageObserver {

    private final ServerFacade serverFacade;

    private UUID authToken = null;
    private final ArrayList<GameData> currentGames = new ArrayList<>();

    private ChessGame.TeamColor teamColor;
    
    private WSCommunicator ws;
    int port;

    public Client(int port) {
        serverFacade = new ServerFacade(port);
        ws = new WSCommunicator(this, port);
    }
    public void main() throws IOException {
        print("CS 240 Chess Server.\nType a number to get started, or help for help.\n");
        while(true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            if (authToken != null) {
                printLoggedInMenu();
            } else {
                printLoggedOutMenu();
            }

            String input = reader.readLine();

            if (authToken != null) {
                handleLoggedInInput(input);
            } else {
                handleLoggedOutInput(input);
            }
        }
    }

    void printLoggedOutMenu() {
        System.out.println("Options:");
        System.out.println("1 -- Register");
        System.out.println("2 -- Login");
        System.out.println("3 -- Quit\n");
    }

    void printLoggedInMenu() {
        System.out.println("Options:");
        System.out.println("1 -- Logout");
        System.out.println("2 -- Create Game");
        System.out.println("3 -- List Games");
        System.out.println("4 -- Join Game");
        System.out.println("5 -- Join Observer");
        System.out.println("6 -- Quit\n");
    }

    void printInGameMenu() {
        print("   1 - Join Game");
        print("   2 - Join Observer");
        print("   3 - Make Move");
        print("   4 - Resign");
        print("   5 - Leave");
        print("   6 - Quit");
    }

    void handleInGameInput(String input) throws Exception {
        switch (input) {
            case "1":
                ws.send(new Gson().toJson(new JoinPlayer(authToken.toString(), 1, ChessGame.TeamColor.WHITE)));
                break;
            case "2":
                ws.send(new Gson().toJson(new JoinObserver(authToken.toString(), 1)));
                break;
            case "3":
                ws.send(new Gson().toJson(new MakeMove("12345", 5, null)));
                break;
            case "4":
                ws.send(new Gson().toJson(new Leave("12345", 5)));
                break;
            case "5":
                ws.send(new Gson().toJson(new Resign("12345", 5)));
                break;
        }
    }

    void unrecognizedCommand() {
        System.out.println("Command not recognized.\n");
    }

    void handleLoggedInInput(String input) {
        switch (input) {
            case "help":
                help();
            case "1":
                logout();
                break;
            case "2":
                createGame();
                break;
            case "3":
                listGames();
                break;
            case "4":
                joinGame();
                break;
            case "5":
                joinObserver();
                break;
            case "6":
            case "q":
            case "quit":
                System.exit(0);
            default:
                unrecognizedCommand();
        }
    }

    void handleLoggedOutInput(String input) {
        switch (input) {
            case "help":
                help();
            case "1":
                register();
                break;
            case "2":
                login();
                break;
            case "7":
                test();
                break;
            case "3":
            case "q":
            case "quit":
                System.exit(0);
            default:
                unrecognizedCommand();

        }
    }

    void gameLoop() {
        printInGameMenu();
        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                String input = reader.readLine();
                if (Objects.equals(input, "6")) {
                    break;
                }
                handleInGameInput(input);
            } catch (Exception e) {
                print("Caught exception " + e.getMessage());
            }
        }
    }

    public void notify(ServerMessage message) {
        switch(message.getServerMessageType()) {
            case NOTIFICATION -> notification((Notification) message);
            case ERROR -> error((Error) message);
            case LOAD_GAME -> loadGame((LoadGame) message);
        }
    }

    public void notification(Notification notification) {
        print("Notification: " + notification.getMessage());
    }

    public void error(Error error) {
        print("Error: " + error.getNotification());
    }

    public void loadGame(LoadGame loadGame) {
        print("Loadgame");
        printChessBoard(loadGame.getGame());
    }

    void test() {
        print("Testing websocket client connection");
//        try {
//            serverFacade.sendWebSocketConnect();
//        } catch (Exception e) {
//            print("Exception: " + e.getMessage());
//        }
    }

    void help() {
        print("The chess client expects a single number to parse. Any other strings will not be recognized, except \"help\", \"q\", or \"quit\". Type a number shown below:");
    }

    void print(String input) {
        System.out.println(input);
    }

    void printChessBoard(ChessGame chessGame) {
        chessGame.getBoard().resetBoard();
        ChessBoard.main(chessGame.convertToMatrix("white"), chessGame.convertToMatrix("black"), teamColor);
    }

    void register() {
        try {
            print("Please enter your username:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String username = reader.readLine();
            print("Please enter your password:");
            String password = reader.readLine();
            print("Please enter your email:");
            String email = reader.readLine();
            AuthData result = serverFacade.sendRegisterRequest(new UserData(username, password, email));
            authToken = result.authToken();
            print("Welcome, " + username);
        } catch (ServerError e) {
            print(e.message());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void logout() {
        try {
            serverFacade.sendLogoutRequest(authToken);
            authToken = null;
        } catch (ServerError e) {
            print(e.message());
        }
    }

    void login() {
        try {
            print("Please enter your username:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String username = reader.readLine();
            print("Please enter your password:");
            String password = reader.readLine();
            AuthData result = serverFacade.sendLoginRequest(new UserData(username, password, null));
            authToken = result.authToken();
            print("Welcome, " + username);
        } catch (ServerError e) {
            print(e.message());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void printGames() {
        if (currentGames.isEmpty()) {
            print("No active games to display.");
            return;
        }

        print("===================================");
        for (int i = 0; i < currentGames.size(); i++) {
            GameData game = currentGames.get(i);
            print("(" + ((int) i + 1) + ") Game Name: " + game.gameName());
            print("   Game ID: " + game.gameID());
            print("   White Username: " + game.whiteUsername());
            print("   Black Username: " + game.blackUsername());
            print("---------------------------------------");
        }
    }

    void listGames() {
        try {
            ListGamesResponse games = serverFacade.getGames(authToken);
            currentGames.clear();
            currentGames.addAll(games.games());
            printGames();
        } catch (ServerError e) {
            print(e.message());
        }
    }

    void joinGame() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            print("Game number?");
            int gameNumber = Integer.parseInt(reader.readLine());
            print("Which color? black/white");
            String color = reader.readLine();
            if (color.equalsIgnoreCase("white")) {
                teamColor = ChessGame.TeamColor.WHITE;
            } else if (color.equalsIgnoreCase("black")) {
                teamColor = ChessGame.TeamColor.BLACK;
            } else {
                print("Invalid team color.");
                joinGame();
                return;
            }
            serverFacade.sendJoinRequest(currentGames, gameNumber, color, authToken);
            //printChessBoard(currentGames.get(gameNumber - 1).game());
            gameLoop();
        } catch (ServerError e) {
            print(e.message());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            print("Please enter a valid game number.");
            joinGame();
        }
    }

    void joinObserver() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            print("Game number?");
            int gameNumber = Integer.parseInt(reader.readLine());
            serverFacade.sendJoinRequest(currentGames, gameNumber, null, authToken);
            teamColor = null;
            gameLoop();
//            printChessBoard(currentGames.get(gameNumber - 1).game());
        } catch (ServerError e) {
            print(e.message());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            print("Please enter a valid game number.");
            joinObserver();
        }
    }

    void createGame() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            print("Game name?");
            String gameName = reader.readLine();
            GameData gameData = serverFacade.sendCreateGameRequest(new GameName(gameName), authToken);
            print("Created game " + gameName + " with ID " + gameData.gameID());
        } catch (ServerError e) {
            print(e.message());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
