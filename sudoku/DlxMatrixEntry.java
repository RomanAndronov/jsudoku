package sudoku;

/*
  By Roman Andronov

  This class represents a 1 in the solution matrix, as
  described by Donald Knuth in his Dancing Links article.
  We call each 1 a matrix entry. It has four links:

	left
	right
	up
	down

  to organize the solution matrix as a collection of
  circular doubly-linked lists of ones. Each row and each
  column is represented by one circular doubly-linked list.
 
  Each entry in the list has a reference to the column
  header object. A column header object is an exact
  copy of a matrix entry object plus some extra fields.
  It, for example, stores the number of entries, the number
  of ones, in this column. This is done to speed up the
  operation of the algorithm
 */

class DlxMatrixEntry
{
	DlxMatrixEntry( int rn )
	{
		rowNumber = rn;
		referToSelf();
	}

	protected void
	referToSelf()
	{
		left = this;
		right = this;
		up = this;
		down = this;
		columnHeader = null;
	}

	DlxMatrixEntry
	getLeft()
	{
		return left;
	}

	DlxMatrixEntry
	getRight()
	{
		return right;
	}

	DlxMatrixEntry
	getUp()
	{
		return up;
	}

	DlxMatrixEntry
	getDown()
	{
		return down;
	}

	DlxColumnHeader
	getColumnHeader()
	{
		return columnHeader;
	}
	
	int
	getRowNumber()
	{
		return rowNumber;
	}

	DlxMatrixEntry
	setLeft( DlxMatrixEntry nl )
	{
		DlxMatrixEntry			pl = left;
		left = nl;
		return pl;
	}

	DlxMatrixEntry
	setRight( DlxMatrixEntry nr )
	{
		DlxMatrixEntry			pr = right;
		right = nr;
		return pr;
	}

	DlxMatrixEntry
	setUp( DlxMatrixEntry nu )
	{
		DlxMatrixEntry			pu = up;
		up = nu;
		return pu;
	}

	DlxMatrixEntry
	setDown( DlxMatrixEntry nd )
	{
		DlxMatrixEntry			pd = down;
		down = nd;
		return pd;
	}

	DlxColumnHeader
	setColumnHeader( DlxColumnHeader nch )
	{
		DlxColumnHeader			pch = columnHeader;
		columnHeader = nch;
		return pch;
	}


	private int				rowNumber = -1;
	private DlxMatrixEntry			left = null;
	private DlxMatrixEntry			right = null;
	private DlxMatrixEntry			up = null;
	private DlxMatrixEntry			down = null;
	private DlxColumnHeader			columnHeader = null;
}
