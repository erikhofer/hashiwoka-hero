package de.erikhofer.hashiwokahero.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import lombok.NonNull;

public class GameEngine {
  
  public interface MainLoop {
    
    /**
     * Advances the game state.
     * 
     * @param period time in ms that passes during this update
     */
    void update(long period);
    
    /**
     * Renders the next frame into the buffer.
     * 
     * @param g graphics context of the buffer
     */
    void render(Graphics g);
    
    /**
     * Paints the next frame. Must be synchronous so that the time taken can be measured.
     */
    void paint(Image buffer);
  }

  /**
   * Max count of successive frames without sleep or yield.
   */
  private static final int MAX_FRAMES_WITHOUT_YIELD = 5;

  private int framesWithoutYield;

  private final MainLoop mainLoopDelegate;
  private Thread mainLoopThread;
  private volatile boolean running;

  /** Time per frame in ms. */
  private long period;

  private BufferedImage buffer;
  private int bufferWidth;
  private int bufferHeight;

  /**
   * Creates a game engine with a default of 30 frames per second.
   */
  public GameEngine(@NonNull MainLoop mainLoopDelegate) {
    this.mainLoopDelegate = mainLoopDelegate;
    setFps(30);
  }

  /**
   * Starts the game engine if it doesn't run already.
   */
  public synchronized void start() {
    if (mainLoopThread == null || !running) {
      running = true;
      mainLoopThread = new Thread(this::mainLoop);
      mainLoopThread.start();
    }
  }

  /**
   * Stops the game engine. It can be started again.
   */
  public synchronized void stop() {
    running = false;
  }

  protected void mainLoop() {
    
    while (running) {

      final long startTime = System.currentTimeMillis();

      // update
      mainLoopDelegate.update(period);

      // render
      if (buffer == null) {
        buffer = new BufferedImage(bufferWidth, bufferHeight, BufferedImage.TYPE_INT_RGB);
      }
      mainLoopDelegate.render(buffer.getGraphics());

      // paint
      mainLoopDelegate.paint(buffer);

      // pause
      final long elapsedTime = System.currentTimeMillis() - startTime;
      final long sleepTime = period - elapsedTime;
      if (sleepTime > 0) {
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
          // ignore
        }
        framesWithoutYield = 0;
      } else { // eventually force a yield
        framesWithoutYield++;
        if (framesWithoutYield > MAX_FRAMES_WITHOUT_YIELD) {
          Thread.yield();
          framesWithoutYield = 0;
        }
      }
    }
  }

  public void setFps(int fps) {
    period = (long) (1000.0 / fps + 0.5);
  }
  
  /**
   * Sets the size of the buffer image in px.
   */
  public void setBufferSize(int width, int height) {
    bufferWidth = width;
    bufferHeight = height;
    buffer = null; // create new buffer
  }

  public synchronized boolean isRunning() {
    return running;
  }
}
