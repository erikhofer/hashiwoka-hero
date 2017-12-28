package de.erikhofer.hashiwokahero.gui;

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
    { loadImage("1.png") },
    { loadImage("2.png") }
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
  public static final ImmutableMap<Orientation, Image[]> CABELS = ImmutableMap.of(
      Orientation.HORIZONTAL, new Image[] { loadImage("cable-h.png") },
      Orientation.VERTICAL, new Image[] { loadImage("cable-v.png") }
  );
  
  private static Image loadImage(String fileName) {
    try {
      return ImageIO.read(Resources.class.getResourceAsStream("/img/" + fileName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
