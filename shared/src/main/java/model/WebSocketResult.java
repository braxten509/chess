package model;

import websocket.messages.ServerMessage;

public record WebSocketResult(ServerMessage serverMessage, String message, String username, GameData data) { }