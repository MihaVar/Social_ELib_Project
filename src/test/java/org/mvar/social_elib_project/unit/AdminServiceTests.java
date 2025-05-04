package org.mvar.social_elib_project.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mvar.social_elib_project.model.*;
import org.mvar.social_elib_project.payload.request.admin.AdminChangeRoleRequest;
import org.mvar.social_elib_project.repository.*;
import org.mvar.social_elib_project.service.AdminService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ExpertCommentRepository expertCommentRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    private void mockAdminAuth() {
        var auth = new UsernamePasswordAuthenticationToken(
                "admin", null,
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void deleteUserByAdmin_shouldDeleteUser() {
        mockAdminAuth();
        var user = new User();
        user.setUsersname("testuser");

        when(userRepository.findUserByUsersname("testuser")).thenReturn(Optional.of(user));

        adminService.deleteUserByAdmin("testuser");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserByAdmin_shouldThrowIfNotAdmin() {
        var auth = new UsernamePasswordAuthenticationToken(
                "user", null,
                List.of(new SimpleGrantedAuthority("USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(SecurityException.class, () -> adminService.deleteUserByAdmin("user"));
    }

    @Test
    void updateUserRole_shouldUpdateRole() {
        mockAdminAuth();
        var user = new User();
        user.setUsersname("johndoe");
        user.setRole(Role.USER);

        when(userRepository.findUserByUsersname("johndoe")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var request = new AdminChangeRoleRequest("johndoe", Role.EXPERT);
        var updated = adminService.updateUserRole(request);

        assertEquals(Role.EXPERT, updated.getRole());
    }

    @Test
    void deleteCommentByAdmin_shouldDeleteComment() {
        mockAdminAuth();
        var comment = new Comment();
        comment.setCommentId(42L);

        when(commentRepository.findCommentByCommentId(42L)).thenReturn(Optional.of(comment));

        adminService.deleteCommentByAdmin(42L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteExpertCommentByAdmin_shouldRemoveExpertCommentFromItem() {
        mockAdminAuth();
        var expertComment = new ExpertComment();
        expertComment.setExpertCommentId(1L);
        expertComment.setItemId(99L);

        var item = new Item();
        item.setItemId(99L);
        item.setExpertComment(Set.of());

        when(expertCommentRepository.findExpertCommentByExpertCommentId(1L)).thenReturn(Optional.of(expertComment));
        when(itemRepository.findById(99L)).thenReturn(Optional.of(item));

        adminService.deleteExpertCommentByAdmin(1L);

        verify(expertCommentRepository).delete(expertComment);
        assertNull(item.getExpertComment());
    }

    @Test
    void deleteItemByAdmin_shouldDeleteItemAndItsComments() {
        mockAdminAuth();
        var item = new Item();
        item.setItemId(100L);

        when(itemRepository.findItemByItemId(100L)).thenReturn(Optional.of(item));

        adminService.deleteItemByAdmin(100L);

        verify(itemRepository).delete(item);
        verify(commentRepository).deleteAllByItemId(100L);
        verify(expertCommentRepository).deleteAllByItemId(100L);
    }
}
