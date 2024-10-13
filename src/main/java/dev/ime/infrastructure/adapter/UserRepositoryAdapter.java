package dev.ime.infrastructure.adapter;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import dev.ime.config.GlobalConstants;
import dev.ime.domain.model.User;
import dev.ime.domain.ports.outbound.UserRepositoryPort;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

	private final R2dbcEntityTemplate r2dbcTemplate;
	
	public UserRepositoryAdapter(R2dbcEntityTemplate r2dbcTemplate) {
		super();
		this.r2dbcTemplate = r2dbcTemplate;
	}

	@Override
	public Mono<User> save(User user) {
		
		return r2dbcTemplate.insert(user);	

	}

	@Override
	public Mono<User> findByEmail(String email) {
		
		return r2dbcTemplate.selectOne(
				Query.query(Criteria.where(GlobalConstants.USER_EMAIL).is(email)), 
				User.class);
		
	}

}
