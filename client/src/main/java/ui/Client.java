package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import exceptions.ClientException;
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
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class Client implements ServerMessageObserver {

    private final ServerFacade serverFacade;

    private UUID authToken = null;
    private final ArrayList<GameData> currentGames = new ArrayList<>();

    private ChessGame.TeamColor teamColor;
    private ChessGame currentGame;
    private int currentGameId;
    
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
        print("   1 -- Help");
        print("   2 -- Redraw Chess Board");
        print("   3 -- Make Move");
        print("   4 -- Leave");
        print("   5 -- Resign");
        print("   6 -- Highlight Legal Moves");
    }

    void handleInGameInput(String input) throws Exception {
        switch (input) {
            case "1":
                //ws.send(new Gson().toJson(new JoinPlayer(authToken.toString(), 1, ChessGame.TeamColor.WHITE)));
                break;
            case "2":
                //ws.send(new Gson().toJson(new JoinObserver(authToken.toString(), 1)));
                break;
            case "3":
                makeMove();
                break;
            case "4":
                //ws.send(new Gson().toJson(new Leave("12345", 5)));
                break;
            case "5":
                //ws.send(new Gson().toJson(new Resign("12345", 5)));
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

    ChessPosition parseSquare(String input) throws Exception {
        if (input.length() != 2) {
            throw new ClientException("Invalid square");
        }
        int row, col;
        char first = input.charAt(0);
        char second = input.charAt(1);
        if (!Character.isLetter(first) || !Character.isDigit(second)) {
            throw new ClientException("Invalid square");
        }
        col = switch (first) {
            case 'a', 'A' -> 1;
            case 'b', 'B' -> 2;
            case 'c', 'C' -> 3;
            case 'd', 'D' -> 4;
            case 'e', 'E' -> 5;
            case 'f', 'F' -> 6;
            case 'g', 'G' -> 7;
            case 'h', 'H' -> 8;
            default -> throw new ClientException("Invalid column");
        };
        row = Integer.parseInt(String.valueOf(second));
        if (row < 1 || row > 8) {
            throw new ClientException("Invalid row");
        }
        return new ChessPosition(row, col);

    }

    void makeMove() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ChessPosition start;
        ChessPosition end;
        try {
            print("Source square?");
            String source = reader.readLine();
            start = parseSquare(source);
            ChessPiece startPiece = currentGame.getBoard().getPiece(start);
            if (startPiece == null || startPiece.getTeamColor() != teamColor) {
                throw new ClientException("Invalid piece.");
            }
            print("Destination square?");
            String destination = reader.readLine();
            end = parseSquare(destination);
        } catch (Exception ex) {
            print("Error: " + ex.getMessage());
            return;
        }
        ChessPiece.PieceType promotionPiece = checkPromotion(start, end, reader);

        ChessMove move = new ChessMove(start, end, promotionPiece);
        ws.send(new MakeMove(authToken.toString(), currentGameId, move));

    }

    public ChessPiece.PieceType checkPromotion(ChessPosition start, ChessPosition end, BufferedReader reader) throws IOException {
        ChessPiece chessPiece = currentGame.getBoard().getPiece(start);
        if (chessPiece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return null;
        }
        if (teamColor == ChessGame.TeamColor.BLACK && end.getRow() == 1 || teamColor == ChessGame.TeamColor.WHITE && end.getRow() == 8) {
            ChessPiece.PieceType promotionPiece = null;
            while(promotionPiece == null) {
                print("Piece to promote to?");
                String input = reader.readLine();
                promotionPiece = translatePromotionPiece(input);
                if (promotionPiece == null) {
                    print("Invalid promotion piece.");
                }
            }
            return promotionPiece;
        }
        return null;
    }

    public ChessPiece.PieceType translatePromotionPiece(String input) {
        input = input.toLowerCase();
        return switch (input) {
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "rook" -> ChessPiece.PieceType.ROOK;
            default -> null;
        };
    }

    public void notify(ServerMessage message) {
//        print("Received " + message.getServerMessageType());
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
        print(error.getNotification());
    }

    public void loadGame(LoadGame loadGame) {
        printChessBoard(loadGame.getGame());
        this.currentGame = loadGame.getGame();
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

    void sendJoin(int gameId) throws Exception {
        JoinPlayer joinPlayer = new JoinPlayer(authToken.toString(), gameId, teamColor);
        currentGameId = gameId;
        ws.send(joinPlayer);
    }

    void sendObserve(int gameId) throws Exception {
        JoinObserver joinObserver = new JoinObserver(authToken.toString(), gameId);
        currentGameId = gameId;
        ws.send(joinObserver);
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
            sendJoin(currentGames.get(gameNumber - 1).gameID());
            gameLoop();
        } catch (ServerError e) {
            print(e.message());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            print("Please enter a valid game number.");
            joinGame();
        } catch (Exception e) {
            print("Error joining game " + e.getMessage());
        }
    }

    void joinObserver() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            print("Game number?");
            int gameNumber = Integer.parseInt(reader.readLine());
            serverFacade.sendJoinRequest(currentGames, gameNumber, null, authToken);
            teamColor = null;
            sendObserve(currentGames.get(gameNumber - 1).gameID());
            gameLoop();
//            printChessBoard(currentGames.get(gameNumber - 1).game());
        } catch (ServerError e) {
            print(e.message());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            print("Please enter a valid game number.");
            joinObserver();
        } catch (Exception e) {
            print("Error joining game as observer " + e.getMessage());
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
