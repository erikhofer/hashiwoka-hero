package de.erikhofer.hashiwokahero;

import lombok.NonNull;
import lombok.Value;

@Value
public class TilePosition {

  private int row;
  private int col;
  
  /**
   * Returns the tile position that is adjacent to this one in the given direction.
   */
  public TilePosition getAdjacent(@NonNull Direction direction) {
    switch (direction) {
      case NORTH:
        return new TilePosition(row - 1, col);
      case EAST:
        return new TilePosition(row, col + 1);
      case SOUTH:
        return new TilePosition(row + 1, col);
      case WEST:
        return new TilePosition(row, col - 1);
      default:
        throw new RuntimeException("Unkonwn direction");
    }
  }
  
  /**
   * Returns the direction this position lies in relative to the given position or null if both
   * positions are the same or don't share a row nor a column.
   */
  public Direction getDirectionRelativeTo(@NonNull TilePosition other) {
    if (equals(other)) {
      return null;
    }
    if (col == other.col) {
      return row < other.row ? Direction.NORTH : Direction.SOUTH;
    }
    if (row == other.row) {
      return col < other.col ? Direction.WEST : Direction.EAST;
    }
    return null;
  }
  
}
