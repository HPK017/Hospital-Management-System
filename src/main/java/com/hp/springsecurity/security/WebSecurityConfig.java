package com.hp.springsecurity.security;

import com.hp.springsecurity.type.PermissionType;
import com.hp.springsecurity.type.RoleType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static com.hp.springsecurity.type.PermissionType.*;
import static com.hp.springsecurity.type.RoleType.ADMIN;
import static com.hp.springsecurity.type.RoleType.DOCTOR;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;

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
                    .requestMatchers(HttpMethod.DELETE, "/admin/**")
                    .hasAnyAuthority(APPOINTMENT_DELETE.name(), USER_MANAGE.name())
                            .requestMatchers("/admin/**").hasRole(ADMIN.name())
                            .requestMatchers("/doctors/**").hasAnyRole(DOCTOR.name(), ADMIN.name())
                            .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(oAuth2 -> oAuth2.failureHandler(
                    (request, response, exception) -> {
                        System.out.println("OAuth2 error: {}"+ exception.getMessage());
                    })
                    .successHandler(oAuth2SuccessHandler)
            ).exceptionHandling(exceptionHandlingConfigurer ->
            exceptionHandlingConfigurer.accessDeniedHandler(new AccessDeniedHandler() {
                @Override
                public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                    handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
                }
            })
                    );
//                .formLogin(Customizer.withDefaults());
    return httpSecurity.build();
}


}
