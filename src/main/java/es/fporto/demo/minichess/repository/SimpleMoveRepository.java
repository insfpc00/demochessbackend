package es.fporto.demo.minichess.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import es.fporto.demo.minichess.model.SimpleMove;

@RepositoryRestResource
public interface SimpleMoveRepository extends PagingAndSortingRepository<SimpleMove,Long>{

}
