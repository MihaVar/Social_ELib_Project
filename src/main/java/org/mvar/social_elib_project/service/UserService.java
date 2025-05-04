package org.mvar.social_elib_project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Role;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void deleteCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
        if (!user.getEmail().equals(email)) {
            throw new SecurityException("Attempt to delete a different user account");
        }
        userRepository.delete(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName(); // або username, якщо токен містить username
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }


    @Transactional
    public User changeUsername(String newUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User currentUser = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        if (userRepository.findUserByUsersname(newUsername).isPresent() &&
                !userRepository.findUserByUsersname(newUsername).get().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Username already exists: " + newUsername);
        }
        currentUser.setUsersname(newUsername);
        return userRepository.save(currentUser);
    }

    public Role getUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
        return user.getRole();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findUserByUsersname(username);
    }
}
