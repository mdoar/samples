package upwords;

import java.util.ArrayList;

public class UpBoard {
	int[][] levels;
	char[][] letters;
	char[] rack;
	
	UpDict dict;
	
	int BOARD_SIZE = 10;
	int RACK_SIZE = 7;
	int MAX_WORD_LENGTH = 10;
	
	int PASS_INVALID = 0;
	int PASS_HORIZONTAL = 1;
	int PASS_VERTICAL = 2;
	
	public UpBoard() {
		levels = new int[BOARD_SIZE][BOARD_SIZE];
		letters = new char[BOARD_SIZE][BOARD_SIZE];
		rack = new char[RACK_SIZE];
	}
	
	public void dump() {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				System.out.print(" " + levels[i][j]);
			}
			System.out.println("");
		}
		
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (levels[i][j] != 0) {
					System.out.print(" " + letters[i][j]);
				} else {
					System.out.print(" .");
				}
			}
			System.out.println("");
		}
	}
	
	private int getHorizontalWordScore(int x, int y, char letter) {
		return(0);
	}
	
	private int getVerticalWordScore(int x, int y, char letter) {
		//  First, find the top and bottom of the word.  The search loops overshoot by one, so roll them back.
		int top, bottom;
		for (top = y; (top >= 0) && (letters[top][x] != 0); top--) {};   // loop is just setting top
		for (bottom = y; (bottom < BOARD_SIZE) && (letters[bottom][x] != 0); bottom++) {};   // loop is just setting bottom
		top++;
		bottom--;
		
		if ((top == bottom) || (bottom < top)) {
			//There's no word.
			return(0);
		}
		
		/*
		 * Build up the word into wordBytes
		 */
		int score = 0;
		int wordLength = 0;
		char[] wordBytes = new char[MAX_WORD_LENGTH];
		for (int i = top; i <= bottom; i++) {
			if (i == y) {
				//  This is the new tile.  Add 1 to the score as this is a new level
				wordBytes[wordLength++] = letter;
				score += (levels[i][x] + 1);
			} else {
				wordBytes[wordLength++] = letters[i][x];
				score += levels[i][x];
			}
		}
		
		/*
		 * Now check to see if we have a valid word
		 */
		String newWord = new String(wordBytes, 0, wordLength);
		ArrayList<String> specificDict = dict.baseDict.get(newWord.length());
		boolean validWord = specificDict.contains(newWord);
		//System.out.println(wordLength + "     " + newWord);
		if (!validWord) {
			return(0);
		}

		System.out.println(newWord + "     " + y + ", " + x + "    Top =" + top + "   Bottom =" + bottom + "    Score =" + score);
		
		return(score);
	}

	private int getTotalWordScore(int passtype, int x, int y, String word) {
		//System.out.println(word);	
		int total = 0;
		if (passtype == PASS_HORIZONTAL) {
			// Horizontal word
			// Walk through the new word adding up all the Vertical words that were changed
			for (int i = 0; i < word.length(); i++) {
				total += getVerticalWordScore(x+i, y, word.charAt(i));
			}
			
			/*
			 * We'll need either a vertical word or an overlap with an existing horizontal word
			 * in order to score.
			 */
			boolean adjacentWords=true;
			if (total == 0) {
				adjacentWords = false;
			}
				
			//  Add up the word itself
			for (int i = 0; i < word.length(); i++) {
				if (levels[y][x+i] != 0) {
					// We're overlapping with an existing tile
					adjacentWords = true;
				}
				total += levels[y][x+i];    // add the current level of the tile we're overwriting
				total += 1;  // add one for the new tile
			}
			
			if (!adjacentWords) {
				total = 0;
			} else {
				System.out.println("(" + y + ", " + x + ")   " + word + "    " + total);
			}
			
		} else {
			// Vertical word
			// Walk through the new word adding up all the Horizontal words that were changed
			// Horizontal word
			// Walk through the new word adding up all the Horizontal words that were changed
			for (int i = 0; i < word.length(); i++) {
				total += getHorizontalWordScore(x, y+i, word.charAt(i));
			}

			/*
			 * We'll need either a horizontal word or an overlap with an existing vertical word
			 * in order to score.
			 */
			boolean adjacentWords=true;
			if (total == 0) {
				adjacentWords = false;
			}

			//  Add up the word itself
			for (int i = 0; i < word.length(); i++) {
				if (levels[y+i][x] != 0) {
					// We're overlapping with an existing tile
					adjacentWords = true;
				}
				total += levels[y+i][x];    // add the current level of the tile we're overwriting
				total += 1;  // add one for the new tile
			}

			if (!adjacentWords) {
				total = 0;
			} else {
				System.out.println("(" + y + ", " + x + ")   " + word + "    " + total);
			}

		}
		
		
		return(total);
	}
	
	/*
	 * Okay, let's do it.  The order of events is this:
	 * 
	 * 1) Remove all dictionary words that don't include at least one rack letter
	 * 1) Remove all dictionary words that aren't entirely made of letters in the rack or on the board
	 * 2) Sort words into buckets by length
	 * 3) Loop through entire board
	 * 3.1) Do horizontal and vertical words checks on each space.  The only difference
	 * 		is that horizontal looks to the right and vertical looks down
	 * 3.4) Walk through the dictionary buckets that match the valid lengths
	 */
	public void processBoard() {
		/*
		 * The dictionary will arrive with only words that are possible.
		 */
		dict = new UpDict(this);
		for (int y = 0; y < BOARD_SIZE; y++) {
			for (int x = 0; x < BOARD_SIZE; x++) {
				/*
				 * let's start with a very simplistic algorithm.  Try every word in each space,
				 * with the exception that we'll only try word lengths that can fit on the rest of the line.
				 */
				int highScore = 0;
				String winningWord = "";
				int winningOrientation = PASS_INVALID;
				for (int wordLength = 0; wordLength < BOARD_SIZE - x; wordLength++) {
					ArrayList<String> words = dict.baseDict.get(wordLength);

					for (String word : words) {
						int score;
						score = getTotalWordScore(PASS_HORIZONTAL, x, y, word);
						if (score > highScore) {
							highScore = score;
							winningWord = word;
							winningOrientation = PASS_HORIZONTAL;
						}
						score = getTotalWordScore(PASS_VERTICAL, x, y, word);
						if (score > highScore) {
							highScore = score;
							winningWord = word;
							winningOrientation = PASS_VERTICAL;
						}
					}
				}
			}
		}
	}
	
	public void useSampleData(int sampleNumber)
	{
		if (sampleNumber == 0) {
			// This is a simple board for simple tests
			rack[0] = 'A';
			rack[1] = 'B';
			rack[2] = 'C';
			rack[3] = 'D';
			rack[4] = 'E';
			rack[5] = 'F';
			rack[6] = 'U';
			
			// Row 2 across "FOUL"
			levels[2][2] = 1;
			letters[2][2] = 'F';
			levels[2][3] = 1;
			letters[2][3] = 'O';
			levels[2][4] = 1;
			letters[2][4] = 'U';
			levels[2][5] = 1;
			letters[2][5] = 'L';


			// column 4 down is 'SHUT'
			levels[0][4] = 1;
			letters[0][4] = 'S';
			levels[1][4] = 1;
			letters[1][4] = 'H';
			levels[3][4] = 1;
			letters[3][4] = 'T';
		}
			
		if (sampleNumber == 1) {
			// This sample corresponds to IMG_0124.jpg
			rack[0] = 'A';
			rack[1] = 'B';
			rack[2] = 'C';
			rack[3] = 'D';
			rack[4] = 'E';
			rack[5] = 'F';
			rack[6] = 'G';
			
			// row 0
			levels[0][2] = 1;
			letters[0][2] = 'J';
			levels[0][3] = 1;
			letters[0][3] = 'I';
			levels[0][4] = 1;
			letters[0][4] = 'B';
			levels[0][5] = 1;
			letters[0][5] = 'I';
			levels[0][6] = 1;
			letters[0][6] = 'N';
			levels[0][7] = 1;
			letters[0][7] = 'G';


			// row 1
			levels[1][1] = 3;
			letters[1][1] = 'H';

			levels[1][3] = 1;
			letters[1][3] = 'R';
			levels[1][4] = 1;
			letters[1][4] = 'I';
			levels[1][5] = 1;
			letters[1][5] = 'F';


			// row 2
			levels[2][1] = 1;
			letters[2][1] = 'E';
			levels[2][2] = 1;
			letters[2][2] = 'W';
			levels[2][3] = 1;
			letters[2][3] = 'E';

			levels[2][7] = 1;
			letters[2][7] = 'M';


			// row 3
			levels[3][1] = 1;
			letters[3][1] = 'R';
			levels[3][2] = 1;
			letters[3][2] = 'E';

			levels[3][5] = 1;
			letters[3][5] = 'F';
			levels[3][6] = 1;
			letters[3][6] = 'O';
			levels[3][7] = 2;
			letters[3][7] = 'U';
			levels[3][8] = 3;
			letters[3][8] = 'L';


			// row 4
			levels[4][1] = 4;
			letters[4][1] = 'L';
			levels[4][2] = 1;
			letters[4][2] = 'A';
			levels[4][3] = 5;
			letters[4][3] = 'B';
			levels[4][4] = 1;
			letters[4][4] = 'S';

			levels[4][7] = 1;
			letters[4][7] = 'M';
			levels[4][8] = 2;
			letters[4][8] = 'U';



			// row 5
			levels[5][2] = 1;
			letters[5][2] = 'L';
			levels[5][3] = 2;
			letters[5][3] = 'O';
			levels[5][4] = 5;
			letters[5][4] = 'C';
			levels[5][5] = 2;
			letters[5][5] = 'I';

			levels[5][7] = 2;
			letters[5][7] = 'U';
			levels[5][8] = 2;
			letters[5][8] = 'G';
			levels[5][9] = 1;
			letters[5][9] = 'H';


			// row 6
			levels[6][3] = 5;
			letters[6][3] = 'Y';
			levels[6][4] = 3;
			letters[6][4] = 'A';
			levels[6][5] = 5;
			letters[6][5] = 'R';

			levels[6][8] = 3;
			letters[6][8] = 'E';


			// row 7
			levels[7][4] = 5;
			letters[7][4] = 'N';
			levels[7][5] = 2;
			letters[7][5] = 'I';
			levels[7][6] = 2;
			letters[7][6] = 'T';
			levels[7][7] = 1;
			letters[7][7] = 'E';
			levels[7][8] = 5;
			letters[7][8] = 'R';
			levels[7][9] = 1;
			letters[7][9] = 'S';


			// row 8
			levels[8][5] = 2;
			letters[8][5] = 'D';
			levels[8][6] = 1;
			letters[8][6] = 'A';
			levels[8][7] = 1;
			letters[8][7] = 'H';

			levels[8][9] = 2;
			letters[8][9] = 'U';


			// row 8
			levels[9][3] = 1;
			letters[9][3] = 'C';
			levels[9][4] = 1;
			letters[9][4] = 'A';
			levels[9][5] = 1;
			letters[9][5] = 'S';
			levels[9][6] = 1;
			letters[9][6] = 'E';

			levels[9][9] = 3;
			letters[9][9] = 'N';
		}
	}
}
