package de.erikhofer.hashiwokahero;

import lombok.Getter;
import lombok.Setter;

public abstract class Tile {
  
  @Getter
  @Setter
  private int variant;
  
  protected Tile() {}
  
  protected Tile(Tile tile) {
    variant = tile.variant;
  }
}
