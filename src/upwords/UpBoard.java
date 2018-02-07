package upwords;

public class UpBoard {
	int[][] levels;
	String[][] letters;
	char[] rack;
	
	int PASS_INVALID = 0;
	int PASS_HORIZONTAL = 1;
	int PASS_VERTICAL = 2;
	
	public UpBoard() {
		levels = new int[10][10];
		letters = new String[10][10];
		rack = new char[7];
	}
	
	public void dump() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				System.out.print(" " + levels[i][j]);
			}
			System.out.println("");
		}
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (levels[i][j] != 0) {
					System.out.print(" " + letters[i][j]);
				} else {
					System.out.print(" .");
				}
			}
			System.out.println("");
		}
	}
	
	/*
	 * Okay, let's do it.  The order of events is this:
	 * 
	 * 1) Remove all dictionary words that don't include at least one rack letter
	 * 1) Remove all dictionary words that aren't entirely made of letters in the rack or on the board
	 * 2) Sort words into buckets by length
	 * 3) Loop through entire board
	 * 3.1) Do horizontal and vertical checks on each space.  The only difference
	 * 		is that horizontal looks to the right and vertical looks down
	 * 3.2) Make a list of spaces worth checking.  Specifically list the line
	 * 		and the two adjacent lines tiles starting with the current space.  
	 * 		If there are no tiles in that list then move on to next line.
	 * 3.3) Make a length list for each worthy space.  Skip any impossible lengths
	 * 		(like 7 when we're 6 tiles from the end)
	 * 3.4) Walk through the dictionary buckets that match the valid lengths
	 */
	public void processBoard() {
		UpDict dict = new UpDict(this);
	}
	
	public void useSampleData(int sampleNumber)
	{
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
			letters[0][2] = "J";
			levels[0][3] = 1;
			letters[0][3] = "I";
			levels[0][4] = 1;
			letters[0][4] = "B";
			levels[0][5] = 1;
			letters[0][5] = "I";
			levels[0][6] = 1;
			letters[0][6] = "N";
			levels[0][7] = 1;
			letters[0][7] = "G";


			// row 1
			levels[1][1] = 3;
			letters[1][1] = "H";

			levels[1][3] = 1;
			letters[1][3] = "R";
			levels[1][4] = 1;
			letters[1][4] = "I";
			levels[1][5] = 1;
			letters[1][5] = "F";


			// row 2
			levels[2][1] = 1;
			letters[2][1] = "E";
			levels[2][2] = 1;
			letters[2][2] = "W";
			levels[2][3] = 1;
			letters[2][3] = "E";

			levels[2][7] = 1;
			letters[2][7] = "M";


			// row 3
			levels[3][1] = 1;
			letters[3][1] = "R";
			levels[3][2] = 1;
			letters[3][2] = "E";

			levels[3][5] = 1;
			letters[3][5] = "F";
			levels[3][6] = 1;
			letters[3][6] = "O";
			levels[3][7] = 2;
			letters[3][7] = "U";
			levels[3][8] = 3;
			letters[3][8] = "L";


			// row 4
			levels[4][1] = 4;
			letters[4][1] = "L";
			levels[4][2] = 1;
			letters[4][2] = "A";
			levels[4][3] = 5;
			letters[4][3] = "B";
			levels[4][4] = 1;
			letters[4][4] = "S";

			levels[4][7] = 1;
			letters[4][7] = "M";
			levels[4][8] = 2;
			letters[4][8] = "U";



			// row 5
			levels[5][2] = 1;
			letters[5][2] = "L";
			levels[5][3] = 2;
			letters[5][3] = "O";
			levels[5][4] = 5;
			letters[5][4] = "C";
			levels[5][5] = 2;
			letters[5][5] = "I";

			levels[5][7] = 2;
			letters[5][7] = "U";
			levels[5][8] = 2;
			letters[5][8] = "G";
			levels[5][9] = 1;
			letters[5][9] = "H";


			// row 6
			levels[6][3] = 5;
			letters[6][3] = "Y";
			levels[6][4] = 3;
			letters[6][4] = "A";
			levels[6][5] = 5;
			letters[6][5] = "R";

			levels[6][8] = 3;
			letters[6][8] = "E";


			// row 7
			levels[7][4] = 5;
			letters[7][4] = "N";
			levels[7][5] = 2;
			letters[7][5] = "I";
			levels[7][6] = 2;
			letters[7][6] = "T";
			levels[7][7] = 1;
			letters[7][7] = "E";
			levels[7][8] = 5;
			letters[7][8] = "R";
			levels[7][9] = 1;
			letters[7][9] = "S";


			// row 8
			levels[8][5] = 2;
			letters[8][5] = "D";
			levels[8][6] = 1;
			letters[8][6] = "A";
			levels[8][7] = 1;
			letters[8][7] = "H";

			levels[8][9] = 2;
			letters[8][9] = "U";


			// row 8
			levels[9][3] = 1;
			letters[9][3] = "C";
			levels[9][4] = 1;
			letters[9][4] = "A";
			levels[9][5] = 1;
			letters[9][5] = "S";
			levels[9][6] = 1;
			letters[9][6] = "E";

			levels[9][9] = 3;
			letters[9][9] = "N";
		}
	}
}
