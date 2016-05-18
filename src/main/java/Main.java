import static spark.Spark.get;
import static spark.SparkBase.port;
import static spark.SparkBase.staticFileLocation;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.florianmarsch.preisomat.fixerio.CurrencyExchangeService;
import de.florianmarsch.preisomat.jpa.DataService;
import de.florianmarsch.preisomat.jpa.SaveService;
import de.florianmarsch.preisomat.vo.Charge;
import de.florianmarsch.preisomat.vo.Cost;
import de.florianmarsch.preisomat.vo.SyncPoint;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class Main {

	private CurrencyExchangeService currencyXChange = new CurrencyExchangeService();
	private DataService dataService = new DataService();
	private SaveService saveService = new SaveService();
	
	public static void main(String[] args) {

		new Main().init();

	}

	public void init() {
		port(Integer.valueOf(System.getenv("PORT")));
		staticFileLocation("/public");

		get("/api/sync", (request, response) -> {
			
			String body = request.body();
			saveSyncs(body);
			
			Map<String, Object> attributes = new HashMap<>();

			JSONObject data = new JSONObject();
			try {
				data.put("charging", getCharging());
				data.put("costs", getCosts());
				data.put("rate", currencyXChange.getSEK());
				data.put("date", getDate());
				data.put("cost", getCost());
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
			attributes.put("data", data.toString() );

			return new ModelAndView(attributes, "json.ftl");
		} , new FreeMarkerEngine());
	}

	BigDecimal getCost() {
		BigDecimal price = new BigDecimal("0");
		List<Cost> allCosts = dataService.getAllCosts();
		for (Cost cost : allCosts) {
			price.add(cost.getPrice());
		}
		return price;
	}

	JSONArray getCharging() {
		JSONArray jsonArray = new JSONArray();
		List<Cost> allCosts = dataService.getAllCosts();
		Map<String,Charge> charges = new HashMap<>();
		
		for (Cost cost : allCosts) {
			String person = cost.getPerson();
			Charge charge = charges.get(person);
			if(charge == null){
				charge = new Charge();
				charge.setPerson(person);
				charges.put(person, charge);
			}
			charge.setCharge(charge.getCharge().add(cost.getPrice()));
		}
		
		BigDecimal average = new BigDecimal(getCost().doubleValue() / charges.size());
		for (String person : charges.keySet()) {
			Charge charge = charges.get(person);
			charge.setSaldo(charge.getCharge().subtract(average));
			jsonArray.put(charge.getJSONObject());
		}
		
		
		return jsonArray;
	}
	
	JSONArray getCosts() {
		
		JSONArray jsonArray = new JSONArray();
		List<Cost> allCosts = dataService.getAllCosts();
		for (Cost cost : allCosts) {
			jsonArray.put(cost.getJSONObject());
		}
		return jsonArray;
	}

	private void saveSyncs(String body) {
		try {
			if(body == null || body.trim().isEmpty()){
				return;
			}
			JSONArray jsonArray = new JSONArray(body);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				SyncPoint syncPoint = new SyncPoint();
				syncPoint.parseFromJSON(jsonObject);
				saveService.saveSyncPoint(syncPoint);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
	}

	String getDate() {
		DateFormat formatter = new SimpleDateFormat();
		return formatter.format(new Date());
	}

}
