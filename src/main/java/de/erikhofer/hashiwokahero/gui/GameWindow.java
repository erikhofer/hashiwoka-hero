package de.erikhofer.hashiwokahero.gui;

import com.google.common.collect.ImmutableList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow extends JFrame implements GameEngine.MainLoop, MouseListener, MouseMotionListener {

  private static final long serialVersionUID = 1L;
  
  private static final int TILE_SIZE = 96;
  private static final int TILE_PADDING = 16;
  private static final int TILE_INNER_SIZE = TILE_SIZE - 2 * TILE_PADDING;
  
  public static void main(String[] args) {
    new GameWindow();
  }
  
  private JPanel canvas;
  private GameEngine gameEngine;
  private GameState gameState;
  private int boardWidth = 3;
  private int boardHeight = 3;
  private TilePosition selectedComponentPostion;
  
  /**
   * Creates a new game window.
   */
  public GameWindow() {
    setTitle("Hashiwoka Hero");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    setResizable(false);
    setLayout(new BorderLayout());
    
    gameState = new GameState();
    
    final int canvasWidth = boardWidth * TILE_SIZE;
    final int canvasHeight = boardHeight * TILE_SIZE;
    
    //set up canvas
    canvas = new JPanel();
    canvas.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
    canvas.setDoubleBuffered(false); // we do our own double buffering
    canvas.addMouseListener(this);
    canvas.addMouseMotionListener(this);
    add(canvas, BorderLayout.CENTER);
    
    gameEngine = new GameEngine(this);
    gameEngine.setBufferSize(canvasWidth, canvasHeight);
    gameEngine.start();
    
    pack();
    setLocationRelativeTo(null); // center
    setVisible(true);
  }

  @Override
  public void update(long period) {
    // this game doesn't have any time-based updates
  }

  @Override
  public void render(Graphics g) {
    g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); // clear
    
    for (int row = 0; row < boardHeight; row++) {
      for (int col = 0; col < boardWidth; col++) {
        
        TilePosition tilePosition = new TilePosition(row, col);
        final Point origin = new Point(col * TILE_SIZE, row * TILE_SIZE);
        
        if (isComponentTile(tilePosition)) {
          renderComponentTile(tilePosition, g, origin);
        } else if (isCableTile(tilePosition)) {
          renderCableTile(tilePosition, g, origin);
        } else {
          throw new RuntimeException("Unknown tile type");
        }
      }
    }
  }
  
  private void renderComponentTile(TilePosition tilePosition, Graphics g, Point origin) {
    ComponentTile componentTile = getTileAtPosition(tilePosition);
    
    Image image = Resources.COMPONENTS[componentTile.getConnections()][componentTile.getVariant()];
    g.drawImage(image, origin.x + TILE_PADDING, origin.y + TILE_PADDING, null);
    
    // connections
    for (Direction direction : Direction.values()) {
      TilePosition adjacentTilePosition = tilePosition.getAdjacent(direction);
      
      if (adjacentTilePosition.getRow() == -1
          || adjacentTilePosition.getCol() == -1
          || adjacentTilePosition.getRow() == boardHeight
          || adjacentTilePosition.getCol() == boardWidth) {
        continue; // there is no adjacent tile in this direction
      }
      
      // components can't be next to each other
      CableTile cableTile = getTileAtPosition(adjacentTilePosition);
      
      int connectionCount = direction.getOrientation() == cableTile.getOrientation()
          ? cableTile.getCables() : 0;
      
      for (int i = 1; i <= 2; i++) {
        Point connectionOrigin = getConnectionOrigin(origin, direction, i == 2);
        Image connectionImage = connectionCount >= i 
            ? Resources.CONNECTIONS.get(direction)[cableTile.getVariant()] 
            : Resources.HOLES.get(direction.getOrientation());
        g.drawImage(connectionImage, connectionOrigin.x, connectionOrigin.y, null);
      }
    }
    
    if (tilePosition.equals(selectedComponentPostion)) {
      g.setColor(Color.GREEN);
      g.fillRect(origin.x + TILE_PADDING, origin.y + TILE_PADDING, 10, 10);
    }
  }
  
  private Point getConnectionOrigin(Point tileOrigin, Direction direction, boolean second) {
    Point connectionOrigin = new Point(tileOrigin);
    if (direction.getOrientation() == Orientation.VERTICAL) {
      connectionOrigin.x += second ? TILE_SIZE / 2 : TILE_PADDING;
      connectionOrigin.y += direction == Direction.NORTH ? 0 : (TILE_SIZE - TILE_PADDING);
    } else {
      connectionOrigin.y += second ? TILE_SIZE / 2 : TILE_PADDING;
      connectionOrigin.x += direction == Direction.WEST ? 0 : (TILE_SIZE - TILE_PADDING);
    }
    return connectionOrigin;
  }
  
  private void renderCableTile(TilePosition tilePosition, Graphics g, Point origin) {
    CableTile cableTile = getTileAtPosition(tilePosition);
    
    for (int i = 0; i < cableTile.getCables(); i++) {
      boolean second = i == 1;
      Point originWithOffset = new Point(origin);
      if (cableTile.getOrientation() == Orientation.HORIZONTAL) {
        originWithOffset.y += second ? TILE_SIZE / 2 : TILE_PADDING;
      } else {
        originWithOffset.x += second ? TILE_SIZE / 2 : TILE_PADDING;
      }
      
      Image image = Resources.CABLES.get(cableTile.getOrientation())[cableTile.getVariant()];
      g.drawImage(image, originWithOffset.x, originWithOffset.y, null);
    }
  }
  
  private boolean isComponentTile(TilePosition tilePosition) {
    return getTileAtPosition(tilePosition) instanceof ComponentTile;
  }
  
  private boolean isCableTile(TilePosition tilePosition) {
    return getTileAtPosition(tilePosition) instanceof CableTile;
  }
  
  @SuppressWarnings("unchecked")
  private <T extends Tile> T getTileAtPosition(TilePosition tilePosition) {
    return (T) gameState.getBoard()[tilePosition.getRow()][tilePosition.getCol()];
  }

  @Override
  public void paint(Image buffer) {
    // Draw actively to the graphics context of the canvas.
    Graphics g = null;
    try {
      g = canvas.getGraphics();
      if (g != null) {
        g.drawImage(buffer, 0, 0, null);
      }
    } finally {
      if (g != null) {
        g.dispose();
      }
    }
  }
  
  @Override
  public void dispose() {
    super.dispose();
    gameEngine.stop();
  }
  
  private TilePosition getTilePosition(MouseEvent e) {
    return new TilePosition(e.getY() / TILE_SIZE, e.getX() / TILE_SIZE);
  }
  
  private ImmutableList<CableTile> getFullCable(TilePosition tilePosition) {
    
    final CableTile cableTile = getTileAtPosition(tilePosition);
    if (cableTile.getCables() == 0) {
      return ImmutableList.of();
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
    final ImmutableList.Builder<CableTile> builder = ImmutableList.builder();
    TilePosition current = start;
    while (true) {
      builder.add(getTileAtPosition(current));
      final TilePosition next = start.getAdjacent(directions.get(1));
      if (!isCableTile(next)) { // this can't be out of bounds
        break;
      }
      current = next;
    }
    
    return builder.build();
  }
  
  private void tryToAddCableBetweenComponents(TilePosition component1, TilePosition component2) {
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

  @Override
  public void mouseClicked(MouseEvent e) {
    System.out.println("Clicked " + e.getPoint());
    
    TilePosition tilePosition = getTilePosition(e);
    
    if (isCableTile(tilePosition)) {
      if (selectedComponentPostion == null) {
        getFullCable(tilePosition).forEach(CableTile::decreaseCables);
      } else {
        // TODO
        selectedComponentPostion = null;
      }
      return;
    }
    
    if (selectedComponentPostion == null) {
      selectedComponentPostion = tilePosition;
    } else if (tilePosition.equals(selectedComponentPostion)) {
      selectedComponentPostion = null;
    } else {
      tryToAddCableBetweenComponents(tilePosition, selectedComponentPostion);
      selectedComponentPostion = null;
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    System.out.println("Pressed " + e.getPoint());
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    System.out.println("Released" + e.getPoint());
  }

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mouseDragged(MouseEvent e) {
    System.out.println("Dragged " + e.getPoint());
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    System.out.println("Moved " + e.getPoint());
  }

}
