package server.websocket;

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

  public void broadcast(String triggeringUser, ServerMessage serverMessage) throws IOException {
    var removeList = new ArrayList<Connection>();
    for (var connection : connections.values()) {
      if (!connection.session.isOpen()) {
        removeList.add(connection);
        continue;
      }

      if (!connection.username.equals(triggeringUser)) {
        connection.send(serverMessage.toString());
      }
    }

    for (var connection : removeList) {
      connections.remove(connection.username);
    }
  }
}