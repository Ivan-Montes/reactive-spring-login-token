package dev.ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

import dev.ime.domain.ports.outbound.UserRepositoryPort;
import reactor.core.publisher.Mono;

@Configuration
public class AppConfig {

   @Bean
   ReactiveUserDetailsService reactiveUserDetailsService(UserRepositoryPort userRepositoryPort) {
       return username -> userRepositoryPort.findByEmail(username).cast(UserDetails.class)
               .switchIfEmpty(Mono.error(new UsernameNotFoundException(GlobalConstants.EX_USERNOTFOUND)));
   }

    @Bean
    PasswordEncoder passwordEncoder() {
    	
        return new BCryptPasswordEncoder();
        
    }
	
	@Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = 
            new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }
    
    @Bean
    AuthorizationServerSettings authorizationServerSettingsWithIssuer(AuthorizationServerProperties authorizationServerProperties) {
        return AuthorizationServerSettings
        		.builder()
                .issuer(authorizationServerProperties.getIssuer())
                .build();
    }

}
