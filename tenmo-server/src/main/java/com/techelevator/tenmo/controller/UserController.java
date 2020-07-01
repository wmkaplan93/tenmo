package com.techelevator.tenmo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.UserDAO;
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
    
    
    @RequestMapping(path = "/{username}/balance", method = RequestMethod.GET)
    public Double getBalance(@PathVariable String username) {
        return accountDAO.returnBalance(userDAO.findIdByUsername(username));
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
