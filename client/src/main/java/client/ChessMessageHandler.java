package client;

import client.formatting.SpacingType;
import com.google.gson.Gson;
import model.GameData;
import model.WebSocketResult;
import websocket.messages.ServerMessage;

import static client.ChessClient.*;
import static client.formatting.EscapeSequences.*;

/**
 * Handles notifications received from WebSocketFacade. Prints these messages to the terminal.
 */
public class ChessMessageHandler {

  public void handleMessage(String message) {

    // printf("(Client.ChessMessageHandler::handleMessage) message received", SpacingType.REGULAR, SET_TEXT_COLOR_LIGHT_GREY);

    WebSocketResult webSocketResult = new Gson().fromJson(message, WebSocketResult.class);

    System.out.println(webSocketResult);

    ServerMessage serverMessage = webSocketResult.serverMessage();
    String toBroadcastMessage = webSocketResult.message();
    String username = webSocketResult.username();
    String userColor = "OBSERVER";
    GameData gameData = webSocketResult.data();

    if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
      userColor = "WHITE";
    } else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
      userColor = "BLACK";
    }

    switch (serverMessage.getServerMessageType()) {
      case NOTIFICATION -> notify(toBroadcastMessage, userColor);
      case LOAD_GAME -> loadGame(username, userColor, gameData);
    }
  }

  /**
   * Prints the message received to the terminal.
   *
   * @param toBroadcastMessage notification
   */
  private void notify(String toBroadcastMessage, String userColor) {
      printf(toBroadcastMessage, SpacingType.SURROUND, SET_BG_COLOR_YELLOW);
      printf("[(" + userColor.toUpperCase() + ") " + userStatus + "] >>> ", SpacingType.REGULAR, null);
  }

  private void loadGame(String username, String userColor, GameData gameData) {
    if (userColor.equals("WHITE")) {
      UserCommands.drawChessboard("WHITE", gameData);
      printf(
          "Success joining game!",
          SpacingType.UNDER,
          SET_TEXT_COLOR_GREEN
      );

      UserCommands.inGame("WHITE", gameData);
    } else if (userColor.equals("BLACK")) {
      UserCommands.drawChessboard("BLACK", gameData);
      printf(
          "Success joining game!",
          SpacingType.UNDER,
          SET_TEXT_COLOR_GREEN
      );

      UserCommands.inGame("BLACK", gameData);
    } else {
      printf(username + " is not in the game!", SpacingType.SURROUND, SET_TEXT_COLOR_RED);
    }
  }
}
