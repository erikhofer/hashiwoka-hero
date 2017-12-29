package de.erikhofer.hashiwokahero;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;
import lombok.Getter;

public class BoardGenerator {
  
  private static final double CONTINUE_CABLE_PROBABILITY = 0.5;
  
  @Getter
  private final long seed;
  private final Random random;
  
  /**
   * Creates a new BoardGenerator with a random seed.
   */
  public BoardGenerator() {
    random = new Random();
    seed = random.nextLong();
    random.setSeed(seed);
  }
  
  public BoardGenerator(long seed) {
    this.seed = seed;
    random = new Random(seed);
  }
  
  /**
   * Generated a random board with the given number of components.
   */
  public Tile[][] generateBoard(int components) {
    checkArgument(components >= 2, "There must be at least 2 components on a board!");
    
    // The algorithm is based on this paper:
    // http://liacs.leidenuniv.nl/assets/Bachelorscripties/2009-11TimoMorsink.pdf
    
    // We don't know the size of the board yet. Thus we allow arbitrary size in all directions (also
    // negative) and transform to a two-dimensional array at the end.
    final Map<TilePosition, Tile> board = new HashMap<>();
    
    // Place an initial component with no connections.
    board.put(new TilePosition(0, 0), new ComponentTile(0));
    
    for (int i = 1; i < components; i++) {
      // To place a new component, select a random existing one and try to place a cable of random
      // length in a random direction and place the new component at the end of the cable. If this
      // is not possible, chose a new component. The paper implies (but not explicitly states) that
      // there is always at least one component where this is possible.
      tryOrigin: while (true) {
        // Select a random origin component
        final TilePosition originPosition = board.entrySet().stream()
            .filter(e -> e.getValue() instanceof ComponentTile)
            .skip(random.nextInt(i))
            .findFirst().get().getKey();
        
        // Select a random direction
        final List<Direction> directions = Arrays.asList(Direction.values());
        Collections.shuffle(directions, random);
        tryDirection: for (Direction direction : directions) {
          final TilePosition cableStart = originPosition.getAdjacent(direction);
          if (board.containsKey(cableStart)) {
            // Either this side is already connected or there is an adjacent crossing cable
            // (components are not placed next to each other). Try next direction.
            continue; 
          }
          
          final int newCableCount = random.nextInt(2) + 1; // 1 or 2
          final Stack<TilePosition> newCablePositions = new Stack<>();
          newCablePositions.push(cableStart);
          
          // Advance the cable until we hit a crossing cable or decide to stop by random.
          TilePosition current = cableStart;
          do {
            current = current.getAdjacent(direction);
            newCablePositions.push(current);
            if (board.containsKey(current)) {
              break; // We hit a crossing cable.
            }
          } while (random.nextDouble() < CONTINUE_CABLE_PROBABILITY);
          
          // We want to place the new component at the last newCablePosition. If this is not
          // possible, take one step back; repeat. If no valid position is found, try next 
          // direction.
          TilePosition newComponentPosition;
          while (!isComponentPlacableAt(newComponentPosition = newCablePositions.pop(), board)) {
            if (newCablePositions.isEmpty()) {
              continue tryDirection;
            }
          }
          
          // Place new cables.
          newCablePositions.forEach(position -> 
              board.put(position, new CableTile(newCableCount, direction.getOrientation())));
          
          // Place new component.
          final int connections = getAdjacentCableCount(newComponentPosition, board);
          board.put(newComponentPosition, new ComponentTile(connections));
          
          // Update origin.
          final int originConnections = getAdjacentCableCount(originPosition, board);
          board.put(originPosition, new ComponentTile(originConnections));
          
          break tryOrigin;
        }
        // Can't place a new component in any direction. Select a new origin.
      }
    }
    
    return transformBoard(board);
  }
  
  private boolean isComponentPlacableAt(TilePosition tilePosition, Map<TilePosition, Tile> board) {
    for (Direction direction: Direction.values()) {
      Tile adjacentTile = board.get(tilePosition.getAdjacent(direction));
      if (adjacentTile instanceof ComponentTile) {
        return false; // Components can't be placed next to each other.
      }
    }
    return true;
  }
  
  private int getAdjacentCableCount(TilePosition tilePosition, Map<TilePosition, Tile> board) {
    int cables = 0;
    for (Direction direction: Direction.values()) {
      Tile adjacentTile = board.get(tilePosition.getAdjacent(direction));
      if (adjacentTile instanceof CableTile) {
        cables += ((CableTile) adjacentTile).getCables();
      }
    }
    return cables;
  }
  
  private Tile[][] transformBoard(Map<TilePosition, Tile> board) {
    
    final IntSummaryStatistics rowSummary = board.keySet().stream()
        .collect(Collectors.summarizingInt(TilePosition::getRow));
    final IntSummaryStatistics colSummary = board.keySet().stream()
        .collect(Collectors.summarizingInt(TilePosition::getCol));
    
    final int boardHeight = rowSummary.getMax() - rowSummary.getMin() + 1;
    final int boardWidth = colSummary.getMax() - colSummary.getMin() + 1;
    
    final Tile[][] transformedBoard = new Tile[boardHeight][boardWidth];
    
    for (int row = 0; row < boardHeight; row++) {
      for (int col = 0; col < boardWidth; col++) {
        Tile tile = board.get(
            new TilePosition(row + rowSummary.getMin(), col + colSummary.getMin()));
        
        transformedBoard[row][col] = tile == null ? new CableTile() : tile;
      }
    }
    
    return transformedBoard;
  }

}
