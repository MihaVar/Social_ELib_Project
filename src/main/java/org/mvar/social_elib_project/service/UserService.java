package org.mvar.social_elib_project.service;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElse(null);
    }

    public void deleteCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        if (!user.getUsername().equals(username)) {
            throw new SecurityException("Attempt to delete a different user account");
        }
        userRepository.delete(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
