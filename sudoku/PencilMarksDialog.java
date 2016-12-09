package sudoku;

/*
  By Roman Andronov
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.JFrame;

class PencilMarksDialog
	extends JDialog
{
	PencilMarksDialog( JFrame pfrm )
	{
		super( pfrm, true );
		setUndecorated( true );
		setLayout( new GridBagLayout() );

		GBC.insets = INSETS;
		GBC.weightx = GBC.weighty = 1.0;
		GBC.fill = GridBagConstraints.BOTH;

		pmEd = new PencilMarksEditor( this );
		add( pmEd, GBC );
		pack();
	}

	String
	edit( SquareView srcSqV )
	{
		Insets		bi = srcSqV.getInsets();
		Point		loc = srcSqV.getLocationOnScreen();
		Dimension	sz = srcSqV.getSize();
		String		txt = srcSqV.getText();
		Color		bgclr = srcSqV.getBackground();

		getContentPane().setBackground( bgclr );

		pmEd.mySquareView = srcSqV;
		pmEd.setText( txt );
		pmEd.setBackground( bgclr );

		loc.x += bi.left;
		loc.y += bi.top;
		setLocation( loc );

		sz.width = sz.width - bi.left - bi.right;
		sz.height = sz.height - bi.top - bi.bottom;
		setSize( sz );

		show();
		pmEd.mySquareView = null;

		return pmEd.pmstr;
	}

	static final GridBagConstraints	GBC = new GridBagConstraints();

	static final Insets		INSETS = new Insets( 5, 5, 5, 5 );

	private PencilMarksEditor	pmEd = null;	
}
