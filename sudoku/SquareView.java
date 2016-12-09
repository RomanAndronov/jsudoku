package sudoku;

/*
  By Roman Andronov
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

class SquareView
	extends JLabel
	implements FocusListener
{
	SquareView( SudokuPanel sdkpnl, Color bgclr )
	{
		super();

		pnlSudoku = sdkpnl;
		MY_BG_CLR = bgclr;
		SV_FONT = getFont();
		TXT_CLR = getForeground();

		addMouseListener( pnlSudoku.SV_MH );

		setMaximumSize( SQUARE_VIEW_DIM );
		setMinimumSize( SQUARE_VIEW_DIM );
		setPreferredSize( SQUARE_VIEW_DIM );
		setLayout( GB_LO );

		setBorder( LINE_BRDR );
		setOpaque( true );
		setBackground( MY_BG_CLR );
		setHorizontalAlignment( SwingConstants.CENTER );
		setVerticalAlignment( SwingConstants.CENTER );
		setFocusable( true );
		addFocusListener( this );
		addMouseListener( pnlSudoku.SV_MH );
		addKeyListener( pnlSudoku.SV_KH );
	}

	void
	setSquare( Square sq )
	{
		mySquare = sq;
	}

	Square
	getSquare()
	{
		return mySquare;
	}

	void
	setValue( int v )
	{
		String		txt = "";

		if ( v > 0 )
		{
			txt += v;
		}

		setText( txt );
	}

	void
	handleTracker( int cmd )
	{
		Color		bgclr = cmd == SHOW_TRACK ? TRACK_CLR : MY_BG_CLR;

		setBackground( bgclr );
	}

	void
	track( int cmd )
	{
		if ( !pnlSudoku.track )
		{
			return;
		}

		for ( int i = 0; i < SudokuPanel.BOARD_SIZE; i++ )
		{
			pnlSudoku.board[ mySquare.getRow() ][ i ].getView().handleTracker( cmd );
			pnlSudoku.board[ i ][ mySquare.getColumn() ].getView().handleTracker( cmd );
		}
	}

	boolean
	hasPencilMarks()
	{
		return hasPencilMarks;
	}

	void
	doPencilMarks()
	{
		String		pmstr = null;
		Font		fnt = null;
		Color		fgclr = null;

		if ( mySquare.getValue() != 0 )
		{
			return;
		}

		hasPencilMarks = true;
		pmstr = pnlSudoku.pmDlg.edit( this );
		if ( pmstr == null || pmstr.length() == 0 )
		{
			hasPencilMarks = false;
		}

		fnt = hasPencilMarks ? PencilMarksEditor.PM_FONT : SV_FONT;
		fgclr = hasPencilMarks ? PencilMarksEditor.FG_CLR : TXT_CLR;

		setText( pmstr );
		setFont( fnt );
		setForeground( fgclr );
	}

	void
	clear()
	{
		setText( "" );
		setFont( SV_FONT );
		setForeground( TXT_CLR );
		setBackground( MY_BG_CLR );
	}

	public void
	focusGained( FocusEvent fe )
	{
		setBorder( RAISED_BRDR );
	}

	public void
	focusLost( FocusEvent fe )
	{
		setBorder( LINE_BRDR );
	}

	static final int			SHOW_TRACK = 0;
	static final int			RM_TRACK = 1;

	static final int			SQUARE_VIEW_SIZE = 50;
	static final Dimension			SQUARE_VIEW_DIM =
		new Dimension( SQUARE_VIEW_SIZE, SQUARE_VIEW_SIZE );

	static final Color			BASE_CLR = new Color( 197, 213, 203 );
	static final Color			ALT_CLR = new Color( 227, 224, 207 );
	static final Color			TRACK_CLR = Color.LIGHT_GRAY;
	static final Color			BRDR_CLR = new Color( 114, 120, 116 );

	static final Border			LINE_BRDR = 
	    BorderFactory.createLineBorder( BRDR_CLR );
	static final BevelBorder		RAISED_BRDR = new BevelBorder( BevelBorder.RAISED );

	static final GridBagLayout		GB_LO = new GridBagLayout();

	final SudokuPanel			pnlSudoku;
	final Font				SV_FONT;

	final Color				MY_BG_CLR;
	final Color				TXT_CLR;
	private Square				mySquare;
	private boolean				hasPencilMarks = false;
}
