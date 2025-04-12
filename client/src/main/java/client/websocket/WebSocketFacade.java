package client.websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

import javax.management.Notification;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Receives requests from Client to upgrade the connection to a WebSocket connection.
 * Responses are sent to NotificationHandler
 */
public class WebSocketFacade extends Endpoint {

  Session session;
  NotificationHandler notificationHandler;

  /**
   * Upgrades client connection to a Websocket connection.
   * First: formats url to -> ws://{url}/ws (as a URI object).
   * Second: makes a new session identified by the URI.
   * Third: onMessage sends that message to a NotificationHandler when WebSocketFacade receives a message.
   *
   * @param url server URL
   * @param notificationHandler class to handle notifications the server receives
   * @throws Exception any errors
   */
  public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
    try {
      url = url.replace("http", "ws");
      URI socketURI = new URI(url + "/ws");
      this.notificationHandler = notificationHandler;

      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      this.session = container.connectToServer(this, socketURI);

      this.session.addMessageHandler(new MessageHandler.Whole<String>() {
        /**
         * What the WebSocketFacade does when a message is received.
         *
         * @param message message
         */
        @Override
        public void onMessage(String message) {
          Notification notification = new Gson().fromJson(message, Notification.class);
          notificationHandler.notify(notification);
        }
      });
    } catch (DeploymentException | IOException | URISyntaxException exception) {
      throw new RuntimeException("500" + exception.getMessage());
    }
  }

  /**
   * What the WebSocketFacade does after a connection is opened.
   * Necessary for interface, but unused in this code.
   *
   * @param session        session
   * @param endpointConfig endpointConfig
   */
  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {
  }

  public void sendCommand(UserGameCommand command) {
    try {
      System.out.println(this.session.isOpen());
      this.session.getBasicRemote().sendText(new Gson().toJson(command));
    } catch (IOException exception) {
      System.out.println("ERROR (500): " + exception);
    }
  }

//  public void joinGame(UserGameCommand command) throws Exception {
//    try {
//      this.session.getBasicRemote().sendText(new Gson().toJson(command));
//    } catch (Exception exception) {
//      System.out.println("ERROR (500): " + exception.getMessage());
//    }
//  }

}
