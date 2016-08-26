package de.florianmarsch.picture;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.JEditorPane;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Screenshot {

	private Boolean wait = Boolean.TRUE;

	public File save(String url) {
		String screensurl = "https://www.googleapis.com/pagespeedonline/v1/runPagespeed?url=" + url
				+ "&screenshot=true&strategy=mobile";
		String content = loadFile(screensurl);
		String base64image = null;

		File output = null;
		try {
			base64image = new JSONObject(content).getJSONObject("screenshot").getString("data").replace("_", "/")
					.replace("-", "+");
			System.out.println(base64image);
			byte[] decoded = Base64.getDecoder().decode(base64image);


			output = File.createTempFile("temp", ".png");

			System.out.println("uses file " + output.getAbsolutePath());
			
			FileUtils.writeByteArrayToFile(output, decoded);
			
			
			BufferedImage originalImgage = ImageIO.read(output);

	        BufferedImage subImgage = originalImgage.getSubimage(0, 0, 320, 160);
			
	        ImageIO.write(subImgage, "png", output);
	        
			System.out.println("image on file");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException(e1.getMessage(), e1);
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
