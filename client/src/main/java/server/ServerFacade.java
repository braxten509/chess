package server;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import java.io.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import model.*;

public class ServerFacade {

  private final int port;

  public ServerFacade(int port) {
    this.port = port;
  }

  public RegisterResult registerUser(
    String username,
    String password,
    String email
  ) {
    String path = "/user";
    return this.makeRequest(
        "POST",
        path,
        null,
        new RegisterRequest(username, password, email),
        RegisterResult.class
      );
  }

  public AuthData loginUser(String username, String password) {
    String path = "/session";
    return this.makeRequest(
        "POST",
        path,
        null,
        new LoginRequest(username, password),
        AuthData.class
      );
  }

  public CreateGameResult createGame(String authToken, String gameName) {
    String path = "/game";
    HashMap<String, String> header = mapAuthToken(authToken);
    return this.makeRequest(
        "POST",
        path,
        header,
        new CreateGameRequest(null, gameName),
        CreateGameResult.class
      );
  }

  /**
   * Connects a player to a game if possible
   * @param authToken authToken
   * @param playerColor this is either "WHITE" or "BLACK"
   * @param gameID game ID
   */
  public void joinGame(String authToken, String playerColor, int gameID) {
    String path = "/game";
    HashMap<String, String> header = mapAuthToken(authToken);
    this.makeRequest(
        "PUT",
        path,
        header,
        new JoinGameRequest(null, playerColor, gameID),
        null
      );
  }

  public ListGamesResult listGames(String authToken) {
    String path = "/game";
    HashMap<String, String> header = mapAuthToken(authToken);
    return this.makeRequest("GET", path, header, null, ListGamesResult.class);
  }

  public void logoutUser(String authToken) {
    String path = "/session";
    HashMap<String, String> header = mapAuthToken(authToken);
    this.makeRequest("DELETE", path, header, null, null);
  }

  public void clearDatabase() {
    String path = "/db";
    this.makeRequest("DELETE", path, null, null, null);
  }

  private HashMap<String, String> mapAuthToken(String authToken) {
    var mappedAuthToken = new HashMap<String, String>();
    mappedAuthToken.put("Authorization", authToken);
    return mappedAuthToken;
  }

  private <T> T makeRequest(
    String method,
    String path,
    Map<String, String> headers,
    Object request,
    Class<T> responseClass
  ) throws RuntimeException {
    try {
      URL url = (new URI("http://localhost:" + port + path)).toURL();
      HttpURLConnection http = (HttpURLConnection) url.openConnection();
      http.setRequestMethod(method);

      http.setDoOutput(method.equals("POST") || method.equals("PUT"));

      if (headers != null) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
          http.setRequestProperty(header.getKey(), header.getValue());
        }
      }

      writeBody(request, http);
      http.connect();
      throwIfNotSuccessful(http);
      return readBody(http, responseClass);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void writeBody(Object request, HttpURLConnection http)
    throws IOException {
    if (request != null && http.getDoOutput()) {
      http.addRequestProperty("Content-Type", "application/json");
      Gson gson = new Gson();
      String reqData = gson.toJson(request);
      try (OutputStream reqBody = http.getOutputStream()) {
        reqBody.write(reqData.getBytes());
      }
    }
  }

  private void throwIfNotSuccessful(HttpURLConnection http)
    throws IOException, RuntimeException {
    var status = http.getResponseCode();
    if (!isSuccessful(status)) {
      if (http.getErrorStream() != null) {
        String errorMessage = new BufferedReader(
          new InputStreamReader(http.getErrorStream())
        )
          .lines()
          .collect(Collectors.joining());
        throw new RuntimeException("(" + status + ") ERROR: " + errorMessage);
      } else {
        throw new RuntimeException("(" + status + ") ERROR: " + status);
      }
    }
  }

  private static <T> T readBody(HttpURLConnection http, Class<T> responseClass)
    throws IOException {
    if (responseClass == null) {
      return null;
    }
    try (InputStream responseBody = http.getInputStream()) {
      InputStreamReader reader = new InputStreamReader(responseBody);
      Gson gson = new Gson();
      return gson.fromJson(reader, responseClass);
    } catch (JsonIOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isSuccessful(int status) {
    return status / 100 == 2;
  }
}
