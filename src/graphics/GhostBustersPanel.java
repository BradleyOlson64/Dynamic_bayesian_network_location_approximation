package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import characters.*;
import util.Coords;
import util.WeightedSet;

public class GhostBustersPanel extends JPanel implements ActionListener {	
	private static final int BOARD_SIZE = 10;
	private static final int SONAR_RANGE = 2; // do not change this value
	private static int MARGIN = 10;	
	
	private Ghost ghost;
	private Pacman pacman;	
	private Sonar sonar;

	private boolean gameOver;	
	private PixelCoords[][] board;	
	
	public GhostBustersPanel(){		
		board = null; // this is created when we call repaint() for the first time
		gameOver = false;
		sonar = new Sonar(BOARD_SIZE, SONAR_RANGE);
		
		// SET THE GHOST TYPE HERE
		ghost = new GoEastGhost(BOARD_SIZE, loadImage("./src/images/ghost_34_35.png"));				
				
		BufferedImage[] pacman_images = new BufferedImage[4];
		pacman_images[0] = loadImage("./src/images/pacman_right.png");
		pacman_images[1] = loadImage("./src/images/pacman_down.png");		
		pacman_images[2] = loadImage("./src/images/pacman_left.png");
		pacman_images[3] = loadImage("./src/images/pacman_up.png");
		
		// SET THE PACMAN TYPE HERE
		pacman = new PacmanParticleFilter(BOARD_SIZE, pacman_images, sonar);
				
		Timer t = new Timer(500, this);
		t.setInitialDelay(1900);
		t.start();
		
	}

	// Reads in an image from file
	protected BufferedImage loadImage(String filename) {
		try {
			return ImageIO.read(new File(filename));
		}
		catch (IOException e) {
			System.err.println("Had a problem loading ./src/ghost.png");
			return null;
		}
	}
	
	// Computes the pixel location of the upper left corner of each cell in the board 
	private void populateBoard(int width, int height) {
		board = new PixelCoords[BOARD_SIZE][BOARD_SIZE];
		final int TILE_WIDTH = (width-2*MARGIN)/BOARD_SIZE;
		final int TILE_HEIGHT = (height-2*MARGIN)/BOARD_SIZE;
				
		int x = MARGIN, y = MARGIN;
		for(int i = 0; i < BOARD_SIZE; i++) {
			for(int j = 0; j < BOARD_SIZE; j++) {
				board[i][j] = new PixelCoords(x, y);
				x += TILE_WIDTH;
			}
			x = MARGIN;
			y += TILE_HEIGHT;
		}
	}
	
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);	
		Graphics2D g2 = (Graphics2D) g;		
		
		// The first time through, calculate the pixel positions of each of the cells in the grid
		if(board == null) {
			populateBoard(getWidth(), getHeight());
		}
		
		final int TILE_WIDTH = (getWidth()-2*MARGIN)/BOARD_SIZE;
		final int TILE_HEIGHT = (getHeight()-2*MARGIN)/BOARD_SIZE;		
		Coords ghost = this.ghost.getLocation();
		Coords pacman = this.pacman.getLocation();
		WeightedSet<Coords> belief = this.pacman.getBelief();
		
		double upperThreshold = 0.1;		
		for(int i = 0; i < BOARD_SIZE; i++) {
			for(int j = 0; j < BOARD_SIZE; j++) {

				double prob = 0.0;				
				if(belief != null) {												
					prob = belief.getWeight(new Coords(i, j));
				}
				if(prob > 0.0) {
					if(prob > upperThreshold) {
						prob = upperThreshold;
					}
					float hue = 1.33333333F;
					float saturation = (float)(prob*(1.0/upperThreshold));
					float brightness = 1.0F; 
					g2.setColor(new Color(Color.HSBtoRGB(hue, saturation, brightness)));					
					g2.fillRect(board[i][j].row, board[i][j].col, TILE_WIDTH, TILE_HEIGHT);
				}
			}
		}
		
		g2.setColor(Color.BLACK);		
		for(int i = 0; i < BOARD_SIZE; i++) {
			for(int j = 0; j < BOARD_SIZE; j++) {
				PixelCoords cell = board[i][j];							
				g2.drawRect(cell.row, cell.col, TILE_WIDTH, TILE_HEIGHT);		
				if(i == ghost.row && j == ghost.col) {
					int imageWidth = this.ghost.getImage().getWidth();
					int imageHeight = this.ghost.getImage().getHeight();					
					int x = (TILE_WIDTH - imageWidth)/2;
					int y = (TILE_HEIGHT - imageHeight)/2;
					g.drawImage(this.ghost.getImage(),cell.row+x, cell.col+y, null);
				}
				if(i == pacman.row && j == pacman.col) {
					int imageWidth = this.pacman.getImage().getWidth();
					int imageHeight = this.pacman.getImage().getHeight();					
					int x = (TILE_WIDTH - imageWidth)/2;
					int y = (TILE_HEIGHT - imageHeight)/2;					
					g.drawImage(this.pacman.getImage(),  cell.row+x, cell.col+y, null);
				}
			}
		}

		FontMetrics metrics = g2.getFontMetrics();
		if(gameOver) {
			int width = metrics.stringWidth("You Win!");
			Font font = new Font("Times Roman", Font.BOLD, 26);				
			g2.setFont(font);
			int fontX = (getWidth()-width)/2;
			int fontY = getHeight()/2-10;
			g2.drawString("You Win!", fontX, fontY);			
			return;
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(!gameOver) {
			// The ghost takes a step
			Coords ghostLocation = ghost.move();
			
			// The radar produces a noisy reading of the Manhattan distance between Pacman and the ghost
			Coords pacmanLocation = pacman.getLocation();			
			int noisyDistance = sonar.getNoisyDistance(ghostLocation, pacmanLocation);

			// Given the noisy reading, we compute the distribution over the location of the ghost
			pacman.update(noisyDistance);			
			
			// Pacman now takes a move given the updated distribution
			pacman.move();
			
			// We check if Pacman has captured the ghost
			if(pacman.getLocation().equals(ghost.getLocation())) {
				gameOver = true;
			}			
			
			// We repaint the screen
			repaint();
		}
	}	

	private class PixelCoords{
		public int row;
		public int col;
		public PixelCoords(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}
}
