import static spark.Spark.get;
import static spark.SparkBase.port;
import static spark.SparkBase.staticFileLocation;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class Main {

	public static void main(String[] args) {

		new Main().init();

	}

	public void init() {
		port(Integer.valueOf(System.getenv("PORT")));
		staticFileLocation("/public");
//Deprecated
		get("/api/team/:id", (request, response) -> {

			String id = request.params(":id");
			String all = loadFile("https://vintagemonster.onefootball.com/api/teams/de/" + id + ".json");
			JSONObject document;
			JSONArray players;
			try {
				document = new JSONObject(all);
				players = document.getJSONObject("data").getJSONObject("team").getJSONArray("players");
			} catch (Exception e) {
				e.printStackTrace();
				players = new JSONArray();
			}

			Map<String, Object> attributes = new HashMap<>();

			attributes.put("data", players.toString());

			return new ModelAndView(attributes, "json.ftl");
		} , new FreeMarkerEngine());

		get("/api/ligue", (request, response) -> {

			String content = loadFile(
					"http://api.football-api.com/2.0/competitions?Authorization=" + System.getenv("apikey"));

			JSONArray data;
			try {
				data = new JSONArray(content);
			} catch (Exception e) {
				data = new JSONArray();
				e.printStackTrace();
			}

			Map<String, Object> attributes = new HashMap<>();
			attributes.put("data", data.toString());
			return new ModelAndView(attributes, "json.ftl");
		} , new FreeMarkerEngine());

		get("/api/ligue/:id/team", (request, response) -> {

			JSONArray data = new JSONArray();
			String id = request.params(":id");

			String content = loadFile(
					"http://api.football-api.com/2.0/standings/" + id + "?Authorization=" + System.getenv("apikey"));

			try {
				JSONArray teams = new JSONArray(content);
				for (int i = 0; i < teams.length(); i++) {
					JSONObject team = teams.getJSONObject(i);
					JSONObject returnTeam = new JSONObject();

					returnTeam.put("name", team.getString("team_name"));
					returnTeam.put("id", team.getString("team_id"));
					returnTeam.put("ligue", id);

					data.put(returnTeam);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Map<String, Object> attributes = new HashMap<>();
			attributes.put("data", data.toString());
			return new ModelAndView(attributes, "json.ftl");
		} , new FreeMarkerEngine());
		
		
		get("/api/team/:id/squad", (request, response) -> {

			JSONArray data = new JSONArray();
			String id = request.params(":id");

			String content = loadFile(
					"http://api.football-api.com/2.0/team/" + id + "?Authorization=" + System.getenv("apikey"));

			try {
				JSONArray squad = new JSONObject(content).getJSONArray("squad");
				for (int i = 0; i < squad.length(); i++) {
					JSONObject player = squad.getJSONObject(i);
					JSONObject returnPlayer = new JSONObject();

					returnPlayer.put("name",normalize( player.getString("name")));
					returnPlayer.put("id", player.getString("id"));
					returnPlayer.put("position", player.getString("position"));
					returnPlayer.put("injured", Boolean.valueOf(player.getString("injured")));
					returnPlayer.put("team", id);

					data.put(returnPlayer);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Map<String, Object> attributes = new HashMap<>();
			attributes.put("data", data.toString());
			return new ModelAndView(attributes, "json.ftl");
		} , new FreeMarkerEngine());
			
		get("/api/ligue/:id/events", (request, response) -> {

			JSONArray data = new JSONArray();
			String id = request.params(":id");

			String content = loadFile("http://api.football-api.com/2.0/matches?comp_id=" + id
					+ "&from_date=01-01-1999&to_date=01-01-2099&Authorization=" + System.getenv("apikey"));

			try {
				JSONArray root = new JSONArray(content);
				for (int i = 0; i < root.length(); i++) {
					JSONObject match = root.getJSONObject(i);
					JSONArray events = match.getJSONArray("events");
					for (int j = 0; j < events.length(); j++) {
						JSONObject event = events.getJSONObject(j);
						String type = event.getString("type");
						if (type.equals("goal")) {

							JSONObject returnEvent = new JSONObject();
							returnEvent.put("id", event.getString("id"));
							String name = event.getString("player");
							if (name.contains(" (pen.)")) {
								name = name.replace(" (pen.)", "");
								type = "penalty";
							}
							if (name.contains(" (o.g.)")) {
								name = name.replace(" (o.g.)", "");
								type = "own";
							}

							returnEvent.put("player", normalize(name));
							returnEvent.put("type", type);

							data.put(returnEvent);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Map<String, Object> attributes = new HashMap<>();
			attributes.put("data", data.toString());
			return new ModelAndView(attributes, "json.ftl");
		} , new FreeMarkerEngine());

		
		//Deprecated
		get("/api/events/:id", (request, response) -> {

			Integer id = Integer.valueOf(request.params(":id")) + 5662927;
			String content = loadFile(
					"http://feedmonster.iliga.de/feeds/il/de/competitions/1/1271/matchdays/" + id + ".json");
			JSONArray data = new JSONArray();
			try {
				JSONObject json = new JSONObject(content);
				JSONArray kickoffs = json.getJSONArray("kickoffs");

				for (int k = 0; k < kickoffs.length(); k++) {
					JSONObject element = kickoffs.getJSONObject(k);
					JSONArray groups = element.getJSONArray("groups");
					for (int gr = 0; gr < groups.length(); gr++) {
						JSONObject group = groups.getJSONObject(gr);
						JSONArray matches = group.getJSONArray("matches");
						for (int m = 0; m < matches.length(); m++) {
							JSONObject match = matches.getJSONObject(m);
							JSONArray goals = match.getJSONArray("goals");
							for (int go = 0; go < goals.length(); go++) {
								JSONObject goal = goals.getJSONObject(go);
								data.put(goal);
							}
						}
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
				throw new RuntimeException("Abbruch", e);
			}

			Map<String, Object> attributes = new HashMap<>();
			attributes.put("data", data.toString());
			return new ModelAndView(attributes, "json.ftl");
		} , new FreeMarkerEngine());

	}

	public String loadFile(String url) {

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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempReturn.toString();
	}

	public String normalize(String aString){
		String norm = Normalizer.normalize(aString, Normalizer.Form.NFD);
		norm = norm.replaceAll("[^\\p{ASCII}]", "");
		return norm;
	}
	
}
