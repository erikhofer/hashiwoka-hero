package de.erikhofer.hashiwokahero;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ComponentTile extends Tile {
  
  private final @Getter int connections;
  private @Getter @Setter boolean correctlyConnected;
  
  /**
   * Creates a copy of the given component tile.
   */
  public ComponentTile(ComponentTile componentTile) {
    super(componentTile);
    connections = componentTile.connections;
    correctlyConnected = componentTile.correctlyConnected;
  }
  
}
