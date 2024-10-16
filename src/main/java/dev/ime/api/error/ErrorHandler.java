package dev.ime.api.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.application.dto.ErrorResponse;
import dev.ime.application.exception.BasicException;
import dev.ime.application.exception.CreateJpaEntityException;
import dev.ime.application.exception.EmailUsedException;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.ValidationException;
import dev.ime.application.utils.LoggerUtil;
import dev.ime.config.GlobalConstants;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class ErrorHandler{

	private final Map<Class<? extends Throwable>, Function<Throwable, Mono<ServerResponse>>> exceptionHandlers;	
	private final LoggerUtil loggerUtil;

    public ErrorHandler(LoggerUtil loggerUtil) {
        this.exceptionHandlers = initializeExceptionHandlers();
		this.loggerUtil = loggerUtil;
    }
    
	private Map<Class<? extends Throwable>, Function<Throwable, Mono<ServerResponse>>> initializeExceptionHandlers() {
		
		return Map.of(
				IllegalArgumentException.class, this::handleIllegalArgumentException,
				ValidationException.class, this::handleBasicExceptionExtendedClasses,
				EmptyResponseException.class, this::handleBasicExceptionExtendedClasses,
				CreateJpaEntityException.class, this::handleBasicExceptionExtendedClasses,
				EmailUsedException.class, this::handleBasicExceptionExtendedClasses
				);		
	 }	 
	
    public Mono<ServerResponse> handleException(Throwable error) {
        
        return exceptionHandlers
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().isInstance(error))
                .findFirst()
                .map(entry -> entry.getValue().apply(error))
                .orElseGet( () -> handleGenericException(error) );
        
    }
    
    private Mono<ServerResponse> handleGenericException(Throwable error) {
    	
        return ServerResponse
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                		new ErrorResponse(
	                		UUID.randomUUID(),
	                		error.getClass().getSimpleName(),
	                		GlobalConstants.EX_PLAIN_DESC, 
	                		Map.of(GlobalConstants.EX_PLAIN, error.getMessage())
                		))
		        .doOnNext(item -> loggerUtil.logInfoAction(this.getClass().getSimpleName(),GlobalConstants.MSG_EVENT_ERROR, error.toString()));
    
    }

	public Mono<ServerResponse> handleIllegalArgumentException(Throwable error) {
		
		return ServerResponse
				.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
				.bodyValue(
		        		new ErrorResponse(
		                		UUID.randomUUID(),
		                		GlobalConstants.EX_ILLEGALARGUMENT,
		                		GlobalConstants.EX_ILLEGALARGUMENT_DESC, 
		                		Map.of(GlobalConstants.EX_PLAIN, error.getMessage())
		            		))		
		        .doOnNext(item -> loggerUtil.logInfoAction(this.getClass().getSimpleName(),GlobalConstants.MSG_EVENT_ERROR, error.toString()));

	}
	
	private Mono<ServerResponse> handleBasicExceptionExtendedClasses(Throwable error) {
		
		BasicException ex = (BasicException)error;
		
		return createServerResponse(ex);
	}

	private Mono<ServerResponse> createServerResponse(BasicException error) {
		
		return ServerResponse
				.status(HttpStatus.I_AM_A_TEAPOT)
                .contentType(MediaType.APPLICATION_JSON)
				.bodyValue(
		        		new ErrorResponse(
		        				error.getIdentifier(),
		        				error.getName(),
		        				error.getDescription(), 
		        				error.getErrors()
		            		))
		        .doOnNext(item -> loggerUtil.logInfoAction(this.getClass().getSimpleName(),GlobalConstants.MSG_EVENT_ERROR, error.toString()));

	}
    
	
}
