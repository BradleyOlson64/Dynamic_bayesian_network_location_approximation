package graphics;


import java.util.Arrays;

import util.Coords;

/**
 * This class provides sonar capabilities. In particular, it has methods for:
 * 
 *  1. Computing the Manhattan distance between two coordinates on the grid
 *  2. It stores the CPT for the distribution p(noisyDistance | trueDistance)
 *  
 *  TODO: Could we ever get a noisy distance that is too large?
 * 
 * This logic was taken from the Pacman Projects:
 * http://ai.berkeley.edu/project_overview.html
 * 
 * @author alchambers
 * @version sp19
 *
 */
public class Sonar {
	/* 
	 * The greatest amount of noise that the sonar will add to the true Manhattan distance is
	 * 
	 * 								noise \in [-SONAR_MAX,SONAR_MAX]
	 * 
	 * Thus, the noisy Manhattan distance is given by
	 * 
	 * 							max{0, true + noise}
	 * 
	 * Where the maximum is taken to ensure that the noisy Manhattan distance is always positive  
	 */	
	private static int SONAR_NOISE_RANGE;	
	private static int SONAR_MAX;
	private int[] noise_values;
	private double[] noise_probs;	
	double[][] cpt;

	public Sonar(int boardSize, int sonarMax) {
		SONAR_MAX = sonarMax;		
		SONAR_NOISE_RANGE = 2*sonarMax + 1;
				
		// Produces the full range of noise values from {-SONAR_MAX,...,0,...,SONAR_MAX}
		noise_values = new int[SONAR_NOISE_RANGE];
		for(int i = 0; i < SONAR_NOISE_RANGE; i++) {
			noise_values[i] = i-SONAR_MAX;
		}
		// Computes the probability of producing each noise value
		noise_probs = new double[SONAR_NOISE_RANGE];
		double normalizer = 0.0;
		for(int i = 0; i < SONAR_NOISE_RANGE; i++) {
			int abs = Math.abs(noise_values[i]);
			noise_probs[i] = Math.pow(2, SONAR_MAX-abs);
			normalizer += noise_probs[i];
		}
		for(int i = 0; i < SONAR_NOISE_RANGE; i++) {
			noise_probs[i] /= normalizer;
		}		
		
		// Constructs the full CPT for p(noisyDistance|trueDistance)
		int maxDistance = 2*boardSize  - 1;
		cpt = new double[maxDistance][maxDistance];		
		
		// We are iterating over all possible noisy distances given the true distance
		for(int trueDistance = 0; trueDistance < maxDistance; trueDistance++ ) {
			for(int i = 0; i < noise_values.length; i++) {								
				// Some possible noisy distances are negative.
				// These values are pooled with the smallest possible distance (0) 				
				int possibleNoisy = trueDistance + noise_values[i];
				if(possibleNoisy < 0) {
					possibleNoisy = 0;
				}				
				
				// Some possible noisy distances are greater than the maximum possible Manhattan distance
				// These values are pooled with the greatest possible distance
				if(possibleNoisy >= maxDistance) {
					possibleNoisy = maxDistance-1;
				}
				
				// Note that we are NOT iterating over every entry in the cpt
				// There are many, many values that remain 0. 
				// In particular, for each row, only those columns in the range [true-MAX, true+MAX] are filled in
				cpt[trueDistance][possibleNoisy] += noise_probs[i];				
			}
		}
	
		// Make sure that each row sums to 1.0
		double sum = 0.0;
		double error = 1e-6;
		for(int trueDistance = 0; trueDistance < maxDistance; trueDistance++) {
			for(int col = 0; col < maxDistance; col++) {
				sum += cpt[trueDistance][col];
			}			
			if(Math.abs(sum-1.0) > error){
				System.err.println("Sonar: The probability distribution for " + trueDistance + " does not sum to 1.0");
				System.exit(-1);
			}
			sum = 0.0;
		}		
	}
	
	
	
	/**
	 * Returns a single row from the CPT corresponding to:
	 * 				
	 * 							p( NoisyDistance | TrueDistance = k)
	 * 
	 * The returned row is a distribution over the noisy distance given that the true distance equals k. As such, 
	 * the following hold true:
	 * 
	 * 1. The length of the returned vector is equal to the number of possible values for noisy distance
	 * 2. The returned vector sums to 1.0
	 *  
	 * @param trueDistance
	 * 					A valid Manhattan Distance
	 * 
	 * @return A probability distribution over the noisy distance given that the true distance equaks k
	 * 
	 */
	public double[] getEmissionTable(int trueDistance) {
		if(trueDistance < 0 || cpt.length <= trueDistance) {
			throw new IndexOutOfBoundsException();
		}
		return cpt[trueDistance];
	}
	
	
	/**
	 * Returns the noisy distance between 2 coordinates on the grid 
	 * @param p1
	 * 			A position on the grid
	 * 
	 * @param p2
	 * 			A position on the grid
	 * @return
	 */
	public int getNoisyDistance(Coords p1, Coords p2) {
		int true_distance = manhattanDistance(p1, p2);
		int noise = sample_noise();		
		return Math.max(0, true_distance+noise);		
	}
	
	// Computes Manhattan distance between two positions on the grid
	public int manhattanDistance(Coords p1, Coords p2) {
		int row_diff = Math.abs(p1.row - p2.row);
		int col_diff = Math.abs(p1.col - p2.col);
		return row_diff + col_diff;
	}

	
	/*------------------------------------------------------
	 * 				PRIVATE HELPER METHODS
	 *------------------------------------------------------*/
	
	private int sample_noise() {
		double rand = Math.random();
		double totProb = 0.0;	
		for(int i = 0; i < SONAR_NOISE_RANGE; i++) {
			totProb += noise_probs[i];			
			if(rand <= totProb) {
				return noise_values[i];
			}		
		}			
		return -1; // should never reach here
	}
	
	
	public static void main(String[] args) {
		Sonar r = new Sonar(3, 2);
		double[] row0 = r.getEmissionTable(0);
		double[] row1 = r.getEmissionTable(1);
		double[] row2 = r.getEmissionTable(2);
		double[] row3 = r.getEmissionTable(3);
		double[] row4 = r.getEmissionTable(4);		
		System.out.println(Arrays.toString(row0));
		System.out.println(Arrays.toString(row1));
		System.out.println(Arrays.toString(row2));
		System.out.println(Arrays.toString(row3));
		System.out.println(Arrays.toString(row4));				
	}
	
}
