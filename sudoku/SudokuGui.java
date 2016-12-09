package sudoku;

/*
  By Roman Andronov
 */

import java.awt.Insets;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

class SudokuGui
{
	SudokuGui( SudokuPanel pnlsudoku )
	{
		pnlSudoku = pnlsudoku;
	}

	void
	init()
	{
		GridBagConstraints	gbc = new GridBagConstraints();
		Insets			dfltInsts = gbc.insets;

		gbc.gridx = gbc.gridy = 0;
		gbc.weightx = gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = INSETS;

		pnlSudoku.parentFrame.setLayout( new GridBagLayout() );

		pnlSudoku.setLayout( new GridBagLayout() );
		pnlSudoku.setBorder( BorderFactory.createLineBorder( CLRGRAY ) );
		pnlSudoku.setBackground( BOARD_CLR );
		pnlSudoku.parentFrame.add( pnlSudoku, gbc );

		pnlSudoku.pnlMain = new JPanel();
		pnlSudoku.pnlMain.setBorder( BorderFactory.createLineBorder( CLRGRAY ) );
		pnlSudoku.pnlMain.setLayout( new GridBagLayout() );
		pnlSudoku.pnlMain.setBackground( BOARD_CLR );
		pnlSudoku.add( pnlSudoku.pnlMain, gbc );

		pnlSudoku.pnlBoard = new JPanel();
		pnlSudoku.pnlBoard.setLayout( new GridBagLayout() );
		gbc.insets = dfltInsts;
		pnlSudoku.pnlMain.add( pnlSudoku.pnlBoard, gbc );
		gbc.insets = INSETS;
		mkBoardPnl();

		gbc.gridy = 1;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnlSudoku.pnlCtrls = new JPanel();
		pnlSudoku.pnlCtrls.setBorder( BorderFactory.createLineBorder( CLRGRAY ) );
		pnlSudoku.pnlCtrls.setLayout( new GridBagLayout() );
		pnlSudoku.add( pnlSudoku.pnlCtrls, gbc );
		mkCtrlsPnl();
	}

	private void
	mkBoardPnl()
	{
		Color			sclr = null;
		int			ndx = -1;
		int			blRow = -1;
		int			blCol = -1;
		SquareView		sv = null;
		JPanel			jpnl = null;
		GridBagLayout		gblo = new GridBagLayout();
		GridBagConstraints	gbc = new GridBagConstraints();

		pnlSudoku.pnlBoard.setBackground( BOARD_CLR );

		pnlSudoku.pmDlg = new PencilMarksDialog( pnlSudoku.parentFrame );

		gbc.gridx = gbc.gridy = 0;
		gbc.weightx = gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;

		pnlSudoku.blocks =
			new JPanel[ SudokuPanel.BLOCKS_PER_ROW ][ SudokuPanel.BLOCKS_PER_ROW ];

		for ( int r = 0; r < SudokuPanel.BLOCKS_PER_ROW; r++ )
		{
			for ( int c = 0; c < SudokuPanel.BLOCKS_PER_ROW; c++ )
			{
				gbc.gridx = c;
				gbc.gridy = r;
				jpnl = new JPanel();
				jpnl.setLayout( gblo );
				jpnl.setBorder( RAISED_BRDR );
				pnlSudoku.blocks[ r ][ c ] = jpnl;
				pnlSudoku.pnlBoard.add( jpnl, gbc );
			}
		}

		pnlSudoku.puzzle =
			new int[ SudokuPanel.BOARD_SIZE ][ SudokuPanel.BOARD_SIZE ];

		pnlSudoku.board =
			new Square[ SudokuPanel.BOARD_SIZE ][ SudokuPanel.BOARD_SIZE ];

		for ( int r = 0; r < SudokuPanel.BOARD_SIZE; r++ )
		{
			for ( int c = 0; c < SudokuPanel.BOARD_SIZE; c++ )
			{
				blRow = r / SudokuPanel.BLOCKS_PER_ROW;
				blCol = c / SudokuPanel.BLOCKS_PER_ROW;

				ndx = SudokuPanel.uniqNdx( blRow, blCol, SudokuPanel.BLOCKS_PER_ROW );
				sclr = ndx % 2 == 0 ? SquareView.BASE_CLR : SquareView.ALT_CLR;
				sv = new SquareView( pnlSudoku, sclr );

				pnlSudoku.board[ r ][ c ] = new Square( r, c, sv );
				sv.setSquare( pnlSudoku.board[ r ][ c ] );

				gbc.gridx = c % SudokuPanel.BLOCKS_PER_ROW;
				gbc.gridy = r % SudokuPanel.BLOCKS_PER_ROW;
				pnlSudoku.blocks[ blRow ][ blCol ].add( sv, gbc );
			}
		}
	}

	private void
	mkCtrlsPnl()
	{
		GridBagConstraints	gbc = new GridBagConstraints();

		gbc.gridx = gbc.gridy = 0;
		gbc.insets = INSETS;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		pnlSudoku.pnlCtrls.setBackground( BOARD_CLR );
		
		gbc.weightx = 0.33;

		pnlSudoku.jbNewGame = new JButton( "New Game" );
		pnlSudoku.jbNewGame.setMnemonic( KeyEvent.VK_N );
		pnlSudoku.jbNewGame.addActionListener( pnlSudoku );
		pnlSudoku.pnlCtrls.add( pnlSudoku.jbNewGame, gbc );

		gbc.gridx = 1;
		pnlSudoku.cmbGivenDigits = new JComboBox( pnlSudoku.ALLOWED_GIVEN_DIGITS );
		pnlSudoku.cmbGivenDigits.setSelectedIndex( 0 );
		pnlSudoku.pnlCtrls.add( pnlSudoku.cmbGivenDigits, gbc );

		gbc.gridx = 2;
		pnlSudoku.lblGivenDigits = new JLabel( "given digits" );
		pnlSudoku.pnlCtrls.add( pnlSudoku.lblGivenDigits, gbc );

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnlSudoku.jbSolve = new JButton( "Solve" );
		pnlSudoku.jbSolve.setMnemonic( KeyEvent.VK_S );
		pnlSudoku.jbSolve.addActionListener( pnlSudoku );
		pnlSudoku.pnlCtrls.add( pnlSudoku.jbSolve, gbc );

		gbc.gridx = 1;
		pnlSudoku.jbExport = new JButton( "Export" );
		pnlSudoku.jbExport.setMnemonic( KeyEvent.VK_X );
		pnlSudoku.jbExport.addActionListener( pnlSudoku );
		pnlSudoku.pnlCtrls.add( pnlSudoku.jbExport, gbc );

		gbc.gridx = 2;
		pnlSudoku.jbImport = new JButton( "Import" );
		pnlSudoku.jbImport.setMnemonic( KeyEvent.VK_I );
		pnlSudoku.jbImport.addActionListener( pnlSudoku );
		pnlSudoku.pnlCtrls.add( pnlSudoku.jbImport, gbc );

		gbc.gridy = 2;
		gbc.gridx = 0;
		pnlSudoku.chkbUseTracker = new JCheckBox( "Use Tracker" );
		pnlSudoku.chkbUseTracker.setSelected( false );
		pnlSudoku.chkbUseTracker.setMnemonic( KeyEvent.VK_T );
		pnlSudoku.chkbUseTracker.setBackground( BOARD_CLR );
		pnlSudoku.chkbUseTracker.addActionListener( pnlSudoku );
		pnlSudoku.pnlCtrls.add( pnlSudoku.chkbUseTracker, gbc );

		gbc.weightx = 1.0;
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		pnlSudoku.lblGameState = new JLabel( "Game: on" );
		pnlSudoku.pnlCtrls.add( pnlSudoku.lblGameState, gbc );
	}

	SudokuPanel				pnlSudoku;

	static final Insets			INSETS = new Insets( 5, 5, 5, 5 );

	static final BevelBorder		RAISED_BRDR = new BevelBorder( BevelBorder.RAISED );

	static final Color			CLRGRAY = Color.GRAY;
	static final Color			CLRLGRAY = Color.LIGHT_GRAY;
	static final Color			BOARD_CLR = new Color( 197, 213, 203 );
}
