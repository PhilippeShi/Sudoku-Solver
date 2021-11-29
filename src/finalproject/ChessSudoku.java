package finalproject;

import java.util.*;
import java.io.*;


public class ChessSudoku
{
	/* SIZE is the size parameter of the Sudoku puzzle, and N is the square of the size.  For
	 * a standard Sudoku puzzle, SIZE is 3 and N is 9.
	 */
	public int SIZE, N;

	/* The grid contains all the numbers in the Sudoku puzzle.  Numbers which have
	 * not yet been revealed are stored as 0.
	 */
	public int grid[][];

	/* Booleans indicating whether of not one or more of the chess rules should be
	 * applied to this Sudoku.
	 */
	public boolean knightRule;
	public boolean kingRule;
	public boolean queenRule;

	// Field that stores the same Sudoku puzzle solved in all possible ways
	public HashSet<ChessSudoku> solutions = new HashSet<ChessSudoku>();


	/* The solve() method should remove all the unknown characters ('x') in the grid
	 * and replace them with the numbers in the correct range that satisfy the constraints
	 * of the Sudoku puzzle. If true is provided as input, the method should find finds ALL
	 * possible solutions and store them in the field named solutions. */
	public void solve(boolean allSolutions) {
		if(allSolutions) {
			solveGridAllSolutions();
			if (!solutions.isEmpty()) {
				for (ChessSudoku sudoku : solutions) {
					this.grid = sudoku.getSudokuCopy().grid;
					break;
				}
			}
		}
		else solveGrid();

	}

	private void solveGridAllSolutions(){
		int[] empty = findBestEmpty();
		int row = empty[0];
		int col = empty[1];

		if(row == -1) {
			solutions.add(this.getSudokuCopy());
			return;
		}
		for (int guessNumber = 1; guessNumber <= N; guessNumber++) {
			if (validGuess(guessNumber,row, col)) {
				this.grid[row][col] = guessNumber;
				solveGridAllSolutions();
				this.grid[row][col] = 0;
			}
		}
		return;
	}

	private boolean solveGrid() {
		int[] empty = this.findBestEmpty();
		int row = empty[0];
		int col = empty[1];

		if (row == -1) return true;

		for (int guessNumber = 1; guessNumber<=N; guessNumber++) {
			if (validGuess(guessNumber, row, col)) {
				this.grid[row][col] = guessNumber;
				if(solveGrid()) return true;
				this.grid[row][col] = 0;
			}
		}
		return false;
	}

	/**
	 * Method used to get a deep copy of a sudoku grid.
	 * @return ChessSudoku object
	 */
	private ChessSudoku getSudokuCopy(){
		ChessSudoku copy = new ChessSudoku(this.SIZE);
		copy.grid = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				copy.grid[i][j] = this.grid[i][j];
		return copy;
	}

//The following methods are used to find the best empty cell to guess a number.

	/**
	 * Method returns the coords of the first empty cell of the best row or the best column,
	 * whichever has the most given cells.
	 * @return int[] {row, col}
	 */
	private int[] findBestEmpty(){
		int[] mostGivenRow = this.findBestRow();
		int[] mostGivenCol = this.findBestCol();

		//the row has the most given cells
		if (mostGivenRow[0] != -1 && mostGivenRow[1] >= mostGivenCol[1]){
			for (int i = 0; i<N;i++){
				if (this.grid[mostGivenRow[0]][i] == 0){
					return new int[] {mostGivenRow[0],i};
				}
			}
		}
		//the column has the most given cells
		else if (mostGivenCol[0]!= -1){
			for (int j = 0; j < N; j++) {
				if (grid[j][mostGivenCol[0]] == 0) {
					return new int[]{j, mostGivenCol[0]};
				}
			}
		}
		return new int[]{-1,-1};
	}

	/**
	 * Method finds a non-full row that has the most given cells
	 * Returns row = -1 if the sudoku has no empty cells.
	 * @return int[] {rowIndex, numOfGiven}
	 */
	private int[] findBestRow(){
		int row = -1;
		int mostGiven = 0;
		for (int i = 0; i<N;i++){
			int given = 0;
			for(int a: grid[i]) {
				if (a != 0) given++;
			}
			if (given < N && given > mostGiven) {
				row = i;
				mostGiven = given;
			}
		}
		return new int[]{row, mostGiven};
	}

	/**
	 * Method finds a non-full column that has the most given cells
	 * Returns col = -1 if the sudoku has no empty cells.
	 * @return int[] {colIndex, numOfGiven}
	 */
	private int[] findBestCol(){
		int col = -1;
		int mostGiven = 0;
		for (int i = 0; i<N;i++){
			int given = 0;
			for (int j = 0; j<N;j++) {
				if (grid[j][i] != 0) given++;
			}
			if (given < N && given > mostGiven) {
				col = i;
				mostGiven = given;
			}
		}
		return new int[]{col, mostGiven};
	}

// The following methods are used to check if a guess is valid or not.

	/**
	 * Checks the validity of a guess nummber with all the extra rules
	 * @param guessNumber
	 * @param row
	 * @param col
	 * @return
	 */
	private boolean validGuess(int guessNumber, int row, int col){
		if (!checkRowCol(guessNumber, row, col)) return false;
		else if (!checkGridInner(guessNumber, row, col)) return false;
		else if (knightRule && !checkKnightRule(guessNumber, row, col)) return false;
		else if (kingRule && !checkKingRule(guessNumber, row, col)) return false;
		else if (queenRule && guessNumber==N &&!checkQueenRule(guessNumber, row, col)) return false;
		return true;
	}

	/**
	 * Given a new integer that we want to add at a specific position on the grid,
	 * the method checks if the number is unique on its row and column.
	 *
	 * For both given row and column,
	 * 	- If the integer is unique  → return true
	 * 	- If the integer is a duplicate → return false
	 *
	 * @param guessNumber
	 * @param indexRow
	 * @param indexCol
	 * @return true if the input is valid, false otherwise
	 */
	private boolean checkRowCol(int guessNumber, int indexRow, int indexCol) {
		for (int i = 0; i < N; i++)
			// ↓ checks for duplicate in the row ↓   ↓ checks for duplicate in the column ↓
			if (this.grid[indexRow][i] == guessNumber || this.grid[i][indexCol] == guessNumber) return false;
		return true;
	}

	/**
	 * Given a new integer that we want to add within a specific inner grid,
	 * the method checks if the integer in unique within the grid.
	 * @param guessNumber
	 * @param indexRow
	 * @param indexCol
	 * @return true if the input is valid, false otherwise
	 */
	private boolean checkGridInner(int guessNumber, int indexRow, int indexCol){
		indexCol = (indexCol / SIZE) * SIZE;
		indexRow = (indexRow  / SIZE) * SIZE;
		for (int j = indexRow; j < indexRow+SIZE; j++)
			for (int i = indexCol; i < indexCol+SIZE; i++)
				if (this.grid[j][i] == guessNumber)
					return false;
		return true;

	}

	/**
	 * The method checks if a guess number at a given position respects the knight rule.
	 *
	 * @param guessNumber
	 * @param indexRow
	 * @param indexCol
	 * @return true of the guess is valid, false otherwise
	 */
	private boolean checkKnightRule(int guessNumber, int indexRow, int indexCol) {
		if (indexCol - 2 >= 0) {
			if (indexRow + 1 < N && grid[indexRow + 1][indexCol - 2] == guessNumber) return false;
			if (indexRow - 1 > 0 && grid[indexRow - 1][indexCol - 2] == guessNumber) return false;
		}

		if (indexCol - 1 >= 0) {
			if (indexRow + 2 < N && grid[indexRow + 2][indexCol - 1] == guessNumber) return false;
			if (indexRow - 2 > 0 && grid[indexRow - 2][indexCol - 1] == guessNumber) return false;
		}

		if (indexCol + 1 < N) {
			if (indexRow + 2 < N && grid[indexRow + 2][indexCol + 1] == guessNumber) return false;
			if (indexRow - 2 > 0 && grid[indexRow - 2][indexCol + 1] == guessNumber) return false;
		}

		if (indexCol + 2 < N) {
			if (indexRow + 1 < N && grid[indexRow + 1][indexCol + 2] == guessNumber) return false;
			if (indexRow - 1 > 0 && grid[indexRow - 1][indexCol + 2] == guessNumber) return false;
		}
		return true;
	}

	/**
	 * The method checks if a guess number at a given position respects the king rule
	 * @param guessNumber
	 * @param indexRow
	 * @param indexCol
	 * @return true if the guess is valid, false otherwise
	 */
	private boolean checkKingRule(int guessNumber, int indexRow, int indexCol) {
		if (indexRow - 1 >= 0) {
			if (indexCol + 1 < N && grid[indexRow - 1][indexCol + 1] == guessNumber) return false;
			if (indexCol - 1 >= 0 && grid[indexRow - 1][indexCol - 1] == guessNumber) return false;
		}

		if (indexRow + 1 < N) {
			if (indexCol + 1 < N && grid[indexRow + 1][indexCol + 1] == guessNumber) return false;
			if (indexCol - 1 >= 0 && grid[indexRow + 1][indexCol - 1] == guessNumber) return false;
		}
		return true;
	}

	/**
	 * The method checks if a guess number at a given position respects the queen rule
	 * @param guessNumber
	 * @param indexRow
	 * @param indexCol
	 * @return true if the guess is valid, false otherwise
	 */
	private boolean checkQueenRule(int guessNumber, int indexRow, int indexCol){
		int row, col;
		//top-left
		row = indexRow; col = indexCol;
		while (row >= 0 && col >=0 ){
			if (grid[row--][col--] == guessNumber) return false;
		}

		//top-right
		row = indexRow; col = indexCol;
		while (row >= 0 && col < N ){
			if (grid[row--][col++] == guessNumber) return false;
		}

		//bot-left
		row = indexRow; col = indexCol;
		while (row < N && col >=0 ){
			if (grid[row++][col--] == guessNumber) return false;
		}
		//bot-right
		row = indexRow; col = indexCol;
		while (row < N && col < N ){
			if (grid[row++][col++] == guessNumber) return false;
		}
		return true;
	}



	/*****************************************************************************/
	/* NOTE: YOU SHOULD NOT HAVE TO MODIFY ANY OF THE METHODS BELOW THIS LINE. */
	/*****************************************************************************/

	/* Default constructor.  This will initialize all positions to the default 0
	 * value.  Use the read() function to load the Sudoku puzzle from a file or
	 * the standard input. */
	public ChessSudoku( int size ) {
		SIZE = size;
		N = size*size;

		grid = new int[N][N];
		for( int i = 0; i < N; i++ )
			for( int j = 0; j < N; j++ )
				grid[i][j] = 0;
	}


	/* readInteger is a helper function for the reading of the input file.  It reads
	 * words until it finds one that represents an integer. For convenience, it will also
	 * recognize the string "x" as equivalent to "0". */
	static int readInteger( InputStream in ) throws Exception {
		int result = 0;
		boolean success = false;

		while( !success ) {
			String word = readWord( in );

			try {
				result = Integer.parseInt( word );
				success = true;
			} catch( Exception e ) {
				// Convert 'x' words into 0's
				if( word.compareTo("x") == 0 ) {
					result = 0;
					success = true;
				}
				// Ignore all other words that are not integers
			}
		}

		return result;
	}


	/* readWord is a helper function that reads a word separated by white space. */
	static String readWord( InputStream in ) throws Exception {
		StringBuffer result = new StringBuffer();
		int currentChar = in.read();
		String whiteSpace = " \t\r\n";
		// Ignore any leading white space
		while( whiteSpace.indexOf(currentChar) > -1 ) {
			currentChar = in.read();
		}

		// Read all characters until you reach white space
		while( whiteSpace.indexOf(currentChar) == -1 ) {
			result.append( (char) currentChar );
			currentChar = in.read();
		}
		return result.toString();
	}


	/* This function reads a Sudoku puzzle from the input stream in.  The Sudoku
	 * grid is filled in one row at at time, from left to right.  All non-valid
	 * characters are ignored by this function and may be used in the Sudoku file
	 * to increase its legibility. */
	public void read( InputStream in ) throws Exception {
		for( int i = 0; i < N; i++ ) {
			for( int j = 0; j < N; j++ ) {
				grid[i][j] = readInteger( in );
			}
		}
	}


	/* Helper function for the printing of Sudoku puzzle.  This function will print
	 * out text, preceded by enough ' ' characters to make sure that the printint out
	 * takes at least width characters.  */
	void printFixedWidth( String text, int width ) {
		for( int i = 0; i < width - text.length(); i++ )
			System.out.print( " " );
		System.out.print( text );
	}


	/* The print() function outputs the Sudoku grid to the standard output, using
	 * a bit of extra formatting to make the result clearly readable. */
	public void print() {
		// Compute the number of digits necessary to print out each number in the Sudoku puzzle
		int digits = (int) Math.floor(Math.log(N) / Math.log(10)) + 1;

		// Create a dashed line to separate the boxes
		int lineLength = (digits + 1) * N + 2 * SIZE - 3;
		StringBuffer line = new StringBuffer();
		for( int lineInit = 0; lineInit < lineLength; lineInit++ )
			line.append('-');

		// Go through the grid, printing out its values separated by spaces
		for( int i = 0; i < N; i++ ) {
			for( int j = 0; j < N; j++ ) {
				printFixedWidth( String.valueOf( grid[i][j] ), digits );
				// Print the vertical lines between boxes
				if( (j < N-1) && ((j+1) % SIZE == 0) )
					System.out.print( " |" );
				System.out.print( " " );
			}
			System.out.println();

			// Print the horizontal line between boxes
			if( (i < N-1) && ((i+1) % SIZE == 0) )
				System.out.println( line.toString() );
		}
	}


	/* The main function reads in a Sudoku puzzle from the standard input,
	 * unless a file name is provided as a run-time argument, in which case the
	 * Sudoku puzzle is loaded from that file.  It then solves the puzzle, and
	 * outputs the completed puzzle to the standard output. */
	public static void main( String args[] ) throws Exception {
		InputStream in = new FileInputStream("hard4x4.txt");

		// The first number in all Sudoku files must represent the size of the puzzle.  See
		// the example files for the file format.
		int puzzleSize = readInteger( in );
		if( puzzleSize > 100 || puzzleSize < 1 ) {
			System.out.println("Error: The Sudoku puzzle size must be between 1 and 100.");
			System.exit(-1);
		}

		ChessSudoku s = new ChessSudoku( puzzleSize );

		// You can modify these to add rules to your sudoku
		s.knightRule = false;
		s.kingRule = false;
		s.queenRule = false;

		// read the rest of the Sudoku puzzle
		s.read( in );

		System.out.println("Before the solve:");
		s.print();
		System.out.println();

		// Solve the puzzle by finding one solution.
		s.solve(false);

		// Print out the (hopefully completed!) puzzle
		System.out.println("After the solve:");
		s.print();
//
//		System.out.println("\nPrinting all solutions: ");
//		for (ChessSudoku c: s.solutions) {
//			c.print();
//			System.out.println("\n");
//		}
//		System.out.printf("There are %d solutions.", s.solutions.size());
	}
}
