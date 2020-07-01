package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.User;
import com.techelevator.view.ConsoleService;


public class UserService {
	
    public static String AUTH_TOKEN = "";
    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private ConsoleService console = new ConsoleService(null, null);

    public UserService(String url) {
        BASE_URL = url;
    }
	
    //get balance
    public String getUserBalance(String username) throws UserServiceException {
    	String balance = "";
    	Account account = null;

        try {
            Account = restTemplate.exchange(BASE_URL + "/" + username + "/account", HttpMethod.GET, makeAuthEntity(), Account.class).getBody();
            balance = account.toString();
        } catch (RestClientResponseException ex) {
            throw new UserServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
        return balance;
    }
	
	
	
    private HttpEntity<User> makeUserEntity(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        return entity;
    }
	
    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

}
