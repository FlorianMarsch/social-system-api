import static spark.Spark.post;
import static spark.SparkBase.port;
import static spark.SparkBase.staticFileLocation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import de.florianmarsch.picture.Screenshot;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Main {

	public static void main(String[] args) {

		new Main().init();

	}

	public void init() {
		port(Integer.valueOf(System.getenv("PORT")));
		staticFileLocation("/public");

		post("/api/twitter", (request, response) -> {

			String message = "";
			try {
				String bodyContent = request.body();
				if (bodyContent == null || bodyContent.trim().isEmpty()) {
					throw new IllegalArgumentException("No Content");
				}

				JSONObject body = new JSONObject(bodyContent);
				System.out.println(body);

				ConfigurationBuilder cb = new ConfigurationBuilder();
				cb.setDebugEnabled(true).setOAuthConsumerKey(body.getString("consumerKey"))
						.setOAuthConsumerSecret(body.getString("consumerSecret"))
						.setOAuthAccessToken(body.getString("accessToken"))
						.setOAuthAccessTokenSecret(body.getString("accessTokenSecret"));
				TwitterFactory tf = new TwitterFactory(cb.build());
				Twitter twitter = tf.getInstance();

				String text = body.getString("tweet");
				if(body.has("image")){
					String image = body.getString("image");
					
					Screenshot sh = new Screenshot();
					File file = sh.save(image);
					
					StatusUpdate latestStatus = new StatusUpdate(text);
					
					latestStatus.setMedia(file);
					
					twitter.updateStatus(latestStatus );
					
				}else{
					Status status = twitter.updateStatus(text);
				}
				message = "created";
			} catch (Exception e) {
				e.printStackTrace();
				message = e.getMessage();
			}

			Map<String, Object> attributes = new HashMap<>();
			attributes.put("data", message);
			return new ModelAndView(attributes, "json.ftl");
		} , new FreeMarkerEngine());

	}
}
