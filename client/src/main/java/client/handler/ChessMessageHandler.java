package client.handler;

import client.GameClient;
import client.formatting.SpacingType;
import client.facade.WebSocketFacade;
import com.google.gson.Gson;
import model.GameData;
import model.WebSocketResult;
import websocket.messages.ServerMessage;

import java.util.ArrayList;

import static client.ChessClient.*;
import static client.formatting.EscapeSequences.*;

/**
 * Handles notifications received from WebSocketFacade. Prints these messages to the terminal.
 */
public class ChessMessageHandler {

  private static final ArrayList<Integer> gamesOpenByID = new ArrayList<>();

  public static WebSocketFacade webSocketFacade;

  public void handleMessage(String message) {

    // printf("(Client.ChessMessageHandler::handleMessage) message received", SpacingType.REGULAR, SET_TEXT_COLOR_LIGHT_GREY);

    WebSocketResult webSocketResult = new Gson().fromJson(message, WebSocketResult.class);

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
      printf(toBroadcastMessage, SpacingType.SURROUND, SET_TEXT_COLOR_YELLOW);
      printf("[(" + userColor.toUpperCase() + ") " + userStatus + "] >>> ", SpacingType.REGULAR, null);
  }

  private void loadGame(String username, String userColor, GameData gameData) {

    if (gameData == null) {
      printf("ERROR: gameData is null!", SpacingType.SURROUND, SET_TEXT_COLOR_RED);
      return;
    }

    for (Integer gameID : gamesOpenByID) {
      if (gameID == gameData.gameID()) {
        return;
      }
    }

    GameClient gameClient = new GameClient(username, userColor, gameData, webSocketFacade);
    gamesOpenByID.add(gameData.gameID());
    gameClient.run();

  }
}
