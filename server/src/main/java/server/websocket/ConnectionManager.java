package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
  public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

  public void add(String username, Session session) {
    var connection = new Connection(username, session);
    connections.put(username, connection);
  }

  public void broadcast(String triggeringUser, ServerMessage serverMessage) {
    var removeList = new ArrayList<Connection>();
    for (var connection : connections.values()) {
      if (!connection.session.isOpen()) {
        removeList.add(connection);
        System.out.println("(Server.ConnectionManager::broadcast) successfully added connection " + connection.username + " to be removed list");
        continue;
      }

      if (!connection.username.equals(triggeringUser)) {
        try {
          connection.send(new Gson().toJson(serverMessage));
          System.out.println("(Server.ConnectionManager::broadcast) successfully sent message to everyone except " + triggeringUser);
        } catch (Exception e) {
          System.out.println("(Server.ConnectionManager::broadcast) error while sending message back to client: " + e.getMessage());
        }
      }
    }

    for (var connection : removeList) {
      connections.remove(connection.username);
      System.out.println("(Server.ConnectionManager::broadcast) successfully removed connection to " + connection.username);
    }
  }
}