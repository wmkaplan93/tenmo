package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDAO {

	void logSend(long accountFrom, long accountTo, double amount);

	List<Transfer> myTransfers(long myAccountId);

	List<Transfer> allTransfers();

	Transfer transferDetails(int transferId);
	

}
