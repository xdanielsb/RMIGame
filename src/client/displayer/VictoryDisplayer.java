package client.displayer;

import interfaces.Team;
import java.awt.Color;
import processing.core.PApplet;
import processing.core.PConstants;

public class VictoryDisplayer {

  /**
   * Draw display the text saying which team won.
   *
   * @param winner Winning team of the game
   * @param sketch The object provided by Processing, to draw shapes
   */
  public static void draw(Team winner, PApplet sketch) {
    sketch.textSize(80);
    sketch.textAlign(PConstants.CENTER);
    Color teamColor = winner.getColor();
    sketch.fill(teamColor.getRed(), teamColor.getGreen(), teamColor.getBlue());
    sketch.text("Team " + winner.getTeamName() + " wins", sketch.width / 2, sketch.height / 2);
  }
}
