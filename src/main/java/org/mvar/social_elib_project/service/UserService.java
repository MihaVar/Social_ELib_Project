package org.mvar.social_elib_project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.*;
import org.mvar.social_elib_project.payload.request.user.ExpertAccomplishmentRequest;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public User favourItem(Long itemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        if (user.getFavouredItems().contains(itemId)) {
            throw new IllegalStateException("User has already favoured this item");
        }
        if (user.getFavouredItems().isEmpty()) {
            user.setFavouredItems(new HashSet<>());
        }
        user.getFavouredItems().add(itemId);
        return userRepository.save(user);
    }

    public User unfavourItem(Long itemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        if (!user.getFavouredItems().contains(itemId)) {
            throw new IllegalStateException("User did not favour this item!");
        }
        user.getFavouredItems().remove(itemId);
        return userRepository.save(user);
    }

    public boolean checkFavoriteStatus(Long itemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        return user.getFavouredItems().contains(itemId);
    }

    public User addExpertAccomplishment(ExpertAccomplishmentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        boolean isExpert = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Role.EXPERT.name()));
        if (!isExpert) {
            throw new IllegalStateException("User does not have permission to add expert comment");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        if(user.getExpertAccomplishments() == null) {
            user.setExpertAccomplishments(new HashSet<>());
        }
        user.getExpertAccomplishments().add(request.accomplishment());
        return userRepository.save(user);
    }

    public User removeExpertAccomplishment(ExpertAccomplishmentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        boolean isExpert = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Role.EXPERT.name()));
        if (!isExpert) {
            throw new IllegalStateException("User does not have permission to add expert comment");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        user.getExpertAccomplishments().remove(request.accomplishment());
        return userRepository.save(user);
    }

    public Set<String> getExpertAccomplishments(String username) {
        User user = userRepository.findUserByUsersname(username)
                .orElseThrow(() -> new IllegalArgumentException("User with username not found: " + username));
        Set<String> expertAccomplishments = user.getExpertAccomplishments();
        System.out.println(expertAccomplishments);
        if (expertAccomplishments == null || expertAccomplishments.isEmpty()) {
            return Collections.emptySet();
        }
        System.out.println(expertAccomplishments);
        return user.getExpertAccomplishments();
    }
}
