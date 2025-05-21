package org.mvar.social_elib_project.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mvar.social_elib_project.model.*;
import org.mvar.social_elib_project.payload.request.admin.AdminChangeRoleRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ExpertCommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.mvar.social_elib_project.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "file:./.env")
@ActiveProfiles("test")
class AdminServiceIntegrationTests {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ExpertCommentRepository expertCommentRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
                .email("test@test.com")
                .password("password")
                .role(Role.USER)
                .build();
        testUser.setUsersname("TestUser_" + System.currentTimeMillis());
        User testAdmin = User.builder()
                .email("testadminemail@gmail.com")
                .password("adminpassword")
                .role(Role.ADMIN)
                .build();
        userRepository.save(testUser);
        userRepository.save(testAdmin);
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(testAdmin.getEmail(), null, "ADMIN"));
    }

    @Test
    public void shouldDeleteUserByAdmin() {
        adminService.deleteUserByAdmin(testUser.getUsersname());

        assertFalse(userRepository.findById(testUser.getUsersname()).isPresent(), "User should be deleted");
    }

    @Test
    public void shouldUpdateUserRole() {
        AdminChangeRoleRequest changeRoleRequest = new AdminChangeRoleRequest(testUser.getUsersname(), Role.EXPERT);
        adminService.updateUserRole(changeRoleRequest);

        User updatedUser = userRepository.findUserByUsersname(testUser.getUsersname()).orElseThrow();
        assertEquals(Role.EXPERT, updatedUser.getRole(), "User role should be updated to EXPERT");
    }

    @Test
    public void shouldDeleteCommentByAdmin() {
        Comment comment = new Comment();
        comment.setText("Test comment");
        testUser.setUsersname("new_int_test_user_comment");
        comment.setUser(testUser.getUsersname());
        comment.setCommentId(1);
        commentRepository.save(comment);

        adminService.deleteCommentByAdmin(comment.getCommentId());

        assertFalse(commentRepository.findById(comment.getCommentId()).isPresent(), "Comment should be deleted");
    }

    @Test
    public void shouldDeleteExpertCommentByAdmin() {
        Item item = new Item();
        item.setItemId(1);
        itemRepository.save(item);

        ExpertComment expertComment = new ExpertComment();
        expertComment.setExpertCommentId(System.currentTimeMillis());
        testUser.setUsersname("new_int_test_user_expert_comment");
        expertComment.setUser(testUser.getUsersname());

        Set<ExpertComment> expertComments = new HashSet<>();
        expertComments.add(expertComment);
        item.setExpertComment(expertComments);
        itemRepository.save(item);

        adminService.deleteExpertCommentByAdmin(expertComment.getExpertCommentId());

        Optional<ExpertComment> deletedComment = expertCommentRepository.findById(expertComment.getExpertCommentId());
        assertFalse(deletedComment.isPresent(), "Expert comment should be deleted");
    }



    @Test
    public void shouldDeleteItemByAdmin() {
        Item item = new Item();
        item.setItemId(2);
        itemRepository.save(item);

        adminService.deleteItemByAdmin(item.getItemId());

        assertFalse(itemRepository.findById(item.getItemId()).isPresent(), "Item should be deleted");
        assertTrue(commentRepository.findCommentsByItemId(item.getItemId()).isEmpty(), "Comments related to item should be deleted");
        assertTrue(expertCommentRepository.findExpertCommentByExpertCommentId(item.getItemId()).isEmpty(), "Expert comments related to item should be deleted");
    }

    @Test
    public void shouldThrowExceptionIfUserIsNotAdmin() {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(testUser.getEmail(), null, "ROLE_USER"));

        SecurityException exception = assertThrows(SecurityException.class, () -> adminService.deleteUserByAdmin(testUser.getUsersname()));

        assertEquals("User does not have permission to delete other users", exception.getMessage(), "Exception message should match");
    }
}

