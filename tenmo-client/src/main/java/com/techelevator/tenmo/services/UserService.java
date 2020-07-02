package com.techelevator.tenmo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.User;
import com.techelevator.view.ConsoleService;


public class UserService {
	
    public static String AUTH_TOKEN = "";
    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();

    public UserService(String url) {
        BASE_URL = url;
    }

    public Double getUserBalance(AuthenticatedUser user) throws UserServiceException {
    	

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(AUTH_TOKEN);
            HttpEntity entity = new HttpEntity<>( headers);
            Account account = restTemplate.exchange(BASE_URL + user.getUser().getUsername() + "/account", 
            		HttpMethod.GET, 
            		entity, 
            		Account.class).getBody();
            Double balance = account.getBalance();
            System.out.println("Current Balance: " + balance);
            return balance;
         } catch (RestClientResponseException ex) {
            throw new UserServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
    }

    
    public boolean sendBucks(AuthenticatedUser user) {
    	//call get all users
    	
    	//check if enough to send 
    	
    	//call add bucks to destination user
    	
    	//call subtract bucks on current user 
    	
    	
		return false;	
    }
    
    //call before sendBucks
    public void printAllUsers() {
    	UserSqlDAO user = new UserSqlDao; 
    	
//    	 List<User> users = new ArrayList<>();
//         while(results.next()) {
//        	 
//         }
    }
	
    private HttpEntity<AuthenticatedUser> makeUserEntity(AuthenticatedUser user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(user.getToken());
        HttpEntity<AuthenticatedUser> entity = new HttpEntity<>(user, headers);
        return entity;
    }
	
    private HttpEntity makeAuthEntity(AuthenticatedUser user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

}
