package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This is a general purpose class that maps elements to a numerical value.
 * The meaning of the numerical value depends upon the usage. The numerical value could be:
 * 
 * - A tally (e.g. for direct sampling or rejection sampling)
 * - A weight (e.g. for likelihood weighting)
 * - A probability (e.g. for a CPT)
 * 
 * <strong><em>Note that I have added a substantial amount of extra functionality to this WeightedSet than
 * was present in the past assignment! Please familiarize yourself with this new functionality.</em></strong> 
 * class. 
 * 
 * @author alchambers
 * @version sp19
 *
 */
public class WeightedSet<E> {
	private HashMap<E, Double> set;
	private double sum;

	
	/**
	 * Creates a new <em>empty</em> weighted set.
	 */
	public WeightedSet() {		
		sum = 0.0;
		set = new HashMap<>();					
	}

	/**
	 * Creates a new weighted set initialized with the elements from the specified list.
	 *  
	 * @param list
	 * 			A list of elements
	 */
	
	public WeightedSet(List<E> list) {
		sum = 0.0;
		set = new HashMap<>();
		for(E e : list) {
			if(contains(e)) {
				increment(e, 1.0);
			}
			else {
				addEvent(e, 1.0);
			}			
		}		
	}
	
	/**
	 * Creates a new weighted set initialized with the elements from the specified array.
	 * 
	 * @param list
	 * 			An array of elements
	 */
	public WeightedSet(E[] list) {
		sum = 0.0;
		set = new HashMap<>();
		for(E e : list) {
			if(contains(e)) {
				increment(e, 1.0);
			}
			else {
				addEvent(e, 1.0);
			}			
		}
	}

	/**
	 * Creates a new weighted set initialized with the elements and weights from the specified array.
	 * 
	 * @param list
	 * 			An array of elements
	 * @param weights
	 * 			The weight for each element
	 */
	public WeightedSet(E[] list, double[] weights) {
		if(list.length != weights.length) {
			throw new IllegalArgumentException();
		}
		sum = 0.0;
		set = new HashMap<>();
		for(int i = 0; i < list.length; i++) {
			E e = list[i];
			double w = weights[i];
			if(contains(e)) {
				increment(e, w);
			}
			else {
				addEvent(e, w);
			}			
		}
	}
	
	
	/**
	 * Returns the number of elements in the set
	 * 
	 * @return The number of elements in the set
	 */
	public int numElements() {
		return set.size();
	}
		
	/**
	 * Adds a new element to the weighted set with the given numerical value.  If the weighted set
	 * previously contained the element, the old value is replaced.
	 *  
	 * @param element
	 * 				An element to be added to the weighted set
	 * @param value
	 * 				The value of the element
	 */
	public void addEvent(E element, double value) {
		if(set.containsKey(element)) {
			double w = set.get(element);
			sum -= w;
		}
		set.put(element, value);
		sum += value;		
	}

	
	/**
	 * Increments the value of the element by the given amount
	 * 
	 * @param element
	 * 			An element in the weighted set
	 * 			
	 * @param amount
	 * 			The amount by which the value of the element is incremented
	 * 
	 * @throws IllegalArgumentException if the element is <strong>not</strong> in the set
	 */
	public void increment(E element, double amount) {
		if(!set.containsKey(element)) {
			throw new IllegalArgumentException();
		}
		double w = set.get(element);
		w += amount;
		sum += amount;
		set.put(element, w);
	}

	/**
	 * Checks if the element is contained in the weighted set
	 * 
	 * @param element
	 * 				An element to be checked 
	 * @return
	 * 				True if the element is contained in the weighted set, false otherwise.
	 */
	public boolean contains(E element) {
		return set.containsKey(element);
	}
	
	/**
	 * Removes all elements from the weighted set
	 */
	public void clear() {
		set.clear();
		sum = 0.0;
	}
	
	/**
	 * Returns the weight associated with the given element. If the element is not in the set,
	 * this method returns 0.0
	 * 
	 * @param element
	 * 			An element in the weighted set
	 */
		public double getWeight(E element) {
		if(!set.containsKey(element)) {
			return 0.0;
		}
		return set.get(element);
	}

	/**
	 * Returns a set of all elements in the weighted set
	 * 
	 * @return
	 * 		All elements in the weighted set
	 */
	public Set<E> getElements() {
		return set.keySet();
	}

	
	/**
	 * Normalizes all weights.
	 * 
	 * @post The sum of all weights is 1.0
	 */
	public void normalize() {		
		if(sum == 0.0) {
			return; 
		}
		for(E element : set.keySet()) {
			double prob = set.get(element);
			set.put(element, prob/sum);
		}
		sum = 1.0;
	}

	/**
	 * Samples an element from the set according to the weights. 
	 *  
	 * 
	 * @return
	 */
	public E sample() {
		if(sum != 1.0) {			
			normalize(); // ensure that the weights have been normalized
		}
		if(sum == 0) {
			throw new IllegalStateException("All elements have weight 0....unable to sample");
		}
		double rand = Math.random();
		double totProb = 0.0;	
		for(E element : set.keySet()) {
			double prob = set.get(element);
			totProb += prob;
			if(rand <= totProb) {
				return element;
			}
		}
		return null; // should never reach here
	}

	/**
	 * Returns a String representation of the contents of the weighted set
	 * @return A string representation of the contents of the weighted set
	 */
	@Override
	public String toString() {
		String s = "Total sum: " + sum + "\n";
		for(E element : set.keySet()) {
			s += element + ": " + set.get(element) + "\n";
		}
		return s;
	}
}
