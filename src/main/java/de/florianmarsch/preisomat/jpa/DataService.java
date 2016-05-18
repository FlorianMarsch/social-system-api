package de.florianmarsch.preisomat.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.florianmarsch.preisomat.vo.Cost;

public class DataService {

	public List<Cost> getAllCosts(){
		
		EntityManager em = new EmFactory().produceEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("Select x from Cost x");
		List resultList = query.getResultList();
		
		em.getTransaction().commit();
		return new ArrayList<>(resultList);
	}
	

	
}