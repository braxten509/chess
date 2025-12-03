package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import java.util.concurrent.ConcurrentHashMap;
import model.GameData;
import model.WebSocketResult;
import websocket.messages.ServerMessage;

/**
 * Handles all WebSocket connections.
 */
public class ConnectionManager {
  public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

  /**
   * Opens a new connection to the designated user.
   *
   * @param username their username
   * @param context their "session"
   */
  public void add(String username, WsContext context) {
    var connection = new Connection(username, context);
    connections.put(username, connection);
  }

  /**
   * Sends a message to the triggeringUser.
   *
   * @param triggeringUser user that triggered the broadcast
   * @param message the message to send the user
   */
  public void broadcast(String triggeringUser, String message) {
    var connection2 = connections.get(triggeringUser);
    connection2.send(
        new Gson().toJson(
            new WebSocketResult(
                new ServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION), message, null, null)
        )
    );

    for (Connection connection : connections.values()) {
      if (!connection.username.equals(triggeringUser)) {
        try {
          ServerMessage serverMessage = new ServerMessage(
              ServerMessage.ServerMessageType.NOTIFICATION
          );
          connection.send(
              new Gson().toJson(new WebSocketResult(serverMessage, message, null, null))
          );
          System.out.println(
              "(Server.ConnectionManager::broadcast) successfully sent message '"
                  + message + "' to '" + connection.username + "'"
          );
        } catch (Exception e) {
          System.out.println(
              "(Server.ConnectionManager::broadcast) error while sending message back to client: "
                  + e.getMessage()
          );
        }
      }
    }
  }

  /**
   * Gets and loads the game for the designated user.
   *
   * @param username the user to load the game for
   * @param gameData the gameData for the game to load
   */
  public void loadGame(String username, GameData gameData) {
    var connection = connections.get(username);
    var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);

    connection.send(
        new Gson().toJson(new WebSocketResult(serverMessage, null, username, gameData))
    );
  }
}