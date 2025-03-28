import static ui.EscapeSequences.*;
import static ui.PrintFunctions.*;

import java.util.Scanner;
import ui.PrintFunctions;
import ui.SpacingType;

public class Main {

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    printf(
      "♕ Welcome to Chess! Type 'help' to get started ♕",
      SpacingType.UNDER,
      null
    );

    while (true) {
      System.out.print(">>> ");
      String response = scanner.next();

      switch (response.toLowerCase()) {
        case "help":
          PrintFunctions.helpText();
          break;
        case "login":
          PrintFunctions.loginText();
          break;
        case "register":
          PrintFunctions.registerText();
          break;
        default:
      }

      if (
        response.equalsIgnoreCase("quit") || response.equalsIgnoreCase("exit")
      ) {
        break;
      }
    }
  }
}
