package dev.ime.domain.ports.outbound;

import dev.ime.domain.model.User;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {

	Mono<User> save (User user);
	Mono<User> findByEmail(String email);
	
}
