package ui;

import chess.ChessGame;
import exceptions.ServerError;
import model.AuthData;
import model.UserData;
import server.ServerFacade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.UUID;

public class Client {

    private ServerFacade serverFacade;
    private boolean loggedIn = false;

    private UUID authToken = null;

    public Client(int port) {
        serverFacade = new ServerFacade(port);
    }
    public void main() throws IOException {
        print("CS 240 Chess Server.");
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
        System.out.println("Type a number to get started, or help for help.\nOptions:");
        System.out.println("1 -- Register");
        System.out.println("2 -- Login");
        System.out.println("3 -- Quit\n");
    }

    void printLoggedInMenu() {
        System.out.println("Type a number to get started, or help for help.\nOptions:");
        System.out.println("1 -- Logout");
        System.out.println("2 -- Create Game");
        System.out.println("3 -- List Games");
        System.out.println("4 -- Join Game");
        System.out.println("5 -- Join Observer");
        System.out.println("6 -- Quit\n");
    }

    void unrecognizedCommand() {
        System.out.println("Command not recognized.\n");
    }

    void handleLoggedInInput(String input) {
        switch (input) {
            case "help":
                help();
            case "1":
                break;
            case "2":
                printChessBoard();
                break;
            case "3":
                listGames();
                break;
            case "4":
                break;
            case "5":
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
            case "3":
            case "q":
            case "quit":
                System.exit(0);
            default:
                unrecognizedCommand();

        }
    }

    void help() {
        print("The chess client expects a single number to parse. Any other strings will not be recognized, except \"help\", \"q\", or \"quit\". Type a number shown below:");
    }

    void print(String input) {
        System.out.println(input);
    }

    void printChessBoard() {
        ChessGame chessGame = new ChessGame();
        chessGame.board.resetBoard();
        ChessBoard.main(chessGame.convertToMatrix("white"), chessGame.convertToMatrix("black"));
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

    void login() {
        serverFacade.sendLoginRequest();
    }

    void listGames() {
        serverFacade.getGames();
    }
}
