package de.erikhofer.hashiwokahero;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GameWindow extends JFrame implements GameEngine.MainLoop, MouseListener,
    MouseMotionListener {

  private static final long serialVersionUID = 1L;
  
  private static final int TILE_SIZE = 96;
  private static final int TILE_PADDING = 16;
  private static final Color BACKGROUND_COLOR = new Color(62, 75, 48);
  private static final Font DIGIT_FONT = new Font("Monospaced", Font.BOLD, 15);
  
  private JPanel canvas;
  private GameEngine gameEngine;
  private GameState gameState;
  private TilePosition selectedComponentPostion;
  private TilePosition hoverTilePostion;
  private boolean displayVerificationResult;
  private Point mousePosition;
  
  /**
   * Creates a new game window.
   */
  public GameWindow(int components, Consumer<GameWindow> closeHandler) {
    setTitle("Hashiwoka Hero");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        closeHandler.accept(GameWindow.this);
      }
    });
    setResizable(false);
    setLayout(new BorderLayout());
    
    gameState = new GameState(components);
    
    final int canvasWidth = gameState.getBoardWidth() * TILE_SIZE;
    final int canvasHeight = gameState.getBoardHeight() * TILE_SIZE;
    
    // set up canvas
    canvas = new JPanel();
    canvas.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
    canvas.setDoubleBuffered(false); // we do our own double buffering
    canvas.addMouseListener(this);
    canvas.addMouseMotionListener(this);
    add(canvas, BorderLayout.CENTER);
    
    // set up controls
    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new GridLayout(1, 2));
    JButton verifySolutionButton = new JButton("Verify Solution");
    verifySolutionButton.addActionListener(e -> verifySolution());
    controlPanel.add(verifySolutionButton);
    JButton showSolutionButton = new JButton("Show Solution");
    showSolutionButton.addActionListener(e -> gameState.setBoardToSolution());
    controlPanel.add(showSolutionButton);
    add(controlPanel, BorderLayout.SOUTH);
    
    gameEngine = new GameEngine(this);
    gameEngine.setBufferSize(canvasWidth, canvasHeight);
  }

  @Override
  public void update(long period) {
    // this game doesn't have any time-based updates
  }

  @Override
  public void render(Graphics g) {
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); // clear
    
    gameState.getAllTilePositions().stream().forEach(tilePosition -> {
      final Point origin = new Point(tilePosition.getCol() * TILE_SIZE,
          tilePosition.getRow() * TILE_SIZE);
      
      if (gameState.isComponentTile(tilePosition)) {
        renderComponentTile(tilePosition, g, origin);
      } else if (gameState.isCableTile(tilePosition)) {
        renderCableTile(tilePosition, g, origin);
      } else {
        throw new RuntimeException("Unknown tile type");
      }
    });
    
    if (selectedComponentPostion != null) {
      g.setColor(Color.RED);
      g.drawLine(
          selectedComponentPostion.getCol() * TILE_SIZE + TILE_SIZE / 2, 
          selectedComponentPostion.getRow() * TILE_SIZE + TILE_SIZE / 2,
          mousePosition.x, mousePosition.y);
    }
    if (hoverTilePostion != null) {
      final Point hoverOrigin = new Point(hoverTilePostion.getCol() * TILE_SIZE,
          hoverTilePostion.getRow() * TILE_SIZE);
      if (gameState.isComponentTile(hoverTilePostion)) {
        g.setColor(selectedComponentPostion == null
            || selectedComponentPostion.getDirectionRelativeTo(hoverTilePostion) != null 
            ? Color.GREEN : Color.RED);
        g.drawOval(hoverOrigin.x + 20, hoverOrigin.y + 20, TILE_SIZE - 40, TILE_SIZE - 40);
      } else if (selectedComponentPostion == null
          && gameState.<CableTile>getTileAtPosition(hoverTilePostion).getCables() > 0) {
        g.setColor(Color.RED);
        g.drawLine(hoverOrigin.x + 15, hoverOrigin.y + 15,
            hoverOrigin.x + TILE_SIZE - 15, hoverOrigin.y + TILE_SIZE - 15);
        g.drawLine(hoverOrigin.x + TILE_SIZE - 15, hoverOrigin.y + 15,
            hoverOrigin.x + 15, hoverOrigin.y + TILE_SIZE - 15);
      }
    }
  }
  
  private void renderComponentTile(TilePosition tilePosition, Graphics g, Point origin) {
    ComponentTile componentTile = gameState.getTileAtPosition(tilePosition);
    
    Image image = Resources.COMPONENTS[componentTile.getConnections()][componentTile.getVariant()];
    g.drawImage(image, origin.x + TILE_PADDING, origin.y + TILE_PADDING, this);
    
    // connections
    for (Direction direction : Direction.values()) {
      TilePosition adjacentTilePosition = tilePosition.getAdjacent(direction);
      
      if (gameState.isOutOfBoardBounds(adjacentTilePosition)) {
        continue; // there is no adjacent tile in this direction
      }
      
      // components can't be next to each other
      CableTile cableTile = gameState.getTileAtPosition(adjacentTilePosition);
      
      int connectionCount = direction.getOrientation() == cableTile.getOrientation()
          ? cableTile.getCables() : 0;
      
      for (int i = 1; i <= 2; i++) {
        Point connectionOrigin = getConnectionOrigin(origin, direction, i == 2);
        Image connectionImage = connectionCount >= i 
            ? Resources.CONNECTIONS.get(direction)
            : Resources.HOLES.get(direction.getOrientation());
        g.drawImage(connectionImage, connectionOrigin.x, connectionOrigin.y, this);
      }
    }
    
    g.setColor(Color.WHITE);
    g.fillOval(origin.x + TILE_PADDING + 2, origin.y + TILE_PADDING + 2, 15, 15);
    g.setColor(displayVerificationResult && !componentTile.isCorrectlyConnected() 
        ? Color.RED : Color.BLACK);
    g.setFont(DIGIT_FONT);
    g.drawString("" + componentTile.getConnections(), origin.x + TILE_PADDING + 5, 
        origin.y + TILE_PADDING + 15);
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
    CableTile cableTile = gameState.getTileAtPosition(tilePosition);
    
    for (int i = 0; i < cableTile.getCables(); i++) {
      boolean second = i == 1;
      Point originWithOffset = new Point(origin);
      if (cableTile.getOrientation() == Orientation.HORIZONTAL) {
        originWithOffset.y += second ? TILE_SIZE / 2 : TILE_PADDING;
      } else {
        originWithOffset.x += second ? TILE_SIZE / 2 : TILE_PADDING;
      }
      
      Image image = Resources.CABLES.get(cableTile.getOrientation())[cableTile.getVariant()];
      g.drawImage(image, originWithOffset.x, originWithOffset.y, this);
    }
  }
  
  @Override
  public void paint(Image buffer) {
    // Draw actively to the graphics context of the canvas.
    Graphics g = null;
    try {
      g = canvas.getGraphics();
      if (g != null) {
        g.drawImage(buffer, 0, 0, this);
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
  
  @Override
  public void setVisible(boolean b) {
    super.setVisible(b);
    if (b) {
      gameEngine.start();
      pack();
      setLocationRelativeTo(null); // center
    } else {
      gameEngine.stop();
    }
  }
  
  private void verifySolution() {
    if (gameState.verifySolution()) {
      JOptionPane.showMessageDialog(this, "This soluion is valid!", "Congratulations",
          JOptionPane.INFORMATION_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(this, "This soluion is not valid!", "Sorry",
          JOptionPane.ERROR_MESSAGE);
    }
    displayVerificationResult = true;
  }
  
  private TilePosition getTilePosition(MouseEvent e) {
    return new TilePosition(e.getY() / TILE_SIZE, e.getX() / TILE_SIZE);
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {
    displayVerificationResult = false;
    TilePosition tilePosition = getTilePosition(e);
    
    if (gameState.isCableTile(tilePosition)) {
      if (selectedComponentPostion == null) {
        gameState.getFullCable(tilePosition).forEach(CableTile::decreaseCables);
      } else {
        selectedComponentPostion = null;
      }
      return;
    }
    
    if (selectedComponentPostion == null) {
      selectedComponentPostion = tilePosition;
    } else if (tilePosition.equals(selectedComponentPostion)) {
      selectedComponentPostion = null;
    } else {
      gameState.tryToAddCableBetweenComponents(tilePosition, selectedComponentPostion);
      selectedComponentPostion = null;
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    System.out.println("Pressed " + e.getPoint());
  }

  @Override
  public void mouseReleased(MouseEvent e) {}

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mouseDragged(MouseEvent e) {}

  @Override
  public void mouseMoved(MouseEvent e) {
    System.out.println("Moved " + e.getPoint());
    mousePosition = e.getPoint();
    hoverTilePostion = getTilePosition(e);
  }
}
