package org.mvar.social_elib_project.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mvar.social_elib_project.model.Role;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.repository.UserRepository;
import org.mvar.social_elib_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
class UserServiceIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsersname("testuser");
        testUser.setRole(Role.USER);

        userRepository.save(testUser);

        // Set authentication manually
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser.getEmail(), null, null)
        );
    }

    @Test
    void getCurrentUser_shouldReturnAuthenticatedUser() {
        User currentUser = userService.getCurrentUser();

        assertNotNull(currentUser);
        assertEquals("test@example.com", currentUser.getEmail());
    }

    @Test
    void changeUsername_shouldUpdateUsername() {
        User updatedUser = userService.changeUsername("newUsername");

        assertEquals("newUsername", updatedUser.getUsersname());
    }

    @Test
    void getAllUsers_shouldReturnUsersList() {
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    void deleteCurrentUser_shouldRemoveUserFromDatabase() {
        userService.deleteCurrentUser();

        Optional<User> deleted = userRepository.findUserByEmail("test@example.com");
        assertTrue(deleted.isEmpty());
    }

    @Test
    void getUserRole_shouldReturnUserRole() {
        Role role = userService.getUserRole();
        assertEquals(Role.USER, role);
    }

    @Test
    void getUserByUsername_shouldReturnCorrectUser() {
        Optional<User> result = userService.getUserByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }
}
