package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountSqlDAO implements AccountDAO{
	
    private JdbcTemplate jdbcTemplate;
//SELECT * FROM accounts
    //then get the balance, if passing around account, queryForRowSet()  
    //look in HW
    //SQLRowSet result = 
    
    
	@Override
	public Double returnBalance(long userId) {
        return jdbcTemplate.queryForObject("SELECT balance FROM accounts WHERE user_id = ?", double.class, userId);
	}
	
	

}
