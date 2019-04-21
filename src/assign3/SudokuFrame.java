package assign3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SudokuFrame extends JFrame {
	private JTextArea puzzleArea, solutionArea;
	private JButton check;
	private JCheckBox box;
	private Container container;
	private static final int TEXT_AREA_WIDTH = 15;
	private static final int TEXT_AREA_HEIGHT= 15;

	public SudokuFrame() {
		super("Sudoku Solver");
		setLayout(new BorderLayout());

		puzzleArea = new JTextArea(TEXT_AREA_WIDTH, TEXT_AREA_HEIGHT);
		solutionArea = new JTextArea(TEXT_AREA_WIDTH, TEXT_AREA_HEIGHT);
		check = new JButton("Check");
		box = new JCheckBox("Auto Check");
		container = new Container();

		container.setLayout(new FlowLayout());

		container.add(check);
		container.add(box);

		check.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				solveSudoku();
			}
		});

		add(container, BorderLayout.SOUTH);
		add(puzzleArea, BorderLayout.CENTER);
		add(solutionArea, BorderLayout.EAST);

		puzzleArea.setBorder(new TitledBorder("Puzzle"));

		Document doc = puzzleArea.getDocument();
		doc.addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (box.isSelected()){
					solveSudoku();
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (box.isSelected()){
					solveSudoku();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (box.isSelected()){
					solveSudoku();
				}
			}
		});

		solutionArea.setBorder(new TitledBorder("Solution"));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * This method solves the puzzle entered by user
	 * */
	 private void solveSudoku() {
		try{
			String s = puzzleArea.getText();
			Sudoku sudoku = new Sudoku(s);
			int count = sudoku.solve();
			String ansString = sudoku.getSolutionText() + "\nElapsed: " + sudoku.getElapsed() + "\nSolutions: " + count + "\n";
			solutionArea.setText(ansString);
		} catch (Exception exp){
			solutionArea.setText("The puzzle is wrong write it correctly");
		}
	 }

	 public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }

		 SudokuFrame frame = new SudokuFrame();
	}

}
