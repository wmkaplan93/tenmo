package com.techelevator.tenmo.dao;

import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import com.techelevator.tenmo.model.Account;

public interface AccountDAO {
	//account_id, user_id, balance
	
	Double returnBalance(long userId);
		
    public List<Account> findAll();
    
    public Account findByUsername(long userId) throws AccountNotFoundException;

	Account returnAccountByUsername(int userId) throws AccountNotFoundException;

	Double minusBucks(Account account, Double balance);    
}
