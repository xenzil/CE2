import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TextBuddy {

	private static FileWriter fw;
	private static BufferedWriter mainWriter;
	private static BufferedReader mainReader;
	private static String fileName;
	private static File file;
	private static Scanner scanner;

	// list of messages
	private static final String WELCOME_MESSAGE = "Welcome to TextBuddy. %s is ready for use";
	private static final String CLEAR_FILE_MESSAGE = "all content deleted from %s";
	private static final String ADD_TEXT_MESSAGE = "added to %1$s: \"%2$s\"";
	private static final String DISPLAY_FILE_MESSAGE = "%1$s. %2$s";
	private static final String DISPLAY_EMPTY_MESSAGE = "%s is empty";
	private static final String DELETE_MESSAGE = "deleted from %1$s: \"%2$s\"";
	private static final String COMMAND_MESSAGE = "command: ";

	public static void main(String[] args) {
		scanner = new Scanner(System.in);
		ArrayList<String> feedback = new ArrayList<String>();
		try {
			feedback = openFile(args);
			showToUser(feedback);
			while (true) {
				System.out.print(COMMAND_MESSAGE);
				String command = scanner.next();
				feedback = executeCommand(command, feedback);
				showToUser(feedback);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param command
	 * @param feedback
	 * @param restOfLine
	 * @return
	 * @throws IOException
	 */
	private static ArrayList<String> executeCommand(String command, ArrayList<String> feedback)
			throws IOException {

		String restOfLine = scanner.nextLine();
		switch (command) {
		// delete file
		case "delete":
			feedback = deleteLine(restOfLine);
			break;

		// display file, say empty if empty
		case "display":
			feedback = displayFile();
			break;

		// clear file and print
		case "clear":
			feedback = clearFile();
			break;

		// add text and print
		case "add":
			feedback = addText(restOfLine);
			break;
		
		case "exit":
			System.exit(0);
		default: feedback.add("unknown command");
		}
		return feedback;
	}

	private static void showToUser(ArrayList<String> feedback) {
		while (!feedback.isEmpty()) {
			System.out.println(feedback.remove(0));
		}
	}

	// method to create file and writers and readers
	private static ArrayList<String> openFile(String[] args) throws IOException, FileNotFoundException {
		fileName = args[0];
		file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		fw = new FileWriter(file.getAbsoluteFile(), true);
		mainWriter = new BufferedWriter(fw);
		mainReader = new BufferedReader(new FileReader(file));

		ArrayList<String> feedback = new ArrayList<String>();
		feedback.add(String.format(WELCOME_MESSAGE, fileName));
		return feedback;
	}

	// method to clear file
	private static ArrayList<String> clearFile() throws IOException {
		mainWriter = new BufferedWriter(new FileWriter(file, false));
		mainWriter.flush();

		ArrayList<String> feedback = new ArrayList<String>();
		feedback.add(String.format(CLEAR_FILE_MESSAGE, fileName));
		return feedback;
	}

	// method to add text
	private static ArrayList<String> addText(String text) throws IOException {
		text = text.substring(1, text.length());
		mainWriter.write(text + System.getProperty("line.separator"));
		mainWriter.flush();

		ArrayList<String> feedback = new ArrayList<String>();
		feedback.add(String.format(ADD_TEXT_MESSAGE, fileName, text));
		return feedback;
	}

	// method to display text
	private static ArrayList<String> displayFile() throws IOException {
		int listIndex = 1;
		File tempFile = file;
		String line;
		String feedbackLine;
		ArrayList<String> feedback = new ArrayList<String>();

		BufferedReader tempReader = new BufferedReader(new FileReader(tempFile));
		while ((line = tempReader.readLine()) != null) {
			feedbackLine = String.format(DISPLAY_FILE_MESSAGE, listIndex, line);
			// lineRead = lineRead + System.getProperty("line.separator");
			feedback.add(feedbackLine);
			listIndex++;
		}
		tempReader.close();

		if (listIndex == 1) {
			feedbackLine = String.format(DISPLAY_EMPTY_MESSAGE, fileName);
			feedback.add(feedbackLine);
		}
		return feedback;
	}

	// method to delete line
	private static ArrayList<String> deleteLine(String indexString) throws IOException {
		mainReader = new BufferedReader(new FileReader(file));
		String currentLine;
		int listIndex = 1;
		indexString = indexString.trim();
		int index = 0;
		ArrayList<String> feedback = new ArrayList<String>();

		// catch if non-integer is used
		try {
			index = Integer.parseInt(indexString);
		} catch (NumberFormatException e) {
			feedback.add("invalid integer");
			return feedback;
		}

		ArrayList<String> remainingText = new ArrayList<String>();
		while ((currentLine = mainReader.readLine()) != null) {
			if (listIndex == index) {
				feedback.add(String.format(DELETE_MESSAGE, fileName, currentLine));
				listIndex++;
				continue;
			}
			listIndex++;
			remainingText.add(currentLine);
		}
		clearFile();

		for (String textLine : remainingText) {
			mainWriter.write(textLine + System.getProperty("line.separator"));
		}
		mainWriter.flush();
		return feedback;
	}
}
