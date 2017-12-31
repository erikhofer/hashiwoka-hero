package de.erikhofer.hashiwokahero;

import com.google.common.collect.ImmutableMap;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {
  
  public static final int VARIANT_1_POSITIVE = 0;
  public static final int VARIANT_1_NEGATIVE = 1;
  public static final int VARIANT_1_MISC = 2;
  public static final int VARIANT_2_GREEN_OFF = 0;
  public static final int VARIANT_2_GREEN_ON = 1;
  public static final int VARIANT_2_RED_OFF = 2;
  public static final int VARIANT_2_RED_ON = 3;
  
  /**
   * Image resources for component tiles. Dimension 1 = number of connections; Dimension 2 =
   * variant.
   */
  public static final Image[][] COMPONENTS = new Image[][] {
    {}, // there is no component with 0 connections
    { loadImage("component-1-red.png"), loadImage("component-1-black.png"),
      loadImage("component-1-blue.png") },
    { loadImage("component-2-off.png"), loadImage("component-2-on.png"),
      loadImage("component-2-red-off.png"), loadImage("component-2-red-on.png") },
    { loadImage("component-3.png"), loadImage("component-3-top.png"), },
    { loadImage("component-4.png"), loadImage("component-4-2.png") },
    { loadImage("component-5.png") },
    { loadImage("component-6.png") },
    { loadImage("component-7.png") },
    { loadImage("component-8.png") }
  };
  
  /**
   * Image resources for connected cables for each direction.
   */
  public static final ImmutableMap<Direction, Image> CONNECTIONS = ImmutableMap.of(
      Direction.NORTH, loadImage("hole-n.png"),
      Direction.EAST, loadImage("hole-e.png"),
      Direction.SOUTH, loadImage("hole-s.png"),
      Direction.WEST, loadImage("hole-w.png")
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
      Orientation.HORIZONTAL, new Image[] {
        loadImage("cable-h-blue.png"),
        loadImage("cable-h-brown.png"),
        loadImage("cable-h-green.png"),
        loadImage("cable-h-white.png"),
        loadImage("cable-h-yellow.png")
      },
      Orientation.VERTICAL, new Image[] {
        loadImage("cable-v-blue.png"),
        loadImage("cable-v-brown.png"),
        loadImage("cable-v-green.png"),
        loadImage("cable-v-white.png"),
        loadImage("cable-v-yellow.png")
      }
  );
  
  public static int getNumberOfCableVariants() {
    return CABLES.get(Orientation.HORIZONTAL).length;
  }
  
  public static int getNumberOfComponentVariants(int connections) {
    return COMPONENTS[connections].length;
  }
  
  private static Image loadImage(String fileName) {
    try {
      return ImageIO.read(Resources.class.getResourceAsStream("/img/" + fileName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
