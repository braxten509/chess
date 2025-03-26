package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import com.google.gson.Gson;
import model.CreateGameRequest;
import model.JoinGameRequest;
import model.LoginRequest;
import model.RegisterRequest;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public Object registerUser(String username, String password, String email) {
        String path = "/user";
        return this.makeRequest("POST", path, null, new RegisterRequest(username, password, email), RegisterRequest.class);
    }

    public Object loginUser(String username, String password) {
        String path = "/session";
        return this.makeRequest("POST", path, null, new LoginRequest(username, password), LoginRequest.class);
    }

    public Object createGame(String authToken, String gameName) {
        String path = "/game";
        return this.makeRequest("POST", path, mapAuthToken(authToken), gameName, CreateGameRequest.class);
    }

    /**
     * Connects a player to a game if possible
     * @param authToken authToken
     * @param playerColor this is either "WHITE" or "BLACK"
     * @param gameID game ID
     * @return response from server
     */
    public Object joinGame(String authToken, String playerColor, int gameID) {
        String path = "/game";
        return this.makeRequest("PUT", path, mapAuthToken(authToken), new JoinGameRequest(playerColor, gameID), JoinGameRequest.class);
    }

    private HashMap<String, String> mapAuthToken(String authToken) {
        var mappedAuthToken = new HashMap<String, String>();
        mappedAuthToken.put("Authorization", authToken);
        return mappedAuthToken;
    }

    private <T> T makeRequest(String method, String path, Map<String,String> headers, Object request, Class<T> responseClass) throws RuntimeException {
        try {

            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

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

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            Gson gson = new Gson();
            String reqData = gson.toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, RuntimeException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream responseError = http.getErrorStream()) {
                if (responseError != null) {
                    throw new RuntimeException("ERROR CODE " + responseError);
                }
            }

            throw new RuntimeException(status + "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream responseBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(responseBody);
                if (responseClass != null) {
                    Gson gson = new Gson();
                    response = gson.fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
