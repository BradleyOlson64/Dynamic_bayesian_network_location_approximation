package characters;

import util.*;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * This ghost randomly chooses 1 of the 4 directions to move.
 * 
 * @author alchambers
 * @version sp19
 */
public class RandomGhost extends Ghost {
	private Random rng;
	
	/**
	 * Creates a new random ghost
	 * 
	 * @param size
	 * 				The size of the grid (e.g. 10 rows/columns)
	 * @param image
	 * 				Image of the ghost
	 */
	public RandomGhost(int size, BufferedImage image) {
		super(size, image);
		rng = new Random();
	}
		
	/**
	 * When this method is called, the ghost updates its location.
	 * 
	 * @return
	 * 			The ghost's current location 
	 */
	@Override
	public Coords move() {
		Direction[] legalMoves = new Direction[4];
		if(0 < location.row) {
			legalMoves[0] = Direction.UP;
		}
		if(location.row < boardSize-1) {
			legalMoves[1] = Direction.DOWN;
		}
		if(0 < location.col) {
			legalMoves[2] = Direction.LEFT;
		}
		if(location.col < boardSize-1) {
			legalMoves[3] = Direction.RIGHT;
		}
		
		int choice = rng.nextInt(NUM_DIRECTIONS+1);
		while(choice != NUM_DIRECTIONS && legalMoves[choice] == null) {
			choice = rng.nextInt(NUM_DIRECTIONS+1);
		}
		
		switch(choice) {
		case 0: location.row = location.row-1; break;
		case 1: location.row = location.row+1; break;
		case 2: location.col = location.col-1; break;
		case 3: location.col = location.col+1; break;
		case 4: break; // stay where we are
		}		
		return location;
	}	
}
