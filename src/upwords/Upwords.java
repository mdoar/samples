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
	
	private static boolean compareTiles(BufferedImage created, BufferedImage sampled) {
		int cred, cgreen, cblue, sred, sgreen, sblue;
		Color mycolor;
		float createdBlack = 0; 
		float bothBlack = 0;
		int COLOR_TOLERANCE = 6;
		int BLACK_BREAKPOINT = 128;
		
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
					
					/*
					 * The sampled image is slightly offset
					 */
					mycolor = new Color(sampled.getRGB(x-9, y));
					sred = mycolor.getRed();
					sgreen = mycolor.getGreen();
					sblue = mycolor.getBlue();
					
					
					String colorText = String.format("%d %d %d    %d %d %d", cred, cgreen, cblue, sred, sgreen, sblue);
					System.out.println(colorText);
					if (numbersClose(sred, sgreen, sblue, COLOR_TOLERANCE) && (sred < BLACK_BREAKPOINT)) {
						bothBlack += 1;
					}
				}
			}
		}
		System.out.println("Created black:" + createdBlack + "   bothBlack:" + bothBlack);
		
		return(true);
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

	   UpCharacterImage fooTextImage = new UpCharacterImage(5);
	   content.add(new JLabel(new ImageIcon(fooTextImage.img)));
	   
	   UpBoardScan scan = new UpBoardScan();
	   BufferedImage img = scan.getTile(3, 4);
	   content.add(new JLabel(new ImageIcon(img)));
	   f.pack();
	   f.setVisible(true);
	   
	   boolean compareTiles = compareTiles(fooTextImage.img, img);
	}
}
