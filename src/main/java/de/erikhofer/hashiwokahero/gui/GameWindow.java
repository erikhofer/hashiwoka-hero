package de.erikhofer.hashiwokahero.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow extends JFrame implements GameEngine.MainLoop {

  private static final long serialVersionUID = 1L;
  
  public static void main(String[] args) {
    new GameWindow();
  }
  
  private JPanel canvas;
  private GameEngine gameEngine;
  
  /**
   * Creates a new game window.
   */
  public GameWindow() {
    setTitle("Hashiwoka Hero");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        
      }
    });
    setResizable(false);
    setLayout(new BorderLayout());
    
    //set up canvas
    canvas = new JPanel();
    canvas.setPreferredSize(new Dimension(800, 600));
    canvas.setDoubleBuffered(false); // we do our own double buffering
    add(canvas, BorderLayout.CENTER);
    
    gameEngine = new GameEngine(this);
    gameEngine.setBufferSize(800, 600);
    gameEngine.start();
    
    pack();
    setLocationRelativeTo(null); // center
    setVisible(true);
  }

  @Override
  public void update(long period) {
    
  }

  @Override
  public void render(Graphics g) {
    
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

}
