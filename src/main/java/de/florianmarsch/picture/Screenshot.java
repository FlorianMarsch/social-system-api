package de.florianmarsch.picture;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import twitter4j.JSONObject;

public class Screenshot {

	private Boolean wait = Boolean.TRUE;

	public File saveDirectly(String url) {
		String content = loadFile(url);
		File output=null;
		try {
			output = null;
			output = File.createTempFile("temp", ".png");
			
			IOUtils.write(content, new FileOutputStream(output));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return output;
	}
	
	
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

	        BufferedImage subImgage = originalImgage;//.getSubimage(0, 0, 320, 569);
			
	        ImageIO.write(subImgage, "jpg", output);
	        
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
