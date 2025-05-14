package dev.anandbose.todoapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class TodoSecurityConfig {

	@Bean
	public SecurityFilterChain appSecurity(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.httpBasic(Customizer.withDefaults()).authorizeHttpRequests(http -> {
			http.anyRequest().authenticated();
		}).csrf(csrf -> csrf.disable()).build();
	}

	@Bean
	public UserDetailsManager userDetailsManager(PasswordEncoder passwordEncoder) {
		UserDetails anand = User.withUsername("anand").password(passwordEncoder.encode("abc123")).build();
		UserDetails meera = User.withUsername("meera").password(passwordEncoder.encode("xyz1234")).build();
		return new InMemoryUserDetailsManager(anand, meera);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
