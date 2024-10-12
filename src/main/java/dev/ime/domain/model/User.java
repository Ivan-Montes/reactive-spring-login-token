package dev.ime.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Table( name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User implements UserDetails {
	
	private static final long serialVersionUID = -4695165278124465518L;

	@Id
	@Column("id")
	private UUID id;
	
	@Column("name")
	private String name;
	
	@Column("lastname")
	private String lastname;
	
	@Column("email")
	private String email;
	
	@Column("password")
	@ToString.Exclude
	private String password;

	@Column("role")
	private Role role;
    
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return List.of(new SimpleGrantedAuthority((role.name()))); 
		
	}
	
	@Override
	public String getUsername() {
		
		return email;
		
	}
	
}
