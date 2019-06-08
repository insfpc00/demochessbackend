package es.fporto.demo.minichess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import es.fporto.demo.minichess.model.Puzzle;

@RepositoryRestResource
public interface PuzzleRepository extends JpaRepository<Puzzle, String> {

}
