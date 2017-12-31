package de.erikhofer.hashiwokahero;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Direction {
  
  NORTH(Orientation.VERTICAL),
  EAST(Orientation.HORIZONTAL),
  SOUTH(Orientation.VERTICAL),
  WEST(Orientation.HORIZONTAL);
  
  /**
   * Returns a list of all direction with the given orientation.
   */
  public static ImmutableList<Direction> forOrientation(Orientation orientation) {
    // this could be cached
    ImmutableList.Builder<Direction> builder = ImmutableList.builderWithExpectedSize(2);
    for (Direction direction : values()) {
      if (direction.orientation == orientation) {
        builder.add(direction);
      }
    }
    return builder.build();
  }
  
  private final @Getter Orientation orientation;

}
