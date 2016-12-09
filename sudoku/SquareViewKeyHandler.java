package sudoku;

/*
  By Roman Andronov
 */

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class SquareViewKeyHandler
	extends KeyAdapter
{
	public void
	keyReleased( KeyEvent ke )
	{
		Square		sqr = null;
		SquareView	sv = null;
		int		row = -1;
		int		col = -1;
		int		v = 0;
		int		kc = ke.getKeyCode();
		Object		o = ke.getComponent();

		if ( !(o instanceof SquareView ) )
		{
			return;
		}

		sv = ( SquareView )o;
		if ( sv.pnlSudoku.gameOver )
		{
			return;
		}

		sqr = sv.getSquare();
		row = sqr.getRow();
		col = sqr.getColumn();

		if ( kc == KeyEvent.VK_1 )
		{
			v = 1;
		}
		else if ( kc == KeyEvent.VK_2 )
		{
			v = 2;
		}
		else if ( kc == KeyEvent.VK_3 )
		{
			v = 3;
		}
		else if ( kc == KeyEvent.VK_4 )
		{
			v = 4;
		}
		else if ( kc == KeyEvent.VK_5 )
		{
			v = 5;
		}
		else if ( kc == KeyEvent.VK_6 )
		{
			v = 6;
		}
		else if ( kc == KeyEvent.VK_7 )
		{
			v = 7;
		}
		else if ( kc == KeyEvent.VK_8 )
		{
			v = 8;
		}
		else if ( kc == KeyEvent.VK_9 )
		{
			v = 9;
		}
		else if ( kc == KeyEvent.VK_BACK_SPACE ||
			kc == KeyEvent.VK_DELETE )

		{
			/*
			  Not applicable to pencil marks
			 */
			if ( sv.hasPencilMarks() )
			{
				return;
			}
			v = 0;
		}
		else if ( kc == KeyEvent.VK_DOWN )
		{
			row++;
			if ( row >= SudokuPanel.BOARD_SIZE )
			{
				return;
			}

			sv.pnlSudoku.curSquareView = sv.pnlSudoku.board[ row ][ col ].getView();
			sv.pnlSudoku.curSquareView.requestFocusInWindow();

			return;
		}
		else if ( kc == KeyEvent.VK_UP )
		{
			row--;
			if ( row < 0 )
			{
				return;
			}

			sv.pnlSudoku.curSquareView = sv.pnlSudoku.board[ row ][ col ].getView();
			sv.pnlSudoku.curSquareView.requestFocusInWindow();

			return;
		}
		else if ( kc == KeyEvent.VK_LEFT )
		{
			col--;
			if ( col < 0 )
			{
				return;
			}

			sv.pnlSudoku.curSquareView = sv.pnlSudoku.board[ row ][ col ].getView();
			sv.pnlSudoku.curSquareView.requestFocusInWindow();

			return;
		}
		else if ( kc == KeyEvent.VK_RIGHT )
		{
			col++;
			if ( col >= SudokuPanel.BOARD_SIZE )
			{
				return;
			}

			sv.pnlSudoku.curSquareView = sv.pnlSudoku.board[ row ][ col ].getView();
			sv.pnlSudoku.curSquareView.requestFocusInWindow();

			return;
		}
		else if ( kc == KeyEvent.VK_ENTER ||
			kc == KeyEvent.VK_SPACE )
		{
			sv.doPencilMarks();

			return;
		}
		else
		{
			return;
		}

		sqr.setValue( v );

		sv.pnlSudoku.checkEndOfGame( ", well done" );
	}
}
