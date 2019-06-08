package es.fporto.demo.minichess.repository;

import java.util.Calendar;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import es.fporto.demo.minichess.model.Match;
import es.fporto.demo.minichess.model.MatchStats;

@RepositoryRestResource
public interface MatchRepository extends JpaRepository<Match, Long>,JpaSpecificationExecutor<Match>  {

	@Query("SELECT m from Match m "
			+ "WHERE m.blackPiecesUser.username=:username "
			+ "OR m.whitePiecesUser.username=:username")
	
	public List<Match> findByUser(@Param("username") String username);
	

	@Query("SELECT"
			+ " new es.fporto.demo.minichess.model.MatchStats("
			+ " SUM(CASE WHEN m.state=es.fporto.demo.minichess.model.MatchState.WHITE_WIN AND m.whitePiecesUser.username=:username THEN 1 ELSE 0 END),"
			+ " SUM(CASE WHEN m.state=es.fporto.demo.minichess.model.MatchState.BLACK_WIN AND m.blackPiecesUser.username=:username THEN 1 ELSE 0 END),"
			+ " SUM(CASE WHEN m.state=es.fporto.demo.minichess.model.MatchState.DRAW THEN 1 ELSE 0 END),"
			+ " COUNT(m), m.timeInSeconds) FROM Match m "
			+ " WHERE m.creationDate <=:until AND m.creationDate>=:since "
			+ " AND (m.blackPiecesUser.username=:username OR m.whitePiecesUser.username=:username)"
			+ " AND m.state IN (es.fporto.demo.minichess.model.MatchState.WHITE_WIN,es.fporto.demo.minichess.model.MatchState.BLACK_WIN,es.fporto.demo.minichess.model.MatchState.DRAW)"
			+ " GROUP by m.timeInSeconds")
			
	
	public List<MatchStats> getStatsByUser(@Param("username") String username, @Param("since") Calendar since, @Param("until") Calendar until);
	
	@Query("SELECT"
			+ " new es.fporto.demo.minichess.model.MatchStats("
			+ " SUM(CASE WHEN m.state=es.fporto.demo.minichess.model.MatchState.WHITE_WIN AND m.whitePiecesUser.username=:username THEN 1 ELSE 0 END),"
			+ " SUM(CASE WHEN m.state=es.fporto.demo.minichess.model.MatchState.BLACK_WIN AND m.blackPiecesUser.username=:username THEN 1 ELSE 0 END),"
			+ " SUM(CASE WHEN m.state=es.fporto.demo.minichess.model.MatchState.DRAW THEN 1 ELSE 0 END),"
			+ " COUNT(m), m.timeInSeconds) FROM Match m "
			+ " WHERE m.state IN (es.fporto.demo.minichess.model.MatchState.WHITE_WIN,es.fporto.demo.minichess.model.MatchState.BLACK_WIN,es.fporto.demo.minichess.model.MatchState.DRAW)"
			+ " AND (m.blackPiecesUser.username=:username OR m.whitePiecesUser.username=:username)"
			+ " GROUP by m.timeInSeconds")
	public List<MatchStats> getOverallStatsByUser(@Param("username") String username);


}
