package ui;

import static ui.EscapeSequences.*;

public class PrintFunctions {

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
      case NONE:
        spacingEnding = "";
        break;
      case REGULAR:
        break;
      case UNDER:
        spacingEnding += "\n";
        break;
      case SURROUND:
        spacingBeginning += "\n";
        spacingEnding += "\n";
        break;
      case DOUBLE_SURROUND:
        spacingBeginning += "\n\n";
        spacingEnding += "\n\n";
        break;
      default:
    }

    if (formatting != null) {
      System.out.print(formatting);
    }

    System.out.print(spacingBeginning + text + spacingEnding);

    System.out.print(RESET_ALL);
  }

  public static void helpText() {
    printf(
      "AVAILABLE COMMANDS",
      SpacingType.SURROUND,
      SET_TEXT_COLOR_YELLOW + SET_TEXT_BOLD
    );
    printf("help", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" : lists all available commands", SpacingType.REGULAR, null);
    printf("login", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" : login an existing user", SpacingType.REGULAR, null);
    printf("register", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" : register a new user", SpacingType.REGULAR, null);
    printf("quit", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" or ", SpacingType.NONE, null);
    printf("exit", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" : exit the program", SpacingType.UNDER, null);
  }

  public static void loginText() {}

  public static void registerText() {}
}
