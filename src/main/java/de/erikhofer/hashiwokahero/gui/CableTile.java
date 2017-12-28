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

}
