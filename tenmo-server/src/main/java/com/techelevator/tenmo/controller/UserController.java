package com.techelevator.tenmo.controller;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
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
    
    
    @RequestMapping(path = "{username}/account/balance", method = RequestMethod.GET)
    public Double getBalance(@PathVariable String username) {
    	//get account(SQL query), then call return balance
        return accountDAO.returnBalance(userDAO.findIdByUsername(username)).getBalance();
    }
    
    @RequestMapping(path = "{username}/account/", method = RequestMethod.GET)
    public Account getUserAccount(@PathVariable String username) throws AccountNotFoundException {
    	return accountDAO.returnAccountByUsername(userDAO.findIdByUsername(username));
    }
    
    
//    static class LoginResponse {
//
//        private String token;
//        private User user;
//
//        LoginResponse(String token, User user) {
//            this.token = token;
//            this.user = user;
//        }
//
//        @JsonProperty("token")
//        String getToken() {
//            return token;
//        }
//
//        void setToken(String token) {
//            this.token = token;
//        }
//
//        @JsonProperty("user")
//		public User getUser() {
//			return user;
//		}
//
//		public void setUser(User user) {
//			this.user = user;
//		}
//    }

}
