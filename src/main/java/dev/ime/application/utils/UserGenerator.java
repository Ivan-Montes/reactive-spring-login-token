package dev.ime.application.utils;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateJpaEntityException;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.model.Role;
import dev.ime.domain.model.User;
import reactor.core.publisher.Mono;

@Component
public class UserGenerator {
	
    private final PasswordEncoder passwordEncoder;

	public UserGenerator(PasswordEncoder passwordEncoder) {
		super();
		this.passwordEncoder = passwordEncoder;
	}
	
	public Mono<User> generateUser() {
		
	    return Mono.fromCallable( () -> 
	        User.builder()
	            .name(generateString())
	            .lastname(generateString())
	            .email(generateEmail())
	            .password(passwordEncoder.encode(generateString()))
	            .role(Role.USER)
	            .build()
	    ).onErrorMap(e -> new CreateJpaEntityException(Map.of( GlobalConstants.USER_CAT, e.getMessage() )));

	    
	}

	private String generateString() {
		
		return UUID.randomUUID().toString().replace("-", "").trim();
		
	}
	
	private String generateEmail() {
		
		StringBuilder strBuilder = new StringBuilder(generateString());
		strBuilder.append("@");
		strBuilder.append(generateString());
		strBuilder.append(".com");
		return strBuilder.toString();
		
	}
	
}
