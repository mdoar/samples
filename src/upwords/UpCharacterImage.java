package upwords;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;



public class UpCharacterImage {

	BufferedImage img;
	String character;
	int level;
	int TILE_WIDTH = 138;
	int TILE_HEIGHT = 138;
	int STACK_HEIGHT=162;
	
	
	/*
	 * Build a standard 138x138 title with a letter or number in it.  This will be matched to the tile in the 
	 * board image.
	 * 
	 * Ideally this should cache the results but it's not really affecting runtime so let's skip that (minor) complexity.
	 */
	private void makeImage(String text, Font font, int xoffset, int yoffset)
	{
	 character = text;
     img = new BufferedImage(TILE_WIDTH, STACK_HEIGHT, BufferedImage.TYPE_INT_ARGB);
     Graphics2D g2d = img.createGraphics();
     g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
     g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
     g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
     g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
     g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
     g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
     g2d.setFont(font);
     g2d.setColor(Color.WHITE);
     g2d.fillRect(0, 0, TILE_WIDTH, STACK_HEIGHT);
     g2d.setColor(Color.BLACK);
     
     FontMetrics fm = g2d.getFontMetrics();
     if (xoffset == -1) {
    	 	xoffset = (TILE_WIDTH - fm.stringWidth(text)) / 2;
     }

     g2d.drawString(text, xoffset, yoffset);

     g2d.dispose();
		
	}
	
	/*
	 * Create the number representing the stack height of the tile.
	 */
	public UpCharacterImage(int number) {
		Font font = new Font("Apple Color Emoji", Font.PLAIN, 28);
		String text = String.format("%d", number);
		
		// The stack height goes with the number, and the number is offset a bit for each level of stack.
		// The image numbers are slightly offset to the left and right as well.
		int[] yoffsets = {0, 51, 43, 35, 27, 19};
		int[] xoffsets = {0, 100, 99, 99, 98, 99};
		
		makeImage(text, font, xoffsets[number], 12 + yoffsets[number]);
	}
	
	/*
	 * Create the single character for the tile.
	 */
    public UpCharacterImage(String text, int level) {
 		Font font = new Font("Helvetica Neue", Font.BOLD, 75);
 		
 		/* 
 		 * (xoffset, yoffset) the upper left corner of the letter.
 		 */
 		int xoffset = 42;
 		int[] yoffsets = {0, 122, 114, 106, 98, 90};
    		makeImage(text, font, -1, yoffsets[level]);
    }

}