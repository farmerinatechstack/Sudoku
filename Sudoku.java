package assign3;

import java.util.*;

/* Class: Sudoku
 * -------------
 * Encapsulates a Sudoku grid to be solved. 
 */
public class Sudoku {
	
	// Easy Sudoku grid
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Medium Sudoku grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Hard Sudoku grid
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 0 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	// Completely empty grid
	public static final int[][] emptyGrid = Sudoku.stringsToGrid(
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0");
	
	// Super hard Sudoku grid
	public static final int[][] extremeGrid = Sudoku.stringsToGrid(
			"006700005",
			"070030080",
			"100009600",
			"400005300",
			"020080040",
			"009400001",
			"008600002",
			"050010030",
			"200007400");
	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	
	private int [][] grid;
	// gridSoln will store the first solution found for a grid
	private int [][] gridSoln;
	
	private long runTime;
	private List<Spot> spots;
	private int numSolns;
		
	
	/* Method: stringsToGrid
	 * ---------------------
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/* Method: textToGrid
	 * ------------------
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}
	
	
	/* Method: stringToInts
	 * --------------------
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	/* main
	 * ----
	 * Currently solves the hard grid
	 */
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);
		System.out.println(sudoku); // print the raw problem
		
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}
	
	/* Method: printSpots
	 * ------------------
	 * Prints out information regarding the empty spots in the grid provided
	 */
	private void printSpots() {
		for(Spot s : spots) {
			System.out.print("Spot (row, col) : " + s.row + ", " + s.col);
			System.out.print(" - PossNums: ");
			s.printPossNums();
		}
	}

	/* Method: printGrid
	 * ----------------- 
	 * Prints the underlying grid structure - useful for testing.
	 */
	private void printGrid() {
		System.out.println();
		System.out.println("Printing Grid");
		for(int curRow = 0; curRow < SIZE; curRow++) {
			for (int curCol = 0; curCol < SIZE; curCol++) {
				System.out.print(grid[curRow][curCol] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	/* Method: sanityCheck
	 * -------------------
	 * Sanity check each spot's possible numbers array - used for testing
	 */
	private void sanityCheck() {
		for(Spot s : spots) {
			for(int curCol = 0; curCol < SIZE; curCol++) {
				int curNum = grid[s.row][curCol];
				if (s.possNums.contains(curNum)) {
					System.out.println(curNum + "detected at " + s.row + " ," + curCol);
				}
			}
			
			for (int curRow = 0; curRow < SIZE; curRow++) {
				int curNum = grid[curRow][s.col];
				if (s.possNums.contains(curNum)) {
					System.out.println(curNum + "detected at " + curRow + " ," + s.col);
				}
			}
			
			int squareX = (s.row / PART) * PART;
			int squareY = (s.col / PART) * PART;
			int endX = squareX + PART;
			int endY = squareY + PART;
			
			for(int curRow = squareX; curRow < endX; curRow++) {
				for(int curCol = squareY; curCol < endY; curCol++) {
					int curNum = grid[curRow][curCol];
					if (s.possNums.contains(curNum)) {
						System.out.println(curNum + "detected at " + curRow + " ," + curCol);
					}
				}
			}
		}
		System.out.println("Sanity check cleared");
	}
	
	/* Constructor: Sudoku
	 * -------------------
	 * Sets up Sudoku based on the given ints. The code assumes that the ints array passed in is not
	 * changed during the computation of the solution. As such, we do not make a deepy copy, but simply
	 * point our grid to the ints array.
	 */
	public Sudoku(int[][] ints) {
		grid = new int [SIZE][SIZE];
		grid = ints;
		gridSoln = null;
		spots = new ArrayList<Spot>();

		// iterate through all grid cells
		for (int curRow = 0; curRow < SIZE; curRow++) { 
			for (int curCol = 0; curCol < SIZE; curCol++) {
				if (grid[curRow][curCol] == 0) {
					// create a spot object if cell unfilled, then add to spots
					Spot s = new Spot(curRow, curCol);
					spots.add(s);
				}
			}
		}
		Collections.sort(spots, new SpotComp());
	}
	
	/* Constructor: Sudoku
	 * -------------------
	 * Sets up based on a text-based puzzle. Converts the text to a representative grid.
	 */
	public Sudoku(String text) {
		this(textToGrid(text));
	}

	/* Method: copyGrid
	 * ----------------
	 * Does a deep copy of one grid representation to another
	 */
	private void copyGrid(int[][] src, int [][] dest) {
		for(int curCol = 0; curCol < SIZE; curCol++) {
			System.arraycopy(src[curCol], 0, dest[curCol], 0, SIZE);
		}
	}
	
	/* Method: solve
	 * -------------
	 * Solves the puzzle, invoking the underlying recursive search in getBoardSolutions
	 */
	public int solve() {
		// the case where the puzzle is solved
		if (spots.size() == 0) {
			gridSoln = new int[SIZE][SIZE];
			copyGrid(grid, gridSoln);
			return 1;
		}
		
		int spotsFilled = 0;
		numSolns = 0;
		// start and time the recursive backtracking
		long start = System.currentTimeMillis();
		getBoardSolutions(spots.get(0), spotsFilled);
		long end = System.currentTimeMillis();
		runTime = end - start;
		
		return numSolns;
	}
	
	/* Method: getBoardSolutions
	 * -------------------------
	 * getBoardSolutions is the recursive backtracking method which solves the Sudoku
	 * problem being represented. The method iterates through each Spot in spots, which represents
	 * the empty cells, and for each Spot tries the possible cell inputs.
	 * 
	 * Because an input in one cell can effect possible inputs for another cell, a Spot's possible
	 * numbers must be recomputed on each recursive call of the method.
	 * 
	 * A few base cases exist. When there are no possible inputs for a given spot, the recursion does
	 * not continue down the given path. If there are inputs and numSolns is less than MAX_SOLUTIONS(100),
	 * then the method tracks how many Spots from the array spots has been filled. When this is equal to 
	 * the size of the spots array and there is only one possible number for a given cell, we have effectively
	 * reached the last spot in the array and have a solution. Otherwise, we continue the recursive method until
	 * either a solution is found or no possible inputs exits for a given Spot.
	 */
	private void getBoardSolutions(Spot curSpot, int spotsFilled) {
		// compute the possible numbers for a spot
		curSpot.getPossNums();
		int numPossible = curSpot.getNumberPossible();
		
		// if there are no possible numbers, backtrack
		if (numPossible != 0 && numSolns < MAX_SOLUTIONS) {
			// if there are possible numbers, increment spotsFilled because we are about to fill a spot
			spotsFilled++;

			// iterate through each possible number for the current Spot
			for(int num : curSpot.possNums) {
				// set the Spot to the current number and continue the recursion
				curSpot.set(num);
				
				// base case when a solution is found - increment numSolns and store only the first solution
				if (spotsFilled == spots.size() && numPossible == 1) {
					numSolns++;
					if (gridSoln == null) {
						gridSoln = new int[SIZE][SIZE];
						copyGrid(grid, gridSoln);
					}
				} else if (spotsFilled < spots.size()) {
					// if we have not filled a Spot for each element in spots, continue the recursion
					getBoardSolutions(spots.get(spotsFilled), spotsFilled); 
				}
			}
			// set all spots back to 0 to maintain original grid state
			curSpot.set(0);
		}
	}
	
	/* Method: gridToStr
	 * -----------------
	 * Converts a grid to a string format
	 */
	private String gridToStr(int[][] sudokuGrid) {
		StringBuilder sb = new StringBuilder();

		for(int curRow = 0; curRow < SIZE; curRow++) {
			for(int curCol = 0; curCol < SIZE; curCol++) {
				sb.append(sudokuGrid[curRow][curCol]);
				sb.append(" ");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	/* Method: toString
	 * ----------------
	 * Return a readable version of the grid.
	 */
	@Override
	public String toString() {
		return gridToStr(grid);
	}
	
	/* Method: getSolutionText
	 * -----------------------
	 * Returns the solution for the Sudoku grid in text format
	 */
	public String getSolutionText() {
		return gridToStr(gridSoln);
	}
	
	/* Method: getElapsed
	 * ------------------
	 * Returns the run-time taken to solve the Sudoku
	 */
	public long getElapsed() {
		return runTime;
	}

	/* Class: SpotComp
	 * ---------------
	 * A custom comparator used to sort the Spots in order of increasing number of possible values
	 */
	private class SpotComp implements Comparator<Spot> {
		@Override
		public int compare(Spot s1, Spot s2) {
			int s1Size = s1.getNumberPossible();
			int s2Size = s2.getNumberPossible();
			
			if(s1Size < s2Size) {
				return -1;
			} else if (s1Size > s2Size) {
				return 1;
			}
			return 0;
		}
		
	}
	
	/* Class: Spot
	 * -----------
	 * Inner Spot class used to represent relevant information for open spots in the Sudoku grid.
	 * Each spot must track its coordinates and possible numbers.
	 */
	private class Spot {
		private int row;
		private int col;
		private Set<Integer> possNums;
		
		/* Constructor: Spot
		 * -----------------
		 * Initializes a spot object, which has a row and column coordinate as well as Set of 
		 * possible values.
		 */
		public Spot (int row, int col) {
			this.row = row;
			this.col = col;

			getPossNums();
		}
		
		/* Method: set
		 * -----------
		 * Assign a Spot in the grid to the given val.
		 */
		private void set(int val) {
			grid[row][col] = val;
		}
		
		/* Method: getNumberPossible
		 * -------------------------
		 * Return the number of possible values for a given Spot in the grid.
		 */
		private int getNumberPossible() {
			return possNums.size();
		}
		
		/* Method: printPossNums
		 * ---------------------
		 * Print the possible numbers which can fill a spot.
		 */
		public void printPossNums() {
			for(Integer i : possNums) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
		
		/* Method: getPossNums
		 * -------------------
		 * Get the possible numbers that can be entered into the spot. First create a HashSet containing
		 * numbers 1-9. Then iterate through the spot's row, column, and square and remove from the HashSet
		 * any numbers that we iterate over.
		 */
		private void getPossNums() {
			possNums = new HashSet<Integer>();
			possNums.add(1);
			possNums.add(2);
			possNums.add(3);
			possNums.add(4);
			possNums.add(5);
			possNums.add(6);
			possNums.add(7);
			possNums.add(8);
			possNums.add(9);
			// remove from set based on col, row, and square nums
			removeRowNums();
			removeColNums();
			removeSquareNums();
		}
		
		/* Method: removeRowNums
		 * ---------------------
		 * Remove from possNums all numbers in the Spot's row
		 */
		private void removeRowNums() {
			for(int curCol = 0; curCol < SIZE; curCol++) {
				int curNum = grid[this.row][curCol];
				if (curNum != 0 && possNums.contains(curNum)) possNums.remove(curNum);
			}
		}
		
		/* Method: removeColNums
		 * ---------------------
		 * Remove from possNums all numbers in the Spot's column
		 */
		private void removeColNums() {
			for (int curRow = 0; curRow < SIZE; curRow++) {
				int curNum = grid[curRow][this.col];
				if (curNum != 0 && possNums.contains(curNum)) possNums.remove(curNum);
			}
		}
		
		/* Method: removeSquareNums
		 * ------------------------ 
		 * Remove from possNums all numbers in the Spot's square
		 */
		private void removeSquareNums() {
			int squareX = (this.row / PART) * PART;
			int squareY = (this.col / PART) * PART;
			int endX = squareX + PART;
			int endY = squareY + PART;
			
			for(int curRow = squareX; curRow < endX; curRow++) {
				for(int curCol = squareY; curCol < endY; curCol++) {
					int curNum = grid[curRow][curCol];
					if (curNum != 0 && possNums.contains(curNum)) possNums.remove(curNum);
				}
			}
		}
	}
}
