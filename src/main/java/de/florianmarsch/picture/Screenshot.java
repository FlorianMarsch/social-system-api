package de.florianmarsch.picture;

import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.JEditorPane;

public class Screenshot {

	public File save(String url) {
		String html = loadFile(url);
		int width = 458, height = 228;
		// Create a `BufferedImage` and create the its `Graphics`
		BufferedImage image = new BufferedImage(100, 50,
                BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.createGraphics();
		// Create an `JEditorPane` and invoke `print(Graphics)`
		JEditorPane jep = new JEditorPane("text/html", html);
		jep.setSize(width, height);
		jep.print(graphics);
		// Output the `BufferedImage` via `ImageIO`

		File output = null;
		try {
			output = File.createTempFile(UUID.fromString(url).toString(), ".png");
			ImageIO.write(image, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	private String loadFile(String url) {

		StringBuffer tempReturn = new StringBuffer();
		try {
			URL u = new URL(url);
			InputStream is = u.openStream();
			DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
			String s;

			while ((s = dis.readLine()) != null) {
				tempReturn.append(s);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tempReturn.toString();
	}

}
