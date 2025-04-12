package client;

import client.formatting.SpacingType;
import client.websocket.NotificationHandler;
import websocket.messages.ServerMessage;
import static client.ChessClient.printf;

import static client.formatting.EscapeSequences.*;

/**
 * Handles notifications received from WebSocketFacade. Prints these messages to the terminal.
 */
public class ChessNotificationHandler implements NotificationHandler {

  /**
   * Prints the message received to the terminal.
   *
   * @param serverMessage notification
   */
  @Override
  public void notify(ServerMessage serverMessage) {

    printf("\n\n(Client.ChessNotificationHandler::notify) ", SpacingType.NONE, SET_TEXT_COLOR_LIGHT_GREY);

    if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
      printf("Client received a NOTIFICATION type ServerMessage", SpacingType.REGULAR, SET_TEXT_COLOR_LIGHT_GREY);
    } else {
      System.out.println(SET_TEXT_COLOR_RED + "Unscripted ServerMessageType");
    }

  }
}
