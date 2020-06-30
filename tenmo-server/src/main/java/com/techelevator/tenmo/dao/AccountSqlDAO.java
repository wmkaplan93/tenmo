package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountSqlDAO implements AccountDAO{
	
    private JdbcTemplate jdbcTemplate;

	@Override
	public Double returnBalance(long userId) {
        return jdbcTemplate.queryForObject("SELECT balance FROM accounts WHERE user_id = ?", double.class, userId);
	}
	
	

}
