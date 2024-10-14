package dev.ime.application.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.AuthResponseDto;
import dev.ime.application.dto.LoginRequestDto;
import dev.ime.application.dto.RegisterRequestDto;
import dev.ime.application.exception.CreateJpaEntityException;
import dev.ime.application.exception.EmailUsedException;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.utils.JwtUtils;
import dev.ime.application.utils.UserGenerator;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.model.Role;
import dev.ime.domain.model.User;
import dev.ime.domain.ports.inbound.ServicePort;
import dev.ime.domain.ports.outbound.UserRepositoryPort;
import reactor.core.publisher.Mono;

@Service
public class AuthService implements ServicePort<LoginRequestDto, RegisterRequestDto>{

	private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
	private final ReactiveAuthenticationManager reactiveAuthenticationManager;
	private final UserGenerator userGenerator;
	private final UserRepositoryPort userRepositoryPort;

	public AuthService(JwtUtils jwtUtils, PasswordEncoder passwordEncoder,
			ReactiveAuthenticationManager reactiveAuthenticationManager, UserGenerator userGenerator,
			UserRepositoryPort userRepositoryPort) {
		super();
		this.jwtUtils = jwtUtils;
		this.passwordEncoder = passwordEncoder;
		this.reactiveAuthenticationManager = reactiveAuthenticationManager;
		this.userGenerator = userGenerator;
		this.userRepositoryPort = userRepositoryPort;
	}

	public Mono<AuthResponseDto> login(LoginRequestDto loginRequestDto) {
		
		return Mono.justOrEmpty(loginRequestDto)
				.flatMap(this::authenticateUser)
				.flatMap(this::returnOptAuthResponse);

	}
	
	private Mono<User> authenticateUser(LoginRequestDto loginRequestDto){
		
		return Mono.justOrEmpty(loginRequestDto)
				.map( login -> new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password()))
				.flatMap(reactiveAuthenticationManager::authenticate)
				.map(Authentication::getPrincipal)
				.cast(User.class)
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.MSG_FLOW_ERROR, GlobalConstants.MSG_NODATA))));
		
	}
	
	public Mono<AuthResponseDto> register(RegisterRequestDto registerRequestDto) {
		
		return Mono.justOrEmpty(registerRequestDto)
				.flatMap(this::isEmailFree)
				.filter( bool -> bool )
				.flatMap( bool -> this.createUser(registerRequestDto))
				.flatMap(this::saveUser)
				.flatMap(this::returnOptAuthResponse)
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.MSG_FLOW_ERROR, GlobalConstants.MSG_NODATA))));
				
	}
	
	private Mono<User> createUser(RegisterRequestDto registerRequestDto){
		
		return Mono.fromCallable( () -> {
			
			String name = registerRequestDto.name();
			String lastname = registerRequestDto.lastname();
			String email = registerRequestDto.email();
			String password = passwordEncoder.encode( registerRequestDto.password() );

			return User.builder()
					.id(UUID.randomUUID())
					.name(name)
					.lastname(lastname)
					.email(email)
					.password(password)
					.role(Role.USER)
					.build();
			
			
	    }).onErrorMap(e -> new CreateJpaEntityException(Map.of( GlobalConstants.USER_CAT, e.getMessage() )));
		
	}
	
	private Mono<User> saveUser(User user){	
		
		return userRepositoryPort.save(user);	
		
	}
	
	
	public Mono<AuthResponseDto> requestToken() {		
		
		return userGenerator.generateUser()
				.flatMap(this::returnOptAuthResponse);
		
	}

	public Mono<AuthResponseDto> returnOptAuthResponse(User user) {
		
		return Mono.justOrEmpty(user)
				.map(jwtUtils::generateToken)
				.map(AuthResponseDto::new)
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.MSG_FLOW_ERROR, GlobalConstants.MSG_NODATA))));
		
	}

	private Mono<Boolean> isEmailFree(RegisterRequestDto registerRequestDto) {
		
		return userRepositoryPort.findByEmail(registerRequestDto.email())
				.flatMap( user -> Mono.error(new EmailUsedException(Map.of(GlobalConstants.USER_EMAIL, registerRequestDto.email()))))
				.thenReturn(true);
		
	}
	
}
