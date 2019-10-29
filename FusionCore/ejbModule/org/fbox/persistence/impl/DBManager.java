package org.fbox.persistence.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.fbox.common.db.ConfigurationParameter;
import org.fbox.common.db.IDBManager;

@Stateless
@LocalBean
public class DBManager implements IDBManager {

	@PersistenceContext(unitName="ConfigParameter")
	private EntityManager entityManager;
	
	@Override
	public Set<ConfigurationParameter> getConfigParameters() {
		String q = "SELECT cp FROM " + ConfigParameter.class.getName() + " cp";
		Query query = entityManager.createQuery(q);
		List<ConfigurationParameter> configParameterList = query.getResultList();
		
		Set<ConfigurationParameter> configParamSet = new HashSet<ConfigurationParameter>(configParameterList);
		
		return configParamSet;
	}
	
	@Override
	public ConfigurationParameter getConfigParameter(String paramName) {
		String q = "SELECT cp FROM " + ConfigParameter.class.getName() + " cp" + " WHERE cp.name = '" + paramName + "'";
		Query query = entityManager.createQuery(q);
		List<ConfigurationParameter> configParameterList = query.getResultList();
		
		if(configParameterList.size() != 0)
			return configParameterList.get(0);
		else
			return null;
	}

	@Override
	public int updateConfigParameter(ConfigurationParameter configParameter) {
		String q = "UPDATE " + ConfigParameter.class.getName() + 
				  " SET value='" + configParameter.getValue() + "'" + ",description='" + configParameter.getDescription() + "'" +
			 	  " WHERE param_name = '" + configParameter.getName() + "'";
		
		Query query = entityManager.createQuery(q);
		int rowsUpdated = query.executeUpdate();
		
		return rowsUpdated;
	}

	@Override
	public int updateConfigParameter(String paramName, String value) {
		String q = "UPDATE " + ConfigParameter.class.getName() + 
				  " SET value='" + value + "'" + 
				  " WHERE param_name = '" + paramName + "'";
		
		Query query = entityManager.createQuery(q);
		int rowsUpdated = query.executeUpdate();
		
		return rowsUpdated;
	}

	

}
