package de.erikhofer.hashiwokahero.gui;

import static com.google.common.base.Preconditions.checkArgument;

import lombok.Getter;
import lombok.Setter;

public class CableTile extends Tile {
  
  @Getter
  @Setter
  private Orientation orientation;
  
  @Getter
  private int cables;
  
  public void setCables(int cables) {
    checkArgument(cables >= 0 && cables <= 2);
    this.cables = cables;
  }
  
  /**
   * Removes one cable from this tile if possible.
   */
  public void decreaseCables() {
    if (cables != 0) {
      cables--;
    }
  }
  
  /**
   * Adds one cable to this tile if possible.
   */
  public void increaseCables() {
    if (cables != 2) {
      cables++;
    }
  }

}
