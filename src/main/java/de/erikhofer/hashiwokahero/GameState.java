package de.erikhofer.hashiwokahero;

import lombok.Getter;

public class GameState {
  
  private final @Getter Tile[][] board;
  private final @Getter int boardWidth;
  private final @Getter int boardHeight;
  
  public GameState() {
    board = new BoardGenerator().generateBoard(5);
    boardHeight = board.length;
    boardWidth = board[0].length;
  }

}
