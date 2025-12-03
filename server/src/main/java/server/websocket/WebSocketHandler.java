package server.websocket;

// NOTE: This is from your Spark implementation - THIS NEEDS COMPLETE REWRITE FOR JAVALIN
// Spark uses @WebSocket annotation and @OnWebSocketMessage
// Javalin uses: javalin.ws("/ws", ws -> { ws.onConnect(...), ws.onMessage(...), ws.onClose(...) })
// See Javalin WebSocket documentation for proper implementation

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.database.DatabaseAuthDataAccess;
import dataaccess.database.DatabaseGameDataAccess;
import dataaccess.database.DatabaseUserDataAccess;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;

public class WebSocketHandler {

  private static final ConnectionManager connections = new ConnectionManager();
  private static final UserService userService =
      new UserService(new DatabaseUserDataAccess(), new DatabaseAuthDataAccess());
  private static final GameService gameService =
      new GameService(new DatabaseGameDataAccess(), new DatabaseAuthDataAccess());

  public static void onMessage(WsMessageContext context) throws DataAccessException {
    UserGameCommand command = new Gson().fromJson(context.message(), UserGameCommand.class);
    UserGameCommand.CommandType commandType = command.getCommandType();
    AuthData authData = userService.getAuthData(command.getAuthToken());
    int gameId = command.getGameID();
    String username = authData.username();

    System.out.println(
        "(Server.WebSocketHandler::onMessage) onMessage triggered by '" + username + "'");

    switch (commandType) {
      case CONNECT -> join(username, gameId, context);
      case MAKE_MOVE -> notify(username);
      case null, default -> System.out.println("Invalid Command.");
    }
  }

  private static void notify(String triggeringUser) {
    System.out.println(
        "(Server.WebSocketHandler::notify) notify triggered by '" + triggeringUser + "'");

    connections.broadcast(triggeringUser, triggeringUser + " made a move");
  }

  private static void join(String username, int gameId, WsContext context)
      throws DataAccessException {
    connections.add(username, context);
    System.out.println("(Server.WebSocketHandler::join) join triggered by '" + username + "'");

    GameData gameData = gameService.getGame(gameId);

    connections.loadGame(username, gameData);
  }
}
