package de.erikhofer.hashiwokahero;

import static com.google.common.base.Preconditions.checkArgument;

import lombok.Getter;
import lombok.Setter;

public class CableTile extends Tile {
  
  @Getter
  @Setter
  private Orientation orientation;
  
  @Getter
  private int cables;
  
  public CableTile() {}
  
  /**
   * Creates a new cable tile with the given initial values.
   */
  public CableTile(int cables, Orientation orientation, int variant) {
    setCables(cables);
    setOrientation(orientation);
    setVariant(variant);
  }
  
  /**
   * Creates a copy of the given cable tile.
   */
  public CableTile(CableTile cableTile) {
    super(cableTile);
    cables = cableTile.cables;
    orientation = cableTile.orientation;
  }
  
  public void setCables(int cables) {
    checkArgument(cables >= 0 && cables <= 2);
    this.cables = cables;
  }
  
  /**
   * Removes one cable from this tile if possible.
   */
  public void decreaseCables() {
    if (cables > 0) {
      cables--;
    }
  }
  
  /**
   * Adds one cable to this tile if possible.
   */
  public void increaseCables() {
    if (cables < 2) {
      cables++;
    }
  }

}
