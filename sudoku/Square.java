package sudoku;

/*
  By Roman Andronov
 */

class Square
{
	Square( int row, int col, SquareView sv )
	{
		myRow = row;
		myColumn = col;
		myValue = 0;
		mySquareView = sv;
	}

	int
	getRow()
	{
		return myRow;
	}

	int
	getColumn()
	{
		return myColumn;
	}

	SquareView
	getView()
	{
		return mySquareView;
	}

	int
	getValue()
	{
		return myValue;
	}

	void
	setValue( int v )
	{
		myValue = v;
		mySquareView.setValue( v );
	}

	void
	clear()
	{
		myValue = 0;
		mySquareView.clear();
	}

	private final int		myRow;
	private final int		myColumn;
	private final SquareView	mySquareView;
	private int			myValue; // Varies
}
