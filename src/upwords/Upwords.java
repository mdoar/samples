package upwords;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class Upwords {
   public static void main(String[] args) {
	 
	   JFrame f = new JFrame("Load Image Sample");

	   f.addWindowListener(new WindowAdapter(){
		   public void windowClosing(WindowEvent e) {
			   System.exit(0);
		   }
	   });

	   f.add(new UpShowImage());
	   f.pack();
	   f.setVisible(true);
	}
}
