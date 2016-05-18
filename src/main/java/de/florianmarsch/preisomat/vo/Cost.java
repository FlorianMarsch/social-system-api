package de.florianmarsch.preisomat.vo;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONException;
import org.json.JSONObject;

@Table
@Entity
public class Cost {

	@Id
	private String id = UUID.randomUUID().toString();

	@Column
	private String person;

	@Column
	private String description;

	@Column
	private BigDecimal price;

	@Column
	private BigDecimal priceSEK;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getPriceSEK() {
		return priceSEK;
	}

	public void setPriceSEK(BigDecimal priceSEK) {
		this.priceSEK = priceSEK;
	}

	public JSONObject getJSONObject() {
		JSONObject JSONObject = new JSONObject();
		try {
			JSONObject.put("id", getId());
			JSONObject.put("person", getPerson());
			JSONObject.put("description", getDescription());
			JSONObject.put("price", getPrice());
			JSONObject.put("priceSEK", getPriceSEK());
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return JSONObject;
	}
}
