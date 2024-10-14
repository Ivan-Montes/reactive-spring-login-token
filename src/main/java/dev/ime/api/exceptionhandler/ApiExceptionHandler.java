package dev.ime.api.exceptionhandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.ime.application.dto.ErrorResponse;
import dev.ime.application.utils.LoggerUtil;
import dev.ime.config.GlobalConstants;

@ControllerAdvice
public class ApiExceptionHandler {
	
    private final LoggerUtil loggerUtil;

	public ApiExceptionHandler(LoggerUtil loggerUtil) {
		super();
		this.loggerUtil = loggerUtil;
	}
	
	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex){

		loggerUtil.logSevereAction(GlobalConstants.EX_VALIDATION + " <==> " + ex.toString());
		
		Map<String, String> errors = new HashMap<>();
		    ex.getBindingResult().getAllErrors().forEach( error -> {
		        String fieldName = ((FieldError) error).getField();
		        String errorMessage = error.getDefaultMessage();
		        errors.put(fieldName, errorMessage);
		    });
		    
	    ErrorResponse response = new ErrorResponse(
	            UUID.randomUUID(),
	            GlobalConstants.EX_VALIDATION,
	            GlobalConstants.EX_VALIDATION_DESC,
	            Map.of(ex.getLocalizedMessage(), ex.getMessage())
	        );
	    
		return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
	}	

	@ExceptionHandler(Exception.class)		
	public ResponseEntity<ErrorResponse> lastExceptionStands(Exception ex){

		loggerUtil.logSevereAction(GlobalConstants.EX_PLAIN + " <==> " + ex.toString());
		
		ErrorResponse response = new ErrorResponse(
	            UUID.randomUUID(),
	            GlobalConstants.EX_PLAIN,
	            GlobalConstants.EX_PLAIN_DESC,
	            Map.of(ex.getLocalizedMessage(), ex.getMessage())
	        );
		   
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}	

}
