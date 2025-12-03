package server.websocket;

import io.javalin.websocket.WsContext;

/** This class is used to transmit messages to the designated user. */
public class Connection {
  public String username;
  public WsContext context;

  /**
   * Constructor for Connection.java.
   *
   * @param username the username of the client with whom to communicate
   * @param context essentially the "session" to use
   */
  public Connection(String username, WsContext context) {
    this.username = username;
    this.context = context;
  }

  /**
   * Sends a message to the user.
   *
   * @param msg message to send
   */
  public void send(String msg) {
    context.send(msg);
  }
}
