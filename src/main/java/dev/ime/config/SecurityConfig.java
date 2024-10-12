package dev.ime.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    public SecurityConfig(ReactiveAuthenticationManager reactiveAuthenticationManager) {
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
    }
    
    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/token", "/login", "/register").permitAll()
                .pathMatchers("/h2-console/**").permitAll()
                .pathMatchers("/api/public/**").permitAll()
                .pathMatchers("/v3/api-docs/**","/swagger-ui.html","/webjars/swagger-ui/**").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
            )
            .headers(headers -> headers
        			.frameOptions(frameOptions -> frameOptions
        				.mode(Mode.SAMEORIGIN)
        			)
        		)
            .csrf(CsrfSpec::disable)
            .authenticationManager(reactiveAuthenticationManager)
			.oauth2ResourceServer( resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
			.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .build();
    }
    
}
