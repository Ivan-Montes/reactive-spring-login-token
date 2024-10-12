package dev.ime.domain.ports.inbound;

import dev.ime.application.dto.AuthResponseDto;
import reactor.core.publisher.Mono;

public interface ServicePort <L,R>{

	Mono<AuthResponseDto> login(L loginRequest);
	Mono<AuthResponseDto> register(R registerRequest) ;
	Mono<AuthResponseDto> requestToken();
	
}
