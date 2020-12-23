package client.displayer;

import interfaces.FeedableObject;
import interfaces.PlayerCell;
import interfaces.SpikeCell;
import java.awt.Color;
import processing.core.PApplet;
import processing.core.PConstants;

public class FeedableDisplayer {

  /**
   * Draw a cell of a player
   *
   * @param cell One of the different cells of the player
   * @param sketch The object provided by Processing, to draw shapes
   */
  public static void draw(PlayerCell cell, PApplet sketch) {
    sketch.stroke(0);
    sketch.strokeWeight(1);
    sketch.pushMatrix();
    sketch.translate(cell.getX(), cell.getY());

    float textSize =
        3; // (int)(1 - (5f*PlayerCell.CELL_MIN_SIZE/(player.getCell().getRadius()*2)));

    Color playerColor = cell.getColor();
    sketch.fill(playerColor.getRed(), playerColor.getGreen(), playerColor.getBlue());
    sketch.circle(0, 0, cell.getRadius() * 2);
    sketch.fill(255);
    sketch.textAlign(PConstants.CENTER);
    sketch.textSize(textSize);
    sketch.text(cell.getPlayer().getName(), -textSize / 6, -textSize);
    sketch.text(cell.getSize(), -textSize / 6, textSize);

    sketch.popMatrix();
  }

  /**
   * Draw a spike cell on the board.
   *
   * @param cell A spike cell
   * @param sketch The object provided by Processing, to draw shapes
   */
  public static void draw(SpikeCell cell, PApplet sketch) {
    sketch.stroke(0);
    sketch.strokeWeight(1);
    sketch.pushMatrix();
    sketch.translate(cell.getX(), cell.getY());
    Color color = cell.getColor();
    sketch.fill(color.getRed(), color.getGreen(), color.getBlue());
    sketch.rectMode(PConstants.CENTER);

    for (int i = 0; i < 6; i++) {
      sketch.pushMatrix();
      sketch.rotate(PApplet.radians(i * 15f));
      sketch.square(0, 0, cell.getRadius() * 1.6f);
      sketch.popMatrix();
    }

    sketch.rectMode(PConstants.CORNER);
    sketch.noStroke();
    sketch.circle(0, 0, cell.getRadius() * 2);

    sketch.popMatrix();
  }

  /**
   * Get a feedable object in parameter and call the right function to draw it
   *
   * @param cell A feedable object cell
   * @param sketch The object provided by Processing, to draw shapes
   */
  public static void draw(FeedableObject cell, PApplet sketch) {
    if (cell instanceof PlayerCell) {
      draw((PlayerCell) cell, sketch);
    } else if (cell instanceof SpikeCell) {
      draw((SpikeCell) cell, sketch);
    }
  }
}
