package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.database.DatabaseAuthDataAccess;
import dataaccess.database.DatabaseGameDataAccess;
import dataaccess.database.DatabaseUserDataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

  private final ConnectionManager connections = new ConnectionManager();
  private final UserService userService = new UserService(new DatabaseUserDataAccess(), new DatabaseAuthDataAccess());
  private final GameService gameService = new GameService(new DatabaseGameDataAccess(), new DatabaseAuthDataAccess());

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws IOException, DataAccessException {
    UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
    UserGameCommand.CommandType commandType = command.getCommandType();
    AuthData authData = userService.getAuthData(command.getAuthToken());
    int gameID = command.getGameID();
    String username = authData.username();

    System.out.println("(Server.WebSocketHandler::onMessage) onMessage triggered by " + username);

    switch (commandType) {
      case CONNECT -> join(username, gameID, session);
      case null, default -> System.out.println("Invalid Command.");
    }
  }

  private void join(String username, int gameID, Session session) throws IOException, DataAccessException {
    connections.add(username, session);
    System.out.println("(Server.WebSocketHandler::join) join triggered by " + username);

    GameData gameData = gameService.getGame(gameID);

    connections.loadGame(username, gameData);
  }

}
