package upwords;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class Upwords {
	
	/*
	 * Arbitrarily large number to represent grey saces
	 */
	static int GREY_TILE = 12000;
	/*
	 * Determine if three integers are within limit of each other
	 */
	private static boolean numbersClose(int first, int second, int third, int limit) {
		if (Math.abs(first - second) > limit) {
			return(false);
		}
		if (Math.abs(first - third) > limit) {
			return(false);
		}
		if (Math.abs(third - second) > limit) {
			return(false);
		}
		return(true);
	}
	
	/*
	 * compareLetterTiles
	 * 
	 * Count up the number of black pixels in the sample tile and subtract the pixels that overlap with the created image. 
	 * The lower the result the more the sample tile resembles the created tile.
	 */
	private static double compareLetterTiles(BufferedImage created, BufferedImage sampled, boolean logging) {
		int cred, cgreen, cblue, sred, sgreen, sblue;
		Color mycolor;
		int blackCount = 0; 
		int COLOR_TOLERANCE = 6;
		int BLACK_BREAKPOINT = 100;
		int X_UPPER_LEFT = 45;
		int Y_UPPER_LEFT = 20;
		int X_WIDTH = 50;
		int Y_HEIGHT = 100;
		double xorBlack = 0;
		double bothBlack = 0;
		
		for (int y = Y_UPPER_LEFT; y < Y_UPPER_LEFT + Y_HEIGHT; y++) {
			for (int x = X_UPPER_LEFT; x < X_UPPER_LEFT + X_WIDTH; x++) {
				
				mycolor = new Color(created.getRGB(x, y));
				cred = mycolor.getRed();
				cgreen = mycolor.getGreen();
				cblue = mycolor.getBlue();
				
				mycolor = new Color(sampled.getRGB(x, y));
				sred = mycolor.getRed();
				sgreen = mycolor.getGreen();
				sblue = mycolor.getBlue();

				boolean createdIsBlack = numbersClose(cred, cgreen, cblue, COLOR_TOLERANCE) && (cred < BLACK_BREAKPOINT);
				boolean sampledIsBlack = numbersClose(sred, sgreen, sblue, COLOR_TOLERANCE) && (sred < BLACK_BREAKPOINT);
				// Treat red like it's black.
				if ((sgreen < sred*.7) && (sblue < sred*.7)) {
					sampledIsBlack = true;
				}
				
				if (createdIsBlack && sampledIsBlack) {
					created.setRGB(x, y, 0xffffffff);
					bothBlack += 1;
				} else		
				if ((sampledIsBlack) || createdIsBlack) {
					xorBlack++;
				}
			}
		}
		if (logging) {
			System.out.println("blackCount =" + blackCount);
		}
		
		
		//System.out.println("blackCount =" + blackCount);
		return(xorBlack/bothBlack);
	}

	
	private static boolean compareTiles(BufferedImage created, BufferedImage sampled, double threshold, boolean logging) {
		int cred, cgreen, cblue, sred, sgreen, sblue;
		Color mycolor;
		float createdBlack = 0; 
		float bothBlack = 0;
		int COLOR_TOLERANCE = 6;
		int BLACK_BREAKPOINT = 100;
		
		for (int y = 0; y < 138; y++) {
			for (int x = 0; x < 138; x++) {

				mycolor = new Color(created.getRGB(x, y));
				cred = mycolor.getRed();
				cgreen = mycolor.getGreen();
				cblue = mycolor.getBlue();

				/*
				 * If there is a black pixel on the created image, check to see if the same pixel black on the sample
				 */
				if (numbersClose(cred, cgreen, cblue, COLOR_TOLERANCE) && (cred < BLACK_BREAKPOINT)) {
					createdBlack += 1;

					mycolor = new Color(sampled.getRGB(x, y));
					sred = mycolor.getRed();
					sgreen = mycolor.getGreen();
					sblue = mycolor.getBlue();
					if (logging) {
						String text = String.format("%d,%d  %d %d %d", x,y, sred, sgreen, sblue);
						System.out.println(text);
					}
					
					// Count the instances where both images' pixels are black.
					if (numbersClose(sred, sgreen, sblue, COLOR_TOLERANCE) && (sred < BLACK_BREAKPOINT)) {
						bothBlack += 1;
					}
					// Treat red like it's black.
					if ((sgreen < sred*.7) && (sblue < sred*.7)) {
						bothBlack +=1;
					}
				}
			}
		}
		if (logging) {
			System.out.println("createdBlack =" + createdBlack + "    bothBlack = " + bothBlack);
		}

		//System.out.println(bothBlack/createdBlack + "%");
		if (bothBlack/createdBlack > threshold) {
			return(true);
		}
		return(false);
	}
	
   private static void loadBoardFromImage() {
	   JFrame f = new JFrame("Load Image Sample");
	   Container content = f.getContentPane();
	   content.setLayout(new FlowLayout());


	   f.addWindowListener(new WindowAdapter(){
		   public void windowClosing(WindowEvent e) {
			   System.exit(0);
		   }
	   });

	   UpBoard board = new UpBoard();
	   UpBoardScan scan = new UpBoardScan();
	   
	   /*
	    *  Get the number titles
	    */
	   UpCharacterImage numberTile = null;
	   BufferedImage scanImg = null;
	   boolean tilesMatch = false;
	   int[] levels = {1, 2, 3, 4, 5};
	   
	   /*
	    * Make a pass through the board to identify stack levels for each space.
	    */
	   for (int y = 0; y < 10; y++) {
		   for (int x = 0; x < 10; x++) {
			   /*
			    * If x and y point to a real location then print some debugging and display the tiles
			    */
			   if ((x == 22) && (y == 0)) {
				   numberTile = new UpCharacterImage(1);
				   scanImg = scan.getTile(x, y);
				   tilesMatch = compareTiles(numberTile.img, scanImg, .8, true);
				   
				   content.add(new JLabel(new ImageIcon(numberTile.img)));
				   content.add(new JLabel(new ImageIcon(scanImg)));
				   f.pack();
				   f.setVisible(true);
			   }
			   
			   /*
			    * Find the stack level of this space
			    */
			   for (int level : levels) {
				   numberTile = new UpCharacterImage(level);
				   scanImg = scan.getTile(x, y);
				   tilesMatch = compareTiles(numberTile.img, scanImg, .8, false);
				   if (tilesMatch) {
					   board.levels[x][y] = level;
					   System.out.print(" " + numberTile.character);
					   break;
				   } 
			   }
			   if (!tilesMatch) {
				   System.out.print(" 0");   
			   }
		   }
		   System.out.println("");
	   }
	   
	   /*
	    * The second pass through the board identifies letters
	    */
	   double letterScore=0;
	   UpCharacterImage letterTile = null;
	   String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

	   for (int y = 0; y < 10; y++) {
		   for (int x = 0; x < 10; x++) {
			   /*
			    * If x and y point to a real location then print some debugging and display the tiles
			    */
			   if ((x == 7) && (y == 7)) {
				   letterTile = new UpCharacterImage("S", board.levels[x][y]);
				   scanImg = scan.getTile(x, y);
				   letterScore = compareLetterTiles(letterTile.img, scanImg, true);
				   System.out.println("letterscore = " + letterScore);
				   content.add(new JLabel(new ImageIcon(letterTile.img)));
				   content.add(new JLabel(new ImageIcon(scanImg)));
				   f.pack();
				   f.setVisible(true);
			   } 
			   
			   /*
			    * Find the character of this space
			    */
			   double lowestScore = GREY_TILE;
			   char probableLetter = '.';
			   for (String letter : letters) {
				   numberTile = new UpCharacterImage(letter, board.levels[x][y]);
				   scanImg = scan.getTile(x, y);
				   letterScore = compareLetterTiles(numberTile.img, scanImg, false);
				   if (letterScore < lowestScore) {
					   lowestScore = letterScore;
					   probableLetter = letter.charAt(0);
				   }
			   }
			   board.letters[x][y] = probableLetter;
			   System.out.print(" " + probableLetter);
		   }
		   System.out.println("");
	   }
	   
	   
	   
	}
   
   public static void main(String[] args) {
	   UpBoard board = new UpBoard();
	   board.useSampleData(0);
	   board.dump();
	   board.processBoard();
   }
}
