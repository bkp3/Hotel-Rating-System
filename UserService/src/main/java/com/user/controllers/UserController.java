package com.user.controllers;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.entities.User;
import com.user.services.UserService;

import ch.qos.logback.classic.Logger;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	private Logger logger = (Logger) LoggerFactory.getLogger(UserController.class);

	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User u = userService.saveUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(u);
	}

	int retryCount = 1;

	@GetMapping("/{userId}")
	// @CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod =
	// "ratingHotelFallback")
	@Retry(name = "ratingHotelService", fallbackMethod = "ratingHotelFallback")
	public ResponseEntity<User> getSingleUser(@PathVariable String userId) {
		logger.info("Retry count:{}", retryCount);
		retryCount++;
		User user = userService.getUser(userId);
		return ResponseEntity.ok(user);
	}

	// creating fallback method for circuit breaker
	public ResponseEntity<User> ratingHotelFallback(String userId, Exception ex) {
		// logger.info("Fallback is executed because service is down", ex.getMessage());

		User user = User.builder().email("dummmy@gmail.com").name("dummy")
				.about("this user is created dummy because some service is down").userId("123421").build();
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<User>> getAllUser() {
		List<User> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}

}
