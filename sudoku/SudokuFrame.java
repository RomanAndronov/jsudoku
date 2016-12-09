package sudoku;

/*
  By Roman Andronov
 */

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

/*
   This will execute sudoku as a stand-alone
   Java program.

   To execute it as a Java applet consult the
   SudokuApplet class in this package
*/

public
class SudokuFrame
	extends JFrame
{
	public
	SudokuFrame()
	{
		super();
		setTitle( "Sudoku" );
		setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
	}

	public static void
	main( String[] args )
	{
		SwingUtilities.invokeLater( new Runnable()
		{
			public void
			run()
			{
				SudokuFrame		sudokufrm = new SudokuFrame();

				sudokufrm.sudokupnl = new SudokuPanel( sudokufrm );
				sudokufrm.pack();
				sudokufrm.setLocationRelativeTo( null );
				sudokufrm.setVisible( true );
			}
		});

	}

	private SudokuPanel		sudokupnl = null;
}
