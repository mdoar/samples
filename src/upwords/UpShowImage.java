package upwords;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class UpShowImage extends Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage img = null;

	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}
	
	
	int TILE_WIDTH=138;
	int TILE_HEIGHT=138;
	int STACK_HEIGHT=162;
	
	public UpShowImage() {
		try {
			img = ImageIO.read(new File("data/boards/IMG_0124.jpg"));
			int xtile = 0;
			int ytile = 0;
			img = img.getSubimage(1050+xtile*TILE_WIDTH, 267+ytile*TILE_HEIGHT, TILE_WIDTH, STACK_HEIGHT);
		} catch (IOException e) {
			System.out.println("Failed to read image");
		}

	}

	public Dimension getPreferredSize() {
		if (img == null) {
			return new Dimension(100,100);
		} else {
			return new Dimension(img.getWidth(null), img.getHeight(null));
		}
	}
}
