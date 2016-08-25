package de.florianmarsch.picture;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JEditorPane;

public class Screenshot {
	
	private Boolean wait = Boolean.TRUE;

	public File save(String url) {
		int width = 458, height = 228;
		// Create a `BufferedImage` and create the its `Graphics`
		BufferedImage image = null;

		File output = null;
		try {

			output = File.createTempFile("temp", ".png");
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			JEditorPane panel = new JEditorPane(url);
			panel.setSize(width, height);
			panel.addPropertyChangeListener("page",new PropertyChangeListener() {
				
				@Override
				public void propertyChange( PropertyChangeEvent evt) {
					System.out.println(evt.getPropertyName());
					wait = Boolean.FALSE;
					
				}
			});
			Integer timer = 0;
			while (wait) {
				try {
					Thread.sleep(1);
					timer = timer +1 ;
					if(timer > 4000){
						wait = Boolean.FALSE;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			wait = Boolean.TRUE;
			panel.paint(image.createGraphics());
			
			
			
			System.out.println("uses file " + output.getAbsolutePath());
			ImageIO.write(image, "png", output);
			System.out.println("image on file");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

}
