package org.mvar.social_elib_project.security;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import static org.mvar.social_elib_project.model.Role.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] WHITELIST_URLS = {
            "/auth/**",
            "/error"
    };

    private static final String[] EXPERTLIST_URLS = {
            "/catalog/{id}/add_expert_comment",
            "/catalog/{id}/remove_expert_comment"
    };

    private static final String[] ADMINLIST_URLS = {
            "/token/purge"
    };
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                request.requestMatchers(WHITELIST_URLS)
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, ADMINLIST_URLS)
                        .hasAnyAuthority(ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, ADMINLIST_URLS)
                        .hasAnyAuthority(ADMIN.name())
                        .requestMatchers(HttpMethod.POST, EXPERTLIST_URLS)
                        .hasAnyAuthority(EXPERT.name())
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutUrl("auth/logout/")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                        .permitAll()
                );

        return httpSecurity.build();

    }
}
