package com.user.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.user.entities.Hotel;
import com.user.entities.Rating;
import com.user.entities.User;
import com.user.exceptions.ResourceNotFoundException;
import com.user.external.services.HotelService;
import com.user.repository.UserRepository;
import com.user.services.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestTemplate restTemplate;

	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private HotelService hotelService;

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
		Rating[] ratingOfUser = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/" + user.getUserId(),
				Rating[].class);

		List<Rating> ratings = Arrays.stream(ratingOfUser).toList();

		logger.info("{} ", ratingOfUser);

		List<Rating> ratingList = ratings.stream().map(rating -> {
			// ResponseEntity<Hotel> forEntity =
			// restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/" +
			// rating.getHotelId(), Hotel.class);
			// Hotel hotel = forEntity.getBody();
			Hotel hotel = hotelService.getHotel(rating.getHotelId());
			// logger.info("response status code : {}", forEntity.getStatusCode());
			rating.setHotel(hotel);
			return rating;
		}).collect(Collectors.toList());
		user.setRatings(ratingList);
		return user;
	}

}
