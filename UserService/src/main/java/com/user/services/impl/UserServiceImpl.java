package com.user.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.user.entities.Rating;
import com.user.entities.User;
import com.user.exceptions.ResourceNotFoundException;
import com.user.repository.UserRepository;
import com.user.services.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestTemplate restTemplate;

	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public User saveUser(User user) {
		String randomUserId = UUID.randomUUID().toString();
		user.setUserId(randomUserId);
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return userRepository.findAll();
	}

	@Override
	public User getUser(String userId) {
		User user = userRepository.findById(userId).orElseThrow(
				() -> new ResourceNotFoundException("User with ID: " + userId + " is not found on server!!"));

		// fetching ratings of the above user from RATING-SERVICE
		ArrayList<Rating> ratingOfUser = restTemplate
				.getForObject("http://localhost:8083/ratings/users/" + user.getUserId(), ArrayList.class);

		logger.info("{} ", ratingOfUser);
		user.setRatings(ratingOfUser);
		return user;
	}

}
