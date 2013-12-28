package assign3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;

/* Class: SudokuFrame
 * ------------------
 * A class to enable users to interact with and solve Sudoku problems.
 */
public class SudokuFrame extends JFrame implements ActionListener {
	 
	 private JButton check;
	 private JCheckBox autoCheck;
	 JTextArea puzzle;
	 Document doc;
	 JTextArea solution;
	
	 /* Constructor: SudokuFrame
	  * ------------------------
	  * Create a GUI to play Sudoku.
	  */
	public SudokuFrame() {
		super("Sudoku Solver");	
		
		setLayout(new BorderLayout(4,4));
		
		check = new JButton("Check");
		check.addActionListener(this);
		autoCheck = new JCheckBox("Auto Check");
		autoCheck.setSelected(true);
		autoCheck.addActionListener(this);

		JPanel controlBox = new JPanel();
		controlBox.add(check);
		controlBox.add(autoCheck);
		add(controlBox, BorderLayout.SOUTH);
		
		// the Puzzle and Solution section should be side-by-side and prepped for document listening
		puzzle = new JTextArea(15, 20);
		puzzle.setBorder(new TitledBorder("Puzzle"));
		add(puzzle, BorderLayout.CENTER);
		doc = puzzle.getDocument();
		doc.addDocumentListener(new MyDocListener());
		
		solution = new JTextArea(15, 20);
		solution.setBorder(new TitledBorder("Solution"));
		add(solution, BorderLayout.EAST);
				
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	/* main
	 * ----
	 * Sets up the Sudoku frame for play.
	 */
	public static void main(String[] args) {
		// GUI Look And Feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		SudokuFrame frame = new SudokuFrame();
	}

	/* Method: displayGame
	 * -------------------
	 * Setup the Sudoku class based on the GUI text and solve the Sudoku
	 */
	private void displayGame() {
		Sudoku problem;

		problem = new Sudoku(puzzle.getText());

		int numSolns = problem.solve();
		String solnText = "";
		solnText += problem.getSolutionText();
		solnText += "solutions: " + numSolns + "\n";
		solnText += "elapsed: " + problem.getElapsed() + "ms";
		solution.setText(solnText);
	}
	
	/* Method: actionPerformed
	 * -----------------------
	 * Responds to action events on the GUI
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == check) {
			try {
				displayGame();
			} catch (Exception ignored) {
				solution.setText("Parsing problem");
			}
		}
	}
	
	/* Class: MyDocListener
	 * --------------------
	 * If any change is made to the puzzle while autocheck is checked, 'click' on check (solve
	 * the Sudoku).
	 */
	private class MyDocListener implements DocumentListener {		 
	    public void insertUpdate(DocumentEvent e) {
	    	if (autoCheck.isSelected()) check.doClick();
	    }
	    
	    public void removeUpdate(DocumentEvent e) {
	    	if (autoCheck.isSelected()) check.doClick();
	    }
	    
	    public void changedUpdate(DocumentEvent e) {
	    	if (autoCheck.isSelected()) check.doClick();
	    }

	}
}
