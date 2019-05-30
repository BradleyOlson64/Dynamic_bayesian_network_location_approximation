package characters;

import java.awt.image.BufferedImage;

import characters.Ghost.Direction;
import util.Coords;

/**
 * This ghost travels east until it hits a wall.
 * 
 * @author alchambers
 * @version sp19
 *
 */
public class GoEastGhost extends Ghost {

	/**
	 * Creates a new ghost that travels east.
	 * 
	 * @param size
	 * 				The size of the grid (e.g. 10 rows/columns)
	 * @param image
	 * 				Image of the ghost
	 */
	public GoEastGhost(int size, BufferedImage image) {
		super(size, image);
	}	
	
	/**
	 * When this method is called, the ghost updates its location.
	 * 
	 * @return
	 * 			The ghost's current location 
	 */
	@Override
	public Coords move() {
		
		if(location.col < boardSize-1) {
			location.col = location.col+1;
		}
		return location;
	}
}
