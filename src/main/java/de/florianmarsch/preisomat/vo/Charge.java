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
public class Charge {
	
	@Id
	private String id = UUID.randomUUID().toString();
	
	@Column
	private String person;
	
	@Column
	private BigDecimal charge;
	
	@Column
	private BigDecimal saldo;
	
	
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
	public BigDecimal getCharge() {
		return charge;
	}
	public void setCharge(BigDecimal charge) {
		this.charge = charge;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	
	public JSONObject getJSONObject() {
		JSONObject JSONObject = new JSONObject();
		try {
			JSONObject.put("id", getId());
			JSONObject.put("person", getPerson());
			JSONObject.put("charge", getCharge());
			JSONObject.put("saldo", getSaldo());
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return JSONObject;
	}

}
