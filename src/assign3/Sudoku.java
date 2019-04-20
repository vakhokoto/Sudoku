package assign3;

import java.util.SortedSet;
import java.util.TreeSet;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {

	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)

	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"6 0 0 0 0 0 0 0 9",
	"7 0 0 0 0 3 6 5 0",
	"0 5 0 9 0 0 8 0 0",
	"2 0 0 4 0 0 0 0 0",
	"8 0 1 0 0 2 0 3 0",
	"0 0 0 0 3 9 1 0 0",
	"0 4 0 0 0 0 0 6 0",
	"0 0 0 6 2 0 0 0 5",
	"0 0 2 0 0 0 0 7 0");

	// Provided medium 5 3 grid

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
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0

	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");

	public static final int SIZE = 9;  // size of the whole 9x9 puzzle

	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;

	private Spot[][] grid, solutionGrid;
	private int solutionNum;
	private long startTime, endTime;

	/* store which numbers can be assigned in an initial row column and square
		if i-th bit is 1 (i + 1) can be assigned */
	private int[] rows, columns, squares;

	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
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


	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
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


	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
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


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.

	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(easyGrid);

		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}



	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		grid = new Spot[SIZE][SIZE];
		for (int i=0; i<SIZE; ++i){
			for (int j=0; j<SIZE; ++j){
				grid[i][j] = new Spot(ints[i][j]);
			}
		}
	}


	/**
	 * Sets up Sudoku puzzle based on string containing
	 * numbers in string format
	 * @param string the initial Sudoku puzzle
	 * */
	public Sudoku(String string) {
		this(textToGrid(string));
	}

	/* offset of column number when stored in SortedSet */
	private static final int COLUMN_OFFSET = 4;

	/* offset of choice number when stored in SortedSet */
	private static final int CHOICE_OFFSET = 8;

	/**
	 * This is actual function which finds the solutions
	 * by backtracking strategy
	 * @param set Sorted which contains Spot information
	 *            number of possible insertions and coordinates
	 * */
	private void findSolution(SortedSet <Integer> set){
		if (set.size() == 0){
			if (solutionGrid == null){
				backUpGrid();
			}
			solutionNum++;
			return;
		}
		Integer low = set.first();
		set.remove(low);
		int x = ((1 << COLUMN_OFFSET) - 1) & low;
		int y = ((1 << COLUMN_OFFSET) - 1) & (low >> COLUMN_OFFSET);
		int sqNum = x / PART * PART + y / PART;
		int choiceNum = rows[x] & columns[y] & squares[sqNum];
		if (choiceNum == 0){
			set.add(low);
			return;
		}
		int[] choices = getChoiceArray(choiceNum);
		for (int i=0; i<choices.length; ++i){
			int curChoice = choices[i];
			updateArrays(x, y, sqNum, curChoice, false);
			grid[x][y].setValue(curChoice);
			findSolution(set);
			if (solutionNum == MAX_SOLUTIONS){
				break;
			}
			updateArrays(x, y, sqNum, curChoice, true);
		}
		grid[x][y].setValue(0);
		set.add(low);
	}

	/**
	 * This method updates arrays rows, columns, squares
	 * @param x row number
	 * @param y column number
	 * @param sqNum square number
	 * @param curChoice current choice to assign in [x, y] Spot
	 * @param backUp indicates if it is case of assigning 0 or 1 in [x, y] Spot
	 * */
	private void updateArrays(int x, int y, int sqNum, int curChoice, boolean backUp) {
		int upVal = (1 << curChoice - 1);
		if (backUp){
			rows[x] |= upVal;
			columns[y] |= upVal;
			squares[sqNum] |= upVal;
		} else {
			upVal = (~upVal);
			rows[x] &= upVal;
			columns[y] &= upVal;
			squares[sqNum] &= upVal;
		}
	}

	/**
	 * This method returns array of choices for a Spot
	 * */
	private int[] getChoiceArray(int choiceNum) {
		int numOnes = Integer.bitCount(choiceNum);
		int[] arr = new int[numOnes];
		int cnt = 0;
		for (int i=0; i<SIZE; ++i){
			if (((1 << i) & choiceNum) == (1 << i)){
				arr[cnt++] = i + 1;
			}
		}
		return arr;
	}

	/**
	 * This method backs up solution
	 * */
	private void backUpGrid() {
		solutionGrid = new Spot[SIZE][SIZE];
		for (int i=0; i<SIZE; ++i){
			for (int j=0; j<SIZE; ++j){
				solutionGrid[i][j] = new Spot(grid[i][j].getValue());
			}
		}
	}

	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		solutionNum = 0;
		solutionGrid = null;
		startTime = System.currentTimeMillis();
		rows = new int[SIZE];
		columns = new int[SIZE];
		squares = new int[SIZE];
		for (int i=0; i<SIZE; ++i){
			rows[i] = columns[i] = squares[i] = (1 << SIZE) - 1;
		}
		for (int i=0; i<grid.length; ++i){
			for (int j=0; j<grid[i].length; ++j){
				int curVal = grid[i][j].getValue();
				if (curVal == 0) {
					continue;
				}
				int squareNum = i / PART * PART + j / PART;
				int andVal = ~(1 << curVal - 1);
				squares[squareNum] &= andVal;
				rows[i] &= andVal;
				columns[j] &= andVal;
			}
		}
		SortedSet <Integer> set = fillInSet();
		findSolution(set);
		endTime = System.currentTimeMillis();
		return solutionNum;
	}

	/**
	 * This method creates TreeSet and fills it
	 *
	 * The information is stored with following format
	 * 		1st COLUMN_OFFSET bits are row number
	 * 		2nd CHOICE_OFFSET - COLUMN_OFFSET bits are column number
	 * 		rest of bits store possible number of insertion for a Spot [x, y]
	 * */
	private SortedSet<Integer> fillInSet() {
		TreeSet <Integer> set = new TreeSet<>();
		for (int i=0; i<grid.length; ++i){
			for (int j=0; j<grid[i].length; ++j) {
				if (grid[i][j].getValue() != 0)
					continue;
				int sqNum = i / PART * PART + j / PART;
				int coef = rows[i] & columns[j] & squares[sqNum];
				int choiceNum = Integer.bitCount(coef);
				int addVal = i + (j << COLUMN_OFFSET) + (choiceNum << CHOICE_OFFSET);
				set.add(addVal);
			}
		}
		return set;
	}

	/**
	 * This method returns solutions for current array in String format
	 * */
	public String getSolutionText() {
		Spot[][] buff = grid;
		grid = solutionGrid;
		String ans = toString();
		grid = buff;
		return ans;
	}

	/**
	 * This method returns time spent for computation of the solution
	 * */
	public long getElapsed() {
		return endTime - startTime;
	}

	/**
	 * This method converts the data of element in String
	 * using inner class's toString method
	 * */
	@Override
	public String toString(){
		String ans = "";
		if (grid == null){
			return ans;
		}
		for (int i=0; i<SIZE; ++i){
			for (int j=0;j<SIZE; ++j){
				ans += grid[i][j].toString() + (j != SIZE - 1 ?" ":"");
			}
			ans += '\n';
		}
		return ans;
	}

/**
 * This class encapsulates the storing of
 * spot of sudoku puzzle
 * */
	private class Spot{
		private int value;

		/**
		 * Initialize Spot variable with initial value
		 * @param value value to be set
		 * */
		public Spot(int value){
			this.value = value;
		}

		/**
		 * This method sets value of spot to newValue
		 * @param newValue new value which to be set
		 * */
		public void setValue(int newValue){
			value = newValue;
		}

		/**
		 * This method returns the value of spot inner value
		 * */
		public int getValue(){
			return value;
		}

		/**
		 * Converts the data of element in String
		 * */
		@Override
		public String toString(){
			return "" + value;
		}
	}
}
