package upwords;

/** High level direction for the Upwords Solver Application
 * 
 * @author tkolar
 *
 */
public class Upwords {
	   
	/** Entry point
	 * 
	 * @param args
	 * 
	 * The main function reads the command line arguments and walks
	 * through the process of creating a new board, scanning the sample
	 * board onto it, and then processing the board to find the highest
	 * score.
	 */
   public static void main(String[] args) {
	   boolean debugging = false;
	   UpBoard board = new UpBoard();
	   
	   /* 
	    * There are several sets of sample data used for testing.  Some of them 
	    * correspond to images in the data directory and are used to confirm that
	    * the board was read correctly.
	    */
	   if (debugging) {
		   board.useSampleData(0);
	   } else {
		   UpBoardScan scan = new UpBoardScan();
		   board = scan.scanBoardFromImage();
	   }
	   
	   // Print the board on the console
	   board.dump();
	   
	   // Print out the highest possible score
	   board.processBoard();
   }
}
