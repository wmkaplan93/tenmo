package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.util.List;
import java.util.Map;

public interface UserDAO {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);
    
    void printAll(List<User> findAll);

	Map<Long, User> findAllMap();
}
