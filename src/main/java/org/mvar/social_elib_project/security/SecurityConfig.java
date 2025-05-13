package org.mvar.social_elib_project.security;

import lombok.RequiredArgsConstructor;
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
            "/error",
            "/catalog/items",
            "/catalog/{itemId}",
            "/catalog/category/{categoryName}",
            "/catalog/{itemId}/comments",
            "/images/{id}",
            "/catalog/items/{username}",
            "/users/{username}",
            "/users/{username}/favourites",
            "/users/{username}/expert-accomplishments"
    };

    private static final String[] EXPERTLIST_URLS = {
            "/catalog/{itemId}/add_expert_comment",
            "/catalog/{itemId}/expert_comments/**",
            "/users/{username}/add-expert-accomplishments"
    };

    private static final String[] AUTHORIZEDLIST_URLS = {
            "/catalog/{itemId}/comments/add_comment",
            "/catalog/add_item",
            "/catalog/delete_item",
            "/catalog/{itemId}/check_update_permission",
            "/catalog/{itemId}/update_item",
            "/catalog/{itemId}/vote",
            "/catalog/{itemId}/unvote",
            "/users/me"
    };

    private static final String[] ADMINLIST_URLS = {
            "/token/purge",
            "/admin/**"
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
                                .requestMatchers(HttpMethod.OPTIONS, "/**")
                                .permitAll()
                                .requestMatchers("/ws/**")
                                .permitAll()
                                .requestMatchers("/api/**")
                                .permitAll()
                                .requestMatchers(AUTHORIZEDLIST_URLS)
                                .hasAnyAuthority(USER.name(), EXPERT.name(), ADMIN.name())
                                .requestMatchers(HttpMethod.GET, AUTHORIZEDLIST_URLS)
                                .hasAnyAuthority(USER.name(), EXPERT.name(), ADMIN.name())
                                .requestMatchers(HttpMethod.POST, AUTHORIZEDLIST_URLS)
                                .hasAnyAuthority(USER.name(), EXPERT.name(), ADMIN.name())
                                .requestMatchers(HttpMethod.PATCH, AUTHORIZEDLIST_URLS)
                                .hasAnyAuthority(USER.name(), EXPERT.name(), ADMIN.name())
                                .requestMatchers(HttpMethod.POST, ADMINLIST_URLS)
                                .hasAnyAuthority(ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, ADMINLIST_URLS)
                                .hasAnyAuthority(ADMIN.name())
                                .requestMatchers(HttpMethod.PATCH, ADMINLIST_URLS)
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
