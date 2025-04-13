package server.websocket;

import com.google.gson.Gson;
import model.GameData;
import model.WebSocketResult;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
  public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

  private void removeClosedConnections() {
    for (Connection connection : connections.values()) {
      if (!connection.session.isOpen()) {
        connections.remove(connection.username);
        System.out.println("(Server.ConnectionManager::broadcast) successfully added connection " + connection.username + " to be removed list");
      }
    }
  }

  public void add(String username, Session session) {
    var connection = new Connection(username, session);
    connections.put(username, connection);
  }

  public void broadcast(String triggeringUser, String message) {
    removeClosedConnections();

    for (Connection connection : connections.values()) {

      if (!connection.username.equals(triggeringUser)) {
        try {
          ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
          connection.send(new Gson().toJson(new WebSocketResult(serverMessage, message, null, null)));
          System.out.println("(Server.ConnectionManager::broadcast) successfully sent message to everyone except " + triggeringUser + ": " + message);
        } catch (Exception e) {
          System.out.println("(Server.ConnectionManager::broadcast) error while sending message back to client: " + e.getMessage());
        }
      }
    }
  }

  public void loadGame(String username, GameData gameData) throws IOException {
    var connection = connections.get(username);
    var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
    broadcast(username, username + " joined the game");

    connection.send(new Gson().toJson(new WebSocketResult(serverMessage, null, username, gameData)));

  }
}