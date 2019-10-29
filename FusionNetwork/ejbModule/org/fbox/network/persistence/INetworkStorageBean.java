package org.fbox.network.persistence;

import javax.ejb.Local;

import org.fbox.network.persistence.impl.PullTimer;

@Local
public interface INetworkStorageBean {	
	public void insertPullTimer(PullTimer fBoxTimer);
	public void initPullTimer(PullTimer fBoxTimer);
	public PullTimer getPullTimer(String timerId);
	public int updatePullTimer(PullTimer fBoxTimer);
}
