package org.mvar.social_elib_project.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Role;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.auth.AuthRequest;
import org.mvar.social_elib_project.payload.request.auth.RegisterRequest;
import org.mvar.social_elib_project.payload.response.AuthResponse;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthResponse register(RegisterRequest registerRequest) {
        if(userRepository.findUserByUsersname(registerRequest.username()).isPresent()) {
            throw new IllegalArgumentException("Username is already in use");
        }
        if(userRepository.findUserByEmail(registerRequest.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }
        User user = User.builder()
                .email(registerRequest.email())
                .usersname(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .isActive(true)
                .role(Role.USER)
                .favouredItems(new HashSet<>())
                .expertAccomplishments(new HashSet<>())
                .userRating(0)
                .build();
        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        tokenService.createToken(jwtToken, jwtService.extractClaim(jwtToken, Claims::getExpiration));

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        User user = userRepository.findUserByEmail(authRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException(authRequest.email()));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.email(),
                        authRequest.password()
                )
        );

        String jwtToken = jwtService.generateToken(user);
        tokenService.createToken(jwtToken, jwtService.extractClaim(jwtToken, Claims::getExpiration));

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void logout(String token) {
        tokenService.invalidateToken(token);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
    }

    public boolean checkExpertRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isExpert = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Role.EXPERT.name()));
        if (!isExpert) {
            throw new IllegalStateException("User does not have permission to add expert comment");
        }
        return isExpert;
    }
}
