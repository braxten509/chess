package client.facade;

import client.handler.ChessMessageHandler;
import client.formatting.SpacingType;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

import static client.ChessClient.printf;
import static client.formatting.EscapeSequences.*;

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
  ChessMessageHandler chessMessageHandler;

  /**
   * Upgrades client connection to a Websocket connection.
   * First: formats url to -> ws://{url}/ws (as a URI object).
   * Second: makes a new session identified by the URI.
   * Third: onMessage sends that message to a NotificationHandler when WebSocketFacade receives a message.
   *
   * @param url server URL
   * @param chessMessageHandler class to handle notifications the server receives
   * @throws Exception any errors
   */
  public WebSocketFacade(String url, ChessMessageHandler chessMessageHandler) throws Exception {
    try {
      url = url.replace("http", "ws");
      URI socketURI = new URI(url + "/ws");
      this.chessMessageHandler = chessMessageHandler;

      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      this.session = container.connectToServer(this, socketURI);

      this.session.addMessageHandler(new MessageHandler.Whole<String>() {
        /**
         * What the WebSocketFacade does when a message is received.
         *
         * @param jsonMessage json formatted message
         */
        @Override
        public void onMessage(String jsonMessage) {
          try {
            printf("(Client.WebSocketFacade::onMessage) received message", SpacingType.REGULAR, SET_TEXT_COLOR_LIGHT_GREY);
            chessMessageHandler.handleMessage(jsonMessage);
            printf("(Client.WebSocketFacade::onMessage) executed successfully", SpacingType.UNDER, SET_TEXT_COLOR_LIGHT_GREY);
          } catch (Exception e) {
            printf("\n(Client.WebSocketFacade::onMessage) got an error: " + e.getMessage(), SpacingType.SURROUND, SET_TEXT_COLOR_RED);
          }
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
    ChessMessageHandler.webSocketFacade = this;
  }

  public void sendCommand(UserGameCommand command) {
    try {
      this.session.getBasicRemote().sendText(new Gson().toJson(command));
    } catch (IOException exception) {
      printf("ERROR (500): " + exception.getMessage(), SpacingType.SURROUND, SET_TEXT_COLOR_RED);
    }
  }
}
