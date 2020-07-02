package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDAO {
	
	public List<Transfer> findAll();
	
	public boolean sendBucks();
	
	public boolean requestBucks();
	

}
