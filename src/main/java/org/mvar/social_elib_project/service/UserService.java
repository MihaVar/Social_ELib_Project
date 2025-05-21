package org.mvar.social_elib_project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.*;
import org.mvar.social_elib_project.payload.request.user.ExpertAccomplishmentRequest;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public void deleteCurrentUser() {
        User user = authService.getAuthenticatedUser();
        userRepository.delete(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getCurrentUser() {
        User user = authService.getAuthenticatedUser();
        return userRepository.findUserByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user.getEmail()));
    }


    @Transactional
    public User changeUsername(String newUsername) {
        User currentUser = authService.getAuthenticatedUser();
        if (userRepository.findUserByUsersname(newUsername).isPresent() &&
                !userRepository.findUserByUsersname(newUsername).get().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Username already exists: " + newUsername);
        }
        currentUser.setUsersname(newUsername);
        return userRepository.save(currentUser);
    }

    public Role getUserRole() {
        User user = authService.getAuthenticatedUser();
        return user.getRole();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findUserByUsersname(username);
    }

    public User favourItem(Long itemId) {
        User user = authService.getAuthenticatedUser();
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
        User user = authService.getAuthenticatedUser();
        if (!user.getFavouredItems().contains(itemId)) {
            throw new IllegalStateException("User did not favour this item!");
        }
        user.getFavouredItems().remove(itemId);
        return userRepository.save(user);
    }

    public boolean checkFavoriteStatus(Long itemId) {
        User user = authService.getAuthenticatedUser();
        return user.getFavouredItems().contains(itemId);
    }

    public User addExpertAccomplishment(ExpertAccomplishmentRequest request) {
        User user = authService.getAuthenticatedUser();
        authService.checkExpertRole();
        if(user.getExpertAccomplishments() == null) {
            user.setExpertAccomplishments(new HashSet<>());
        }
        user.getExpertAccomplishments().add(request.accomplishment());
        return userRepository.save(user);
    }

    public User removeExpertAccomplishment(ExpertAccomplishmentRequest request) {
        User user = authService.getAuthenticatedUser();
        authService.checkExpertRole();
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
