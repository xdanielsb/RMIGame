package client.displayer;

import interfaces.Board;
import interfaces.FeedableObject;
import interfaces.Food;
import interfaces.Player;
import java.util.ArrayList;
import java.util.Collections;
import processing.core.PApplet;

public class BoardDisplayer {

  double screenSize;
  double initialPlayerScreenProportion;
  double initialPlayerSize;
  double maximumPlayerScreenProportion;
  double maximumPlayerSize;

  double maximumPlayerSizeMargeRatio;

  public BoardDisplayer(
      double screenSize,
      double initialPlayerScreenProportion,
      double initialPlayerSize,
      double maximumPlayerScreenProportion,
      double maximumPlayerSize) {
    this.screenSize = screenSize;
    this.initialPlayerScreenProportion = initialPlayerScreenProportion;
    this.initialPlayerSize = initialPlayerSize;
    this.maximumPlayerScreenProportion = maximumPlayerScreenProportion;
    this.maximumPlayerSize = maximumPlayerSize;

    maximumPlayerSizeMargeRatio = Math.sqrt(maximumPlayerSize - initialPlayerSize);
  }

  /**
   * Draws the differents elements composing the board of the game.
   *
   * @param board The board of the game
   * @param player Player controlled by the client
   * @param centerX Center of the X axis
   * @param centerY Center of the Y axis
   * @param sketch The object provided by Processing, to draw shapes
   */
  public void draw(Board board, Player player, float centerX, float centerY, PApplet sketch) {
    sketch.pushMatrix();
    float zoomRatio = calculateScale(player);
    sketch.scale(zoomRatio);
    sketch.strokeWeight(1);
    sketch.translate((centerX / zoomRatio) - player.getX(), (centerY / zoomRatio) - player.getY());
    for (Food food : board.getFoods()) {
      if (food.isAlive()) {
        FoodDisplayer.draw(food, sketch);
      }
    }
    ArrayList<FeedableObject> cells = new ArrayList<>();
    for (Player p : board.getPlayers()) {
      if (p.isAlive()) {
        cells.addAll(p.getCells());
      }
    }
    cells.addAll(board.getSpikeCells());
    Collections.sort(cells);
    for (FeedableObject cellToDraw : cells) {
      FeedableDisplayer.draw(cellToDraw, sketch);
    }
    OuterBoundsDisplayer.draw(board, player.getX(), player.getY(), sketch);
    sketch.popMatrix();
  }

  /**
   * Calculate and return the zoom ratio depending on the player size.
   *
   * @param player Player controlled by the client
   * @return The value of the zoom ratio
   */
  public float calculateScale(Player player) {
    double playerScreenProportion;
    if (player.getSize() < initialPlayerSize) {
      playerScreenProportion = initialPlayerScreenProportion;
    } else if (player.getSize() > maximumPlayerSize) {
      playerScreenProportion = maximumPlayerScreenProportion;
    } else {
      playerScreenProportion =
          initialPlayerScreenProportion
              + (maximumPlayerScreenProportion - initialPlayerScreenProportion)
                  * (Math.sqrt(player.getSize() - initialPlayerSize) / maximumPlayerSizeMargeRatio);
    }
    double newScreenSize = player.getRadius() * 2 / playerScreenProportion;
    return (float) (screenSize / newScreenSize);
  }
}
