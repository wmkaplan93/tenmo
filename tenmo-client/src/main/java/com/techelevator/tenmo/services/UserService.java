package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
	
    
// need to instantiate AUTH_TOKEN
    
    //get balance
    public Double getUserBalance(AuthenticatedUser user) throws UserServiceException {
    	System.out.println("Do I make it to userService");

    	// pass around whole account, then get the balance
    	//System.out.print account.getBalance()
        try {
        	System.out.println("Made it to the try block");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(user.getToken());
            HttpEntity<AuthenticatedUser> entity = new HttpEntity<>(user, headers);
            Account account = restTemplate.exchange(BASE_URL + user.getUser().getUsername() + "/account", 
            		HttpMethod.GET, entity, Account.class).getBody();
            System.out.println("made it past the account generation");
            Double balance = account.getBalance();
            System.out.println("Current Balance: " + balance);
            return balance;
        } catch (RestClientResponseException ex) {
            throw new UserServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
//        System.out.println("Current Balance: " + balance);
//        return balance;
    }
//we need to get AuthToken from current Authenticated user	
	
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
