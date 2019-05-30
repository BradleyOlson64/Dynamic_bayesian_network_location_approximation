package characters;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import graphics.Sonar;
import util.Coords;
import util.WeightedSet;

/**
 * The version of Pacman that uses particle filtering to estimate the ghost's current location
 * 
 * Please fill in the methods below and do not alter them (for the sake of my unit tests).
 * Feel free to add whatever private/protected methods you need.
 * 
 * 
 * @author Bradley Olson
 * @version 4/10/19
 */
public class PacmanParticleFilter extends Pacman{
	Coords[] particles;
	
	/**
	 * Creates a new Pacman agent that uses particle filtering
	 * @param size
	 * 			The size of the grid (e.g. 10 rows/columns)
	 *  
	 * @param images
	 * 			Images of Pacman in the 4 cardinal directions
	 * 
	 * @param sonar
	 * 			Pacman's sonar apparatus (i.e. his ears)
	 */
	public PacmanParticleFilter(int size, BufferedImage[] images, Sonar sonar) {
		super(size, images, sonar);
		//Getting initial belief
		this.belief = new WeightedSet<>();
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				Coords coord = new Coords(i,j);
				this.belief.addEvent(coord, 1.00/(Double.valueOf(size)* Double.valueOf(size)));
			}
		}
		//Getting initial sampling
		particles = new Coords[40];
		for(int i=0;i<particles.length;i++) {
			particles[i] = belief.sample();
		}
	}
	
	
	
	/**
	 * Elapses each sample forward using the transition distribution
	 * 
	 * @param samples
	 * 				The current set of samples
	 * @return
	 * 				An updated set of samples
	 */
	protected Coords[] elapse(Coords[] samples) {
		Coords[] newPositions = new Coords[samples.length];
		for(int i=0;i<samples.length;i++) {
			WeightedSet<Coords> probs = new WeightedSet<>();
			// Finds the weight of positions at time t given the position at t-1
			double theWeight = 1.0/(getLegalNeighbors(samples[i]).size()+1);
			// Applying the weight to the possible positions
			for(Coords coord: getLegalNeighbors(samples[i])) {
				probs.addEvent(coord, theWeight);
			}
			probs.addEvent(samples[i], theWeight);
			newPositions[i] = probs.sample();
		}
		return newPositions;
	}
	
	/**
	 * Weights each sample using the emission distribution
	 * 
	 * @param samples
	 * 			A set of samples 
	 * 
	 * @param noisyDistance
	 * 			The noisy distance between Pacman and the ghost
	 * 			
	 * @param pacmanLocation
	 * 			Pacman's location
	 * 
	 * @return
	 * 			The weights for each sample
	 * 			
	 */
	protected double[] weight(Coords[] samples, int noisyDistance, Coords pacmanLocation) {
		double[] probs = new double[samples.length];
		for(int i=0;i<samples.length;i++) {
			// A table of probabilities for the true distance given different noisy distances
			double[] emissionTable = sonar.getEmissionTable(sonar.manhattanDistance(samples[i], pacmanLocation));
			// The table entry for our given noisy distance
			double probGivenEvidence = emissionTable[noisyDistance];
			probs[i] = probGivenEvidence;
		}
		return probs;
	}
	
	/**
	 * Resamples a new set of unweighted samples
	 * 
	 * @param samples
	 * 				A set of samples
	 * 
	 * @param weights
	 * 				The weights for each sample
	 * @return
	 * 			A new set of unweighted samples
	 */
	protected Coords[] resample(Coords[] samples, double[] weights) {
		Coords[] newSamples = new Coords[samples.length];
		WeightedSet<Coords> resamplingDistribution = new WeightedSet<>();
		// Summing weights from samples to obtain resampling distribution
		for(int i=0;i<samples.length;i++) {
			if(resamplingDistribution.contains(samples[i])) {
				resamplingDistribution.increment(samples[i], weights[i]);
			}
			else {
				resamplingDistribution.addEvent(samples[i], weights[i]);
			}
		}
		resamplingDistribution.normalize();
		// Resampling
		for(int i=0;i<samples.length;i++) {
			newSamples[i] = resamplingDistribution.sample();
		}
		return newSamples;
	}
		
	/**
	 * Updates Pacman's belief of the ghost's location 
	 * 
	 * @param noisyDistance
	 * 			A noisy distance reading -- i.e., the noisy distance from Pacman to the ghost  
	 */
	public void update(int noisyDistance){	
		// Getting new particles
		Coords[] elapsed = elapse(particles);
		double[] weights = weight(elapsed,noisyDistance,location);
		particles = resample(elapsed,weights);
		// Clearing belief
		for(Coords element: belief.getElements()) {
			belief.addEvent(element, 0.0);
		}
		// Updating belief using particles
		for(int i=0;i<particles.length;i++) {
			if(belief.contains(particles[i])) {
				belief.increment(particles[i], 1);
			}
			else {
				belief.addEvent(particles[i], 1);
			}
		}
		belief.normalize();
	}
	
}
