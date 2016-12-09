package sudoku;

/*
  By Roman Andronov
 */

class DlxColumnHeader
	extends DlxMatrixEntry
{
	DlxColumnHeader()
	{
		super( -1 ); // The row number for a column is irrelevant
		size = 0;
		setColumnHeader( this );
	}

	void
	rmAllEntries()
	{
		size = 0;
		setUp( this );
		setDown( this );
		setColumnHeader( this );
	}
	
	int
	getSize()
	{
		return size;
	}

	void
	addEntry( DlxMatrixEntry dme )
	{
		/*
		  The new entries are added to the end of the
		  list. Since it is a circular list, the column
		  header's up member references the last entry
		  in the list
		 */
		DlxMatrixEntry		last = getUp();


		/*
		  The new last entry's up member will
		  point to the previously last entry
		 */
		dme.setUp( last );


		/*
		  The down member of the last entry
		  always points to the column header
		 */
		dme.setDown( this );


		/*
		  Double link this entry by setting the
		  previous entry's down member
		 */
		last.setDown( dme );


		/*
		  Keep the circle closed
		 */
		setUp( dme );

		size++;
	}

	void
	linkEntryIn( DlxMatrixEntry dme )
	{
		/*
		  Link this entry in between up and down
		 */
		dme.getUp().setDown( dme );
		dme.getDown().setUp( dme );
		size++;
	}
	
	void
	linkEntryOut( DlxMatrixEntry dme )
	{
		/*
		  Link around this entry: an entry on top
		  should point down to what this entry points
		  down.
		  An entry below should point up to what this
		  entry points up
		 */
		dme.getUp().setDown( dme.getDown() );
		dme.getDown().setUp( dme.getUp() );
		size--;
	}
	
	/*
	  Removing a column is "covering" it in D. Knuth lingo:

	 	1) Remove the column header from all headers list.

	 	2) For each row in this column:

	 		2.1) Move right once

	 		2.2) If this is not the row entry we started with,
	 			remove this entry from it's column

	 		2.3) Move right once, go to 2.2

	  Note that the row entry we start with serves
	  as a loop termination marker: this row entry
	  is never removed from the column we are removing.
	  However, the column header is removed from the list
	  and will not be selected during the next iteration
	  through the matrix
	 */
	void
	remove()
	{
		/*
		  Unlink this column from the headers list
		 */
		getLeft().setRight( getRight() );
		getRight().setLeft( getLeft() );


		/*
		  As long as this column has entries, move downward ...
		 */
		DlxMatrixEntry		curr = null;
		DlxMatrixEntry		currEntry = getDown();

		while ( currEntry != this )
		{
			/*
			  Move through this row rightward
			 */
			curr = currEntry.getRight();
			while ( curr != currEntry )
			{
				/*
				  Remove this entry from it's column
				 */
				curr.getColumnHeader().linkEntryOut( curr );
				
				/*
				  Move through all the entries
				  in this row rightward
				 */
				curr = curr.getRight();
			}
			
			/*
			  Move through all the rows downward
			 */
			currEntry = currEntry.getDown();
		}
	}
	
	/*
	  Add column back to the dlx matrix:
	  "uncover" it in D. Knuth lingo.
	  Do it in exactly the reverse order of remove()
	 */
	void
	add()
	{
		/*
		  As long as this column has
		  entries, move upward...
		 */
		DlxMatrixEntry		curr;
		DlxMatrixEntry		currEntry = getUp();

		while ( currEntry != this )
		{
			/*
			  Move through all the entries
			  in the row leftward
			 */
			curr = currEntry.getLeft();
			while ( curr != currEntry )
			{
				/*
				  Add it back to this column
				 */
				curr.getColumnHeader().linkEntryIn( curr );
				
				/*
				  Move leftward
				 */
				curr = curr.getLeft();
			}

			/*
			  Move through all the rows upward
			 */
			currEntry = currEntry.getUp();
		}
		
		/*
		  Link the header back in
		 */
		getLeft().setRight( this );
		getRight().setLeft( this );
	}

	/*
	  The number of ones in this column,
	  which is a number of entries in the list
	 */
	private int			size;
}
