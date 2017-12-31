package de.erikhofer.hashiwokahero;

import com.google.common.collect.ImmutableMap;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {
  
  /**
   * Image resources for component tiles. Dimension 1 = number of connections; Dimension 2 =
   * variant.
   */
  public static final Image[][] COMPONENTS = new Image[][] {
    {}, // there is no component with 0 connections
    { loadImage("component-1-red.png"), loadImage("component-1-black.png") },
    { loadImage("component-2-off.png"), loadImage("component-2-on.png") },
    { loadImage("component-3.png") },
    { loadImage("component-4.png") },
    { loadImage("component-5.png") },
    { loadImage("component-6.png") },
    { loadImage("component-7.png") },
    { loadImage("component-8.png") }
  };
  
  /**
   * Image resources for connected cables for each direction. Dimension 1 = variant.
   */
  public static final ImmutableMap<Direction, Image[]> CONNECTIONS = ImmutableMap.of(
      Direction.NORTH, new Image[] { loadImage("hole-n.png") },
      Direction.EAST, new Image[] { loadImage("hole-e.png") },
      Direction.SOUTH, new Image[] { loadImage("hole-s.png") },
      Direction.WEST, new Image[] { loadImage("hole-w.png") }
  );
  
  /**
   * Image resources for holes (unconnected connections).
   */
  public static final ImmutableMap<Orientation, Image> HOLES = ImmutableMap.of(
      Orientation.HORIZONTAL, loadImage("hole-h.png"),
      Orientation.VERTICAL, loadImage("hole-v.png")
  );
  
  /**
   * Image resources for cable tiles. Dimension 1 = variant.
   */
  public static final ImmutableMap<Orientation, Image[]> CABLES = ImmutableMap.of(
      Orientation.HORIZONTAL, new Image[] { loadImage("cable-h-yellow.png") },
      Orientation.VERTICAL, new Image[] { loadImage("cable-v-yellow.png") }
  );
  
  private static Image loadImage(String fileName) {
    try {
      return ImageIO.read(Resources.class.getResourceAsStream("/img/" + fileName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
