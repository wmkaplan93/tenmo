package com.techelevator.tenmo.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.view.ConsoleService;


public class UserService {
	
    public static String AUTH_TOKEN = "";
    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();

    public UserService(String url) {
        BASE_URL = url;
    }

    public Double getUserBalance(User user) throws UserServiceException {
    	
        HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<User> entity = new HttpEntity<>(user, headers);
		String userName = user.getUsername();
		System.out.println("Username = " + userName);
		Account account = restTemplate.exchange(BASE_URL + userName + "/account", HttpMethod.GET, entity, Account.class).getBody();
		Double balance = account.getBalance();
		return balance;
    }
    
    public Long getAccountId(User user) throws UserServiceException {
    	

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<User> entity = new HttpEntity<>(user, headers);
            Account account = restTemplate.exchange(BASE_URL + user.getUsername() + "/account", HttpMethod.GET, entity, Account.class).getBody();
            Long accountId = account.getAccountId();
            return accountId;
        } catch (RestClientResponseException ex) {
            throw new UserServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
    }
    
//    public Long getForeignAccountId(User user) throws UserServiceException {
//    	
//
//        try {
//            Account account = restTemplate.exchange(BASE_URL + user.getUsername() + "/account", HttpMethod.GET, makeUserEntity(user), Account.class).getBody();
//            Long accountId = account.getAccountId();
//            return accountId;
//         } catch (RestClientResponseException ex) {
//            throw new UserServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
//        }
//    }
    
    
    
    public Map<Long, String> listUsers() {
    	Map<Long, User> users = mapUsers();
		System.out.println("------------------------------");
		System.out.println("User ID          Username");
		System.out.println("------------------------------");
		Map<Long, String> newUsers = new HashMap<Long, String>();
		for (Map.Entry<Long, User> entry : users.entrySet()) {
			StringBuilder mapValueAsString = new StringBuilder();
			mapValueAsString.append(entry.getValue());
			String line = mapValueAsString.toString();
			String pattern = "(?<=username=)(\\w*)";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(line);
			if (m.find()) {
				System.out.println(entry.getKey() + "                " + m.group());
				Long key = Long.parseLong(entry.getKey().toString());
				newUsers.put(key, m.group());
			} else {
				System.out.println("No Match");
			}
			
		}
		System.out.println("------------------------------");
		return newUsers;
    }
    
    public Map<Long, User> mapUsers() {
    	Map<Long, User> response = restTemplate.getForObject(BASE_URL + "mapUsers", Map.class);
    	return response;
    }
    
    public boolean sendBucks(AuthenticatedUser currentUser, Double sendAmt, Long toUserId, String toUserName) throws UserServiceException {
    	Double lessAmt = getUserBalance(currentUser.getUser()) - sendAmt;
    	minusBucks(currentUser, lessAmt);
    	Integer intUserId = Integer.parseInt(toUserId.toString());
    	addBucks(sendAmt, intUserId, toUserName);
       	System.out.println("Success!");
    	System.out.println("New Balance: " + getUserBalance(currentUser.getUser()));
    	System.exit(0);
    	
    	//call add bucks to destination user
    	
    	//call subtract bucks on current user 
    	
    	
		return false;	
    }
    
    public boolean addBucks (Double sendAmt, Integer toUserId, String toUserName) throws UserServiceException {
    	Account account = new Account();
    	User thisUser = new User();
    	thisUser.setId(toUserId);
    	thisUser.setUsername(toUserName);
    	Long userId = (long)toUserId;
    	Double balance = getUserBalance(thisUser) + sendAmt;
    	
    	try {
    		account.setUserId(userId);
    		account.setBalance(balance);
    		account.setAccountId(getAccountId(thisUser));
    	} catch (UserServiceException e) {
    		e.getMessage();
    	}
    	return false;
    }
    
    
    public Account minusBucks(AuthenticatedUser user, double balance) {
    	Account account = new Account();
    	User thisUser = user.getUser();
       	try {
        	account.setUserId((long)thisUser.getId());
			account.setBalance(balance);
			account.setAccountId(getAccountId(thisUser));
			
    		restTemplate.put(BASE_URL + thisUser.getId() + "/account", makeAccountEntity(account));

		} catch (UserServiceException e) {
			e.getMessage();
		}
    	
    	return account;
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
    
    private HttpEntity<Account> makeAccountEntity(Account account) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<Account> entity = new HttpEntity<>(account, headers);
    	return entity;
    }

}
