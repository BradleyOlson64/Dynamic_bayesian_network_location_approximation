package characters;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import graphics.Sonar;
import util.Coords;
import util.WeightedSet;

/**
 * This class controls Pacman. It encapsulates all functionality related to Pacman including
 * - Pacman's location
 * - Pacman's belief of the ghost's location
 * - Logic for updating Pacman's location given Pacman's belief of the ghost's location 
 *  
 * Note that this an abstract class.
 * 
 * @author alchambers
 * @version spring2019
 */
public abstract class Pacman {
	protected static final int RIGHT = 0;
	protected static final int DOWN = 1;
	protected static final int LEFT = 2;
	protected static final int UP = 3;

	private BufferedImage current;  // The current image of Pacman that is being displayed to the user
	private BufferedImage[] images;	// Images of Pacman facing right, down, left, and up

	protected int size;
	protected Coords location;
	protected WeightedSet<Coords> belief;
	protected Sonar sonar;			// This is Pacman's sonar apparatus for taking readings of the ghost's location


	/**
	 * Creates a new Pacman agent
	 * 
	 * @param size
	 * 			The size of the grid (e.g. 10 rows/columns)
	 *  
	 * @param images
	 * 			Images of Pacman in the 4 cardinal directions
	 * 
	 * @param sonar
	 * 			Pacman's sonar apparatus (i.e. his ears)
	 */
	public Pacman(int size, BufferedImage[] images, Sonar sonar) {
		this.images = images;		
		this.current = images[0]; // Pacman begins facing right	

		this.size = size;
		this.location = new Coords(0, 0); // Pacman always starts off in the upper left corner
		this.belief = null;
		this.sonar = sonar;
	}


	/**
	 * Returns the current image of Pacman (facing either right, down, left, or up)
	 * 
	 * @return
	 * 			Pacman's image
	 */
	public BufferedImage getImage() {
		return current;
	}

	/**
	 * Returns Pacman's current location on the grid
	 * @return
	 * 			Pacman's current location
	 */
	public Coords getLocation() {
		return location;
	}

	/**
	 * Returns Pacman's belief of the ghost's location
	 *  
	 * @return
	 * 			A distribution over the coordinates of the grid
	 */
	public WeightedSet<Coords> getBelief(){		
		return belief;
	}	

	/**
	 * When this method is called, Pacman takes a single step towards the coordinate
	 * with the highest probability. That is, Pacman moves towards the most likely position of the
	 * ghost on the grid.
	 * 
	 * @return
	 * 		Pacman's updated location
	 */
	public Coords move() {				
		// Find the cell with the highest probability
		Set<Coords> cells = belief.getElements();
		Coords mostLikely = null;
		double highestProb = 0.0;	
		for(Coords cell : cells) {
			double prob = belief.getWeight(cell);
			if(prob > highestProb) {
				mostLikely = cell;
				highestProb = prob;
			}
		}						

		// Get the possible next steps
		List<Integer> legalDirs =  getLegalDirections(location);
		List<Coords> neighbors = getLegalNeighbors(location);
		
		// Choose the neighbor that minimizes the Manhattan distance between the ghost
		// and the most likely location of the ghost
		int pos = 0;
		int minDistance = sonar.manhattanDistance(neighbors.get(pos), mostLikely);
		for(int i = 1; i < neighbors.size(); i++) {
			int d = sonar.manhattanDistance(neighbors.get(i), mostLikely);
			if(d < minDistance) {
				pos = i;
				minDistance = d;
			}
			if(d == minDistance && Math.random() <= 0.5) {
				pos = i;
				minDistance = d;
			}
		}
		movePacman(legalDirs.get(pos));
		return location;		
	}


	/**
	 * Updates Pacman's belief of the ghost's location.  
	 * 
	 * @param noisyDistance
	 * 			A noisy distance reading -- i.e., the noisy distance from Pacman to the ghost  
	 */
	public abstract void update(int noisyDistance);


	/*----------------------------------------------
	 *		FEEL FREE TO USE THESE HELPER METHODS
	/*---------------------------------------------*/ 

	// Returns the legal neighbors of a given state
	// The ordering must match the ordering returned by getLegalDirections()
	protected List<Coords> getLegalNeighbors(Coords center){
		List<Coords> neighbors = new ArrayList<>();
		if(center.row > 0) { // UP
			neighbors.add(new Coords(center.row-1, center.col));
		}
		if(center.row < size-1) { // DOWN
			neighbors.add(new Coords(center.row+1, center.col));
		}
		if(center.col > 0) { // LEFT
			neighbors.add(new Coords(center.row, center.col-1));
		}
		if(center.col < size-1) { // RIGHt
			neighbors.add(new Coords(center.row, center.col+1));
		}
		return neighbors;
	}

	// Returns the legal directions of a given state
	// The ordering must match the ordering returned by getLegalNeighbors()	
	protected List<Integer> getLegalDirections(Coords center) {
		List<Integer> directions = new ArrayList<>();		
		if(center.row > 0) {
			directions.add(UP);
		}
		if(center.row < size-1) {
			directions.add(DOWN);
		}
		if(center.col > 0) {
			directions.add(LEFT);
		}
		if(center.col < size-1) {
			directions.add(RIGHT);
		}
		return directions;
	}

	// Alter pacman's location and image in response to the direction 
	protected void movePacman(int direction) {
		switch(direction) {
		case UP: 
			location.row = location.row-1;
			current = images[UP];
			break;
		case DOWN: 
			location.row = location.row+1;
			current = images[DOWN];	
			break;
		case LEFT:
			location.col = location.col-1;
			current = images[LEFT];
			break;
		case RIGHT:
			location.col = location.col+1;	
			current = images[RIGHT];
			break;
		}
	}

}
