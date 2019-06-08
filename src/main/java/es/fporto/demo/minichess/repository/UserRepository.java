package es.fporto.demo.minichess.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import es.fporto.demo.minichess.user.model.User;

@RepositoryRestResource

public interface UserRepository extends PagingAndSortingRepository<User,String>{
	
	public User findByUsername(String name);
	
	@Query("SELECT u FROM User u WHERE TYPE(u) IN (UCIEngine)")
	
	public List<User> findAllEngines();
}
