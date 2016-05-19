import static spark.Spark.get;
import static spark.SparkBase.port;
import static spark.SparkBase.staticFileLocation;

import java.io.InputStream;
import java.net.URL;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class Main {

	public static void main(String[] args) {

		new Main().init();

	}

	public void init() {
		port(Integer.valueOf(System.getenv("PORT")));
		staticFileLocation("/public");

		get("/api/lineup/:id", (request, response) -> {

			String id = request.params(":id");
			Set<String> recivedLineUp = reciveLineUp(id);

			Map<String, Object> attributes = new HashMap<>();

			JSONArray data = new JSONArray();
			for (String playerName : recivedLineUp) {
				data.put(playerName);
			}
			attributes.put("data", data.toString());

			return new ModelAndView(attributes, "json.ftl");
		} , new FreeMarkerEngine());
	}

	public Set<String> reciveLineUp(String id) {
		if (id == null | id.trim().isEmpty()) {
			return new HashSet<>();
		}
		try {
			String html = null;
			String urlString = "http://classic.comunio.de/playerInfo.phtml?pid="+id;
			InputStream is = (InputStream) new URL(urlString).getContent();
			html = IOUtils.toString(is, "UTF-8");

			html = Normalizer.normalize(html, Normalizer.Form.NFD);
			html = html.replaceAll("[^\\p{ASCII}]", "");

			Document doc = Jsoup.parse(html);
			Elements lines = doc.select(".name_cont");

			Set<String> teamList = new HashSet<String>();
			for (int i = 0; i < lines.size(); i++) {
				Element line = lines.get(i);
				String tempName = line.html();
				tempName = StringEscapeUtils.unescapeHtml(tempName);
				String norm = Normalizer.normalize(tempName, Normalizer.Form.NFD);
				norm = norm.replaceAll("[^\\p{ASCII}]", "");
				String trim = norm.trim();

				teamList.add(trim);
			}
			return teamList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("abbruch", e);
		}
	}

}
