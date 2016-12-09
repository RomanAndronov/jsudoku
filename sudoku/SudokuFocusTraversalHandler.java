package sudoku;

/*
  By Roman Andronov
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

class SudokuFocusTraversalHandler
	extends FocusTraversalPolicy
{
	SudokuFocusTraversalHandler( SudokuPanel sdkpnl )
	{
		super();

		pnlSudoku = sdkpnl;
	}

	public Component 
	getComponentAfter( Container focusCycleRoot, Component aComponent )
	{
		int			r = -1;
		int			c = -1;
		SquareView		sv = null;

		if ( !( aComponent instanceof SquareView ) )
		{
			return null;
		}

		sv = ( SquareView )aComponent;
		r = sv.getSquare().getRow();
		c = sv.getSquare().getColumn();

		if ( ( c + 1 ) < SudokuPanel.BOARD_SIZE )
		{
			c++;
		}
		else
		{
			c = 0;
			if ( ( r + 1 ) < SudokuPanel.BOARD_SIZE )
			{
				r++;
			}
			else
			{
				r = 0;
			}
		}

		return pnlSudoku.board[ r ][ c ].getView();
	}

	public Component 
	getComponentBefore( Container focusCycleRoot, Component aComponent )
	{
		int			r = -1;
		int			c = -1;
		SquareView		sv = null;

		if ( !( aComponent instanceof SquareView ) )
		{
			return null;
		}

		sv = ( SquareView )aComponent;
		r = sv.getSquare().getRow();
		c = sv.getSquare().getColumn();

		if ( ( c - 1 ) >= 0 )
		{
			c--;
		}
		else
		{
			c = SudokuPanel.BOARD_SIZE - 1;
			if ( ( r - 1 ) >= 0 )
			{
				r--;
			}
			else
			{
				r = SudokuPanel.BOARD_SIZE - 1;
			}
		}

		return sv.pnlSudoku.board[ r ][ c ].getView();
	}

	public Component 
	getFirstComponent( Container focusCycleRoot )
	{
		return pnlSudoku.board[ 0 ][ 0 ].getView();
	}

	public Component 
	getLastComponent( Container focusCycleRoot )
	{
		return pnlSudoku.board[ SudokuPanel.BOARD_SIZE ][ SudokuPanel.BOARD_SIZE ].getView();
	}

	public Component 
	getDefaultComponent( Container focusCycleRoot )
	{
		return pnlSudoku.curSquareView;
	}

	private final SudokuPanel		pnlSudoku;
}
