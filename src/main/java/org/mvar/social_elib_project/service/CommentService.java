package org.mvar.social_elib_project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvar.social_elib_project.model.*;
import org.mvar.social_elib_project.payload.request.comment.AddCommentRequest;
import org.mvar.social_elib_project.payload.request.comment.AddExpertCommentRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ExpertCommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ExpertCommentRepository expertCommentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final IdCounterService idCounterService;

    public Comment addCommentToItem(AddCommentRequest addCommentRequest, long itemId) {
        Item item = itemRepository.findItemByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        Comment comment = Comment.builder()
                .text(addCommentRequest.text())
                .itemId(item.getItemId())
                .user(user.getUsersname())
                .build();
        comment.setDate(LocalDateTime.now());
        comment.setCommentId(idCounterService.generateSequence("comments_sequence"));
        return commentRepository.save(comment);
    }

    public void deleteComment(long id) {
        Comment comment = commentRepository.findCommentByCommentId(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + id));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        if (!comment.getUser().equals(user.getUsersname())) {
            throw new IllegalArgumentException("User not authorized to delete comment");
        }
        commentRepository.deleteByCommentId(id);
    }

    public List<Comment> getCommentsByItem(long itemId) {
        return commentRepository.findCommentsByItemId(itemId);
    }

    public Item addExpertCommentToItem(AddExpertCommentRequest addExpertCommentRequest, long itemId) {
        Item item = itemRepository.findItemByItemId(itemId)
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

        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        ExpertComment expertComment = ExpertComment.builder()
                .text(addExpertCommentRequest.text())
                .itemId(item.getItemId())
                .user(user.getUsersname())
                .creationDate(LocalDateTime.now())
                .build();
        item.setExpertComment(expertComment);
        expertComment.setExpertCommentId(idCounterService.generateSequence("expert_comments_sequence"));
        return itemRepository.save(item);
    }

    public void deleteExpertComment(long id) {
        ExpertComment expertComment = expertCommentRepository.findExpertCommentByExpertCommentId(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + id));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        if (!expertComment.getUser().equals(user.getUsersname())) {
            throw new IllegalArgumentException("User not authorized to delete comment");
        }
        expertCommentRepository.deleteByExpertCommentId(id);
    }
}
