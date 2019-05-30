package util;

/**
 * Stores the row and column coordinates of a given cell in the grid.
 *  
 * @author alchambers
 * @version sp19
 *
 */
public class Coords{
	public int row;
	public int col;
	
	/**
	 * Stores the row and column coordinates of a given cell
	 * @param row
	 * 			The row 
	 * @param col
	 * 			The column
	 */
	public Coords(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	/**
	 * Returns a hashcode for a particular cell in the grid
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		return result;
	}
	
	/**
	 * Checks if two cells are equal
	 * @return True if both cells have the same row and column, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(obj == this) {
			return true;
		}
		
		if(obj instanceof Coords) {
			Coords other = (Coords) obj;
			return other.row == this.row && other.col == this.col;
		}		
		return false;
	}
		
	@Override
	public String toString() {
		return "("+row+","+col+")";
	}
}
