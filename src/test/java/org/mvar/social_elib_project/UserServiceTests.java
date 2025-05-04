package org.mvar.social_elib_project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mvar.social_elib_project.model.Role;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mvar.social_elib_project.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    private void mockAuth() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@example.com", null, List.of())
        );
    }

    @Test
    void getCurrentUser_shouldReturnUser() {
        mockAuth();
        var user = new User();
        user.setEmail(email);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        var result = userService.getCurrentUser();
        assertEquals(email, result.getEmail());
    }

    @Test
    void deleteCurrentUser_shouldDeleteUser() {
        mockAuth();
        var user = new User();
        user.setEmail(email);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        userService.deleteCurrentUser();
        verify(userRepository).delete(user);
    }

    @Test
    void deleteCurrentUser_shouldThrowIfNotSameEmail() {
        mockAuth();
        var user = new User();
        user.setEmail("different@example.com");
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(SecurityException.class, () -> userService.deleteCurrentUser());
    }

    @Test
    void changeUsername_shouldUpdateSuccessfully() {
        mockAuth();
        var user = new User();
        user.setEmail(email);
        user.setUsersname("old");

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.findUserByUsersname("new")).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var updated = userService.changeUsername("new");
        assertEquals("new", updated.getUsersname());
    }

    @Test
    void changeUsername_shouldThrowIfAlreadyTakenByAnother() {
        mockAuth();
        var currentUser = new User();
        currentUser.setEmail(email);
        currentUser.setId(String.valueOf(1L));

        var existingUser = new User();
        existingUser.setUsersname("taken");
        existingUser.setId(String.valueOf(2L));

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(currentUser));
        when(userRepository.findUserByUsersname("taken")).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () -> userService.changeUsername("taken"));
    }

    @Test
    void getUserRole_shouldReturnRole() {
        mockAuth();
        var user = new User();
        user.setEmail(email);
        user.setRole(Role.EXPERT);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        var result = userService.getUserRole();
        assertEquals(Role.EXPERT, result);
    }

    @Test
    void getAllUsers_shouldReturnList() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);
        assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    void getUserByUsername_shouldReturnUser() {
        var user = new User();
        user.setUsersname("john");
        when(userRepository.findUserByUsersname("john")).thenReturn(Optional.of(user));
        var result = userService.getUserByUsername("john");
        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsersname());
    }
}
