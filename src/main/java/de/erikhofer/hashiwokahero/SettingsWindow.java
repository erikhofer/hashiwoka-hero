package de.erikhofer.hashiwokahero;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class SettingsWindow extends JFrame {

  private static final long serialVersionUID = 1L;
  
  public static void main(String[] args) {
    new SettingsWindow();
  }
  
  /**
   * Creates a new settings window.
   */
  public SettingsWindow() {
    setTitle("Hashiwoka Hero");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(false);
    
    JPanel content = new JPanel();
    content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    content.setLayout(new GridLayout(3, 1, 10, 10));
    setContentPane(content);
    
    content.add(new JLabel("Number of Components"));
    JSpinner componentsSpinner = new JSpinner(new SpinnerNumberModel(10, 2, Integer.MAX_VALUE, 1));
    content.add(componentsSpinner);
    
    JButton startButton = new JButton("Start Game");
    startButton.addActionListener(e -> {
      new GameWindow((int) componentsSpinner.getValue(), this::closeGameWindow).setVisible(true);
      setVisible(false);
    });
    content.add(startButton);
    
    pack();
    setLocationRelativeTo(null); // center
    setVisible(true);
  }
  
  private void closeGameWindow(GameWindow gameWindow) {
    gameWindow.dispose();
    setVisible(true);
  }

}
