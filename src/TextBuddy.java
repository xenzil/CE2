import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TextBuddy {

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
	private static final String SORTED_MESSAGE = "List has been sorted alphabetically";
	private static final String SEARCH_MESSAGE = "Search Results";

	public static void main(String[] args) {
		scanner = new Scanner(System.in);
		ArrayList<String> feedback = new ArrayList<String>();
		try {
			feedback = openFile(args[0]);
			showToUser(feedback);
			
			while (true) {
				System.out.print(COMMAND_MESSAGE);
				String command = scanner.next();
				String restOfLine = scanner.nextLine();
				feedback = executeCommand(command, restOfLine);
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
	public static ArrayList<String> executeCommand(String command, String restOfLine)
			throws IOException {
		ArrayList<String> feedback = new ArrayList<String>();
		
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
		
		//sorts text file alphabetically
		case "sort":
			feedback = sortFile();
			break;
		
		//search text file for keyword
		case "search":
			feedback = searchWord(restOfLine);
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
	public static ArrayList<String> openFile(String givenName) throws IOException, FileNotFoundException {
		fileName = givenName;
		
		file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}

		ArrayList<String> feedback = new ArrayList<String>();
		feedback.add(String.format(WELCOME_MESSAGE, fileName));
		return feedback;
	}
	
	//method to sort file
	public static ArrayList<String> sortFile() throws IOException{
		ArrayList<String> feedback = new ArrayList<String>();
		
		File tempFile = file;
		BufferedReader tempReader = new BufferedReader(new FileReader(tempFile));
		String line;
		ArrayList<String> sortedList = new ArrayList<String>();
		while ((line = tempReader.readLine()) != null) {
			sortedList.add(line);
		}
		Collections.sort(sortedList);
		
		BufferedWriter mainWriter = new BufferedWriter(new FileWriter(file,false));
		for (String textLine : sortedList){
			mainWriter.write(textLine + System.getProperty("line.separator"));
		}
		tempReader.close();
		mainWriter.flush();
		feedback.add(SORTED_MESSAGE);
		
		return feedback;
	}
	
	//method to search word
	public static ArrayList<String> searchWord(String keyword) throws IOException{
		ArrayList<String> feedback = new ArrayList<String>();
		File tempFile = file;
		String line;
		String tempLine;
		keyword = keyword.trim();

		BufferedReader tempReader = new BufferedReader(new FileReader(tempFile));
		while ((line = tempReader.readLine()) != null) {
			tempLine = line;

			String[] words = tempLine.split(" ");

			for	(int i=0; i < words.length; i++){

				//System.out.println(words[i]);
				
				if(keyword.equals(words[i])){
					feedback.add(line);
					break;
				}
			}
			
		}
		tempReader.close();
		feedback.add(0,SEARCH_MESSAGE);
		
		return feedback;
	}
	
	// method to clear file
	public static ArrayList<String> clearFile() throws IOException {
		BufferedWriter mainWriter = new BufferedWriter(new FileWriter(file, false));

		ArrayList<String> feedback = new ArrayList<String>();
		feedback.add(String.format(CLEAR_FILE_MESSAGE, fileName));
		mainWriter.flush();
		return feedback;
	}

	// method to add text
	private static ArrayList<String> addText(String text) throws IOException {
		text = text.substring(1, text.length());
		BufferedWriter mainWriter = new BufferedWriter(new FileWriter(file, true));
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
		BufferedReader mainReader = new BufferedReader(new FileReader(file));
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

		BufferedWriter mainWriter = new BufferedWriter(new FileWriter(file, false));
		for (String textLine : remainingText) {
			mainWriter.write(textLine + System.getProperty("line.separator"));
		}
		mainWriter.flush();
		return feedback;
	}
}
