package upwords;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/** Convert an image of an Upwords board into text in a 2D array
 * 
 * @author tkolar
 *
 */
public class UpBoardScan {

	BufferedImage img = null;

	int TILE_WIDTH=138;
	int TILE_HEIGHT=138;
	int STACK_HEIGHT=162;
	int LEFT_OF_IMAGE=1050;
	int TOP_OF_IMAGE = 267;
	
	/*
	 * Arbitrarily large number to represent grey spaces
	 */
	static int GREY_TILE = 12000;
	
	
	/*
	 * Determine if three integers are within 'limit' of one another
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
		final int COLOR_TOLERANCE = 6;
		final int BLACK_BREAKPOINT = 100;
		final int X_UPPER_LEFT = 45;
		final int Y_UPPER_LEFT = 20;
		final int X_WIDTH = 50;
		final int Y_HEIGHT = 100;
		double xorBlack = 0;
		double bothBlack = 0;
		int cred;
		int cgreen; 
		int cblue;
		int sred;
		int sgreen; 
		int sblue;
		Color mycolor;
		int blackCount = 0; 
		
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
				
				/*
				 * If the pixel is black in both images, white it out in the created 
				 * image.  This makes it easy to visually inspect the overlap.
				 */
				if (createdIsBlack && sampledIsBlack) {
					created.setRGB(x, y, 0xffffffff);
					bothBlack += 1;
				} else		
				if ((sampledIsBlack) || createdIsBlack) {
					// It they aren't both set but one of them is, that would be an XOR
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
		final int COLOR_TOLERANCE = 6;
		final int BLACK_BREAKPOINT = 100;
		final int BOARD_SQUARE_SIZE = 138;
		
		for (int y = 0; y < BOARD_SQUARE_SIZE; y++) {
			for (int x = 0; x < BOARD_SQUARE_SIZE; x++) {

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
	
	/** Use the given image to create a filled in UpBoard
	 * 
	 */
   public UpBoard scanBoardFromImage() {
	   JFrame f = new JFrame("Load Image Sample");
	   Container content = f.getContentPane();
	   content.setLayout(new FlowLayout());


	   /*
	    * Add a window so we can see the images when we're debugging them.
	    */
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
	   final int BOARD_SIZE = 10;

	   
	   /*
	    * Make a pass through the board to identify stack levels for each space.
	    */
	   for (int y = 0; y < BOARD_SIZE; y++) {
		   for (int x = 0; x < BOARD_SIZE; x++) {
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
			    * Find the stack level of this space by generating a number image a 
			    */
			   final double MIN_MATCH_RATIO = .8;
			   final int[] LEVELS = {1, 2, 3, 4, 5};
			   for (int level : LEVELS) {
				   numberTile = new UpCharacterImage(level);
				   scanImg = scan.getTile(x, y);
				   tilesMatch = compareTiles(numberTile.img, scanImg, MIN_MATCH_RATIO, false);
				   if (tilesMatch) {
					   board.levels[x][y] = level;
					   // print the stack level to the console for visual confirmation
					   // that we're reading correctly.
					   System.out.print(" " + numberTile.character);
					   break;
				   } 
			   }
			   if (!tilesMatch) {
				   // print the stack level to the console for visual confirmation
				   // that we're reading correctly.
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

	   for (int y = 0; y < BOARD_SIZE; y++) {
		   for (int x = 0; x < BOARD_SIZE; x++) {
			   /*
			    * If x and y point to a real location then print some debugging and display the tiles
			    */
			   if ((x == 17) && (y == 7)) {
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
			   final String[] LETTERS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
	                     				 "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
			   double lowestScore = GREY_TILE;
			   char probableLetter = '.';
			   for (String letter : LETTERS) {
				   numberTile = new UpCharacterImage(letter, board.levels[x][y]);
				   scanImg = scan.getTile(x, y);
				   letterScore = compareLetterTiles(numberTile.img, scanImg, false);
				   if (letterScore < lowestScore) {
					   lowestScore = letterScore;
					   probableLetter = letter.charAt(0);
				   }
			   }
			   board.letters[x][y] = probableLetter;
			   // print the letter to the console for visual confirmation
			   // that we're reading correctly.
			   System.out.print(" " + probableLetter);
		   }
		   // print the letter to the console for visual confirmation
		   // that we're reading correctly.
		   System.out.println("");
	   }	
	   return(board);
	}

	/*
	 * Return the image of the tile in space x,y
	 */
	private BufferedImage getTile(int x, int y) {
		BufferedImage tile = img.getSubimage(LEFT_OF_IMAGE+x*TILE_WIDTH, TOP_OF_IMAGE+y*TILE_HEIGHT,
											 TILE_WIDTH, STACK_HEIGHT);
		return(tile);
	}

	public UpBoardScan() {
		try {
			img = ImageIO.read(new File("data/boards/IMG_0132.jpg"));

		} catch (IOException e) {
			System.out.println("Failed to read image");
		}

	}
}

