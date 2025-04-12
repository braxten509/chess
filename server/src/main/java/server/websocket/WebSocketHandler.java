package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.database.DatabaseAuthDataAccess;
import dataaccess.database.DatabaseUserDataAccess;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

  private final ConnectionManager connections = new ConnectionManager();
  private final UserService userService = new UserService(new DatabaseUserDataAccess(), new DatabaseAuthDataAccess());

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws IOException, DataAccessException {
    UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
    UserGameCommand.CommandType commandType = command.getCommandType();
    AuthData authData = userService.getAuthData(command.getAuthToken());
    String username = authData.username();

    System.out.println("onMessage executed");

    switch (commandType) {
      case CONNECT -> join(username, session);
      case null, default -> System.out.println("Invalid Command.");
    }
  }

  private void join(String username, Session session) throws IOException {
    connections.add(username, session);
    System.out.println(connections);
    var message = String.format("%s joined the game", username);
    var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
    connections.broadcast(username, serverMessage);
  }

}
