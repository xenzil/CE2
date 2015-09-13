import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.Test;

public class TextBuddyTest extends TextBuddy {

	@Test
	public void testExecuteCommand() throws IOException {
		ArrayList<String> expected = openFile("testfile.txt");
		clearFile();
		
		expected = new ArrayList<String>();
		expected.add("added to testfile.txt: \"little brown fox\"");		
		testOneCommand("add first", expected, "add", " little brown fox");
		
		expected = new ArrayList<String>();
		expected.add("added to testfile.txt: \"ate a bunny\"");
		testOneCommand("add second", expected, "add", " ate a bunny");
		
		expected = new ArrayList<String>();
		expected.add("added to testfile.txt: \"in the sea\"");
		testOneCommand("add third", expected, "add", " in the sea");
		
		expected = new ArrayList<String>();
		expected.add("1. little brown fox");
		expected.add("2. ate a bunny");
		expected.add("3. in the sea");
		testOneCommand("display first", expected, "display", "");
		
		expected = new ArrayList<String>();
		expected.add("List has been sorted alphabetically. Here is the new list");
		expected.add("1. ate a bunny");
		expected.add("2. in the sea");
		expected.add("3. little brown fox");
		testOneCommand("display sorted", expected, "sort", "");
	}

	private void testOneCommand(String description, ArrayList<String> expected, String command, String restOfLine) throws IOException {
		
		assertEquals(description, expected, TextBuddy.executeCommand(command, restOfLine));
	}

}
