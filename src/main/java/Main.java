import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import de.florianmarsch.picture.Screenshot;
import de.florianmarsch.server.Server;
import de.florianmarsch.vo.SlackVO;
import de.florianmarsch.vo.TweetVO;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Main {

	public static void main(String[] args) {

		Integer onPort = Integer.valueOf(System.getenv("PORT"));

		Server server = new Server();
		server.start(onPort);
		server.post("/api/twitter", (request, response) -> {

			String message = "";
			try {
				String bodyContent = request.body();
				if (bodyContent == null || bodyContent.trim().isEmpty()) {
					throw new IllegalArgumentException("No Content");
				}

				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				TweetVO tweet = mapper.readValue(bodyContent, TweetVO.class);

				ConfigurationBuilder cb = new ConfigurationBuilder();
				cb.setDebugEnabled(true).setOAuthConsumerKey(tweet.getConsumerKey())
						.setOAuthConsumerSecret(tweet.getConsumerSecret()).setOAuthAccessToken(tweet.getAccessToken())
						.setOAuthAccessTokenSecret(tweet.getAccessTokenSecret());
				TwitterFactory tf = new TwitterFactory(cb.build());
				Twitter twitter = tf.getInstance();

				String text = tweet.getTweet();
				if (tweet.getImage() != null) {
					String image = tweet.getImage();

					Screenshot sh = new Screenshot();
					File file = null;
					if (tweet.getIsImage()) {
						file = sh.saveDirectly(image);
					} else {
						file = sh.save(image);
					}

					StatusUpdate latestStatus = new StatusUpdate(text);

					latestStatus.setMedia(file);

					twitter.updateStatus(latestStatus);

				} else {
					Status status = twitter.updateStatus(text);
				}
				message = "created";
			} catch (Exception e) {
				e.printStackTrace();
				message = e.getMessage();
			}

			Map<String, Object> attributes = new HashMap<>();
			attributes.put("data", message);
			return attributes;
		});

	server.post("/api/slack", (request, response) -> {

		String message = "";
		try {
			String bodyContent = request.body();
			if (bodyContent == null || bodyContent.trim().isEmpty()) {
				throw new IllegalArgumentException("No Content");
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			SlackVO tweet = mapper.readValue(bodyContent, SlackVO.class);

			SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(tweet.getAccessToken());
			message = webApiClient.postMessage(tweet.getChannel(), tweet.getTweet(), "Copa-Bot", Boolean.FALSE);

		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("data", message);
		return attributes;
	});
	
	}
}
