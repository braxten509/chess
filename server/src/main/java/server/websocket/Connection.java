package server.websocket;

// NOTE: This is from your Spark implementation - needs to be adapted for Javalin
// Javalin uses io.javalin.websocket.WsConnectContext instead of org.eclipse.jetty.websocket.api.Session

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
  public String username;
  public Session session;

  public Connection(String username, Session session) {
    this.username = username;
    this.session = session;
  }

  public void send(String msg) throws IOException {
    session.getRemote().sendString(msg);
  }
}