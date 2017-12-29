package de.erikhofer.hashiwokahero;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ComponentTile extends Tile {
  
  @Getter
  private final int connections;
  
}
