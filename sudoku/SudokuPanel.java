package sudoku;

/*
  By Roman Andronov
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

class SudokuPanel
	extends JPanel
	implements ActionListener
{
	SudokuPanel( JFrame pfrm )
	{
		super();

		parentFrame = pfrm;

		SV_MH = new SquareViewMouseHandler();
		SV_KH = new SquareViewKeyHandler();

		S_FTH = new SudokuFocusTraversalHandler( this );
		setFocusCycleRoot( true );
		setFocusTraversalPolicy( S_FTH );

		dlxsm = new DlxSudokuMatrix();

		init();
	}

	public void
	actionPerformed( ActionEvent ae )
	{
		Object		o = ae.getSource();

		if ( o instanceof JButton )
		{
			JButton		jb = ( JButton )o;

			if ( jb == jbNewGame )
			{
				doNewGame();
			}
			else if ( jb == jbSolve )
			{
				solvePuzzle();
			}
			else if ( jb == jbExport )
			{
				exportPuzzle();
			}
			else if ( jb == jbImport )
			{
				importPuzzle();
			}
		}
		else if ( o instanceof JCheckBox )
		{
			SquareView		sqv = null;

			track = chkbUseTracker.isSelected();
			if ( track )
			{
				return;
			}

			for ( int r = 0; r < BOARD_SIZE; r++ )
			{
				for ( int c = 0; c < BOARD_SIZE; c++ )
				{
					sqv = board[ r ][ c ].getView();
					sqv.setBackground( sqv.MY_BG_CLR );
				}
			}
		}
	}

	private void
	init()
	{
		gui = new SudokuGui( this );

		gui.init();

		doNewGame();
	}

	private void
	clearBoard()
	{
		for ( int r = 0; r < BOARD_SIZE; r++ )
		{
			for ( int c = 0; c < BOARD_SIZE; c++ )
			{
				board[ r ][ c ].clear();
			}
		}
	}

	private void
	selectFirstSquare()
	{
		int		sqval = 1;

		for ( int r = 0; r < BOARD_SIZE; r++ )
		{
			for ( int c = 0; c < BOARD_SIZE; c++ )
			{
				sqval = board[ r ][ c ].getValue();
				if ( sqval == 0 )
				{
					curSquareView = board[ r ][ c ].getView();
					curSquareView.requestFocusInWindow();
					break;
				}
			}
			if ( sqval == 0 )
			{
				break;
			}
		}
	}

	private void
	doNewGame()
	{
		if ( !gameOver )
		{
			int		a = JOptionPane.showConfirmDialog( this, 
				"Your current game is not over yet.\n" +
				"Start a new game anyway?", "New Game?",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );

			if ( a == JOptionPane.NO_OPTION )
			{
				return;
			}
		}

		initDigits = Integer.parseInt( ( String )cmbGivenDigits.getSelectedItem() );

		dlxsm.newPuzzle( initDigits, puzzle );

		newGame();
	}

	private void
	newGame()
	{
		gameOver = false;
		lblGameState.setText( "Game: on" );
		clearBoard();
		binToBoard();
		selectFirstSquare();
	}

	private void
	invalidSudokuMsg( int ns )
	{
		String		txt = "Game: is invalid (" + ns + " solutions)";

		lblGameState.setText( txt );
	}


	/*
	  Convert the game's view to its binary representation ...
	 */
	void
	boardToBin()
	{
		for ( int r = 0; r < BOARD_SIZE; r++ )
		{
			for ( int c = 0; c < BOARD_SIZE; c++ )
			{
				puzzle[ r ][ c ] = board[ r ][ c ].getValue();
			}
		}
	}

	/*
	  and vica versa
	 */
	void
	binToBoard()
	{
		for ( int r = 0; r < BOARD_SIZE; r++ )
		{
			for ( int c = 0; c < BOARD_SIZE; c++ )
			{
				board[ r ][ c ].setValue( puzzle[ r ][ c ] );
			}
		}
	}

	boolean
	checkEndOfGame( String opt )
	{
		int		ns = 0;
		String		eog = null;

		/*
		  Are all the digits filled in?
		 */
		for ( int r = 0; r < BOARD_SIZE; r++ )
		{
			for ( int c = 0; c < BOARD_SIZE; c++ )
			{
				if ( board[ r ][ c ].getValue() == 0 )
				{
					return false;
				}
			}
		}
		
		/*
		  Is it any good?
		 */
		boardToBin();
		dlxsm.setPuzzle( puzzle );
		ns = dlxsm.solve();
		if ( ns != 1 )
		{
			invalidSudokuMsg( ns );
			return false;
		}
		
		gameOver = true;
		eog = "Game: over";
		if ( opt != null && opt.length() > 0 )
		{
			eog += opt;
		}
		lblGameState.setText( eog );

		return true;
	}

	/*
	  This function "flattens" the square nxn matrix
	  by "stretching" it into one contiguous row by
	  computing a unique index, a column number in a
	  single row, of each row/column entry
	 */
	static int
	uniqNdx( int r, int c, int n )
	{
		int		rv = r * n + c;

		return rv;
	}

	void
	solvePuzzle()
	{
		int		ns = 0;

		boardToBin();
		dlxsm.setPuzzle( puzzle );
		ns = dlxsm.solve();
		if ( ns != 1 )
		{
			invalidSudokuMsg( ns );
			return;
		}

		dlxsm.getSolution( puzzle );
		binToBoard();
		gameOver = true;
		lblGameState.setText( "Game: solved" );
	}

	File
	getFile( String title )
	{
		int		rv = -1;

		if ( fileChooser == null )
		{
			fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		}

		fileChooser.setCurrentDirectory( new File( "." ) );
		rv = fileChooser.showDialog( this, title );
		if ( rv != JFileChooser.APPROVE_OPTION )
		{
			return null;
		}

		return fileChooser.getSelectedFile();
	}
    
	void
	importPuzzle()
	{
		if ( !gameOver )
		{
			int		a = JOptionPane.showConfirmDialog( this, 
				"Your current game is not over yet.\n" +
				"Would you like to import a new game anyway?", "Import anyway?",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );

			if ( a == JOptionPane.NO_OPTION )
			{
				return;
			}
		}

		boolean		err = false;
		File		f = getFile( "Import Game" );

		if ( f == null )
		{
			return;
		}

		clearBoard();

		try
		{
			int			row;
			int			col;
			String			s = "";
			FileReader		fr = new FileReader( f );
			BufferedReader		br = new BufferedReader( fr );

			int			v;
			int			n;
			int			sl;
			
			row = col = 0;
			while ( ( s = br.readLine() ) != null )
			{
				if ( row >= BOARD_SIZE )
				{
					break;
				}

				sl = s.length();
				n = sl < BOARD_SIZE ? sl : BOARD_SIZE;
				for ( int i = 0; i < n; i++, col++ )
				{
					try
					{
						v = Integer.parseInt( Character.toString( s.charAt( i ) ) );
					}
					catch ( Exception e )
					{
						v = 0;
					}
					puzzle[ row ][ col ] = v;
				}
				row++;
				col = 0;
			}
			fr.close();
		}
		catch ( Exception e )
		{
			err = true;
			JOptionPane.showMessageDialog( this, 
				"Sorry, couldn't import this puzzle: " + 
				e.getMessage(), 
				"Sorry, couldn't import " + f.getName() + " puzzle.", 
				JOptionPane.ERROR_MESSAGE );
		}

		if ( !err )
		{
			newGame();
			checkEndOfGame( null );
		}
	}
    
	void
	exportPuzzle()
	{
		File		f = getFile( "Export Puzzle" );

		if ( f == null )
		{
			return;
		}

		if ( f.exists() )
		{
			int		a = JOptionPane.showConfirmDialog( this, 
				"Puzzle " + f.getName() + " exists. Export anyway?", 
				"Puzzle " + f.getName() + " exists", 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE );

			if ( a == JOptionPane.NO_OPTION )
			{
				return;
			}
		}

		try
		{
			int			n;
			String			s = "";
			FileWriter		fw = new FileWriter( f );
			BufferedWriter		bw = new BufferedWriter( fw );
			PrintWriter		pw = new PrintWriter( bw );

			for ( int r = 0; r < BOARD_SIZE; r++ )
			{
				for ( int c = 0; c < BOARD_SIZE; c++ )
				{
					n = board[ r ][ c ].getValue();
					s += Integer.toString( n );
				}
				pw.println( s );
				s = "";
			}
			pw.flush();
			pw.close();
		}
		catch ( Exception e )
		{
			JOptionPane.showMessageDialog( this, 
				"Sorry, couldn't export this puzzle: " + 
				e.getMessage(), 
				"Sorry, couldn't export " + f.getName() + " puzzle", 
				JOptionPane.ERROR_MESSAGE );
		}
	}

	static final int		BOARD_SIZE = 9;
	static final int		BLOCKS_PER_ROW = 3;

	final SquareViewMouseHandler	SV_MH;
	final SquareViewKeyHandler	SV_KH;
	final SudokuFocusTraversalHandler	S_FTH;
	static final String[]		ALLOWED_GIVEN_DIGITS = { "36", "35",
		"34", "33", "32", "31", "30", "29", "28", "27", "26" };

	JFrame				parentFrame = null;
	SudokuGui			gui = null;
	SquareView			curSquareView = null;

	JPanel				pnlMain = null;
	JPanel				pnlBoard = null;
	PencilMarksDialog		pmDlg = null;

	JPanel[][]			blocks = null;

	JPanel				pnlCtrls = null;
	JButton				jbNewGame = null;

	JLabel				lblGivenDigits = null;
	JComboBox			cmbGivenDigits = null;

	JButton				jbSolve = null;
	JButton				jbExport = null;
	JButton				jbImport = null;

	JCheckBox			chkbUseTracker = null;
	JLabel				lblGameState = null;

	JFileChooser			fileChooser = null;

	DlxSudokuMatrix			dlxsm = null;
	int				initDigits = 36;
	Square[][]			board = null;
	int[][]				puzzle = null;
	boolean				gameOver = true;
	Square				pressedSquare = null;
	boolean				track = false;
}
