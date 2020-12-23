package client.displayer;

import client.view.HeaderHandler;
import interfaces.Team;
import java.awt.Color;
import processing.core.PApplet;
import processing.core.PConstants;

public class HeaderHandlerDisplayer {

  /**
   * Draw the header of the client
   *
   * @param headerHandler Model containing the header values to display
   * @param sketch The object provided by Processing, to draw shapes
   */
  public static void draw(HeaderHandler headerHandler, PApplet sketch) {
    float headerHeight = sketch.height * 0.07f;
    sketch.textAlign(PConstants.CENTER);
    sketch.fill(0);
    sketch.textSize(24);
    sketch.text(
        "Timer : " + (int) headerHandler.gameTimer / 1000 + " seconds",
        sketch.width / 2,
        headerHeight);

    int i = 1;
    int textSize = 20;
    for (Team t : headerHandler.teams) {
      Color c = t.getColor();
      sketch.textAlign(PConstants.LEFT);
      sketch.textSize(textSize);
      sketch.fill(c.getRed(), c.getGreen(), c.getBlue());
      sketch.text("Score : " + t.getScore(), 10, i * textSize + 5);
      i++;
    }
  }
}
