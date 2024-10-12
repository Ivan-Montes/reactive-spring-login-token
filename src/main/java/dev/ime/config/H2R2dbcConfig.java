package dev.ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;

import io.r2dbc.spi.ConnectionFactory;

@Configuration
public class H2R2dbcConfig  {

	  @Bean
	  ConnectionFactory connectionFactory(H2R2dbcProperties h2R2dbcProperties) {		  
		  
		  return ConnectionFactoryBuilder.withUrl(h2R2dbcProperties.getUrl()).build();
	
	  }
	  
	  @Bean
	  ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
		  
		  ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		  initializer.setConnectionFactory(connectionFactory);
		
		  CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
		  populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
		  populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("data.sql")));
		  initializer.setDatabasePopulator(populator);
		
		  return initializer;
		  
	  }
	
}
