package sudoku;

import java.util.LinkedList;
import java.util.Random;

/*
  By Roman Andronov

  This object holds the entire dancing links matrix in memory.
  To optimize the memory usage it allocates memory once at
  construction time. The dancing links matrix object should
  be reused across multiple games as follows:
 
 	DlxSudokuMatrix		dsm = new DlxMatrix();
 
 	dsm.setPuzzle();
 	dsm.solve();
 	9 x 9 int array = dsm.getSolution();
 
  To obtain a solution call the solve() method. It will
  return the number of solutions. If this number is zero, no
  solutions, or greater than one, more than one solution, this
  is not a valid Sudoku puzzle, and calling getSolution() will
  return null. If solve() returns one, calling getSolution()
  will return a 2-D integer array filled with the solution.

 */

class DlxSudokuMatrix
{
	DlxSudokuMatrix()
	{
		/*
		  Allocate memory for all the structures once.

		  The total number of constraints equals the
		  number of columns in the dancing links matrix
		 */
		rootHeader = new DlxColumnHeader();
		allHeaders = new DlxColumnHeader[ STD_SUDOKU_CNSTRNTS ];
		for ( int i = 0; i < allHeaders.length; i++ )
		{
			allHeaders[ i ] = new DlxColumnHeader();
		}

		/*
		  All the entries go into a 2-D array for ease
		  of reference later. Each row in this array
		  represents a dancing links matrix row. Each
		  column represents one constraint
		 */
		allEntries =
			new DlxMatrixEntry[ STD_SUDOKU_DLX_ROWS ][ STD_SUDOKU_CNSTRNTS_PER_ROW ];
		for ( int r = 0; r < STD_SUDOKU_DLX_ROWS; r++ )
		{
			for ( int c = 0; c < STD_SUDOKU_CNSTRNTS_PER_ROW; c++ )
			{
				allEntries[ r ][ c ] = new DlxMatrixEntry( r );
			}
		}
		
		/*
		  The dlx solution rows store the row numbers
		  from the all entries array. Call getSolution()
		  to map the dlx rows into a 2-D int array
		 */
		dlxSolutionRows = new LinkedList();
		solvedPuzzle =
			new int[ STD_SUDOKU_GRID_ROWS ][ STD_SUDOKU_GRID_COLUMNS];
		newPuzzle = new int[ STD_SUDOKU_GRID_ROWS ][ STD_SUDOKU_GRID_COLUMNS];
		
		/*
		 * Put the dlx matrix in an initial working state
		 */
		reset();
	}

	void
	setPuzzle( int[][] puzzle )
	{
		/*
		  To set the puzzle we just remove the rows
		  corresponding to the initially supplied numbers
		 */
		int			dlxrow = 0;

		reset();

		for ( int row = 0; row < STD_SUDOKU_GRID_ROWS; row++ )
		{
			for ( int col = 0; col < STD_SUDOKU_GRID_COLUMNS; col++ )
			{
				for ( int digit = 0; digit < STD_SUDOKU_DIGITS; digit++ )
				{
					if ( puzzle[ row ][ col ] == ( digit + 1 ) )
					{
						dlxSolutionRows.addFirst( new Integer( dlxrow ) );
						allEntries[ dlxrow ][ 0 ].getColumnHeader().remove();
						removeAllColumnsInRow( allEntries[ dlxrow ][ 0 ] );
					}
					dlxrow++;
				}
			}
		}

	}
	
	int
	solve()
	{
		dance();

		return numberOfSolutions;
	}

	void
	getSolution( int[][] dest )
	{
		if ( numberOfSolutions != 1 )
		{
			return;
		}
		
		fillSolvedMatrix( solvedPuzzle );

		copyMatrix( dest, solvedPuzzle );
	}
	
	void
	reset()
	{
		linkAllHeaders();
		linkAllEntries();
		numberOfSolutions = 0;
		dlxSolutionRows.clear();
	}
	
	void
	newPuzzle( int ind, int[][] puzzle )
	{
		int		d = -1;
		int		r = -1;
		int		c = -1;
		int		N = STD_SUDOKU_GRID_ROWS * STD_SUDOKU_GRID_COLUMNS - ind;

		/*
		  Create a seed, random, matrix. Make sure
		  that it has at least one solution. Take
		  that solution, keep zeroing out its entires,
		  subject to one solution requirement, untill
		  all but the asked number of entries are left
		 */

		while ( true )
		{
			mkRandomMatrix( newPuzzle );
			setPuzzle( newPuzzle );
			if ( solve() > 0 )
			{
				fillSolvedMatrix( solvedPuzzle );
				copyMatrix( newPuzzle, solvedPuzzle );
				break;
			}

		}

		for ( int i = 0; i < N; i++ )
		{
			while ( true )
			{
				while ( true )
				{
					r = randomSudokuDigit() - 1;
					c = randomSudokuDigit() - 1;
					if ( newPuzzle[ r ][ c ] != 0 )
					{
						break;
					}
				}
				d = newPuzzle[ r ][ c ];
				newPuzzle[ r ][ c ] = 0;
				setPuzzle( newPuzzle );
				if ( solve() == 1 )
				{
					break;
				}
				newPuzzle[ r ][ c ] = d;
			}
		}

		copyMatrix( puzzle, newPuzzle );
	}

	static boolean
	isValidEntry( int d, int r, int c, int[][] sm )
	{
		if ( d < 1 || d > 9 )
		{
			return false;
		}
		
		/*
		  The candidate entry must satisfy all the
		  constraints. No duplicate entries in this row
		 */
		for ( int i = 0; i < sm[ 0 ].length; i++ )
		{
			if ( sm[ r ][ i ] == d )
			{
				return false;
			}
		}
		
		/*
		  No duplicate entries in this column
		 */
		for ( int i = 0; i < sm.length; i++ )
		{
			if ( sm[ i ][ c ] == d )
			{
				return false;
			}
		}
		
		/*
		  No duplicate entries within this box
		 */
		int		brf = ( r / STD_SUDOKU_ROWS_PER_BOX ) * STD_SUDOKU_ROWS_PER_BOX;
		int		brt = brf + STD_SUDOKU_ROWS_PER_BOX;
		int		bcf = ( c / STD_SUDOKU_COLS_PER_BOX ) * STD_SUDOKU_COLS_PER_BOX;
		int		bct = bcf + STD_SUDOKU_COLS_PER_BOX;

		for ( int i = brf; i < brt; i++ )
		{
			for ( int j = bcf; j < bct; j++ )
			{
				if ( sm[ i ][ j ] == d )
				{
					return false;
				}
			}
		}

		return true;
	}
	
	static boolean
	isValidMatrix( int[][] sm )
	{
		int			d;
		
		for ( int r = 0; r < sm.length; r++ )
		{
			for ( int c = 0; c < sm[ 0 ].length; c++ )
			{
				d = sm[ r ][ c ];
				if ( d == 0 )
				{
					continue;
				}
				if ( !isValidEntry( d, r, c, sm ) )
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void
	linkAllHeaders()
	{
		/*
		  The root header closes the circle
		 */
		rootHeader.setRight( allHeaders[ 0 ] );
		rootHeader.setLeft( allHeaders[ allHeaders.length - 1 ] );

		/*
		  Skip the first entry in allHeaders. Set
		  it up manually after the loop is done
		 */
		for ( int i = 1; i < allHeaders.length; i++ )
		{
			allHeaders[ i ].rmAllEntries();
			allHeaders[ i ].setLeft( allHeaders[ i - 1 ] );
			allHeaders[ i - 1 ].setRight( allHeaders[ i ] );
		}

		/*
		  Close the circle
		 */
		allHeaders[ 0 ].rmAllEntries();
		allHeaders[ 0 ].setLeft( rootHeader );
		allHeaders[ allHeaders.length - 1 ].setRight( rootHeader );
	}

	private void
	linkAllEntries()
	{
		/*
		  To figure out which column gets an entry we do not use
		  the name field as sugested by Mr. D. Knuth. Instead, we
		  use a column's ordinal position in the allHeaders array.
		  To do that we come up with a way to assign a monotonically
		  increasing number to each constraint.

		  A standard Sudoku board is a 9x9 grid with four constraints:

		  1) Row-column: each row-column intersection has one and
		 	only one number. Since standard sudoku has nine rows
		 	and nine columns, we have a total of 9*9=81 row-column
		 	constraints. To map these constraints to a nice number
		 	we just calculate linear square number:

		 	    9 * row + column

		  2) Row-number: each number, 1 to 9, must appear in a row
		 	exactly once. Nine numbers over nine rows makes it
		 	9*9=81 constraints:

		 	    9 * row + number

		  3) Column-number: each number must appear exactly once in
		 	a column. Nine numbers over nine columns is 81
		 	constraints:

		 	    9 * column + number

		  4) Box-number constraint: each number must appear exactly
		 	once in each three by three box. Nine numbers over
		 	nine boxes makes 81 constraints:

		 	    ( 3 * boxrow + boxcol ) * 9 + number
		 
		  Dlx columns follow the above order for assigning column
		  numbers to the corresponding constraints
		 */

		int		dlxrow = 0;
		int[]		dlxcols = new int[ STD_SUDOKU_CNSTRNTS_PER_ROW ];

		for ( int row = 0; row < STD_SUDOKU_GRID_ROWS; row++ )
		{
			for ( int col = 0; col < STD_SUDOKU_GRID_COLUMNS; col++ )
			{
				for ( int digit = 0; digit < STD_SUDOKU_DIGITS; digit++ )
				{
					dlxcols[ ROW_COL_CNSTRNT ] = 9 * row + col;
					dlxcols[ ROW_NUM_CNSTRNT ] = ROW_NUM_OFFSET + 9 * row + digit;
					dlxcols[ COL_NUM_CNSTRNT ] = COL_NUM_OFFSET + 9 * col + digit;
					dlxcols[ BOX_NUM_CNSTRNT ] = BOX_NUM_OFFSET + 
						9 * ( ( row / 3 ) * 3 + col / 3 ) + digit;
					linkRow( dlxrow, dlxcols );
					dlxrow++;
				}
			}
		}
	}

	/*
	  Link each entry within the row left/right and,
	  within the column, up/down
	 */
	private void
	linkRow( int dlxrow, int[] dc )
	{
		DlxMatrixEntry			dme;
		DlxColumnHeader			dch;

		for ( int i = 0; i < dc.length; i++ )
		{
			dch = allHeaders[ dc[ i ] ];
			dme = allEntries[ dlxrow ][ i ];
			dme.setColumnHeader( dch );
			dch.addEntry( dme );
			if ( i > 0 )
			{
				dme.setLeft( allEntries[ dlxrow ][ i - 1 ] );
				allEntries[ dlxrow ][ i - 1 ].setRight( dme );
			}
		}
	    
		/*
		  Close the circle by linking the
		  first and the last entries
		 */
		int			last = allEntries[ 0 ].length - 1;

		allEntries[ dlxrow ][ 0 ].setLeft( allEntries[ dlxrow ][ last ] );
		allEntries[ dlxrow ][ last ].setRight( allEntries[ dlxrow ][ 0 ] );
	}
	
	/*
	  Recursive solution as per Mr. D. Knuth
	 */
	private void
	dance()
	{
		/*
		  This test terminates the recursion: all the columns
		  have been eliminated from the matrix, nothing to search.

		  Ideally we should get here once and only once, if it's 
		  a valid Sudoku puzzle
		 */
		if ( rootHeader.getRight() == rootHeader )
		{
			if ( dlxSolutionRows.size() == STD_SUDOKU_SOLUTION_SIZE )
			{
				numberOfSolutions++;
			}
			return;
		}
		
		DlxColumnHeader			col = shortestColumn();
		DlxMatrixEntry			ent = col.getDown();
		
		col.remove();
		while ( ent != col )
		{
			pushSolution( ent );
			removeAllColumnsInRow( ent );
			dance();

			/*
			  Remove this test if looking for
			  ALL the possible solutions
			 */
			if ( numberOfSolutions > 1 )
			{
				return;
			}

			popSolution();
			addAllColumnsInRow( ent );
			ent = ent.getDown();
		}
		col.add();
	}
	
	private DlxColumnHeader
	shortestColumn()
	{
		DlxColumnHeader			c = ( DlxColumnHeader )rootHeader.getRight();
	
		if ( c == rootHeader )
		{
			return null; /* The matrix is empty */
		}
		
		int				s = c.getSize();
		DlxColumnHeader			shortest = c;

		while ( c != rootHeader )
		{
			if ( c.getSize() < s )
			{
				s = c.getSize();
				shortest = c;
			}
			c = ( DlxColumnHeader )c.getRight();
		}
		
		return shortest;
	}
	
	private void
	removeAllColumnsInRow( DlxMatrixEntry rowHead )
	{
		DlxMatrixEntry		curr = rowHead.getRight();

		while ( curr != rowHead )
		{
			curr.getColumnHeader().remove();
			curr = curr.getRight();
		}
	}
	
	private void
	addAllColumnsInRow( DlxMatrixEntry rowHead )
	{
		DlxMatrixEntry		curr = rowHead.getLeft();

		while ( curr != rowHead )
		{
			curr.getColumnHeader().add();
			curr = curr.getLeft();
		}
	}
	
	private void
	pushSolution( DlxMatrixEntry dme )
	{
		/*
		  A small optimization: do not add to list
		  if this is an invalid Sudoku puzzle
		 */
		if ( numberOfSolutions > 0 )
		{
			return;
		}

		dlxSolutionRows.addFirst( new Integer( dme.getRowNumber() ) );
	}
	
	private void
	popSolution()
	{
		/*
		  Do not remove the entries from the list
		  once a solution has been found
		 */
		if ( numberOfSolutions > 0 )
		{
			return;
		}

		dlxSolutionRows.removeFirst();
	}
	
	private void
	initNewPuzzle()
	{
		for ( int r = 0; r < newPuzzle.length; r++ )
		{
			for ( int c = 0; c < newPuzzle[ 0 ].length; c++ )
			{
				newPuzzle[ r ][ c ] = 0;
			}
		}
	}
	
	private int
	randomSudokuDigit() // Between 1 and 9
	{
		int			rsd = ( new Random() ).nextInt( 65927 ) % 9 + 1;		

		return rsd;
	}
	
	private void
	fillSolvedMatrix( int[][] sm )
	{
		int			row = -1;
		int			col = -1;
		int			digit = -1;
		int			dlxrow = -1;
		int			N = dlxSolutionRows.size(); // Should be 81
		
		for ( int i = 0; i < N; i++ )
		{
			dlxrow = ( ( Integer )dlxSolutionRows.get( i ) ).intValue();
			digit = dlxrow % STD_SUDOKU_DIGITS;
			dlxrow /= STD_SUDOKU_DIGITS;
			col = dlxrow % STD_SUDOKU_DIGITS;
			dlxrow /= STD_SUDOKU_DIGITS;
			row = dlxrow % STD_SUDOKU_DIGITS;
			sm[ row ][ col ] = digit + 1;
		}
	}
	
	private void
	copyMatrix( int[][] to, int[][] from )
	{
		for ( int r = 0; r < from.length; r++ )
		{
			for ( int c = 0; c < from[ 0 ].length; c++ )
			{
				to[ r ][ c ] = from[ r ][ c ];
			}
		}
	}
	
	private void
	mkRandomMatrix( int[][] sm )
	{
		int			d = -1;
		int			r = -1;
		int			c = -1;
		
		initNewPuzzle();
		for ( int i = 0; i < 17; i++ )
		{
			while ( true )
			{
				d = randomSudokuDigit();
				while ( true )
				{
					r = randomSudokuDigit() - 1;
					c = randomSudokuDigit() - 1;
					if ( sm[ r ][ c ] == 0 )
					{
						break;
					}
				}
				if ( isValidEntry( d, r, c, newPuzzle ) )
				{
					sm[ r ][ c ] = d;
					break;
				}
			}
		}
	}


	private static final int		STD_SUDOKU_CNSTRNTS = 324;
	private static final int		STD_SUDOKU_DLX_ROWS = 9 * 9 * 9;
	private static final int		STD_SUDOKU_CNSTRNTS_PER_ROW = 4;
	private static final int		STD_SUDOKU_GRID_ROWS = 9;
	private static final int		STD_SUDOKU_GRID_COLUMNS = 9;
	private static final int		STD_SUDOKU_DIGITS = 9;
	private static final int		STD_SUDOKU_ROWS_PER_BOX = 3;
	private static final int		STD_SUDOKU_COLS_PER_BOX = 3;
	private static final int		ROW_COL_CNSTRNT = 0;
	private static final int		ROW_NUM_CNSTRNT = 1;
	private static final int		COL_NUM_CNSTRNT = 2;
	private static final int		BOX_NUM_CNSTRNT = 3;
	private static final int		ROW_NUM_OFFSET = 81;
	private static final int		COL_NUM_OFFSET = ROW_NUM_OFFSET + 81;
	private static final int		BOX_NUM_OFFSET = COL_NUM_OFFSET + 81 ;
	private static final int		STD_SUDOKU_SOLUTION_SIZE = 81;


	/*
	  A root header is an end of traversal marker
	 */
	private DlxColumnHeader			rootHeader;


	/*
	  All the headers live in a fixed size array since
	  the number of constraints is known ahead of time
	  and does not change
	 */
	private DlxColumnHeader			allHeaders[];
	

	/*
	  All the entries array makes it
	  easy to relink the matrix
	 */
	private DlxMatrixEntry			allEntries[][];


	/*
	  A list to store the dlx row numbers which
	  are the solutions to this puzzle
	 */
	private LinkedList			dlxSolutionRows;


	/*
	  The number of solutions is used to inform
	  the user about the puzzle in question. Valid
	  Sudoku puzzles have only one solution
	 */
	private int				numberOfSolutions;


	/*
	  A solution array storage is also
	  reused across multiple games
	 */
	private int[][]				solvedPuzzle;


	/*
	  A new puzzles go here; storage reused
	 */
	private int[][]				newPuzzle;
}
