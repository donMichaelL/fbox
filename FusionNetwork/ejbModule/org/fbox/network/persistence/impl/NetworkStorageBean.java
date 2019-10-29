package org.fbox.network.persistence.impl;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.fbox.network.persistence.INetworkStorageBean;
import org.fbox.network.sos.util.DateTimeUtils;


@Stateless
@LocalBean
public class NetworkStorageBean implements INetworkStorageBean {

	@PersistenceContext(unitName="PullTimer")
	private EntityManager entityManager;
	

	// TODO: see persist in EntityManager doc - is INSERT ok? - What is the difference
	@Override
	public void insertPullTimer(PullTimer pullTimer) {
		System.out.println("[NSB] Inserting pullTimer...");
		entityManager.persist(pullTimer);
	}
	
	@Override
	public PullTimer getPullTimer(String timerId) {
		
		String q = "SELECT t FROM " + PullTimer.class.getName() + " t" + " WHERE t.timerId = '" + timerId + "'";
		Query query = entityManager.createQuery(q);
		List<PullTimer> timers = query.getResultList();
		
		// for(PullTimer t : timers)
		//	System.out.println("*********** " + t.toString());
		
		if(timers.size() != 0)
			return timers.get(0);
		else
			return null;
	}


	@Override
	public void initPullTimer(PullTimer pullTimer) {
		System.out.println("[NSB] Initializing pullTimer...");
		
		// If the pullTimer exists...update the timestamp of the existing pullTimer
		int updateCount = updatePullTimer(pullTimer);
		
		// Otherwise, insert a new pullTimer instance
		if(updateCount == 0)
			insertPullTimer(pullTimer);
	}


	@Override
	public int updatePullTimer(PullTimer pullTimer) {
		System.out.println("[NSB] Updating pullTimer...");
		
		SimpleDateFormat sdf = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT);
		
		String q = "UPDATE " + PullTimer.class.getName() + 
				" SET timestamp=" + "'" + sdf.format(pullTimer.getTimestamp()) + "'" +
			 	" WHERE timer_id = '" + pullTimer.getTimerId() + "'";
		
		Query query = entityManager.createQuery(q);
		int updateCount = query.executeUpdate();
		
		return updateCount;
	}

}
