package com.techelevator.tenmo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.security.jwt.TokenProvider;

@RestController
@PreAuthorize("permitAll()")
public class UserController {
	
    private final TokenProvider tokenProvider;
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDAO userDAO;
    private AccountDAO accountDAO;

    public UserController(TokenProvider tokenProvider, UserDAO userDAO, AccountDAO accountDAO) {
        this.tokenProvider = tokenProvider;
        this.userDAO = userDAO;
        this.accountDAO = accountDAO;
    }
    
    @RequestMapping(path = "{username}/", method = RequestMethod.GET)
    public User getUserByUsername(@PathVariable String username) {
    	return userDAO.findByUsername(username);
    }
      
    @RequestMapping(path = "{username}/account", method = RequestMethod.GET)
    public Account getUserAccount(@PathVariable String username) throws AccountNotFoundException {
    	return accountDAO.returnAccountByUsername(userDAO.findIdByUsername(username));
    }
    
    @RequestMapping(path = "mapUsers", method = RequestMethod.GET)
    public Map<Long, User> mapUsers() {
    	Map<Long, User> userMap = userDAO.findAllMap();
    	return userMap;
    }
    
    @RequestMapping(path = "{userId}/account", method = RequestMethod.PUT)
    public void minusBucks(@RequestBody Account account, Double balance, @PathVariable Long userId)
    	throws AccountNotFoundException {
    	account.setUserId(userId);
    	accountDAO.minusBucks(account, balance);
    }
    	
//    @RequestMapping(path = "{username}/account/sendMoney", method = RequestMethod.PUT)
//    public Account sendMoney(@RequestBody Account account, @PathVariable String username)
//    	throws AccountNotFoundException {
//    return userDAO.
//    }
//    
//    @RequestMapping(path = "{username}/account/addMoney", method = RequestMethod.PUT)
//    public void addMoney() {
//    	
//    }
    

}
