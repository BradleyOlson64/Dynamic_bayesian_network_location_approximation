package characters;

import java.awt.image.BufferedImage;
import java.util.Random;

import util.Coords;

/**
 * This ghost chooses a random location and stays there.
 * 
 * @author alchambers
 * @version sp19
 */
public class Ghost{
	protected static final int NUM_DIRECTIONS = 4;	
	protected Coords location;
	protected int boardSize;
	protected BufferedImage image;
	
	/**
	 * Creates a new stationary ghost
	 * 
	 * @param size
	 * 				The size of the grid (e.g. 10 rows/columns)
	 * @param image
	 * 				Image of the ghost
	 */
	public Ghost(int size, BufferedImage image) {
		this.boardSize = size;		
		this.image = image;
		Random rng = new Random();
		this.location = new Coords(rng.nextInt(boardSize), rng.nextInt(boardSize));	
	}
	
	/**
	 * Returns the image of the ghost
	 * 
	 * @return
	 * 			Image of the ghost
	 */
	public BufferedImage getImage() {
		return image;
	}
	
	/**
	 * When this method is called, the ghost updates its location. 
	 * 
	 * @return
	 * 		The ghost's current location
	 */
	public Coords move() {
		return location;
	}

	/**
	 * Returns the ghost's current location
	 * 
	 * @return
	 * 		The ghost's current location
	 */
	public Coords getLocation() {
		return location;
	}
	
	protected enum Direction {
		UP, DOWN, LEFT, RIGHT;	
	}	
}
