package client;

import client.websocket.NotificationHandler;

import javax.management.Notification;

import static client.formatting.EscapeSequences.*;

/**
 * Handles notifications received from WebSocketFacade. Prints these messages to the terminal.
 */
public class ChessNotificationHandler implements NotificationHandler {
  /**
   * Prints the message received to the terminal.
   *
   * @param notification notification
   */
  @Override
  public void notify(Notification notification) {
    System.out.println(SET_TEXT_COLOR_LIGHT_GREY + notification.getMessage());
  }
}
