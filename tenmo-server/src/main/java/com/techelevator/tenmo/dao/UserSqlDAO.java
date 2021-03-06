package com.techelevator.tenmo.dao;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.model.User;

@Service
public class UserSqlDAO implements UserDAO {

    private static final double STARTING_BALANCE = 1000;
    private JdbcTemplate jdbcTemplate;

    public UserSqlDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        return jdbcTemplate.queryForObject("select user_id from users where username = ?", int.class, username);
    }
    
    @Override
    public String usernameFromAccountId(long accountId) {
    	return jdbcTemplate.queryForObject("SELECT username FROM users u "
    			+ "JOIN accounts a ON a.user_id = u.user_id "
    			+ "WHERE a.account_id = ?", String.class, accountId);
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        Map<Long, String> userMap = new HashMap<Long, String>();
        String sql = "select * from users";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
            userMap.put(user.getId(), user.getUsername());   
        }
        return users;
    }
    
    @Override
    public Map<Long, User> findAllMap() {
    	Map<Long, User> userMap = new HashMap<Long, User>();
        String sql = "select * from users";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            userMap.put(user.getId(), user);   
        }
        return userMap;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        for (User user : this.findAll()) {
            if( user.getUsername().toLowerCase().equals(username.toLowerCase())) {
                return user;
            }
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public boolean create(String username, String password) {
        boolean userCreated = false;
        boolean accountCreated = false;

        // create user
        String insertUser = "insert into users (username,password_hash) values(?,?)";
        String password_hash = new BCryptPasswordEncoder().encode(password);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String id_column = "user_id";
        userCreated = jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(insertUser, new String[]{id_column});
                    ps.setString(1, username);
                    ps.setString(2,password_hash);
                    return ps;
                }
                , keyHolder) == 1;
        int newUserId = (int) keyHolder.getKeys().get(id_column);

        // create account
        String insertAccount = "insert into accounts (user_id,balance) values(?,?)";
        accountCreated = jdbcTemplate.update(insertAccount,newUserId,STARTING_BALANCE) == 1;

        return userCreated && accountCreated;
    }
    //HEY LOOK AT THIS
//    public Account minusBucks(AuthenticatedUser user, double less) {
//    	Account account = new Account();
//    	try {
//        	account.setUserId((long)user.getUser().getId());
//			account.setBalance(getUserBalance(user) - less);
//			account.setAccountId(getAccountId(user));
//			
//    		restTemplate.exchange(BASE_URL + user.getUser().getUsername() + "/account", HttpMethod.PUT, makeAccountEntity(account), Account.class);
//
//		} catch (UserServiceException e) {
//			e.getMessage();
//		}
//    	
//    	return account;
//    }
    /*
     *     public Location update(Location location, int id) throws LocationNotFoundException {
        Location result = location;
        boolean finished = false;

        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getId() == id) {
                if( result.getId() == 0 ) {
                    result.setId(id);
                }
                locations.set(i, result);
                finished = true;
                break;
            }
        }
        if (!finished) {
            throw new LocationNotFoundException();
        }

        return result;
    }
     */
//    public Account update(Account account, double less)
    

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("ROLE_USER");
        return user;
    }

	@Override
	public void printAll(List<User> findAll) {
		// TODO Auto-generated method stub
		
	}
}
