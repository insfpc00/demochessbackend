package es.fporto.demo.minichess.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import es.fporto.demo.minichess.user.model.Role;

@RepositoryRestResource

public interface RoleRepository extends PagingAndSortingRepository<Role,Long>{
	public Role findByName(String name);
}
