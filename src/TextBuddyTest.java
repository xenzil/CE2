import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.Test;

public class TextBuddyTest extends TextBuddy {

	@Test
	public void testExecuteCommand() throws IOException {
		ArrayList<String> expected = openFile("testfile.txt");
		expected = new ArrayList<String>();
		expected.add("added to testfile.txt: \"little brown fox\"");		
		testOneCommand("add first", expected, "add", " little brown fox");
		expected = new ArrayList<String>();


	}

	private void testOneCommand(String description, ArrayList<String> expected, String command, String restOfLine) throws IOException {
		
		assertEquals(description, expected, TextBuddy.executeCommand(command, restOfLine));
	}

}
