package upwords;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class Upwords {
	
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
	
	private static boolean compareTiles(BufferedImage created, BufferedImage sampled, boolean logging) {
		int cred, cgreen, cblue, sred, sgreen, sblue;
		Color mycolor;
		float createdBlack = 0; 
		float bothBlack = 0;
		int COLOR_TOLERANCE = 6;
		int BLACK_BREAKPOINT = 100;
		int RED_REDLIMIT = 100;
		int RED_GREENLIMIT = 70;
		int RED_BLUELIMIT = 70;
		
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
	    // If 80% of points were both black we have a match
		if (bothBlack/createdBlack > .8) {
			return(true);
		}
		return(false);
	}
	
   public static void main(String[] args) {
	 
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
			   if ((x == 13) && (y == 6)) {
				   numberTile = new UpCharacterImage(2);
				   scanImg = scan.getTile(x, y);
				   tilesMatch = compareTiles(numberTile.img, scanImg, true);
				   
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
				   tilesMatch = compareTiles(numberTile.img, scanImg, false);
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
	   String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

	   for (int y = 0; y < 10; y++) {
		   for (int x = 0; x < 10; x++) {
			   /*
			    * If x and y point to a real location then print some debugging and display the tiles
			    */
			   if ((x == 13) && (y == 6)) {
				   numberTile = new UpCharacterImage("A");
				   scanImg = scan.getTile(x, y);
				   tilesMatch = compareTiles(numberTile.img, scanImg, true);
				   
				   content.add(new JLabel(new ImageIcon(numberTile.img)));
				   content.add(new JLabel(new ImageIcon(scanImg)));
				   f.pack();
				   f.setVisible(true);
			   } 
			   
			   /*
			    * Find the character of this space
			    */
			   for (String letter : letters) {
				   numberTile = new UpCharacterImage(letter);
				   scanImg = scan.getTile(x, y);
				   tilesMatch = compareTiles(numberTile.img, scanImg, false);
				   if (tilesMatch) {
					   board.letters[x][y] = letter;
					   System.out.print(" " + letter);
					   break;
				   } 
			   }
			   if (!tilesMatch) {
				   board.letters[x][y] = ".";
				   System.out.print(" .");   
			   }
		   }
		   System.out.println("");
	   }
	   
	   
	   
	}
}
