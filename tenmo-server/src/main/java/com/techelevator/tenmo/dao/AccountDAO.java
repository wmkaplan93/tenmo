package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDAO {
	//account_id, user_id, balance
	
	Double returnBalance(long userId);

}
