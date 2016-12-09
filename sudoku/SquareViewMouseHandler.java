package sudoku;

/*
  By Roman Andronov
 */

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

class SquareViewMouseHandler
	extends MouseInputAdapter
{
	public void
	mouseClicked( MouseEvent me )
	{
		Object		o = me.getComponent();

		if ( o instanceof SquareView )
		{
			int		cc = me.getClickCount();
			SquareView	sv = ( SquareView )o;

			if ( cc == 1 && !me.isConsumed() )
			{
				me.consume();
				sv.pnlSudoku.curSquareView = sv;
				sv.requestFocusInWindow();
			}
			else if ( cc == 2 && !me.isConsumed() )
			{
				me.consume();
				sv.doPencilMarks();
			}
		}
	}

	public void
	mouseEntered( MouseEvent me )
	{
		Object		o = me.getComponent();

		if ( o instanceof SquareView )
		{
			SquareView	sv = ( SquareView )o;

			me.consume();
			sv.track( SquareView.SHOW_TRACK );
		}
	}

	public void
	mouseExited( MouseEvent me )
	{
		Object		o = me.getComponent();

		if ( o instanceof SquareView )
		{
			SquareView	sv = ( SquareView )o;

			me.consume();
			sv.track( SquareView.RM_TRACK );
		}
	}
}
