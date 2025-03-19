package com.example.sport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
	        .csrf(csrf -> csrf.disable()) // Disabling CSRF allows GET logout (⚠️ risky)
	        .logout(logout -> logout
	            .logoutUrl("/logout") // Allows logout via GET
	            .logoutSuccessUrl("/login1") // Redirects after logout
	            .invalidateHttpSession(true) // Clears session
	            .deleteCookies("JSESSIONID") // Clears cookies
	            .permitAll()
	        );

	    return http.build();
	}

}
