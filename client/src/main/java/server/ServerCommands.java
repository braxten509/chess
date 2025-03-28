package server;

import model.AuthData;
import ui.SpacingType;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class ServerCommands {

  public static String userStatus = "LOGGED_OUT";
  private static final Scanner scanner = new Scanner(System.in);
  private static String authToken = "";

  /**
   * Returns the same output as System.out.print() but with formatting
   * @param text text to print (everything will be formatted as specified)
   * @param formatting formatting. Include multiple with '+'
   * @param spacing specifies spacing around text
   */
  public static void printf(
    String text,
    SpacingType spacing,
    String formatting
  ) {
    String spacingBeginning = "";
    String spacingEnding = "\n";

    switch (spacing) {
      case NONE -> spacingEnding = "";
      case REGULAR -> {}
      case ABOVE -> spacingBeginning += "\n";
      case UNDER -> spacingEnding += "\n";
      case SURROUND -> {
          spacingBeginning += "\n";
          spacingEnding += "\n";
      }
      case DOUBLE_SURROUND -> {
        spacingBeginning += "\n\n";
        spacingEnding += "\n\n";
      }
    }

    if (formatting != null) {
      System.out.print(formatting);
    }

    System.out.print(spacingBeginning + text + spacingEnding);

    System.out.print(RESET_ALL);
  }

  public static void clearCommand() {
    printf("", SpacingType.REGULAR, ERASE_SCREEN);
    System.out.flush();
    printf(
            "♕ Welcome to Chess! Type 'help' to get started ♕",
            SpacingType.UNDER,
            null
    );
  }

  public static void helpCommand() {
    printf(
      "AVAILABLE COMMANDS",
      SpacingType.SURROUND,
      SET_TEXT_COLOR_YELLOW + SET_TEXT_BOLD
    );
    printf("help", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" : lists all available commands", SpacingType.REGULAR, null);
    if (userStatus.equals("LOGGED_OUT")) {
      printf("login", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
      printf(" : login an existing user", SpacingType.REGULAR, null);
      printf("register", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
      printf(" : register a new user", SpacingType.REGULAR, null);
    } else {
      printf("logout", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
      printf(" : logout of current session", SpacingType.REGULAR, null);
      printf("create <name>", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
      printf(" : create a new game", SpacingType.REGULAR, null);
      printf("list", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
      printf(" : list games", SpacingType.REGULAR, null);
      printf("join <ID> [WHITE|BLACK]", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
      printf(" : join an existing game", SpacingType.REGULAR, null);
      printf("observe <ID>", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
      printf(" : watch an ongoing game", SpacingType.REGULAR, null);
    }
    printf("clear", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" : clears the current terminal", SpacingType.REGULAR, null);
    printf("quit", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" or ", SpacingType.NONE, null);
    printf("exit", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" : exit the program", SpacingType.UNDER, null);
  }

  public static void loginCommand(ServerFacade serverFacade) {
    String username, password;
    printf(
            "LOGIN PANEL",
            SpacingType.ABOVE,
            SET_TEXT_COLOR_YELLOW + SET_TEXT_BOLD
    );

    printf("Enter username: ", SpacingType.REGULAR, SET_TEXT_COLOR_BLUE);
    printf(">>> ", SpacingType.NONE, null);
    username = scanner.next();

    printf("Enter password: ", SpacingType.ABOVE, SET_TEXT_COLOR_BLUE);
    printf(">>> ", SpacingType.NONE, null);
    password = scanner.next();

    try {
      AuthData authData = serverFacade.loginUser(username, password);
      authToken = authData.authToken();

      printf("Success! You have been logged in", SpacingType.SURROUND, SET_TEXT_COLOR_GREEN);
      userStatus = username.toUpperCase();
    } catch (Exception e) {
      printf("Error: Invalid credentials", SpacingType.ABOVE, SET_TEXT_COLOR_RED);
      loginCommand(serverFacade);
    }
  }

  public static void logoutCommand(ServerFacade serverFacade) {
    try {
      serverFacade.logoutUser(authToken);
      userStatus = "LOGGED_OUT";
      printf("Logout successful!", SpacingType.SURROUND, SET_TEXT_COLOR_GREEN);
    } catch (Exception e) {
      printf("Error logging out", SpacingType.SURROUND, SET_TEXT_COLOR_RED);
    }
  }

  public static void registerCommand(ServerFacade serverFacade) {
    String username, password, email;
    printf(
            "USER REGISTRATION",
            SpacingType.ABOVE,
            SET_TEXT_COLOR_YELLOW + SET_TEXT_BOLD
    );

    printf("Type desired username: ", SpacingType.REGULAR, SET_TEXT_COLOR_BLUE);
    printf(">>> ", SpacingType.NONE, null);
    username = scanner.next();

    while (true) {
      printf("Type desired password: ", SpacingType.ABOVE, SET_TEXT_COLOR_BLUE);
      printf(">>> ", SpacingType.NONE, null);
      password = scanner.next();

      printf("Confirm password: ", SpacingType.ABOVE, SET_TEXT_COLOR_BLUE);
      printf(">>> ", SpacingType.NONE, null);
      String passwordConfirmation = scanner.next();

      if (!password.equals(passwordConfirmation)) {
        printf("Error: Passwords do not match", SpacingType.ABOVE, SET_TEXT_COLOR_RED);
      } else {
        break;
      }
    }

    printf("Type desired email: ", SpacingType.ABOVE, SET_TEXT_COLOR_BLUE);
    printf(">>> ", SpacingType.NONE, null);
    email = scanner.next();
    printf("", SpacingType.REGULAR, null);

    try {
      serverFacade.registerUser(username, password, email);

      AuthData authData = serverFacade.loginUser(username, password);
      authToken = authData.authToken();

      printf("Success! You have been logged in", SpacingType.UNDER, SET_TEXT_COLOR_GREEN);
      userStatus = username.toUpperCase();
    } catch (Exception e) {
      if (e.getMessage().equals("java.lang.RuntimeException: (403) ERROR: {\"message\":\"Error: already taken\"}")) {
        printf("Error: Username already taken", SpacingType.REGULAR, SET_TEXT_COLOR_RED);
        registerCommand(serverFacade);
      }
    }
  }
}
