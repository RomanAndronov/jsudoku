MF = /tmp/sudokuManifest

SUDOKU = sudoku.jar
SRCDIR = sudoku

JFLAGS = -g
JAVAC = javac -cp ./$(SRCDIR):${CLASSPATH}

.SUFFIXES: .java .class
.java.class:
	$(JAVAC) $(JFLAGS) $<

_SUDOKU_SRC = SudokuFrame.java \
	SudokuPanel.java \
	SudokuGui.java \
	Square.java \
	SquareView.java \
	DlxSudokuMatrix.java \
	DlxMatrixEntry.java \
	DlxColumnHeader.java \
	SquareViewMouseHandler.java \
	SquareViewKeyHandler.java \
	SudokuFocusTraversalHandler.java \
	PencilMarksDialog.java \
	PencilMarksEditor.java

SUDOKU_SRC = $(_SUDOKU_SRC:%=$(SRCDIR)/%)

SUDOKU_CLASSES = $(SUDOKU_SRC:.java=.class)

$(SUDOKU):	$(SUDOKU_SRC) $(SUDOKU_CLASSES)
	rm -f $(MF)
	echo "Main-Class: $(SRCDIR)/SudokuFrame" > $(MF)
	jar cmf $(MF) $@ $(SRCDIR)/*.class
	rm -f $(MF)

clean:
	rm -f $(SUDOKU) $(SRCDIR)/*.class
