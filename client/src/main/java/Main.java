import static ui.PrintFunctions.*;

import java.util.Scanner;
import ui.PrintFunctions;
import ui.SpacingType;

public class Main {

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    String userStatus = "LOGGED_OUT";

    printf(
      "♕ Welcome to Chess! Type 'help' to get started ♕",
      SpacingType.UNDER,
      null
    );

    while (true) {
      System.out.print("[" + userStatus + "] >>> ");
      String response = scanner.next();

      switch (response.toLowerCase()) {
        case "help" -> PrintFunctions.loggedOutHelpText();
        case "login" -> PrintFunctions.loginText();
        case "register" -> PrintFunctions.registerText();
      }

      if (
        response.equalsIgnoreCase("quit") || response.equalsIgnoreCase("exit")
      ) {
        break;
      }
    }
  }
}
