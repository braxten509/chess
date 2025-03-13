import chess.*;
import dataaccess.DatabaseManager;
import javax.xml.crypto.Data;
import server.Server;

public class Main {

  public static void main(String[] args) {
    var piece = new ChessPiece(
      ChessGame.TeamColor.WHITE,
      ChessPiece.PieceType.PAWN
    );
    System.out.println("♕ 240 Chess Server: " + piece);

    try {
      DatabaseManager.createDatabase();
      DatabaseManager.createTables();
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }

    var server = new Server();

    server.run(8080);
  }
}
