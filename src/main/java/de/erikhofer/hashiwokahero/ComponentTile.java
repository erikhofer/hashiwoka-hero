package de.erikhofer.hashiwokahero;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ComponentTile extends Tile {
  
  @Getter
  private final int connections;
  
  /**
   * Creates a copy of the given component tile.
   */
  public ComponentTile(ComponentTile componentTile) {
    super(componentTile);
    connections = componentTile.connections;
  }
  
}
