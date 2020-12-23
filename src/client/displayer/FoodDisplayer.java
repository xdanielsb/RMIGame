package client.displayer;

import interfaces.Food;
import java.awt.Color;
import processing.core.PApplet;

public class FoodDisplayer {

  /**
   * Draw a food on the board
   *
   * @param food A food to draw
   * @param sketch The object provided by Processing, to draw shapes
   */
  public static void draw(Food food, PApplet sketch) {
    sketch.stroke(0);
    sketch.strokeWeight(1);
    Color foodColor = food.getColor();
    sketch.fill(foodColor.getRed(), foodColor.getGreen(), foodColor.getBlue());
    float diameter;
    if (food.isPersistent()) {
      diameter = 3;
    } else {
      diameter = food.getRadius() * 2;
    }
    sketch.circle((float) (food.getX()), (float) (food.getY()), diameter);
  }
}
