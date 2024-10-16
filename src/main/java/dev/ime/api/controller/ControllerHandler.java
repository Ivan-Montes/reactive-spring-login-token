package dev.ime.api.controller;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.api.error.ErrorHandler;
import dev.ime.api.validation.DtoValidator;
import dev.ime.application.dto.AuthResponseDto;
import dev.ime.application.dto.LoginRequestDto;
import dev.ime.application.dto.RegisterRequestDto;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.service.AuthService;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.ports.inbound.ControllerHandlerPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@Component
@Tag(name = "ms-oauth2r", description = "API for authentication and request tokens")
public class ControllerHandler implements ControllerHandlerPort {
	
	private final DtoValidator dtoValidator;
	private final ErrorHandler errorHandler;
	private final AuthService authService;	
	
	public ControllerHandler(DtoValidator dtoValidator, ErrorHandler errorHandler, AuthService authService) {
		super();
		this.dtoValidator = dtoValidator;
		this.errorHandler = errorHandler;
		this.authService = authService;
	}

	@Operation(
            summary = "Login",
            description = "Login",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login information",
                    required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = LoginRequestDto.class)
                    )
                )        
            )
    @ApiResponse(
        responseCode = "200", 
        description = "Return JWT Token",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = AuthResponseDto.class)
        )
    )
	public Mono<ServerResponse> login(ServerRequest serverRequest) {
		
		return serverRequest.bodyToMono(LoginRequestDto.class)
				.flatMap(dtoValidator::validateDto)
				.flatMap(authService::login)
				.flatMap( authResponse -> ServerResponse.ok().bodyValue(authResponse))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))
				.onErrorResume(errorHandler::handleException);
		
	}

	@Operation(
            summary = "Register",
            description = "Register",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Register information",
                    required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = RegisterRequestDto.class)
                    )
                )        
            )
    @ApiResponse(
        responseCode = "200", 
        description = "Return JWT Token",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = AuthResponseDto.class)
        )
    )
	public Mono<ServerResponse> register(ServerRequest serverRequest) {
		
		return serverRequest.bodyToMono(RegisterRequestDto.class)
				.flatMap(dtoValidator::validateDto)
				.flatMap(authService::register)
				.flatMap( authResponse -> ServerResponse.ok().bodyValue(authResponse))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))
				.onErrorResume(errorHandler::handleException);
		
	}
	@Operation(
            summary = "Request Token",
            description = "Request Token"
			)
    @ApiResponse(
        responseCode = "200", 
        description = "Return JWT Token",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = AuthResponseDto.class)
        )
    )
	public Mono<ServerResponse> requestToken(ServerRequest serverRequest) {
		
		return authService.requestToken()
				.flatMap( authResponse -> ServerResponse.ok().bodyValue(authResponse))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))
				.onErrorResume(errorHandler::handleException);
		
	}

}
