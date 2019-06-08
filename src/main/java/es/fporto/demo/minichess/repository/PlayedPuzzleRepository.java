package es.fporto.demo.minichess.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import es.fporto.demo.minichess.model.PlayedPuzzle;
import es.fporto.demo.minichess.model.Puzzle;
import es.fporto.demo.minichess.user.model.User;

@RepositoryRestResource
public interface PlayedPuzzleRepository extends JpaRepository<PlayedPuzzle, Long> {

	public Optional<PlayedPuzzle> findByPlayerAndPuzzle(User player, Puzzle puzzle);
	public Optional<PlayedPuzzle> findByPlayerUsernameAndPuzzleLabel(String username, String label);
	public List<PlayedPuzzle> findByPlayerUsername(String username);
	
}
