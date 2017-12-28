package de.erikhofer.hashiwokahero.gui;

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
  
}
