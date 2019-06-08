package es.fporto.demo.minichess.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import es.fporto.demo.minichess.user.model.Avatar;

@RepositoryRestResource
public interface AvatarRepository extends PagingAndSortingRepository<Avatar,Long>{

}
