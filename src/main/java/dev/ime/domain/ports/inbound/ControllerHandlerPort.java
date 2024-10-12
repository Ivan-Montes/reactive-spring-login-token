package dev.ime.domain.ports.inbound;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

public interface ControllerHandlerPort {

	Mono<ServerResponse>login(ServerRequest serverRequest);
	Mono<ServerResponse>register(ServerRequest serverRequest);
	Mono<ServerResponse>requestToken(ServerRequest serverRequest);
	
}
