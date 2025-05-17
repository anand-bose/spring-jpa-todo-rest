package dev.anandbose.todoapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class TodoSecurityConfig {

	@Bean
	public SecurityFilterChain appSecurity(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.authorizeHttpRequests(http -> {
			http.requestMatchers(HttpMethod.GET, "/todo/**").hasAuthority("SCOPE_todo.read")
			.requestMatchers(HttpMethod.POST, "/todo/**").hasAuthority("SCOPE_todo.write")
			.requestMatchers(HttpMethod.PATCH, "/todo/**").hasAuthority("SCOPE_todo.write")
			.requestMatchers(HttpMethod.DELETE, "/todo/**").hasAuthority("SCOPE_todo.write");
		})
		.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
		.build();
	}

	// @Bean
	// public UserDetailsManager userDetailsManager(PasswordEncoder passwordEncoder) {
	// 	UserDetails anand = User.withUsername("anand").password(passwordEncoder.encode("abc123")).build();
	// 	UserDetails meera = User.withUsername("meera").password(passwordEncoder.encode("xyz1234")).build();
	// 	return new InMemoryUserDetailsManager(anand, meera);
	// }

	// @Bean
	// public PasswordEncoder passwordEncoder() {
	// 	return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	// }
}
