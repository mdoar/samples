package upwords;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class UpBoardScan {

	BufferedImage img = null;

	int TILE_WIDTH=138;
	int TILE_HEIGHT=138;
	int STACK_HEIGHT=162;
	
	public BufferedImage getTile(int xtile, int ytile) {
		BufferedImage tile = img.getSubimage(1050+xtile*TILE_WIDTH, 267+ytile*TILE_HEIGHT, TILE_WIDTH, STACK_HEIGHT);
		return(tile);
	}

	public UpBoardScan() {
		try {
			img = ImageIO.read(new File("data/boards/IMG_0124.jpg"));

		} catch (IOException e) {
			System.out.println("Failed to read image");
		}

	}
}

