package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
	
	 public User getUserBalance(String username) throws UserServiceException {
		 User user = null;
		 makeUserEntity
		 try {
			 user = restTemplate.exchange(BASE_URL + "/" + username, HttpMethod.GET, makeAuthEntity(), User.class).getBody();
;
		 } catch (RestClientResponseException ex) {
			 throw new UserServiceException(ex.getRawStatusCode() + " : " +ex.getResponseBodyAsString());
			 
		 }
		 return user;
	 }
	
	 private HttpEntity makeAuthEntity() {
		    HttpHeaders headers = new HttpHeaders();
		    headers.setBearerAuth(AUTH_TOKEN);
		    HttpEntity entity = new HttpEntity<>(headers);
		    return entity;
		  }
	
	
	
}
