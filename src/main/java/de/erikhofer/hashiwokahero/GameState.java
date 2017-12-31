package de.erikhofer.hashiwokahero;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

public class GameState {
  
  private @Getter Tile[][] board;
  private final Tile[][] solution;
  private final @Getter int boardWidth;
  private final @Getter int boardHeight;
  private final @Getter ImmutableList<TilePosition> allTilePositions;
  private @Getter boolean gameOver;
  
  /**
   * Creates a game state with a randomly generated board with the given number of components.
   */
  public GameState(int components) {
    this(new BoardGenerator().generateBoard(components));
  }
  
  /**
   * Creates a new game state based on the given solution.
   */
  public GameState(Tile[][] solution) {
    this.solution = solution;
    boardHeight = solution.length;
    boardWidth = solution[0].length;
    
    // cache a list of all tile positions
    ImmutableList.Builder<TilePosition> allTilePositionsBuilder = 
        ImmutableList.builderWithExpectedSize(boardHeight * boardWidth);
    for (int row = 0; row < boardHeight; row++) {
      for (int col = 0; col < boardWidth; col++) {
        allTilePositionsBuilder.add(new TilePosition(row, col));
      }
    }
    allTilePositions = allTilePositionsBuilder.build();
    
    setBoardToSolution(true);
  }
  
  private void setBoardToSolution(boolean removeCables) {
    board = new Tile[boardHeight][boardWidth]; 
    for (int row = 0; row < boardHeight; row++) {
      for (int col = 0; col < boardWidth; col++) {
        Tile solutionTile = solution[row][col];
        if (solutionTile instanceof ComponentTile) {
          board[row][col] = new ComponentTile((ComponentTile) solutionTile);
        } else if (solutionTile instanceof CableTile) {
          CableTile boardTile = new CableTile((CableTile) solutionTile);
          if (removeCables) {
            boardTile.setCables(0);
          }
          board[row][col] = boardTile;
        } else {
          throw new RuntimeException("Unknown tile type!");
        }
      }
    }
  }
  
  public void setBoardToSolution() {
    setBoardToSolution(false);
  }
  
  /**
   * Checks if the current board is solved correctly. Calls
   * {@link ComponentTile#setCorrectlyConnected(boolean)} on all components accordingly.
   * 
   * @return whether the board is solved correctly
   */
  public boolean verifySolution() {
    // We can't simply compare the current board to the saved solution because there could be more
    // than one.
    
    boolean allComponentsCorrectlyConnected = true;
    for (TilePosition tilePosition : allTilePositions) {
      if (!isComponentTile(tilePosition)) {
        continue;
      }
      final ComponentTile componentTile = getTileAtPosition(tilePosition);
      final boolean correctlyConnected = getAdjacentCableCount(tilePosition)
          == componentTile.getConnections();
      componentTile.setCorrectlyConnected(correctlyConnected);
      if (!correctlyConnected) {
        allComponentsCorrectlyConnected = false;
      }
    }
    
    return allComponentsCorrectlyConnected;
  }
  
  private int getAdjacentCableCount(TilePosition tilePosition) {
    int cables = 0;
    for (Direction direction: Direction.values()) {
      TilePosition adjacentTilePosition = tilePosition.getAdjacent(direction);
      if (!isOutOfBoardBounds(adjacentTilePosition) && isCableTile(adjacentTilePosition)) {
        cables += this.<CableTile>getTileAtPosition(adjacentTilePosition).getCables();
      }
    }
    return cables;
  }
  
  /**
   * Returns whether the given tile position is outside of the board.
   */
  public boolean isOutOfBoardBounds(TilePosition tilePosition) {
    return tilePosition.getRow() < 0
        || tilePosition.getCol() < 0
        || tilePosition.getRow() >= boardHeight
        || tilePosition.getCol() >= boardWidth;
  }

  public boolean isComponentTile(TilePosition tilePosition) {
    return getTileAtPosition(tilePosition) instanceof ComponentTile;
  }

  public boolean isCableTile(TilePosition tilePosition) {
    return getTileAtPosition(tilePosition) instanceof CableTile;
  }

  @SuppressWarnings("unchecked")
  public <T extends Tile> T getTileAtPosition(TilePosition tilePosition) {
    return (T) board[tilePosition.getRow()][tilePosition.getCol()];
  }

  /**
   * Adds a cable between the given components, if possible.
   */
  public void tryToAddCableBetweenComponents(TilePosition component1, TilePosition component2) {
    Direction relativeDirection = component1.getDirectionRelativeTo(component2);
    if (relativeDirection == null) {
      return;
    }
    
    TilePosition current = component2.getAdjacent(relativeDirection);
    List<CableTile> cableTiles = new ArrayList<>();
    while (isCableTile(current)) {
      CableTile cableTile = getTileAtPosition(current);
      if (cableTile.getCables() == 2 || (cableTile.getCables() > 0 
          && cableTile.getOrientation() != relativeDirection.getOrientation())) {
        return; // there are already 2 cables or there is a crossing cable
      }
      cableTiles.add(cableTile);
      current = current.getAdjacent(relativeDirection);
    }
    
    cableTiles.forEach(cableTile -> {
      cableTile.increaseCables();
      cableTile.setOrientation(relativeDirection.getOrientation());
    });
  }

  /**
   * Returns all tile position that are belong to the same cable(s) as the given one or an empty
   * list if there is no cable on the given tile.
   */
  public List<CableTile> getFullCable(TilePosition tilePosition) {
    
    final CableTile cableTile = getTileAtPosition(tilePosition);
    if (cableTile.getCables() == 0) {
      return Collections.emptyList();
    }
    
    // look for the start in one direction
    final List<Direction> directions = Direction.forOrientation(cableTile.getOrientation());
    TilePosition start = tilePosition;
    while (true) {
      final TilePosition next = start.getAdjacent(directions.get(0));
      if (!isCableTile(next)) { // this can't be out of bounds
        break;
      }
      start = next;
    }
    
    // add tiles in the other direction
    final List<CableTile> fullCable = new ArrayList<>();
    TilePosition current = start;
    while (true) {
      fullCable.add(getTileAtPosition(current));
      final TilePosition next = current.getAdjacent(directions.get(1));
      if (!isCableTile(next)) { // this can't be out of bounds
        break;
      }
      current = next;
    }
    
    return fullCable;
  }
}
