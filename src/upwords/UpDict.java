package upwords;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;


public class UpDict {
	String dictionaryFilename = "data/dictionary.txt";
	boolean valid = true;
	ArrayList<ArrayList> baseDict;   // 10 bins of words, one for each length
	
	
	private static String makeRawBoardAndRackPattern(UpBoard board) {
		/*
		 * Create a regex that will only find letters that don't yet exist
		 * on the board or in the rack.
		 */
		
		// Let's start with the set of letters on the board.  We're going to expand set later so leave some room.
		Set<String> boardTileSet = new HashSet<String>();
		for (int x = 0; x < 10; x++) {
			for (int y =  0; y < 10; y++) {
				if (board.letters[x][y] == null) {
					continue;
				}
				boardTileSet.add(board.letters[x][y]);
			}
		}
		/*
		 * Convert the set to an array of Strings.
		 */
		String[] tileLetters = boardTileSet.toArray(new String[boardTileSet.size()]);

		/*
		 * Create the onBoardTiles array and start moving things into it.  Obviously there can only be twenty six
		 * unique members, but we're probably going to add duplicates fromt the rack so leave plenty of room.
		 */
		char[] onBoardTiles = new char[52];
		int onBoardTilesIndex = 0;
		for (onBoardTilesIndex = 0; onBoardTilesIndex < tileLetters.length; onBoardTilesIndex++) {
			onBoardTiles[onBoardTilesIndex] = tileLetters[onBoardTilesIndex].charAt(0);
		}
		
		/*
		 * Add the rack letters.
		 */
		for (int i = 0; i < board.rack.length; i++) {
			onBoardTiles[onBoardTilesIndex++] = board.rack[i];
		}
		
		/*
		 * OnBoardTiles now contains all the tiles on the board as well as the rack.  We'll 
		 * remove these letters from a list of A-Z to get the inverse list.  Any word with letters
		 * on the inverse list will be invalid because the letter isn't in play.
		 */
		char inverseList[] = new char[52];
		int findex = 0;
		for (int i = 0; i < 26; i++) {
			boolean found = false;
			char letter = (char)('A' + i);
			for (int o = 0; o < onBoardTiles.length; o++) {
				if (letter == onBoardTiles[o]) {
					found = true;
					break;
				}
			}
			if (found) {
				continue;
			}
			inverseList[findex++] = letter;
		}
		String regex = String.format(".*[%s].*", new String(inverseList));
		return regex;
	}
	

	private String makeRawRackPattern(char[] rack) {
		String regex = String.format(".*[%s].*", new String(rack));
		return regex;
	}

	
	public UpDict(UpBoard board) {
	
	    // This will reference one line at a time
	    String line;
	    baseDict = new ArrayList<ArrayList>();
	    for (int i = 0; i < 11; i++) {
	    		baseDict.add(i, new ArrayList<String>());
	    }
	
	    try {
	        // FileReader reads text files in the default encoding.
	        FileReader fileReader = 
	            new FileReader(dictionaryFilename);
	
	        // Always wrap FileReader in BufferedReader.
	        BufferedReader bufferedReader = 
	            new BufferedReader(fileReader);

	        /*
	         * Get the pattern that matches words that contain letters in the rack
	         */
			String rawRackPattern = makeRawRackPattern(board.rack);
			Pattern rackPattern = Pattern.compile(rawRackPattern);
			
			/*
			 * Get the pattern that matches words that contain letters that are not on the rack or the board.
			 */
	        	String rawBoardAndRackPattern = makeRawBoardAndRackPattern(board) ;
	        	Pattern boardAndRackPattern = Pattern.compile(rawBoardAndRackPattern);
    		
    			System.out.println("Letters on the rack pattern: " + rawRackPattern);
    			System.out.println("Letters not on the rack or the board pattern: " + rawBoardAndRackPattern);

	        while((line = bufferedReader.readLine()) != null) {
        			int len = line.length();
	        		if (len > 10) {
	        			/*
	        			 * Ten is the longest a word can be because of the board size
	        			 */
	        			continue;
	        		}
	        		
	        		// Strip out words without any letters that are in the rack
	        		Matcher m = rackPattern.matcher(line);
	        		if (!m.matches()) {
	        			continue;
	        		}
	        		
	        		// Strip out words with letters that aren't on the board or in the rack
	        		m = boardAndRackPattern.matcher(line);
	        		if (m.matches()) {
	        			continue;
	        		}

	        		ArrayList dictForSize = baseDict.get(len);
	        		dictForSize.add(line);
	        }   
	
	        // Always close files.
	        bufferedReader.close();         
	    }
	    catch(FileNotFoundException ex) {
	        System.out.println(
	            "Unable to open file '" + 
	            		dictionaryFilename + "'");       
	        valid = false;
	    }
	    catch(IOException ex) {
	        System.out.println(
	            "Error reading file '" 
	            + dictionaryFilename + "'");                  
	        // Or we could just do this: 
	        // ex.printStackTrace();
	        valid = false;
	    }
	}
}