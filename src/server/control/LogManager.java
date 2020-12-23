package server.control;

import java.time.LocalDateTime;

;

public class LogManager {

  public static void writeLog(String message) {
    System.out.println("<" + LocalDateTime.now() + "> : " + message);
  }
}
