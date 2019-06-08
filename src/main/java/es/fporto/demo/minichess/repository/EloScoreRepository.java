package es.fporto.demo.minichess.repository;

import java.util.Calendar;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import es.fporto.demo.minichess.user.model.EloScore;

@RepositoryRestResource
public interface EloScoreRepository extends PagingAndSortingRepository<EloScore, Long> {
	
	@Query("SELECT e from User u INNER JOIN u.blitzEloScores e " + "WHERE u.username=:username " + "AND e.date<=:until "
			+ "AND e.date >= (select max(e2.date) FROM User u2 INNER JOIN u2.blitzEloScores e2 WHERE e2.date<=:from) ORDER by e.date ASC")

	public List<EloScore> findBlitzEloRatings(@Param("username") String username, @Param("from") Calendar from,
			@Param("until") Calendar until);

	@Query("SELECT e from User u INNER JOIN u.bulletEloScores e " + "WHERE u.username=:username " + "AND e.date<=:until "
			+ "AND e.date >= (select max(e2.date) FROM User u2 INNER JOIN u2.bulletEloScores e2 WHERE e2.date<=:from) ORDER by e.date ASC")

	public List<EloScore> findBulletEloRatings(@Param("username") String username, @Param("from") Calendar from,
			@Param("until") Calendar until);

	@Query("SELECT e from User u INNER JOIN u.rapidEloScores e " + "WHERE u.username=:username " + "AND e.date<=:until "
			+ "AND e.date >= (select max(e2.date) FROM User u2 INNER JOIN u2.rapidEloScores e2 WHERE e2.date<=:from) ORDER by e.date ASC")

	public List<EloScore> findRapidEloRatings(@Param("username") String username, @Param("from") Calendar from,
			@Param("until") Calendar until);

}
