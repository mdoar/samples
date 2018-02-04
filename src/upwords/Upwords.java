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
					
					if (numbersClose(sred, sgreen, sblue, COLOR_TOLERANCE) && (sred < BLACK_BREAKPOINT)) {
						bothBlack += 1;
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

	   UpBoardScan scan = new UpBoardScan();
	   
	   UpCharacterImage numberTile = null;
	   BufferedImage scanImg = null;
	   boolean tilesMatch = false;
	   int[] levels = {1, 2, 3, 4, 5};
	   for (int y = 0; y < 10; y++) {
		   for (int x = 0; x < 10; x++) {
			   if ((x == 19) && (y == 1)) {
				   numberTile = new UpCharacterImage(2);
				   scanImg = scan.getTile(x, y);
				   tilesMatch = compareTiles(numberTile.img, scanImg, true);
				   
				   content.add(new JLabel(new ImageIcon(numberTile.img)));
				   content.add(new JLabel(new ImageIcon(scanImg)));
				   f.pack();
				   f.setVisible(true);
			   }
			   for (int level : levels) {
				   numberTile = new UpCharacterImage(level);
				   scanImg = scan.getTile(x, y);
				   tilesMatch = compareTiles(numberTile.img, scanImg, false);
				   if (tilesMatch) {
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
	   
	}
}
