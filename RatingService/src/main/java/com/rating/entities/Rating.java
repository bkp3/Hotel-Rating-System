package com.rating.entities;

import java.util.List;



import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "micro_ratings")
public class Rating {
	@Id
	private String ratingId;
	private int rating;
	private String feedback;
	private String userId;
	private String hotelId;
	
}
