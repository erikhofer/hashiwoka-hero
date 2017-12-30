package de.erikhofer.hashiwokahero;

import lombok.Getter;

public class GameState {
  
  private @Getter Tile[][] board;
  private final Tile[][] solution;
  private final @Getter int boardWidth;
  private final @Getter int boardHeight;
  private @Getter boolean gameOver;
  
  public GameState(int components) {
    this(new BoardGenerator().generateBoard(components));
  }
  
  public GameState(Tile[][] solution) {
    this.solution = solution;
    boardHeight = solution.length;
    boardWidth = solution[0].length;
    
    // create a copy of the solution and remove all cables
    board = new Tile[boardHeight][boardWidth]; 
    for (int row = 0; row < boardHeight; row++) {
      for (int col = 0; col < boardWidth; col++) {
        Tile solutionTile = solution[row][col];
        if (solutionTile instanceof ComponentTile) {
          board[row][col] = new ComponentTile((ComponentTile) solutionTile);
        } else if (solutionTile instanceof CableTile) {
          CableTile boardTile = new CableTile((CableTile) solutionTile);
          boardTile.setCables(0);
          board[row][col] = boardTile;
        } else {
          throw new RuntimeException("Unknown tile type!");
        }
      }
    }
  }
  
  public void setBoardToSolution() {
    board = solution;
  }
}
