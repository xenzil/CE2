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

	private static final String WELCOME_MESSAGE = "Welcome to TextBuddy. %s is ready for use";
	private static final String CLEAR_FILE_MESSAGE = "all content deleted from %s";
	private static final String ADD_TEXT_MESSAGE = "added to %1$s: \"%2$s\"";
	private static final String DISPLAY_FILE_MESSAGE = "%1$s. %2$s";
	private static final String DISPLAY_EMPTY_MESSAGE = "%s is empty";
	private static final String DELETE_MESSAGE = "deleted from %1$s: \"%2$s\"";
	private static final String COMMAND_MESSAGE = "command: ";
	private static final String SORTED_MESSAGE = "List has been sorted alphabetically";
	private static final String SEARCH_MESSAGE = "Search Results";
	private static final String INVALID_INTEGER_MESSAGE = "invalid integer";
	private static final String UNKNOWN_COMMAND_MESSAGE = "unknown command";
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_SEARCH = "search";
	private static final String COMMAND_SORT = "sort";
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_DELETE = "delete";
	private static String commandFirstWord;
	private static String commandRestOfLine;

	public static void main(String[] args) {
		scanner = new Scanner(System.in);
		ArrayList<String> feedback = new ArrayList<String>();
		try {
			feedback = openFile(args[0]);
			showToUser(feedback);		
			while (true) {
				printCommandMessage();
				readCommand();
				feedback = executeCommand(commandFirstWord, commandRestOfLine);
				showToUser(feedback);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void printCommandMessage() {
		System.out.print(COMMAND_MESSAGE);
	}

	private static void readCommand() {
		String command = scanner.nextLine();
		commandFirstWord = command.substring(0, command.indexOf(" "));
		commandRestOfLine = command.substring(command.indexOf(" "),command.length());
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
		case COMMAND_DELETE:
			feedback = deleteLine(restOfLine);
			break;

		// display file, say empty if empty
		case COMMAND_DISPLAY:
			feedback = displayFile();
			break;

		// clear file and print
		case COMMAND_CLEAR:
			feedback = clearFile();
			break;

		// add text and print
		case COMMAND_ADD:
			feedback = addText(restOfLine);
			break;
		
		//sorts text file alphabetically
		case COMMAND_SORT:
			feedback = sortFile();
			break;
		
		//search text file for keyword
		case COMMAND_SEARCH:
			feedback = searchWord(restOfLine);
			break;
			
		case COMMAND_EXIT:
			System.exit(0);
		default: feedback.add(UNKNOWN_COMMAND_MESSAGE);
		}
		return feedback;
	}

	private static void showToUser(ArrayList<String> feedbackLines) {
		while (!feedbackLines.isEmpty()) {
			System.out.println(feedbackLines.remove(0));
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
		
		ArrayList<String> sortedList = new ArrayList<String>();
		BufferedReader tempReader = readFileIntoArrayList(sortedList);	
		sortArrayList(sortedList);		
		rewriteSortedList(sortedList, tempReader);
		
		feedback.add(SORTED_MESSAGE);		
		return feedback;
	}

	private static void rewriteSortedList(ArrayList<String> sortedList, BufferedReader tempReader)
			throws IOException {
		BufferedWriter mainWriter = new BufferedWriter(new FileWriter(file,false));
		for (String textLine : sortedList){
			mainWriter.write(textLine + System.getProperty("line.separator"));
		}
		tempReader.close();
		mainWriter.flush();
	}

	private static void sortArrayList(ArrayList<String> sortedList) {
		Collections.sort(sortedList);
	}

	private static BufferedReader readFileIntoArrayList(ArrayList<String> sortedList)
			throws FileNotFoundException, IOException {
		BufferedReader tempReader = readFile();
		String line;
		
		while ((line = tempReader.readLine()) != null) {
			sortedList.add(line);
		}
		return tempReader;
	}
	
	public static ArrayList<String> searchWord(String keyword) throws IOException{
		ArrayList<String> feedback = new ArrayList<String>();
		BufferedReader tempReader = readFile();
		
		addSearchedLinesToFeedback(keyword, feedback, tempReader);
		feedback.add(0,SEARCH_MESSAGE);
		
		return feedback;
	}

	private static void addSearchedLinesToFeedback(String keyword, ArrayList<String> feedback,
			BufferedReader tempReader) throws IOException {
		keyword = keyword.trim();
		String line;
		String tempLine;
		while ((line = tempReader.readLine()) != null) {
			tempLine = line;
			String[] words = tempLine.split(" ");
			for	(int i=0; i < words.length; i++){				
				if(keyword.equals(words[i])){
					feedback.add(line);
					break;
				}
			}	
		}
		tempReader.close();
	}

	private static BufferedReader readFile() throws FileNotFoundException {
		File tempFile = file;
		BufferedReader tempReader = new BufferedReader(new FileReader(tempFile));
		return tempReader;
	}
	
	public static ArrayList<String> clearFile() throws IOException {
		BufferedWriter mainWriter = new BufferedWriter(new FileWriter(file, false));

		ArrayList<String> feedback = new ArrayList<String>();
		feedback.add(String.format(CLEAR_FILE_MESSAGE, fileName));
		mainWriter.flush();
		return feedback;
	}

	private static ArrayList<String> addText(String text) throws IOException {
		ArrayList<String> feedback = new ArrayList<String>();
		text = text.substring(1, text.length());
		BufferedWriter mainWriter = new BufferedWriter(new FileWriter(file, true));
		mainWriter.write(text + System.getProperty("line.separator"));
		mainWriter.flush();
		feedback.add(String.format(ADD_TEXT_MESSAGE, fileName, text));
		
		return feedback;
	}

	private static ArrayList<String> displayFile() throws IOException {
		ArrayList<String> feedback = new ArrayList<String>();	
		
		int listIndex = readTextFile(feedback);		
		checkEmptyFile(listIndex, feedback);
		
		return feedback;
	}

	private static int readTextFile(ArrayList<String> feedback) throws FileNotFoundException, IOException {
		int listIndex = 1;
		File tempFile = file;
		String line;
		String fileContent;

		BufferedReader tempReader = new BufferedReader(new FileReader(tempFile));
		while ((line = tempReader.readLine()) != null) {
			fileContent = String.format(DISPLAY_FILE_MESSAGE, listIndex, line);
			feedback.add(fileContent);
			listIndex++;
		}
		tempReader.close();
		return listIndex;
	}

	private static void checkEmptyFile(int listIndex, ArrayList<String> feedback) {
		String feedbackLine;
		if (listIndex == 1) {
			feedbackLine = String.format(DISPLAY_EMPTY_MESSAGE, fileName);
			feedback.add(feedbackLine);
		}
	}

	private static ArrayList<String> deleteLine(String indexString) throws IOException {
		ArrayList<String> feedback = new ArrayList<String>();		
		int index = 0;

		// catch if non-integer is used
		try {
			index = Integer.parseInt(indexString);
		} catch (NumberFormatException e) {
			feedback.add(INVALID_INTEGER_MESSAGE);
			return feedback;
		}
	
		ArrayList<String> remainingText = getUndeletedLines(indexString, feedback, index);
		clearFile();
		writeUndeletedLines(remainingText);

		return feedback;
	}

	private static void writeUndeletedLines(ArrayList<String> remainingText) throws IOException {
		BufferedWriter mainWriter = new BufferedWriter(new FileWriter(file, false));
		for (String textLine : remainingText) {
			mainWriter.write(textLine + System.getProperty("line.separator"));
		}
		mainWriter.flush();
	}

	private static ArrayList<String> getUndeletedLines(String indexString, ArrayList<String> feedback, int index)
			throws FileNotFoundException, IOException {
		ArrayList<String> remainingText = new ArrayList<String>();
		BufferedReader mainReader = new BufferedReader(new FileReader(file));
		String currentLine;
		indexString = indexString.trim();
		int listIndex = 1;
		while ((currentLine = mainReader.readLine()) != null) {
			if (listIndex == index) {
				feedback.add(String.format(DELETE_MESSAGE, fileName, currentLine));
				listIndex++;
				continue;
			}
			listIndex++;
			remainingText.add(currentLine);
		}
		return remainingText;
	}
}
