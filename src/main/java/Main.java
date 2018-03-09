import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpHeader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Action;
import allbegray.slack.type.Attachment;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
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
	
	static Map<String, File> pictures = new HashMap<>();

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

		server.getBinary("/api/image/:uuid", (request, response) -> {
			String key = request.params(":uuid");
			File file = pictures.get(key);
			
			response.type( "image/png");
			
			
			HttpServletResponse raw = response.raw();
			ServletOutputStream outputStream = raw.getOutputStream();
			
			IOUtils.copy(new FileInputStream(file), outputStream);
			outputStream.close();
			return raw;
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
			ChatPostMessageMethod messagePost = new ChatPostMessageMethod(tweet.getChannel(), tweet.getTweet());
			messagePost.setAs_user(Boolean.FALSE);
			messagePost.setUsername("Copa-Bot");
			if(tweet.getImage() != null) {
				List<Attachment> attachments = new ArrayList<>();
				Attachment attachment = new Attachment();
				
				
					String image = tweet.getImage();

					
					
					if (tweet.getIsImage()) {
						attachment.setImage_url(tweet.getImage());
					} else {
						Screenshot sh = new Screenshot();
						File file = null;
						file = sh.save(image);
						String key = UUID.randomUUID().toString();
						System.out.println(key);
						pictures.put(key, file);
						
						attachment.setImage_url("http://social-system-api.herokuapp.com/api/image/"+key);
					}

					
				
				
				
				
				
				
				attachment.setFallback(tweet.getTweet());
				attachments.add(attachment );
				messagePost.setAttachments(attachments );
			}
			message = webApiClient.postMessage(messagePost );
			
			
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
