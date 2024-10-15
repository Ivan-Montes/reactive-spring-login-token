package dev.ime.api.controller;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class ControllerRouter {
	
	private static final RequestPredicate ACCEPT_JSON = RequestPredicates.accept(MediaType.APPLICATION_JSON);
	
	@RouterOperations({
		@RouterOperation(
				path = "/login", 
				beanClass = ControllerHandler.class, 
				beanMethod = "login",
		        method = RequestMethod.POST,
		        produces = MediaType.APPLICATION_JSON_VALUE),
	    @RouterOperation(
	    		path = "/register", 
	    		beanClass = ControllerHandler.class, 
	    		beanMethod = "register",
	            method = RequestMethod.POST,
	            produces = MediaType.APPLICATION_JSON_VALUE
	    		),
	    @RouterOperation(
	    		path = "/token", 
	    		beanClass = ControllerHandler.class, 
	    		beanMethod = "requestToken",
	            method = RequestMethod.GET,
	            produces = MediaType.APPLICATION_JSON_VALUE
	    		)
		})
	@Bean
	RouterFunction<ServerResponse> commandEndpointRoutes(ControllerHandler controllerHandler) {
	    return RouterFunctions.route(RequestPredicates.POST("/login").and(ACCEPT_JSON), controllerHandler::login)
	        .andRoute(RequestPredicates.POST("/register").and(ACCEPT_JSON), controllerHandler::register)
	        .andRoute(RequestPredicates.GET("/token"), controllerHandler::requestToken);
	
	}

}

