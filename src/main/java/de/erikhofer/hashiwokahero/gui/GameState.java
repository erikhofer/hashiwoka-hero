package de.erikhofer.hashiwokahero.gui;

import lombok.Getter;

public class GameState {
  
  @Getter
  private Tile[][] board;
  
  public GameState() {
    CableTile c1 = new CableTile();
    c1.setCables(2);
    c1.setOrientation(Orientation.HORIZONTAL);
    CableTile c2 = new CableTile();
    c2.setCables(1);
    c2.setOrientation(Orientation.VERTICAL);
    
    this.board = new Tile[][] {
      {new ComponentTile(1), c1, new ComponentTile(2)},
      {new CableTile(), new CableTile(), c2},
      {new CableTile(), new CableTile(), new ComponentTile(1)},
    };
  }

}
