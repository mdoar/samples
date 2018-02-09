package upwords;


public class Upwords {
	   
   public static void main(String[] args) {
	   boolean debugging = false;
	   UpBoard board = new UpBoard();
	   if (debugging) {
		   board.useSampleData(0);
	   } else {
		   UpBoardScan scan = new UpBoardScan();
		   board = scan.scanBoardFromImage();
	   }
	   board.dump();
	   board.processBoard();
   }
}
