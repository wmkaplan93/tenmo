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

    public Double getUserBalance(AuthenticatedUser user) throws UserServiceException {
    	

        try {
            Account account = restTemplate.exchange(BASE_URL + user.getUser().getUsername() + "/account", HttpMethod.GET, makeUserEntity(user), Account.class).getBody();
            Double balance = account.getBalance();
            return balance;
         } catch (RestClientResponseException ex) {
            throw new UserServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
    }
    
    public Long getAccountId(AuthenticatedUser user) throws UserServiceException {
    	

        try {
            Account account = restTemplate.exchange(BASE_URL + user.getUser().getUsername() + "/account", HttpMethod.GET, makeUserEntity(user), Account.class).getBody();
            Long accountId = account.getAccountId();
            return accountId;
         } catch (RestClientResponseException ex) {
            throw new UserServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
    }
    
    
    public void listUsers() {
//    	User thisUser = new User();
    	Map<Long, User> users = mapUsers();
//    	List<User> userList = 
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("------------------------------");
		System.out.println("User ID          Username");
		System.out.println("------------------------------");
		for (Map.Entry<Long, User> entry : users.entrySet()) {
			User thisUser = restTemplate.getForObject(BASE_URL + "mapUsers", User.class);
			StringBuilder mapValueAsString = new StringBuilder();
			mapValueAsString.append(entry.getValue());
			String line = mapValueAsString.toString();
			String pattern = "(?<=username=)(\\w*)";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(line);
			if (m.find()) {
				System.out.println(entry.getKey() + "                " + m.group(0));
			} else {
				System.out.println("No Match");
			}
			
		}
//		for (int i = 0; i < users.size(); i++) {
////			thisUser.setId(users.get(users.keySet().toArray()[i]).getId());
////			thisUser.setUsername(users.get(users.keySet().toArray()[i]).getUsername());
//			System.out.println(users.keySet().toArray()[i] + "                " + users.get(users.keySet().toArray()[i]));
//		}
		System.out.println("------------------------------");
    }
    
    public Map<Long, User> mapUsers() {
    	Map<Long, User> response = restTemplate.getForObject(BASE_URL + "mapUsers", Map.class);
    	return response;
    }
    
    public boolean sendBucks(AuthenticatedUser currentUser, Double sendAmt, Long toUserId) {
    	System.out.println("Great Googly Moogly");
    	System.exit(0);
    	
    	//call add bucks to destination user
    	
    	//call subtract bucks on current user 
    	
    	
		return false;	
    }
    
    //add money to account balance
//    public Account minusAccount(AuthenticatedUser user, double less) {
//    	Account account = new Account();
//    	try {
//        	account.setUserId((long)user.getUser().getId());
//			account.setBalance(getUserBalance(user) - less);
//			account.setAccountId(getAccountId(user));
//		} catch (UserServiceException e) {
//			e.getMessage();
//		}
//		return account;
//    }
    
    public Account minusBucks(AuthenticatedUser user, double less) {
    	Account account = new Account();
    	try {
        	account.setUserId((long)user.getUser().getId());
			account.setBalance(getUserBalance(user) - less);
			account.setAccountId(getAccountId(user));
			
    		restTemplate.exchange(BASE_URL + user.getUser().getUsername() + "/account", HttpMethod.PUT, makeAccountEntity(account), Account.class);

		} catch (UserServiceException e) {
			e.getMessage();
		}
    	
    	return account;
    }
//    public Location update(String CSV) throws LocationServiceException {
//        Location location = makeLocation(CSV);
//        try {
//            restTemplate.exchange(BASE_URL + "/" + location.getId(), HttpMethod.PUT, makeLocationEntity(location), Location.class);
//        } catch (RestClientResponseException ex) {
//            throw new LocationServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
//        }
//        return location;
//    }
    
    // register user using name/pw
//    public void register(UserCredentials credentials) throws AuthenticationServiceException {
//    	HttpEntity<UserCredentials> entity = createRequestEntity(credentials);
//        sendRegistrationRequest(entity);
//    }
    
    //subtract money from account balance
    
//	public CatFact getFact() {
//	RestTemplate restTemplate = new RestTemplate();
//	CatFact catFact = restTemplate.getForObject("https://cat-fact.herokuapp.com/facts/random", CatFact.class);
//
//	return catFact;
//}
//	public String getUsername(Map<Long, User> userMap) {
//		RestTemplate restTemplate = new RestTemplate();
//		for (Map.Entry<Long, User> entry : userMap.entrySet()) {
//			String username = restTemplate.getForObject(BASE_URL + entry.getValue(), String.class);
//		}
//		
//	}
		
	
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
    
    private HttpEntity makeAccountEntity(Account account) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity entity = new HttpEntity<>(account);
    	return entity;
    }

}
