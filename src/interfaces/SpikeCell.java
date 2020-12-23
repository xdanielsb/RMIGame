package interfaces;

import java.awt.Color;

public interface SpikeCell extends FeedableObject {

  static final long serialVersionUID = 1L;

  public static final Color SPIKE_COLOR = new Color(0, 255, 0);
  public static final int MIN_SPIKE_SIZE = 400;
  public static final int MAX_SPIKE_SIZE = 800;
  public static final int EXPLOSION_NUMBER = 20;
  public static final float EXPLOSION_INITIAL_RATIO = 0.1f;
}
