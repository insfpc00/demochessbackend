package es.fporto.demo.minichess.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import es.fporto.demo.minichess.model.Piece;
import es.fporto.demo.minichess.model.PieceId;

import org.springframework.data.repository.PagingAndSortingRepository;

@RepositoryRestResource
public interface PieceRepository extends PagingAndSortingRepository<Piece,PieceId>{

}
