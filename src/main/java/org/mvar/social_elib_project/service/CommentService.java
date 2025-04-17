package org.mvar.social_elib_project.service;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Comment;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.comment.AddCommentRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
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
}
