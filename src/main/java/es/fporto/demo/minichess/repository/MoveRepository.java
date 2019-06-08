package es.fporto.demo.minichess.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import es.fporto.demo.minichess.model.Move;

@RepositoryRestResource
public interface MoveRepository extends PagingAndSortingRepository<Move,Long>{
	
}
