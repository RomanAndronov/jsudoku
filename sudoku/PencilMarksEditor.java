package sudoku;

/*
  By Roman Andronov
 */

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.Font;

import javax.swing.JTextArea;

class PencilMarksEditor
	extends JTextArea
{
	PencilMarksEditor( PencilMarksDialog pmdlg )
	{
		super();

		pmDlg = pmdlg;

		setColumns( 3 );
		setRows( 2 );
		setLineWrap( true );
		setFont( PM_FONT );
		setForeground( FG_CLR );
		setBorder( null );
		enableEvents( AWTEvent.KEY_EVENT_MASK );
	}

	void
	close()
	{
		pmDlg.setVisible( false );
	}

	protected void 
	processKeyEvent( KeyEvent ke )
	{
		int		id = ke.getID();

		if ( id == KeyEvent.KEY_RELEASED )
		{
			int		kc = ke.getKeyCode();

			if ( kc == KeyEvent.VK_ENTER )
			{
				// Accept the changes
				pmstr = getText();
				close();
				return;
			}
			else if ( kc == KeyEvent.VK_ESCAPE )
			{
				// Ignore the changes
				close();
			}
		}
		else if ( id == KeyEvent.KEY_PRESSED )
		{
			int		kc = ke.getKeyCode();

			switch ( kc )
			{						
				case KeyEvent.VK_DELETE:
				case KeyEvent.VK_BACK_SPACE:
				case KeyEvent.VK_UP:
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_RIGHT:
					super.processKeyEvent( ke );
					return;

				default:
					break;
			}
		}
		else if( id == KeyEvent.KEY_TYPED )
		{
			char	ch = ke.getKeyChar();

			switch ( ch )
			{
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case '\b': // Backspace
					super.processKeyEvent( ke );
					return;

				default:
					break;
			}

		}
		ke.consume();
	}

	static final Font		PM_FONT = 
		new Font( "Tahoma", Font.PLAIN, 11 );

	static final Color		FG_CLR = new Color( 0, 51, 153 );

	final PencilMarksDialog		pmDlg;
	String				pmstr = null;
	SquareView			mySquareView = null;
}
