package de.florianmarsch.preisomat.vo;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.json.JSONException;
import org.json.JSONObject;

@Table
@Entity
public class SyncPoint {
	private String name;
	private String description;
	private String price;
	private String currency;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void parseFromJSON(JSONObject aJSONObject) {
		try {
			setCurrency(aJSONObject.getString("currency"));
			setDescription(aJSONObject.getString("description"));
			setName(aJSONObject.getString("name"));
			setPrice(aJSONObject.getString("price"));
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
