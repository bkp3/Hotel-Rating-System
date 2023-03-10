package com.user.services;

import java.util.List;

import com.user.entities.User;

public interface UserService {

	// create
	User saveUser(User user);

	//// get All user
	List<User> getAllUsers();

	// get user by userId
	User getUser(String userId);
}
