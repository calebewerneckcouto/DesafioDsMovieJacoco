package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

	@Mock
	private ScoreRepository scoreRepository;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private UserService userService;

	private long existingMovieId, nonExistingMovieId;

	private MovieEntity movieEntity;
	private MovieDTO movieDTO;
	private UserEntity userEntity;

	private ScoreEntity scoreEntity;
	private ScoreDTO scoreDTO;
	@BeforeEach
	void setUp() throws Exception {
		existingMovieId = 1L;
		nonExistingMovieId = 2L;

		movieEntity = MovieFactory.createMovieEntity();
		movieDTO = MovieFactory.createMovieDTO();
		userEntity = UserFactory.createUserEntity();
		scoreEntity = ScoreFactory.createScoreEntity();
		scoreDTO = ScoreFactory.createScoreDTO();

		Mockito.when(userService.authenticated()).thenReturn(userEntity);

		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movieEntity));
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

		Mockito.when(scoreRepository.saveAndFlush(any())).thenReturn(scoreEntity);
		Mockito.when(scoreRepository.save(any())).thenReturn(movieEntity);
		Mockito.when(movieRepository.save(any())).thenReturn(movieEntity);

	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		movieEntity.getScores().add(scoreEntity);
		MovieDTO result = service.saveScore(scoreDTO);

		double sum = 0.0;
		for (ScoreEntity s : movieEntity.getScores()) {
			sum = sum + s.getValue();
		}
		double avg = sum / movieEntity.getScores().size();
		movieEntity.setScore(avg);
		movieEntity.setCount(movieEntity.getScores().size());

		Assertions.assertNotNull(result);
		Assertions.assertEquals(movieEntity.getId(), result.getId());
		Assertions.assertEquals(movieEntity.getScore(), result.getScore());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		ScoreDTO dto = new ScoreDTO(nonExistingMovieId, 4.0);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(dto);
		});
	}
}