package upwords;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class UpBoardScan {

	BufferedImage img = null;

	int TILE_WIDTH=138;
	int TILE_HEIGHT=138;
	int STACK_HEIGHT=162;
	int LEFT_OF_IMAGE=1050;
	int TOP_OF_IMAGE = 267;
	
	public BufferedImage getTile(int xtile, int ytile) {
		BufferedImage tile = img.getSubimage(LEFT_OF_IMAGE+xtile*TILE_WIDTH, TOP_OF_IMAGE+ytile*TILE_HEIGHT,
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

