package com.techelevator.tenmo.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
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
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.view.ConsoleService;


public class UserService {
	
    public static String AUTH_TOKEN = "";
    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private Scanner scanner = new Scanner(System.in);

    public UserService(String url) {
        BASE_URL = url;
    }

    public Double getUserBalance(AuthenticatedUser user) throws UserServiceException {
    	
//        HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<User> entity = new HttpEntity<>(user, headers);
		String userName = user.getUser().getUsername();
		Account account = restTemplate.exchange(BASE_URL + userName + "/account", HttpMethod.GET, makeAuthEntity(user), Account.class).getBody();
		Double balance = account.getBalance();
		return balance;
    }
    
    public Double getUnAuthUserBalance(User user) throws UserServiceException {
		Account account = restTemplate.exchange(BASE_URL + user.getId() + "/account/balance", HttpMethod.GET, makeUserEntity(user), Account.class).getBody();
		Double balance = account.getBalance();
		return balance;
    }
    
    public Long getAccountIdFromUser(AuthenticatedUser user) throws UserServiceException {

        Account account = restTemplate.exchange(BASE_URL + user.getUser().getUsername() + "/account", HttpMethod.GET, makeAuthEntity(user), Account.class).getBody();
        Long accountId = account.getAccountId();
        return accountId;
    }
    
    public Long getUnAuthAccountIdFromUser(User user) throws UserServiceException {

        Account account = restTemplate.exchange(BASE_URL + user.getId() + "/account/balance", HttpMethod.GET, makeUserEntity(user), Account.class).getBody();
        Long accountId = account.getAccountId();
        return accountId;
    }
    
    public String getUsernameFromAccountId(long accountId) {
        HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity entity = new HttpEntity<>(headers);
    	String userName = restTemplate.exchange(BASE_URL + "account/" + accountId, HttpMethod.GET, entity, String.class).getBody();
    	return userName;
    }

    
	   public Map<String, String> listUsers(AuthenticatedUser user) {
	    	Map<Long, User> users = mapUsers(user);
			System.out.println("------------------------------");
			System.out.println("User ID          Username");
			System.out.println("------------------------------");
			Map<String, String> newUsers = new HashMap<String, String>();
			Set set = users.entrySet();
			Iterator i = set.iterator();
			while(i.hasNext()) {
				Map.Entry me = (Map.Entry)i.next();
				StringBuilder mapValueAsString = new StringBuilder();
				mapValueAsString.append(me.getValue());
				String line = mapValueAsString.toString();
				String pattern = "(?<=username=)(\\w*)";
				Pattern p = Pattern.compile(pattern);
				Matcher m = p.matcher(line);
				if (m.find()) {
					System.out.println(me.getKey() + "                " + m.group());
				} else {
					System.out.println("No Match");
				}
				String userKey = me.getKey().toString();
				newUsers.put(userKey, m.group());

			}
		
		System.out.println("------------------------------");
		return newUsers;
    }
    
	   
    public Map<Long, User> mapUsers(AuthenticatedUser user) {
    	Map<Long, User> response = restTemplate.getForObject(BASE_URL + "mapUsers", Map.class);
    	return response;
    }
    
    public void sendBucks(AuthenticatedUser currentUser, Double sendAmt, Long toUserId, String toUserName) throws UserServiceException {
    	Double lessAmt = getUserBalance(currentUser) - sendAmt;
    	minusBucks(currentUser, lessAmt);
    	
    	Integer intUserId = Integer.parseInt(toUserId.toString());
    	User thisUser = new User();
    	thisUser.setId(intUserId);
    	thisUser.setUsername(toUserName);
    	
    	addBucks(sendAmt, thisUser);
       	System.out.println("Success!");
    	System.out.println("New Balance: " + getUserBalance(currentUser));
    	logSend(getAccountIdFromUser(currentUser), getUnAuthAccountIdFromUser(thisUser), sendAmt);
    }

	public void addBucks (Double sendAmt, User user) throws UserServiceException {
    	Account account = new Account();
    	Long userId = (long) user.getId();
    	Double balance = getUnAuthUserBalance(user) + sendAmt;
    	
    	try {
    		account.setUserId(userId);
    		account.setBalance(balance);
    		account.setAccountId(getUnAuthAccountIdFromUser(user));
    		
    		restTemplate.put(BASE_URL + user.getId() + "/account", makeAccountEntity(account));
    	} catch (UserServiceException e) {
    		e.printStackTrace();
    	}
    }
    
    
    public Account minusBucks(AuthenticatedUser user, double balance) {
    	Account account = new Account();
    	User thisUser = user.getUser();
       	try {
        	account.setUserId((long)thisUser.getId());
			account.setBalance(balance);
			account.setAccountId(getUnAuthAccountIdFromUser(thisUser));
			
    		restTemplate.put(BASE_URL + thisUser.getId() + "/account", makeAccountEntity(account));

		} catch (UserServiceException e) {
			e.getMessage();
		}
    	
    	return account;
    }
    
    public void logSend(long accountFrom, long accountTo, double amount) throws UserServiceException {
    	Transfer transfer = new Transfer();
    	transfer.setAccountFrom(accountFrom);
		transfer.setAccountTo(accountTo);
		transfer.setAmount(amount);
		
		restTemplate.postForEntity(BASE_URL + "transfers", transfer, Transfer.class);
    }
    
    public Transfer[] myTransfers(AuthenticatedUser user) {
    	Transfer[] myTransfers = null;
    	myTransfers = restTemplate.exchange(BASE_URL + user.getUser().getUsername() + "/myTransfers", HttpMethod.GET, makeAuthEntity(user), Transfer[].class).getBody();
    	System.out.println("-------------------------------");
    	System.out.println("---        Transfers        ---");
    	System.out.println("---                         ---");
    	System.out.println("#)  ID       From/To     Amount");
    	System.out.println("-------------------------------");
    	for (int i = 0; i < myTransfers.length; i++) {
    		System.out.print(i + 1 + ") ");
        	System.out.print(myTransfers[i].getTransferId() + "       ");
        	if (myTransfers[i].getAccountFrom() == (long) user.getUser().getId()) {
        		String toUser = getUsernameFromAccountId(myTransfers[i].getAccountTo());
        		System.out.print("TO:   " + toUser + "   ");
        	} else if (myTransfers[i].getAccountTo() == (long) user.getUser().getId()) {
        		String fromUser = getUsernameFromAccountId(myTransfers[i].getAccountFrom());
        		System.out.print("FROM: " + fromUser + "     ");
        	}
        	System.out.println("   $" + myTransfers[i].getAmount());

    	}
    	System.out.println("-------------------------------");
    	System.out.println("");
    	System.out.println("For more transfer information, please type a number (1, 2 etc).");
    	System.out.println("Or type [0] to return to the previous menu.");
    	String userChoice = scanner.nextLine();
    	Transfer chosenTransfer = new Transfer();
    	int userInt = Integer.parseInt(userChoice);
    	if (userInt == 0) {
    		
    	} else if (userInt <= myTransfers.length + 1 && userInt > 0) {
    		transferDetails(myTransfers[userInt - 1]);
    	}
    	
    	return myTransfers;
    }
    
    public void transferDetails(Transfer transfer) {
    	System.out.println("----------------------------");
    	System.out.println("---   Transfer Details   ---");
    	System.out.println("----------------------------");
    	System.out.println("ID: " + transfer.getTransferId());
    	System.out.println("From: " + getUsernameFromAccountId(transfer.getAccountFrom()));
    	System.out.println("To: " + getUsernameFromAccountId(transfer.getAccountTo()));
    	System.out.print("Type: ");
    	if (2 == transfer.getTransferType()) {
    		System.out.println("Send");
    	} else if (1 == transfer.getTransferType()) {
    		System.out.println("Request");
    	}
    	System.out.print("Status: ");
    	if (1 == transfer.getTransferStatus()) {
    		System.out.println("Pending");
    	} else if (2 == transfer.getTransferStatus()) {
    		System.out.println("Approved");
    	} else if (3 == transfer.getTransferStatus()) {
    		System.out.println("Rejected");
    	}
    	System.out.println("Amount: $" + transfer.getAmount());
    }
    
//    public void allTransferDetails(Transfer transfer) {
//    	restTemplate.exchange(BASE_URL + "transfers/{transferId}", HttpMethod.GET, makeTransferEntity(transfer), Transfer.class).getBody();
//    }	
	
    private HttpEntity<User> makeUserEntity(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        return entity;
    }
	
    private HttpEntity<AuthenticatedUser> makeAuthEntity(AuthenticatedUser user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity<AuthenticatedUser> entity = new HttpEntity<>(headers);
        return entity;
    }
    
    private HttpEntity<Account> makeAccountEntity(Account account) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<Account> entity = new HttpEntity<>(account, headers);
    	return entity;
    }
    
    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
    	return entity;
    }

}
