package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Main extends Application {

	// countSections variable stores the total number of sections read from the file
	private int countSections = 0;

	// sectionNum variable, is used to store the number of the section shown on the
	// screen
	private int sectionNum = 0;

	// this cursor array object is used to create any list for any stack in the
	// program
	private Cursor<Comparable> cursor = new Cursor<>(200);

	// buttons, buttons font, text area and labels
	private Button btnLoad = new Button("Load");
	private Button btnPrev = new Button("Previous");
	private Button btnNext = new Button("Next");
	private Font buttonFont = Font.font("Times New Romans", 13);
	private Label lblPath = new Label("No file has been selected");
	private TextArea equationArea = new TextArea();

	// this file variable will reference the file object to be read later by the
	// program
	private File file;

	// this array will store the equations that will be read form the file, each
	// array
	// index references a group of equations affiliated with a certain section
	private String[] equations;

	@Override
	public void start(Stage primaryStage) {
		try {
			// set the root pane
			BorderPane root = primary();

			// set primary scene
			Scene scene = new Scene(root, 600, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			// set the scene in the stage
			primaryStage.setScene(scene);

			// show the stage
			primaryStage.show();

			// set the font of all buttons as buttonFont
			btnLoad.setFont(buttonFont);
			btnPrev.setFont(buttonFont);
			btnNext.setFont(buttonFont);

			// set preferred sizes for all buttons
			btnLoad.setPrefSize(70, 10);
			btnPrev.setPrefSize(80, 10);
			btnNext.setPrefSize(80, 10);

			// create a file chooser object
			FileChooser chooser = new FileChooser();

			// set on action
			btnLoad.setOnAction(e -> {
				countSections = 0;
				file = chooser.showOpenDialog(primaryStage);
				if (file != null) {
					try {
						sectionNum = 0;
						lblPath.setText(file.getAbsolutePath());
						btnPrev.setDisable(true);
						String tags = extractTags();
						if (checkBalance(tags)) {
//							System.out.println("valid");
							equations = new String[countSections];
							if (countSections > 0) {
								sectionNum = 1;
								extractEquations();
								equationArea.setText(equations[sectionNum - 1]);
							}
						} else {
//							System.out.println("Invalid");
							throw new Exception();
						}
						// if number of sections is greater than 1, then enable next button
						if (countSections > 1) {
							btnNext.setDisable(false);
						} else {
							btnNext.setDisable(true);
						}
					} catch (Exception e1) {
						countSections = 0;
						e1.printStackTrace();
						equationArea.setText("Invalid File");
					}

				}
			});

			// when next button is clicked, enable previous button
			btnNext.setOnAction(e -> {
				btnPrev.setDisable(false);
				sectionNum += 1;
				equationArea.setText(equations[sectionNum - 1]);
				if (sectionNum == countSections)
					btnNext.setDisable(true);
			});

			// when previous button is clicked, enable next button 
			btnPrev.setOnAction(e -> {
				btnNext.setDisable(false);
				sectionNum--;
				equationArea.setText(equations[sectionNum - 1]);
				if (sectionNum == 1)
					btnPrev.setDisable(true);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	private String extractTags() {
		StringBuilder b = new StringBuilder();
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNext()) {
				String line = sc.nextLine();
				if (line.contains("<242>"))
					b.append("<242>" + " ");

				if (line.contains("</242>"))
					b.append("</242>" + " ");

				if (line.contains("<section>"))
					b.append("<section>" + " ");

				// with end of each section, increment the conutSections variable
				if (line.contains("</section>")) {
					b.append("</section>" + " ");
					countSections++;
				}
				if (line.contains("<infix>"))
					b.append("<infix>" + " ");

				if (line.contains("</infix>"))
					b.append("</infix>" + " ");

				if (line.contains("<postfix>"))
					b.append("<postfix>" + " ");

				if (line.contains("</postfix>"))
					b.append("</postfix>" + " ");

				if (line.contains("<equation>"))
					b.append("<equation>" + " ");

				if (line.contains("</equation>"))
					b.append("</equation>" + " ");

			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return b.toString();
	}

	// check whether the tags extracted form the file are balanced or not
	private boolean checkBalance(String exp) {
		boolean isBalanced = true;
		CStack<String> stack = new CStack<>();
		String[] tags = exp.split(" ");

		int i = 0;
		while (isBalanced == true && i < tags.length) {
			String next = tags[i];
			switch (next) {
			// for any open tag, push into the stack
			case "<242>":
			case "<section>":
			case "<infix>":
			case "<postfix>":
			case "<equation>":
				stack.push(next);
				break;
			case "</242>":
			case "</section>":
			case "</infix>":
			case "</postfix>":
			case "</equation>":
				// if a closed tag is read and the open_tags' stack is empty, then the
				// expression in unbalanced
				if (stack.isEmpty()) {
					return false;
				} else {
					// if the open tag is not empty, pop the top tag from the stack and check
					// whether it forms a pair with the closed one, if they don't, then the
					// expression is unbalanced
					String startTag = stack.pop();
					if (!isPair(startTag, next)) {
						return false;
					}
				}
				break;
			}
			i++;
		}
		// if the stack has some remaining open tags after end of expression, then these
		// open tags do not have accompanying end tags, therefore, this expression is
		// considered to be unbalanced
		if (!stack.isEmpty())
			isBalanced = false;

		return isBalanced;
	}

	// this method checks whether two given tags represent a pair or not
	private boolean isPair(String startTag, String endTag) {
		if (startTag.equals("<242>") && endTag.equals("</242>")
				|| startTag.equals("<section>") && endTag.equals("</section>")
				|| startTag.equals("<infix>") && endTag.equals("</infix>")
				|| startTag.equals("<postfix>") && endTag.equals("</postfix>")
				|| startTag.equals("<equation>") && endTag.equals("</equation>"))
			return true;
		return false;
	}

	// this method takes an infex expression and converts it into a postfix
	// expression
	private String infexToPostfix(String infex) {
		infex = infex.trim();
		StringBuilder postfix = new StringBuilder("");
		CStack<Character> operators = new CStack<>();
		String[] next = infex.split(" ");
		for (int i = 0; i < next.length; i++) {

			// always push the open parenthesis into the operators stack
			if (next[i].equals("("))
				operators.push(next[i].charAt(0));

			// if the character is a closed parenthesis, keep popping the operators from the
			// stack and appending them to the postfix expression until an open parenthesis
			// is popped
			else if (next[i].equals(")")) {
				while (operators.peek() != '(')
					postfix.append(operators.pop() + " ");
				operators.pop();

			} else if (next[i].equals("+") || next[i].equals("-") || next[i].equals("/") || next[i].equals("*")
					|| next[i].equals("^")) {

				// if the operators stack is empty, then push the operator into the stack
				if (operators.isEmpty())
					operators.push(next[i].charAt(0));

				// if the stack is not empty, then pop all the operators that have the
				// precedence over this operator and append them into the postfix expression,
				// then push this operator into the stack
				else {
					while (operators.peek() != null && !hasPrecedence(next[i].charAt(0), operators.peek()))
						postfix.append(operators.pop() + " ");
					operators.push(next[i].charAt(0));
				}

				// if next is not an operator, then it has to be an operand
			} else {
				// try to parse next into a double value, if that succeeded, append it to the
				// postfix expression, otherwise, ignore it
				try {
					double value = Double.parseDouble(new StringBuilder("").append(next[i]).toString());
					postfix.append(value + " ");
				} catch (ClassCastException e) {
					continue;
				}
			}
		}
		// append the remaining operators in the stack into the postfix expression
		while (!operators.isEmpty()) {
			postfix.append(operators.pop() + " ");
		}

		return postfix.toString();
	}

	// this method takes two operators and check whether the first has a precedence
	// over the second or not
	private boolean hasPrecedence(char op2, char op1) {
		if (op1 == '^')
			return false;
		else if ((op1 == '/' || op1 == '*') && (op2 == '+' || op2 == '-'))
			return false;
		else if ((op1 == '/' && op2 == '*') || (op1 == '+' && op2 == '-') || (op1 == '*' && op2 == '/')
				|| (op1 == '-' && op2 == '+'))
			return false;
		else
			return true;
	}

	// this method takes a valid postfix expression as an argument and returns its
	// evaluation
	private double postfixEvaluation(String postfix) {
		CStack<Double> values = new CStack<>();
		String[] next = postfix.split(" ");
		for (int i = 0; i < next.length; i++) {
			// if next is an operator, then pop the top two operands from the operands stack
			// and apply the corresponding operation on them and push the result into the
			// stack
			if (next[i].equals("+") || next[i].equals("-") || next[i].equals("/") || next[i].equals("*")
					|| next[i].equals("^")) {
				double v2 = values.pop();
				double v1 = values.pop();
				double result = applyOperation(v1, v2, next[i].charAt(0));
				values.push(result);
			} else
				// if next is not an operator, then it must be an operand
				try {
					// try to parse next into a double value, if that succeeds, then it is an
					// operand, so push into the stack, if not, just ignore it
					double value = Double.parseDouble(new StringBuilder("").append(next[i]).toString());
					values.push(value);
				} catch (ClassCastException e) {
					continue;
				}
		}
		return values.pop();
	}

	// this method takes a postfix expression and converts it into prefix
	private String postfixToPrefix(String postfix) {
		CStack<String> stack = new CStack<>();
		String[] symbols = postfix.split(" ");
		for (String symbol : symbols) {
			if (symbol.equals("+") || symbol.equals("-") || symbol.equals("*") || symbol.equals("/")
					|| symbol.equals("^")) {
				try {
					String value2 = stack.pop();
					String value1 = stack.pop();
					String entry = symbol + " " + value1 + " " + value2;
					stack.push(entry);
				} catch (NullPointerException n) {
					System.out.println("ERROR!");
					return null;
				}
			} else {
				// if the symbol is not an operator, then it must be an operand
				try {
					// try to parse symbol into double and push it into the stack if the process
					// succeeded, if not, just ignore it
					Double.parseDouble(symbol);
					stack.push(symbol);
				} catch (ClassCastException e) {
					continue;
				}
			}
		}
		return stack.pop();
	}

	// this method takes a prefix expression and evaluates it
	private double Prefixevaluation(String prefix) {
		CStack<Double> stack = new CStack<>();
		String[] symbols = prefix.split(" ");
		for (int i = symbols.length - 1; i >= 0; i--) {
			String symbol = symbols[i];
			if (symbol.equals("+") || symbol.equals("-") || symbol.equals("*") || symbol.equals("/")
					|| symbol.equals("^")) {
				double value1 = stack.pop();
				double value2 = stack.pop();
				double result = applyOperation(value1, value2, symbol.charAt(0));
				stack.push(result);
			} else {
				try {
					double value = Double.parseDouble(symbol);
					stack.push(value);
				} catch (ClassCastException e) {
					continue;
				}
			}
		}
		return stack.pop();
	}

	// this method takes two operands and one operator as arguments and apply the
	// operation on them, then returns the result
	private double applyOperation(double v1, double v2, char op) {
		double result = 0;
		switch (op) {
		case '+':
			result = v1 + v2;
			break;
		case '-':
			result = v1 - v2;
			break;
		case '/':
			result = v1 / v2;
			break;
		case '*':
			result = v1 * v2;
			break;
		case '^':
			result = Math.pow(v1, v2);
			break;
		}
		return result;
	}

	// this method returns the primary parent to be shown
	private BorderPane primary() {
		// make the root pane
		BorderPane pane = new BorderPane();
		pane.setStyle("-fx-background-color: white;");

		// put the selection view at Top
		Label lblFile = new Label("File: ");
		lblFile.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));
		lblPath.setFont(buttonFont);
		lblPath.setTextFill(Color.RED);
		HBox fileBox = new HBox(10);
		fileBox.getChildren().addAll(lblFile, lblPath, btnLoad);
		fileBox.setAlignment(Pos.CENTER);
		fileBox.setPadding(new Insets(10));
		pane.setTop(fileBox);

		// put the TextArea holding the data in the center
		pane.setCenter(equationArea);
		equationArea.setEditable(false);
		equationArea.setStyle("-fx-background-color: White; -fx-font-size: 13;");

		// put the previous and next buttons at bottom
		HBox buttonsBox = new HBox(5);
		buttonsBox.getChildren().addAll(btnPrev, btnNext);
		buttonsBox.setAlignment(Pos.CENTER);
		buttonsBox.setPadding(new Insets(10));
		pane.setBottom(buttonsBox);

		// disable all buttons except btnLoad
		btnPrev.setDisable(true);
		btnNext.setDisable(true);

		pane.setPadding(new Insets(20));

		return pane;
	}

	// this method is used to extract the equations of each section from the file
	// and store them in some index of the equations array
	private void extractEquations() {
		if (equations != null) {

			// initialize the array, i.e, fill each index with an empty string
			for (int i = 0; i < equations.length; i++) {
				equations[i] = "";
			}
			try {
				// scan the file to extract the equations
				Scanner sc = new Scanner(file);
				int i = -1;
				String exp = "";
				while (sc.hasNext()) {
					String line = sc.nextLine();
					line.trim();
					if (line.contains("<section>"))
						i++;
					else if (line.contains("<infix>")) {
						equations[i] = equations[i].concat("\nInfex:\n");
						exp = "infex";
					} else if (line.contains("<postfix>")) {
						equations[i] = equations[i].concat("\nPostfix:\n");
						exp = "postfix";
					} else if (line.contains("<equation>")) {
						String eq = line.split("[<>]")[2].trim();
						equations[i] = equations[i].concat("\t" + eq);
						if (exp.equals("infex")) {
							String postfix = infexToPostfix(eq);
							equations[i] = equations[i]
									.concat(" ==> " + postfix + " ==> " + postfixEvaluation(postfix) + "\n");
						} else if (exp.equals("postfix")) {
							String prefix = postfixToPrefix(eq);
							equations[i] = equations[i]
									.concat(" ==> " + prefix + " ==> " + Prefixevaluation(prefix) + "\n");
						}
					}
				}
				sc.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
