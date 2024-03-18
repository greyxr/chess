import chess.*;
import ui.Client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        Client client = new Client(8080);
        client.main();
    }
}