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
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.security.jwt.TokenProvider;

@RestController
//@PreAuthorize("isAuthenticated()")
public class UserController {
	
    private final TokenProvider tokenProvider;
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDAO userDAO;
    private AccountDAO accountDAO;
    private TransferDAO transferDAO;

    public UserController(TokenProvider tokenProvider, UserDAO userDAO, AccountDAO accountDAO, TransferDAO transferDAO) {
        this.tokenProvider = tokenProvider;
        this.userDAO = userDAO;
        this.accountDAO = accountDAO;
        this.transferDAO = transferDAO;
    }
    
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "{username}/", method = RequestMethod.GET)
    public User getUserByUsername(@PathVariable String username) {
    	return userDAO.findByUsername(username);
    }
    
    @PreAuthorize("permitAll()")
    @RequestMapping(path = "{username}/account", method = RequestMethod.GET)
    public Account getUserAccount(@PathVariable String username) throws AccountNotFoundException {
    	return accountDAO.returnAccountByUserId(userDAO.findIdByUsername(username));
    }
    
    @PreAuthorize("permitAll()")
    @RequestMapping(path = "{userId}/account/balance", method = RequestMethod.GET)
    public Account getUnAuthAccount(@PathVariable Long userId) throws AccountNotFoundException {
    	return accountDAO.returnAccountByUserId(Integer.parseInt(userId.toString()));
    }
    
    @PreAuthorize("permitAll()")
    @RequestMapping(path = "account/{accountId}", method = RequestMethod.GET)
    public String getAccountFromAccountId(@PathVariable long accountId) throws AccountNotFoundException {
    	String username = userDAO.usernameFromAccountId(accountId);
    	return username;
    }
    
    @PreAuthorize("permitAll()")
    @RequestMapping(path = "mapUsers", method = RequestMethod.GET)
    public Map<Long, User> mapUsers() {
    	Map<Long, User> userMap = userDAO.findAllMap();
    	return userMap;
    }
    
    @PreAuthorize("permitAll()")
    @RequestMapping(path = "{userId}/account", method = RequestMethod.PUT)
    public void changeBucks(@RequestBody Account account, @PathVariable Long userId)
    	throws AccountNotFoundException {
    	account.setUserId(userId);
    	Double balance = account.getBalance();
    	accountDAO.changeBucks(account, balance);
    }
    

    
    @PreAuthorize("permitAll()")
    @RequestMapping(path = "transfers", method = RequestMethod.POST) 
    public void logTransfer(@RequestBody Transfer transfer) {
//    	Long accountFrom = (long) transfer.getAccountFrom();
//    	Long accountTo = (long) transfer.getAccountTo();
//    	Double amount = transfer.getAmount();
    	transferDAO.logSend(transfer);
    }
    
    @PreAuthorize("permitAll()")
    @RequestMapping(path = "transfers/{transferId}", method = RequestMethod.GET)
    public Transfer transferDetails(@PathVariable int transferId) {
    	return transferDAO.transferDetails(transferId);
    	
    }
    
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "{username}/myTransfers", method = RequestMethod.GET)
    public List<Transfer> myTransfers(@PathVariable String username) throws AccountNotFoundException {
    	long myAccountId = accountDAO.returnAccountByUserId(userDAO.findIdByUsername(username)).getAccountId();
    	return transferDAO.myTransfers(myAccountId);
    }

    

}
