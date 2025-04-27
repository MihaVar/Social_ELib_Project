package org.mvar.social_elib_project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.repository.UserRepository;
import org.mvar.social_elib_project.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void deleteCurrentUser_success() {
        User user = new User();
        user.setUsersname("testUser");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findUserByUsersname("testUser")).thenReturn(Optional.of(user));

        userService.deleteCurrentUser();

        verify(userRepository).delete(user);
    }

    @Test
    void deleteCurrentUser_userNotAuthenticated_throwsException() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> userService.deleteCurrentUser());
    }

    @Test
    void deleteCurrentUser_userMismatch_throwsException() {
        User user = new User();
        user.setUsersname("otherUser");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findUserByUsersname("testUser")).thenReturn(Optional.of(user));

        assertThrows(SecurityException.class, () -> userService.deleteCurrentUser());
    }

    @Test
    void getAllUsers_returnsList() {
        List<User> users = Collections.singletonList(new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    void changeUsername_success() {
        User user = new User();
        user.setId("662d4ab8bcce4508b6214d7a");
        user.setUsersname("oldUsername");
        user.setEmail("test@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findUserByUsersname("newUsername")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User updatedUser = userService.changeUsername("newUsername");

        assertEquals("newUsername", updatedUser.getUsersname());
    }

    @Test
    void changeUsername_usernameAlreadyExists_throwsException() {
        User currentUser = new User();
        currentUser.setId("662d4ab8bcce4508b6214d7a");
        currentUser.setUsersname("oldUsername");
        currentUser.setEmail("test@example.com");

        User anotherUser = new User();
        anotherUser.setId("613ghab4rhe4ba18b624yb8");
        anotherUser.setUsersname("newUsername");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findUserByUsersname("newUsername")).thenReturn(Optional.of(anotherUser));

        assertThrows(IllegalArgumentException.class, () -> userService.changeUsername("newUsername"));
    }
}
