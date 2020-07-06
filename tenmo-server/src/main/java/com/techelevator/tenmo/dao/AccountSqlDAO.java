package com.techelevator.tenmo.dao;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

@Service
public class AccountSqlDAO implements AccountDAO{
	
    private JdbcTemplate jdbcTemplate;
    public AccountSqlDAO(JdbcTemplate jdbcTemplate) {
    	this.jdbcTemplate = jdbcTemplate;
    }
//SELECT * FROM accounts
    //then get the balance, if passing around account, queryForRowSet()  
    //look in HW
    //SQLRowSet result = 
    
    
	@Override
	public Double returnBalance(long userId) {
		Account a = new Account();
		
		SqlRowSet results = jdbcTemplate.queryForRowSet("SELECT * FROM accounts WHERE user_id = ?", double.class, userId);
			if (results.next()) {
				a = mapRowToAccount(results);
			}
        return a.getBalance();
	}
	
	@Override
	public Double changeBucks(Account account, Double balance) {
		Long accountId = account.getAccountId();
//		Double newBalance = account.getBalance();
		Double newBalance = balance;
		String SQLupdate = "UPDATE accounts SET balance = ? WHERE account_id = ?";
		jdbcTemplate.update(SQLupdate, newBalance, accountId);
		return account.getBalance();
	}
	
    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            Account account = mapRowToAccount(results);
            accounts.add(account);
        }

        return accounts;
    }

    @Override
    public Account returnAccountByUserId(long userId) throws AccountNotFoundException {
        for (Account account : this.findAll()) {
            if( account.getUserId() == userId) {
                return account;
            }
        }
        throw new AccountNotFoundException("");
    }
    
    @Override
    public Account getAccountByAccountId(long accountId) throws AccountNotFoundException {
    	for (Account account : this.findAll()) {
    		if( account.getAccountId() == accountId) {
    			return account;
    		}
    	}
    	throw new AccountNotFoundException();
    }
    
    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getLong("account_id"));
        account.setUserId(rs.getLong("user_id"));
        account.setBalance(rs.getDouble("balance"));
        return account;
    }

	@Override
	public Account findByUsername(long userId) throws AccountNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	

}
