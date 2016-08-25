package de.florianmarsch.picture;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import gui.ava.html.image.generator.HtmlImageGenerator;

public class Screenshot {

	public File save(String url) {
		int width = 458, height = 228;
		// Create a `BufferedImage` and create the its `Graphics`
	
		
		HtmlImageGenerator gen = new HtmlImageGenerator();
		gen.setSize(new Dimension(width, height));
		gen.loadUrl(url);
		BufferedImage image = gen.getBufferedImage();
		
		// Output the `BufferedImage` via `ImageIO`

		File output = null;
		try {
			output = File.createTempFile("temp", ".png");
			System.out.println("uses file "+output.getAbsolutePath());
			ImageIO.write(image, "png", output);
			System.out.println("image on file");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	

}
