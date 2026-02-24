package com.hp.springsecurity.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .csrf(csrfConfig -> csrfConfig.disable())
//                .sessionManagement(sessionConfig ->
//                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                                .requestMatchers("/public/**", "/auth/**").permitAll()
////                        .requestMatchers("/admin/**").hasRole("ADMIN")
////                        .requestMatchers("/doctors/**").hasAnyRole("DOCTOR", "ADMIN")
//                                .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
////                .formLogin(Customizer.withDefaults());
//        return httpSecurity.build();
//    }


@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
            .csrf(csrfConfig -> csrfConfig.disable())
            .sessionManagement(sessionConfig ->
                    sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/public/**", "/auth/**").permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/doctors/**").hasAnyRole("DOCTOR", "ADMIN")
                            .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(oAuth2 -> oAuth2.failureHandler(
                    ((request, response, exception) -> {
                        System.out.println("OAuth2 error: {}"+ exception.getMessage());
                    })
            ));
//                .formLogin(Customizer.withDefaults());
    return httpSecurity.build();
}


}
