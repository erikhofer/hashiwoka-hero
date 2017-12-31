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
  private boolean displayVerificationResult;
  
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
    
    for (int row = 0; row < gameState.getBoardHeight(); row++) {
      for (int col = 0; col < gameState.getBoardWidth(); col++) {
        
        TilePosition tilePosition = new TilePosition(row, col);
        final Point origin = new Point(col * TILE_SIZE, row * TILE_SIZE);
        
        if (gameState.isComponentTile(tilePosition)) {
          renderComponentTile(tilePosition, g, origin);
        } else if (gameState.isCableTile(tilePosition)) {
          renderCableTile(tilePosition, g, origin);
        } else {
          throw new RuntimeException("Unknown tile type");
        }
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
            ? Resources.CONNECTIONS.get(direction)[cableTile.getVariant()] 
            : Resources.HOLES.get(direction.getOrientation());
        g.drawImage(connectionImage, connectionOrigin.x, connectionOrigin.y, this);
      }
    }
    
    if (tilePosition.equals(selectedComponentPostion)) {
      g.setColor(Color.GREEN);
      g.fillRect(origin.x + TILE_PADDING, origin.y + TILE_PADDING, 10, 10);
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
    System.out.println("Clicked " + e.getPoint());
    
    TilePosition tilePosition = getTilePosition(e);
    
    if (gameState.isCableTile(tilePosition)) {
      if (selectedComponentPostion == null) {
        gameState.getFullCable(tilePosition).forEach(CableTile::decreaseCables);
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
      gameState.tryToAddCableBetweenComponents(tilePosition, selectedComponentPostion);
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
