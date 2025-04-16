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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthResponse register(RegisterRequest registerRequest) {
        User user = User.builder()
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .isActive(true)
                .role(Role.USER)
                .build();
        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        tokenService.createToken(jwtToken, jwtService.extractClaim(jwtToken, Claims::getExpiration));

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        User user = userRepository.findUserByUsername(authRequest.username())
                .orElseThrow(() -> new UsernameNotFoundException(authRequest.username()));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.username(),
                        authRequest.password()
                )
        );

        String jwtToken = jwtService.generateToken(user);
        tokenService.createToken(jwtToken, jwtService.extractClaim(jwtToken, Claims::getExpiration));

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
