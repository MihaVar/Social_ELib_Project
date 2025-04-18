package org.mvar.social_elib_project.service;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Comment;
import org.mvar.social_elib_project.model.ExpertComment;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.model.Role;
import org.mvar.social_elib_project.payload.request.comment.AddCommentRequest;
import org.mvar.social_elib_project.payload.request.comment.AddExpertCommentRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ExpertCommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ExpertCommentRepository expertCommentRepository;
    private final ItemRepository itemRepository;

    public Comment addCommentToItem(AddCommentRequest addCommentRequest, String itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String user = authentication.getName();
        Comment comment = Comment.builder()
                .text(addCommentRequest.text())
                .itemId(item.getId())
                .user(user)
                .build();
        comment.setDate(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public void deleteComment(String id, String user) {
        Comment comment = commentRepository.findCommentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + id));
        if (!comment.getUser().equals(user)) {
            throw new IllegalArgumentException("User not authorized to delete comment");
        }
        commentRepository.deleteById(id);
    }

    public List<Comment> getCommentsByItem(String itemId) {
        return commentRepository.findCommentsByItemId(itemId);
    }

    public Item addExpertCommentToItem(AddExpertCommentRequest addExpertCommentRequest, String itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        boolean isExpert = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Role.EXPERT.name()));
        if (!isExpert) {
            throw new IllegalStateException("User does not have permission to add expert comment");
        }

        String user = authentication.getName();

        ExpertComment expertComment = ExpertComment.builder()
                .id(UUID.randomUUID().toString())
                .text(addExpertCommentRequest.text())
                .itemId(item.getId())
                .user(user)
                .creationDate(LocalDateTime.now())
                .build();
        item.setExpertComment(expertComment);
        return itemRepository.save(item);
    }

    public void deleteExpertComment(String id, String user) {
        ExpertComment expertComment = expertCommentRepository.findExpertCommentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + id));
        if (!expertComment.getUser().equals(user)) {
            throw new IllegalArgumentException("User not authorized to delete comment");
        }
        expertCommentRepository.deleteById(id);
    }
}
