package de.erikhofer.hashiwokahero.gui;

import java.awt.Point;
import java.util.function.BiConsumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Direction {
  
  NORTH(Orientation.VERTICAL, (p, d) -> p.y -= d),
  EAST(Orientation.HORIZONTAL, (p, d) -> p.x += d),
  SOUTH(Orientation.VERTICAL, (p, d) -> p.y += d),
  WEST(Orientation.HORIZONTAL, (p, d) -> p.x -= d);
  
  @Getter
  private final Orientation orientation;
  private final BiConsumer<Point, Integer> mover;
  
  /**
   * Returns a new point that is the given distance away from the given point in this direction.
   */
  public Point move(Point source, int distance) {
    Point target = new Point(source);
    mover.accept(target, distance);
    return target;
  }

}
