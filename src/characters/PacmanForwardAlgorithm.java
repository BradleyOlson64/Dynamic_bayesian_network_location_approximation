package characters;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;

import graphics.Sonar;
import graphics.GhostBustersPanel;
import util.Coords;
import util.WeightedSet;
/**
 * The version of Pacman that uses the Forward Algorithm to estimate the ghost's current location
 * 
 * Please fill in the methods below and do not alter them (for the sake of my unit tests).
 * Feel free to add whatever private/protected methods you need.
 * 
 * @author Bradley Olson
 * @version 4/10/19
 */
public class PacmanForwardAlgorithm extends Pacman{
	
	
	/**
	 * Creates a new Pacman agent that uses the Forward Algorithm
	 * @param size
	 * 			The size of the grid (e.g. 10 rows/columns)
	 *  
	 * @param images
	 * 			Images of Pacman in the 4 cardinal directions
	 * 
	 * @param sonar
	 * 			Pacman's sonar apparatus (i.e. his ears)
	 */
	public PacmanForwardAlgorithm(int size, BufferedImage[] images, Sonar sonar) {
		super(size, images, sonar);
		this.belief = new WeightedSet<>();
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				Coords coord = new Coords(i,j);
				this.belief.addEvent(coord, 1.00/(Double.valueOf(size)* Double.valueOf(size)));
			}
		}
	}

	/**
	 * Updates Pacman's belief of the ghost's location.  
	 * 
	 * @param noisyDistance
	 * 			A noisy distance reading -- i.e., the noisy distance from Pacman to the ghost  
	 */
	public void update(int noisyDistance){
		WeightedSet<Coords> newBelief = new WeightedSet<>();
		
		
		// looping through points in belief
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				Coords newCoord = new Coords(i,j);
				double sum = 0.0;
				for(Coords coord: belief.getElements()) {
					sum += getCurrGivenLast(coord,newCoord) * belief.getWeight(coord);
				}
				double[] emissionTable = sonar.getEmissionTable(sonar.manhattanDistance(newCoord, location));
				double newVal = emissionTable[noisyDistance]*sum;
				newBelief.addEvent(newCoord, newVal);
			}
		}
		newBelief.normalize();
		// Updating belief
		this.belief = newBelief;
	}
	
	//Gives the probability of a ghost coordinate at time t given its coordinate at t-1
	private double getCurrGivenLast(Coords lastCoord, Coords currCoord) {
		double prob = 0.0;
		if(getLegalNeighbors(lastCoord).contains(currCoord)) {
			prob = 1.0/(getLegalNeighbors(lastCoord).size()+1);
		}
		if(lastCoord.equals(currCoord)) {
			prob = 1.0/(getLegalNeighbors(lastCoord).size()+1);
		}
		return prob;
	}
	
	public static void main(String[] args) {
//		Sonar sonar = new Sonar(10, 2);
//		BufferedImage[] pacman_images = new BufferedImage[4];
//		pacman_images[0] = GhostBustersPanel.loadImage("./src/pacman_right.png");
//		pacman_images[1] = loadImage("./src/pacman_down.png");		
//		pacman_images[2] = loadImage("./src/pacman_left.png");
//		pacman_images[3] = loadImage("./src/pacman_up.png");
//		PacmanForwardAlgorithm test = new PacmanForwardAlgorithm(10,pacman_images ,sonar);
//		Coords curr = new Coords(1,1);
//		Coords newC = new Coords(2,1);
//		System.out.println(test.getCurrGivenLast(curr,newC));
	}
}
