package org.mvar.social_elib_project.service;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.*;
import org.mvar.social_elib_project.payload.request.admin.AdminChangeRoleRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ExpertCommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final ExpertCommentRepository expertCommentRepository;

    public void deleteUserByAdmin(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Role.ADMIN.name()));
        if (!isAdmin) {
            throw new SecurityException("User does not have permission to delete other users");
        }
        User userToDelete = userRepository.findUserByUsersname(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        userRepository.delete(userToDelete);
    }

    public User updateUserRole(AdminChangeRoleRequest request) {
        adminAuth();
        User userToUpdate = userRepository.findUserByUsersname(request.user())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.user()));
        userToUpdate.setRole(request.role());
        return userRepository.save(userToUpdate);
    }

    public void deleteCommentByAdmin(long commentId) {
        adminAuth();
        Comment comment = commentRepository.findCommentByCommentId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
        commentRepository.delete(comment);
    }

    public void deleteExpertCommentByAdmin(long expertCommentId) {
        adminAuth();
        Optional<Item> itemOptional = itemRepository.findAll().stream()
                .filter(item -> item.getExpertComment().stream()
                        .anyMatch(comment -> comment.getExpertCommentId() == expertCommentId))
                .findFirst();
        if (itemOptional.isEmpty()) {
            throw new IllegalArgumentException("ExpertComment not found in any item: " + expertCommentId);
        }
        Item item = itemOptional.get();

        ExpertComment commentToDelete = item.getExpertComment().stream()
                .filter(comment -> comment.getExpertCommentId() == expertCommentId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ExpertComment not found: " + expertCommentId));
        item.getExpertComment().remove(commentToDelete);
        itemRepository.save(item);
    }

    public void deleteItemByAdmin(long itemId) {
        adminAuth();
        Item item = itemRepository.findItemByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
        itemRepository.delete(item);
        commentRepository.deleteAllByItemId(itemId);
        expertCommentRepository.deleteAllByItemId(itemId);
    }

    private void adminAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Role.ADMIN.name()));
        if (!isAdmin) {
            throw new SecurityException("User does not have permission to update user roles");
        }
    }
}
